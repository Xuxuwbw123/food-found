package com.freshtrace.unified.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.freshtrace.unified.common.Result;
import com.freshtrace.unified.common.UserContext;
import com.freshtrace.unified.config.ChatWebSocketHandler;
import com.freshtrace.unified.entity.*;
import com.freshtrace.unified.mapper.OrderInfoMapper;
import com.freshtrace.unified.mapper.ProductMapper;
import com.freshtrace.unified.mapper.SysUserMapper;
import com.freshtrace.unified.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class ChatController {

    @Autowired private ChatMessageService chatService;
    @Autowired private SysUserMapper userMapper;
    @Autowired private OrderInfoService orderInfoService;
    @Autowired private OrderItemService orderItemService;
    @Autowired private ProductMapper productMapper;
    @Autowired private PaymentInfoService paymentInfoService;
    @Autowired private OrderLogService orderLogService;
    @Autowired private AfterSalesOrderService afterSalesService;
    @Autowired private MemberPointService memberPointService;
    @Autowired private PointLogService pointLogService;
    @Autowired private MemberLevelConfigService levelConfigService;
    @Autowired private OrderInfoMapper orderInfoMapper;
    @Autowired private SysConfigService configService;

    private static final Long ADMIN_ID = 1000000000000001L;
    private static final int MAX_AI_ROUNDS = 10;

    private String getAiConfig(String key, String defaultValue) {
        SysConfig config = configService.getOne(new LambdaQueryWrapper<SysConfig>()
                .eq(SysConfig::getConfigKey, key));
        return config != null && config.getConfigValue() != null ? config.getConfigValue() : defaultValue;
    }

    // 跟踪每个用户的AI对话轮数
    private static final Map<Long, Integer> aiRoundCount = new ConcurrentHashMap<>();

    // AI系统提示词
    private static final String AI_SYSTEM_PROMPT = "你是农臻溯源电商平台的智能客服助手。你只能回答以下简单问题：\n" +
            "1. 发货时间：一般下单后1-3个工作日内发货\n" +
            "2. 预计送达时间：发货后2-5天到达，具体看地区\n" +
            "3. 物流查询：请到\"我的订单\"页面查看物流信息\n" +
            "4. 商品信息：可到商品详情页查看\n" +
            "5. 账户问题：会员、积分、优惠券的基本说明\n\n" +
            "严格规则：\n" +
            "- 只回答上述简单问题，回答不超过50字\n" +
            "- 用户问与业务无关的问题（闲聊、政治、其他平台等），回复：\"抱歉，我只能处理与订单相关的问题。\"\n" +
            "- 涉及以下任何内容，必须回复：\"这个问题需要人工客服为您处理，正在为您转接...\" 然后加上 [TRANSFER_TO_HUMAN]：\n" +
            "  * 退款、退货、换货\n" +
            "  * 订单异常、商品质量问题\n" +
            "  * 投诉、差评\n" +
            "  * 具体订单操作（取消、修改地址等）\n" +
            "  * 用户表达不满或情绪\n" +
            "- 不要闲聊，不要扩展话题，不要主动提问";

    // ============ 用户端：发送消息 ============
    @PostMapping("/api/chat/send")
    public Result<?> userSend(@RequestBody Map<String, Object> body) {
        Long userId = UserContext.getUserId();
        String content = (String) body.get("content");
        String msgType = (String) body.getOrDefault("msgType", "text");
        boolean useAI = Boolean.TRUE.equals(body.get("useAI"));

        if (content == null || content.trim().isEmpty()) {
            return Result.error(400, "消息内容不能为空");
        }

        // 保存用户消息
        Long toUserId = useAI ? 0L : (findChatAdmin(userId) != null ? findChatAdmin(userId) : ADMIN_ID);
        ChatMessage userMsg = new ChatMessage();
        userMsg.setFromUserId(userId);
        userMsg.setToUserId(toUserId);
        userMsg.setContent(content);
        userMsg.setMsgType(msgType);
        userMsg.setIsRead(0);
        userMsg.setCreateTime(LocalDateTime.now());
        chatService.save(userMsg);

        if (useAI) {
            // AI回复
            int rounds = aiRoundCount.getOrDefault(userId, 0);
            if (rounds >= MAX_AI_ROUNDS) {
                // 超过轮数，转人工
                aiRoundCount.remove(userId);
                String transferMsg = "您已达到AI客服对话上限，正在为您转接人工客服...";
                ChatMessage aiReply = saveAiReply(userId, transferMsg);
                Map<String, Object> push = new HashMap<>();
                push.put("type", "ai_reply");
                push.put("message", buildMessageVO(aiReply, userId));
                push.put("transferToHuman", true);
                ChatWebSocketHandler.sendToUser(userId, push);

                // 通知管理员有新用户需要人工服务
                notifyAdminNewUser(userId);
                return Result.success(Map.of("message", buildMessageVO(userMsg, userId), "aiReply", buildMessageVO(aiReply, userId), "transferToHuman", true));
            }

            // 调用AI API
            aiRoundCount.put(userId, rounds + 1);
            String aiResponse = callAI(content, userId);
            boolean transferToHuman = aiResponse.contains("[TRANSFER_TO_HUMAN]");
            if (transferToHuman) {
                aiResponse = aiResponse.replace("[TRANSFER_TO_HUMAN]", "").trim();
                aiRoundCount.remove(userId);
            }

            ChatMessage aiReply = saveAiReply(userId, aiResponse);
            Map<String, Object> push = new HashMap<>();
            push.put("type", "ai_reply");
            push.put("message", buildMessageVO(aiReply, userId));
            if (transferToHuman) push.put("transferToHuman", true);
            ChatWebSocketHandler.sendToUser(userId, push);

            if (transferToHuman) notifyAdminNewUser(userId);

            return Result.success(Map.of("message", buildMessageVO(userMsg, userId), "aiReply", buildMessageVO(aiReply, userId), "transferToHuman", transferToHuman));
        } else {
            // 发送给人工客服
            Map<String, Object> push = new HashMap<>();
            push.put("type", "new_message");
            push.put("message", buildMessageVO(userMsg, userId));
            ChatWebSocketHandler.sendToUser(toUserId, push);
            return Result.success(Map.of("message", buildMessageVO(userMsg, userId)));
        }
    }

    // ============ 用户端：切换到人工客服 ============
    @PostMapping("/api/chat/transfer")
    public Result<?> transferToHuman() {
        Long userId = UserContext.getUserId();
        aiRoundCount.remove(userId);

        Long adminId = findChatAdmin(userId);
        if (adminId == null) adminId = ADMIN_ID;

        ChatMessage msg = new ChatMessage();
        msg.setFromUserId(userId);
        msg.setToUserId(adminId);
        msg.setContent("用户请求转接人工客服");
        msg.setMsgType("system");
        msg.setIsRead(0);
        msg.setCreateTime(LocalDateTime.now());
        chatService.save(msg);

        notifyAdminNewUser(userId);
        return Result.success("已转接人工客服");
    }

    // ============ 管理员端：发送消息 ============
    @PostMapping("/admin/chat/send")
    public Result<?> adminSend(@RequestBody Map<String, Object> body) {
        Long adminId = UserContext.getUserId();
        Long toUserId = Long.valueOf(body.get("toUserId").toString());
        String content = (String) body.get("content");
        String msgType = (String) body.getOrDefault("msgType", "text");

        if (content == null || content.trim().isEmpty()) {
            return Result.error(400, "消息内容不能为空");
        }

        ChatMessage msg = new ChatMessage();
        msg.setFromUserId(adminId);
        msg.setToUserId(toUserId);
        msg.setContent(content);
        msg.setMsgType(msgType);
        msg.setIsRead(0);
        msg.setCreateTime(LocalDateTime.now());
        chatService.save(msg);

        Map<String, Object> push = new HashMap<>();
        push.put("type", "new_message");
        push.put("message", buildMessageVO(msg, adminId));
        ChatWebSocketHandler.sendToUser(toUserId, push);

        return Result.success(msg);
    }

    // ============ 管理员端：处理退款（完整同步） ============
    @PostMapping("/admin/chat/refund")
    @org.springframework.transaction.annotation.Transactional
    public Result<?> processRefund(@RequestBody Map<String, Object> body) {
        Long adminId = UserContext.getUserId();
        String orderIdStr = body.get("orderId").toString().trim();
        Long userId = Long.valueOf(body.get("userId").toString());
        String reason = (String) body.getOrDefault("reason", "客服处理退款");

        // 支持订单号（FD开头）或订单ID（纯数字）
        OrderInfo order;
        if (orderIdStr.startsWith("FD") || orderIdStr.startsWith("fd")) {
            order = orderInfoService.getOne(new LambdaQueryWrapper<OrderInfo>()
                    .eq(OrderInfo::getOrderNo, orderIdStr.toUpperCase()));
        } else {
            order = orderInfoService.getById(Long.valueOf(orderIdStr));
        }
        if (order == null) return Result.error(404, "订单不存在，请检查订单号");
        if (order.getOrderStatus() == 4) return Result.error(400, "订单已取消");

        BigDecimal refundAmount = order.getPayAmount() != null ? order.getPayAmount() : order.getTotalAmount();

        // 1. 回滚库存
        List<OrderItem> items = orderItemService.list(new LambdaQueryWrapper<OrderItem>()
                .eq(OrderItem::getOrderId, order.getId()));
        for (OrderItem item : items) {
            Product p = productMapper.selectById(item.getProductId());
            if (p != null) {
                p.setStock(p.getStock() + item.getQuantity());
                p.setSales(Math.max(0, (p.getSales() != null ? p.getSales() : 0) - item.getQuantity()));
                productMapper.updateById(p);
            }
        }

        // 2. 更新订单状态
        order.setOrderStatus(4);
        order.setCancelReason(reason);
        order.setCancelTime(LocalDateTime.now());
        orderInfoMapper.updateById(order);

        // 3. 更新支付记录
        PaymentInfo pay = paymentInfoService.getOne(new LambdaQueryWrapper<PaymentInfo>()
                .eq(PaymentInfo::getOrderId, order.getId()));
        if (pay != null && pay.getPayStatus() == 1) {
            pay.setPayStatus(3); // 已退款
            paymentInfoService.updateById(pay);
        }

        // 4. 会员卡支付退款到余额
        if (order.getPayType() != null && order.getPayType() == 3) {
            SysUser user = userMapper.selectById(userId);
            if (user != null) {
                user.setBalance(user.getBalance().add(refundAmount));
                userMapper.updateById(user);
            }
        }

        // 5. 扣除积分
        SysUser paidUser = userMapper.selectById(userId);
        int memberLevel = paidUser != null && paidUser.getMemberLevel() != null ? paidUser.getMemberLevel() : 0;
        int pointsRate = 1;
        if (memberLevel > 0) {
            MemberLevelConfig config = levelConfigService.getOne(new LambdaQueryWrapper<MemberLevelConfig>()
                    .eq(MemberLevelConfig::getLevel, memberLevel));
            if (config != null && config.getPointsRate() != null) pointsRate = config.getPointsRate();
        }
        int deductPoints = refundAmount.intValue() * pointsRate;
        if (deductPoints > 0) {
            MemberPoint mp = memberPointService.getById(userId);
            if (mp != null) {
                mp.setAvailablePoint(Math.max(0, mp.getAvailablePoint() - deductPoints));
                mp.setTotalPoint(Math.max(0, mp.getTotalPoint() - deductPoints));
                memberPointService.updateById(mp);

                PointLog log = new PointLog();
                log.setUserId(userId); log.setType("refund");
                log.setPoint(-deductPoints); log.setBalance(mp.getAvailablePoint());
                log.setRemark("客服退款扣除积分");
                log.setCreateTime(LocalDateTime.now());
                pointLogService.save(log);
            }
        }

        // 6. 创建售后工单
        AfterSalesOrder afterSales = new AfterSalesOrder();
        afterSales.setAfterSalesNo("AS" + System.currentTimeMillis());
        afterSales.setOrderId(order.getId());
        afterSales.setOrderNo(order.getOrderNo());
        afterSales.setUserId(userId);
        afterSales.setFarmerId(order.getFarmerId());
        afterSales.setAfterSalesType(1); // 1=退款
        afterSales.setReason(reason);
        afterSales.setRefundAmount(refundAmount);
        afterSales.setStatus(1); // 已审核
        afterSales.setAdminId(adminId);
        afterSales.setAdminRemark("客服退款处理");
        afterSales.setApplyTime(LocalDateTime.now());
        afterSales.setAuditTime(LocalDateTime.now());
        afterSales.setCreateTime(LocalDateTime.now());
        afterSalesService.save(afterSales);

        // 7. 记录订单日志
        OrderLog orderLog = new OrderLog();
        orderLog.setOrderId(order.getId()); orderLog.setOrderNo(order.getOrderNo());
        orderLog.setOrderStatus(4); orderLog.setOperatorType(3); orderLog.setOperatorId(adminId);
        orderLog.setRemark("客服退款：" + reason);
        orderLog.setCreateTime(LocalDateTime.now());
        orderLogService.save(orderLog);

        // 8. 发送消息通知用户
        String refundMsg = String.format("【退款成功】\n订单号：%s\n退款金额：¥%s\n退款方式：%s\n%s",
                order.getOrderNo(), refundAmount,
                order.getPayType() == 3 ? "退回会员卡余额" : "原路退回",
                order.getPayType() == 3 ? "退款已退回您的会员卡余额，请查收。" : "退款将在1-3个工作日内原路退回。");
        ChatMessage msg = new ChatMessage();
        msg.setFromUserId(adminId); msg.setToUserId(userId);
        msg.setContent(refundMsg); msg.setMsgType("refund");
        msg.setIsRead(0); msg.setCreateTime(LocalDateTime.now());
        chatService.save(msg);

        Map<String, Object> push = new HashMap<>();
        push.put("type", "new_message");
        push.put("message", buildMessageVO(msg, adminId));
        ChatWebSocketHandler.sendToUser(userId, push);

        return Result.success("退款成功", Map.of("refundAmount", refundAmount));
    }

    // ============ 用户端：获取聊天记录 ============
    @GetMapping("/api/chat/messages")
    public Result<?> userMessages() {
        Long userId = UserContext.getUserId();
        Long adminId = findChatAdmin(userId);
        final Long targetAdminId = adminId != null ? adminId : ADMIN_ID;

        // 标记已读
        chatService.update(new LambdaUpdateWrapper<ChatMessage>()
                .set(ChatMessage::getIsRead, 1)
                .eq(ChatMessage::getFromUserId, targetAdminId)
                .eq(ChatMessage::getToUserId, userId)
                .eq(ChatMessage::getIsRead, 0));

        // 我发给管理员的
        List<ChatMessage> list1 = chatService.list(new LambdaQueryWrapper<ChatMessage>()
                .eq(ChatMessage::getFromUserId, userId)
                .eq(ChatMessage::getToUserId, targetAdminId)
                .orderByAsc(ChatMessage::getCreateTime));

        // AI发给我的
        List<ChatMessage> list2 = chatService.list(new LambdaQueryWrapper<ChatMessage>()
                .eq(ChatMessage::getFromUserId, 0L)
                .eq(ChatMessage::getToUserId, userId)
                .orderByAsc(ChatMessage::getCreateTime));

        // 管理员发给我的
        List<ChatMessage> list3 = chatService.list(new LambdaQueryWrapper<ChatMessage>()
                .eq(ChatMessage::getFromUserId, targetAdminId)
                .eq(ChatMessage::getToUserId, userId)
                .orderByAsc(ChatMessage::getCreateTime));

        List<ChatMessage> allMessages = new ArrayList<>();
        allMessages.addAll(list1);
        allMessages.addAll(list2);
        allMessages.addAll(list3);
        allMessages.sort((a, b) -> a.getCreateTime().compareTo(b.getCreateTime()));

        List<Map<String, Object>> result = new ArrayList<>();
        for (ChatMessage m : allMessages) {
            result.add(buildMessageVO(m, userId));
        }
        return Result.success(result);
    }

    // ============ 管理员端：获取会话列表 ============
    @GetMapping("/admin/chat/conversations")
    public Result<?> adminConversations() {
        Long adminId = UserContext.getUserId();
        List<ChatMessage> allMessages = chatService.list(new LambdaQueryWrapper<ChatMessage>()
                .and(w -> w.eq(ChatMessage::getFromUserId, adminId).or().eq(ChatMessage::getToUserId, adminId))
                .orderByDesc(ChatMessage::getCreateTime));

        Map<Long, ChatMessage> convMap = new LinkedHashMap<>();
        for (ChatMessage m : allMessages) {
            Long otherUserId = m.getFromUserId().equals(adminId) ? m.getToUserId() : m.getFromUserId();
            if (otherUserId == 0L) continue; // AI消息
            convMap.putIfAbsent(otherUserId, m);
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<Long, ChatMessage> entry : convMap.entrySet()) {
            Long userIdx = entry.getKey();
            ChatMessage lastMsg = entry.getValue();
            SysUser user = userMapper.selectById(userIdx);

            long unreadCount = chatService.count(new LambdaQueryWrapper<ChatMessage>()
                    .eq(ChatMessage::getFromUserId, userIdx)
                    .eq(ChatMessage::getToUserId, adminId)
                    .eq(ChatMessage::getIsRead, 0));

            Map<String, Object> conv = new HashMap<>();
            conv.put("userId", userIdx);
            conv.put("nickname", user != null ? (user.getNickname() != null ? user.getNickname() : user.getUsername()) : "用户" + userIdx);
            conv.put("lastMessage", lastMsg.getContent());
            conv.put("lastTime", lastMsg.getCreateTime());
            conv.put("unreadCount", unreadCount);
            conv.put("online", ChatWebSocketHandler.isOnline(userIdx));
            result.add(conv);
        }
        return Result.success(result);
    }

    // ============ 管理员端：获取与指定用户的聊天记录 ============
    @GetMapping("/admin/chat/messages/{userId}")
    public Result<?> adminMessages(@PathVariable Long userId) {
        Long adminId = UserContext.getUserId();

        // 标记已读
        chatService.update(new LambdaUpdateWrapper<ChatMessage>()
                .set(ChatMessage::getIsRead, 1)
                .eq(ChatMessage::getFromUserId, userId)
                .eq(ChatMessage::getToUserId, adminId)
                .eq(ChatMessage::getIsRead, 0));

        // 查询：用户发给我的 + 用户发给AI的 + AI发给用户的 + 我发给用户的
        List<ChatMessage> list1 = chatService.list(new LambdaQueryWrapper<ChatMessage>()
                .eq(ChatMessage::getFromUserId, userId)
                .eq(ChatMessage::getToUserId, adminId)
                .orderByAsc(ChatMessage::getCreateTime));

        List<ChatMessage> list2 = chatService.list(new LambdaQueryWrapper<ChatMessage>()
                .eq(ChatMessage::getFromUserId, 0L)
                .eq(ChatMessage::getToUserId, userId)
                .orderByAsc(ChatMessage::getCreateTime));

        List<ChatMessage> list3 = chatService.list(new LambdaQueryWrapper<ChatMessage>()
                .eq(ChatMessage::getFromUserId, adminId)
                .eq(ChatMessage::getToUserId, userId)
                .orderByAsc(ChatMessage::getCreateTime));

        List<ChatMessage> list4 = chatService.list(new LambdaQueryWrapper<ChatMessage>()
                .eq(ChatMessage::getFromUserId, userId)
                .eq(ChatMessage::getToUserId, 0L)
                .orderByAsc(ChatMessage::getCreateTime));

        List<ChatMessage> allMessages = new ArrayList<>();
        allMessages.addAll(list1);
        allMessages.addAll(list2);
        allMessages.addAll(list3);
        allMessages.addAll(list4);
        allMessages.sort((a, b) -> a.getCreateTime().compareTo(b.getCreateTime()));

        List<Map<String, Object>> result = new ArrayList<>();
        for (ChatMessage m : allMessages) {
            result.add(buildMessageVO(m, adminId));
        }
        return Result.success(result);
    }

    // ============ 用户端：获取我的订单列表（供客服使用） ============
    @GetMapping("/api/chat/orders")
    public Result<?> chatOrders() {
        Long userId = UserContext.getUserId();
        List<OrderInfo> orders = orderInfoService.list(new LambdaQueryWrapper<OrderInfo>()
                .eq(OrderInfo::getUserId, userId)
                .ne(OrderInfo::getOrderStatus, 4)
                .orderByDesc(OrderInfo::getCreateTime)
                .last("LIMIT 20"));

        List<Map<String, Object>> result = new ArrayList<>();
        for (OrderInfo o : orders) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", o.getId());
            item.put("orderNo", o.getOrderNo());
            item.put("totalAmount", o.getTotalAmount());
            item.put("payAmount", o.getPayAmount());
            item.put("orderStatus", o.getOrderStatus());
            item.put("statusText", getStatusText(o.getOrderStatus()));
            item.put("createTime", o.getCreateTime());
            result.add(item);
        }
        return Result.success(result);
    }

    // ============ 管理员端：测试AI连接 ============
    @PostMapping("/admin/chat/test-ai")
    public Result<?> testAi(@RequestBody Map<String, String> body) {
        try {
            String apiUrl = body.get("apiUrl");
            String apiKey = body.get("apiKey");
            String model = body.get("model");

            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            List<Map<String, String>> messages = new ArrayList<>();
            Map<String, String> msg = new HashMap<>();
            msg.put("role", "user");
            msg.put("content", "hello");
            messages.add(msg);
            requestBody.put("messages", messages);
            requestBody.put("max_tokens", 20);

            String jsonBody = mapper.writeValueAsString(requestBody);

            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(15000);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                String errBody = new String(conn.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);
                return Result.error(responseCode, "API返回错误: " + errBody.substring(0, Math.min(200, errBody.length())));
            }

            String responseBody = new String(conn.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            JsonNode root = mapper.readTree(responseBody);
            String reply = root.path("choices").get(0).path("message").path("content").asText();
            return Result.success(Map.of("reply", reply.substring(0, Math.min(100, reply.length()))));
        } catch (Exception e) {
            return Result.error(500, "连接失败: " + e.getMessage());
        }
    }

    // ============ 未读消息数 ============
    @GetMapping("/api/chat/unread")
    public Result<?> userUnread() {
        Long userId = UserContext.getUserId();
        Long adminId = findChatAdmin(userId);
        if (adminId == null) adminId = ADMIN_ID;
        long count = chatService.count(new LambdaQueryWrapper<ChatMessage>()
                .eq(ChatMessage::getFromUserId, adminId)
                .eq(ChatMessage::getToUserId, userId)
                .eq(ChatMessage::getIsRead, 0));
        return Result.success(Map.of("count", count));
    }

    @GetMapping("/admin/chat/unread")
    public Result<?> adminUnread() {
        Long adminId = UserContext.getUserId();
        long count = chatService.count(new LambdaQueryWrapper<ChatMessage>()
                .eq(ChatMessage::getToUserId, adminId)
                .eq(ChatMessage::getIsRead, 0));
        return Result.success(Map.of("count", count));
    }

    // ============ 内部方法 ============

    private String callAI(String userMessage, Long userId) {
        try {
            String apiUrl = getAiConfig("ai_api_url", "https://api.xiaomimimo.com/v1/chat/completions");
            String apiKey = getAiConfig("ai_api_key", "");
            String model = getAiConfig("ai_model", "mimo-v2.5-pro");

            if (apiKey == null || apiKey.isEmpty()) {
                return "AI服务未配置，请联系管理员设置API Key。[TRANSFER_TO_HUMAN]";
            }

            // 获取用户最近的聊天记录作为上下文
            List<ChatMessage> recentMsgs = chatService.list(new LambdaQueryWrapper<ChatMessage>()
                    .and(w -> w
                            .and(x -> x.eq(ChatMessage::getFromUserId, userId).eq(ChatMessage::getToUserId, 0L))
                            .or().and(x -> x.eq(ChatMessage::getFromUserId, 0L).eq(ChatMessage::getToUserId, userId))
                    )
                    .orderByDesc(ChatMessage::getCreateTime)
                    .last("LIMIT 10"));

            // 构建消息列表
            List<Map<String, String>> messages = new ArrayList<>();
            Map<String, String> systemMsg = new HashMap<>();
            systemMsg.put("role", "system");
            systemMsg.put("content", AI_SYSTEM_PROMPT);
            messages.add(systemMsg);

            for (int i = recentMsgs.size() - 1; i >= 0; i--) {
                ChatMessage m = recentMsgs.get(i);
                Map<String, String> msg = new HashMap<>();
                msg.put("role", m.getFromUserId() == 0L ? "assistant" : "user");
                msg.put("content", m.getContent());
                messages.add(msg);
            }

            Map<String, String> currentMsg = new HashMap<>();
            currentMsg.put("role", "user");
            currentMsg.put("content", userMessage);
            messages.add(currentMsg);

            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("messages", messages);
            requestBody.put("max_tokens", 200);
            requestBody.put("temperature", 0.3);

            String jsonBody = mapper.writeValueAsString(requestBody);

            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(15000);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                return "AI服务暂时不可用，正在为您转接人工客服...[TRANSFER_TO_HUMAN]";
            }

            String responseBody = new String(conn.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            JsonNode root = mapper.readTree(responseBody);
            String aiContent = root.path("choices").get(0).path("message").path("content").asText();
            return aiContent;

        } catch (Exception e) {
            return "AI服务暂时不可用，正在为您转接人工客服...[TRANSFER_TO_HUMAN]";
        }
    }

    private ChatMessage saveAiReply(Long userId, String content) {
        ChatMessage msg = new ChatMessage();
        msg.setFromUserId(0L); // 0 = AI
        msg.setToUserId(userId);
        msg.setContent(content);
        msg.setMsgType("text");
        msg.setIsRead(0);
        msg.setCreateTime(LocalDateTime.now());
        chatService.save(msg);
        return msg;
    }

    private void notifyAdminNewUser(Long userId) {
        SysUser user = userMapper.selectById(userId);
        String nickname = user != null ? (user.getNickname() != null ? user.getNickname() : user.getUsername()) : "用户" + userId;

        Map<String, Object> push = new HashMap<>();
        push.put("type", "user_transfer");
        push.put("userId", userId);
        push.put("nickname", nickname);
        push.put("message", nickname + " 请求人工客服");
        ChatWebSocketHandler.sendToUser(ADMIN_ID, push);
    }

    private Long findChatAdmin(Long userId) {
        ChatMessage last = chatService.getOne(new LambdaQueryWrapper<ChatMessage>()
                .and(w -> w.eq(ChatMessage::getFromUserId, userId).or().eq(ChatMessage::getToUserId, userId))
                .orderByDesc(ChatMessage::getCreateTime)
                .last("LIMIT 1"));
        if (last != null) {
            return last.getFromUserId().equals(userId) ? last.getToUserId() : last.getFromUserId();
        }
        return null;
    }

    private Map<String, Object> buildMessageVO(ChatMessage m, Long currentUserId) {
        Map<String, Object> vo = new HashMap<>();
        vo.put("id", m.getId());
        vo.put("fromUserId", m.getFromUserId());
        vo.put("toUserId", m.getToUserId());
        vo.put("content", m.getContent());
        vo.put("msgType", m.getMsgType());
        vo.put("isRead", m.getIsRead());
        vo.put("createTime", m.getCreateTime());
        vo.put("isMine", m.getFromUserId().equals(currentUserId));
        vo.put("isAI", m.getFromUserId() == 0L);
        return vo;
    }

    private String getStatusText(Integer status) {
        if (status == null) return "未知";
        switch (status) {
            case 0: return "待付款";
            case 1: return "待发货";
            case 2: return "已发货";
            case 3: return "已完成";
            case 4: return "已取消";
            default: return "未知";
        }
    }
}
