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

    private static final Long getAdminId() = 1000000000000001L;
    private static final int MAX_AI_ROUNDS = 10;

    private String getAiConfig(String key, String defaultValue) {
        SysConfig config = configService.getOne(new LambdaQueryWrapper<SysConfig>()
                .eq(SysConfig::getConfigKey, key));
        return config != null && config.getConfigValue() != null ? config.getConfigValue() : defaultValue;
    }

    // 璺熻釜姣忎釜鐢ㄦ埛鐨凙I瀵硅瘽杞暟
    private static final Map<Long, Integer> aiRoundCount = new ConcurrentHashMap<>();

    // AI绯荤粺鎻愮ず璇?
    private static final String AI_SYSTEM_PROMPT = "浣犳槸鍐滆嚮婧簮鐢靛晢骞冲彴鐨勬櫤鑳藉鏈嶅姪鎵嬨€備綘鍙兘鍥炵瓟浠ヤ笅绠€鍗曢棶棰橈細\n" +
            "1. 鍙戣揣鏃堕棿锛氫竴鑸笅鍗曞悗1-3涓伐浣滄棩鍐呭彂璐n" +
            "2. 棰勮閫佽揪鏃堕棿锛氬彂璐у悗2-5澶╁埌杈撅紝鍏蜂綋鐪嬪湴鍖篭n" +
            "3. 鐗╂祦鏌ヨ锛氳鍒癨"鎴戠殑璁㈠崟\"椤甸潰鏌ョ湅鐗╂祦淇℃伅\n" +
            "4. 鍟嗗搧淇℃伅锛氬彲鍒板晢鍝佽鎯呴〉鏌ョ湅\n" +
            "5. 璐︽埛闂锛氫細鍛樸€佺Н鍒嗐€佷紭鎯犲埜鐨勫熀鏈鏄嶾n\n" +
            "涓ユ牸瑙勫垯锛歕n" +
            "- 鍙洖绛斾笂杩扮畝鍗曢棶棰橈紝鍥炵瓟涓嶈秴杩?0瀛梊n" +
            "- 鐢ㄦ埛闂笌涓氬姟鏃犲叧鐨勯棶棰橈紙闂茶亰銆佹斂娌汇€佸叾浠栧钩鍙扮瓑锛夛紝鍥炲锛歕"鎶辨瓑锛屾垜鍙兘澶勭悊涓庤鍗曠浉鍏崇殑闂銆俓"\n" +
            "- 娑夊強浠ヤ笅浠讳綍鍐呭锛屽繀椤诲洖澶嶏細\"杩欎釜闂闇€瑕佷汉宸ュ鏈嶄负鎮ㄥ鐞嗭紝姝ｅ湪涓烘偍杞帴...\" 鐒跺悗鍔犱笂 [TRANSFER_TO_HUMAN]锛歕n" +
            "  * 閫€娆俱€侀€€璐с€佹崲璐n" +
            "  * 璁㈠崟寮傚父銆佸晢鍝佽川閲忛棶棰榎n" +
            "  * 鎶曡瘔銆佸樊璇刓n" +
            "  * 鍏蜂綋璁㈠崟鎿嶄綔锛堝彇娑堛€佷慨鏀瑰湴鍧€绛夛級\n" +
            "  * 鐢ㄦ埛琛ㄨ揪涓嶆弧鎴栨儏缁猏n" +
            "- 涓嶈闂茶亰锛屼笉瑕佹墿灞曡瘽棰橈紝涓嶈涓诲姩鎻愰棶";

    // ============ 鐢ㄦ埛绔細鍙戦€佹秷鎭?============
    @PostMapping("/api/chat/send")
    public Result<?> userSend(@RequestBody Map<String, Object> body) {
        Long userId = UserContext.getUserId();
        String content = (String) body.get("content");
        String msgType = (String) body.getOrDefault("msgType", "text");
        boolean useAI = Boolean.TRUE.equals(body.get("useAI"));

        if (content == null || content.trim().isEmpty()) {
            return Result.error(400, "娑堟伅鍐呭涓嶈兘涓虹┖");
        }

        // 淇濆瓨鐢ㄦ埛娑堟伅
        Long toUserId = useAI ? 0L : (findChatAdmin(userId) != null ? findChatAdmin(userId) : getAdminId());
        ChatMessage userMsg = new ChatMessage();
        userMsg.setFromUserId(userId);
        userMsg.setToUserId(toUserId);
        userMsg.setContent(content);
        userMsg.setMsgType(msgType);
        userMsg.setIsRead(0);
        userMsg.setCreateTime(LocalDateTime.now());
        chatService.save(userMsg);

        if (useAI) {
            // AI鍥炲
            int rounds = aiRoundCount.getOrDefault(userId, 0);
            if (rounds >= MAX_AI_ROUNDS) {
                // 瓒呰繃杞暟锛岃浆浜哄伐
                aiRoundCount.remove(userId);
                String transferMsg = "鎮ㄥ凡杈惧埌AI瀹㈡湇瀵硅瘽涓婇檺锛屾鍦ㄤ负鎮ㄨ浆鎺ヤ汉宸ュ鏈?..";
                ChatMessage aiReply = saveAiReply(userId, transferMsg);
                Map<String, Object> push = new HashMap<>();
                push.put("type", "ai_reply");
                push.put("message", buildMessageVO(aiReply, userId));
                push.put("transferToHuman", true);
                ChatWebSocketHandler.sendToUser(userId, push);

                // 閫氱煡绠＄悊鍛樻湁鏂扮敤鎴烽渶瑕佷汉宸ユ湇鍔?
                notifyAdminNewUser(userId);
                return Result.success(Map.of("message", buildMessageVO(userMsg, userId), "aiReply", buildMessageVO(aiReply, userId), "transferToHuman", true));
            }

            // 璋冪敤AI API
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
            // 鍙戦€佺粰浜哄伐瀹㈡湇
            Map<String, Object> push = new HashMap<>();
            push.put("type", "new_message");
            push.put("message", buildMessageVO(userMsg, userId));
            ChatWebSocketHandler.sendToUser(toUserId, push);
            return Result.success(Map.of("message", buildMessageVO(userMsg, userId)));
        }
    }

    // ============ 鐢ㄦ埛绔細鍒囨崲鍒颁汉宸ュ鏈?============
    @PostMapping("/api/chat/transfer")
    public Result<?> transferToHuman() {
        Long userId = UserContext.getUserId();
        aiRoundCount.remove(userId);

        Long adminId = findChatAdmin(userId);
        if (adminId == null) adminId = getAdminId();

        ChatMessage msg = new ChatMessage();
        msg.setFromUserId(userId);
        msg.setToUserId(adminId);
        msg.setContent("鐢ㄦ埛璇锋眰杞帴浜哄伐瀹㈡湇");
        msg.setMsgType("system");
        msg.setIsRead(0);
        msg.setCreateTime(LocalDateTime.now());
        chatService.save(msg);

        notifyAdminNewUser(userId);
        return Result.success("宸茶浆鎺ヤ汉宸ュ鏈?);
    }

    // ============ 绠＄悊鍛樼锛氬彂閫佹秷鎭?============
    @PostMapping("/admin/chat/send")
    public Result<?> adminSend(@RequestBody Map<String, Object> body) {
        Long adminId = UserContext.getUserId();
        Long toUserId = Long.valueOf(body.get("toUserId").toString());
        String content = (String) body.get("content");
        String msgType = (String) body.getOrDefault("msgType", "text");

        if (content == null || content.trim().isEmpty()) {
            return Result.error(400, "娑堟伅鍐呭涓嶈兘涓虹┖");
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

    // ============ 绠＄悊鍛樼锛氬鐞嗛€€娆撅紙瀹屾暣鍚屾锛?============
    @PostMapping("/admin/chat/refund")
    @org.springframework.transaction.annotation.Transactional
    public Result<?> processRefund(@RequestBody Map<String, Object> body) {
        Long adminId = UserContext.getUserId();
        String orderIdStr = body.get("orderId").toString().trim();
        Long userId = Long.valueOf(body.get("userId").toString());
        String reason = (String) body.getOrDefault("reason", "瀹㈡湇澶勭悊閫€娆?);

        // 鏀寔璁㈠崟鍙凤紙FD寮€澶达級鎴栬鍗旾D锛堢函鏁板瓧锛?
        OrderInfo order;
        if (orderIdStr.startsWith("FD") || orderIdStr.startsWith("fd")) {
            order = orderInfoService.getOne(new LambdaQueryWrapper<OrderInfo>()
                    .eq(OrderInfo::getOrderNo, orderIdStr.toUpperCase()));
        } else {
            order = orderInfoService.getById(Long.valueOf(orderIdStr));
        }
        if (order == null) return Result.error(404, "璁㈠崟涓嶅瓨鍦紝璇锋鏌ヨ鍗曞彿");
        if (order.getOrderStatus() == 4) return Result.error(400, "璁㈠崟宸插彇娑?);

        BigDecimal refundAmount = order.getPayAmount() != null ? order.getPayAmount() : order.getTotalAmount();

        // 1. 鍥炴粴搴撳瓨
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

        // 2. 鏇存柊璁㈠崟鐘舵€?
        order.setOrderStatus(4);
        order.setCancelReason(reason);
        order.setCancelTime(LocalDateTime.now());
        orderInfoMapper.updateById(order);

        // 3. 鏇存柊鏀粯璁板綍
        PaymentInfo pay = paymentInfoService.getOne(new LambdaQueryWrapper<PaymentInfo>()
                .eq(PaymentInfo::getOrderId, order.getId()));
        if (pay != null && pay.getPayStatus() == 1) {
            pay.setPayStatus(3); // 宸查€€娆?
            paymentInfoService.updateById(pay);
        }

        // 4. 浼氬憳鍗℃敮浠橀€€娆惧埌浣欓
        if (order.getPayType() != null && order.getPayType() == 3) {
            SysUser user = userMapper.selectById(userId);
            if (user != null) {
                user.setBalance(user.getBalance().add(refundAmount));
                userMapper.updateById(user);
            }
        }

        // 5. 鎵ｉ櫎绉垎
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
                log.setRemark("瀹㈡湇閫€娆炬墸闄ょН鍒?);
                log.setCreateTime(LocalDateTime.now());
                pointLogService.save(log);
            }
        }

        // 6. 鍒涘缓鍞悗宸ュ崟
        AfterSalesOrder afterSales = new AfterSalesOrder();
        afterSales.setAfterSalesNo("AS" + System.currentTimeMillis());
        afterSales.setOrderId(order.getId());
        afterSales.setOrderNo(order.getOrderNo());
        afterSales.setUserId(userId);
        afterSales.setFarmerId(order.getFarmerId());
        afterSales.setAfterSalesType(1); // 1=閫€娆?
        afterSales.setReason(reason);
        afterSales.setRefundAmount(refundAmount);
        afterSales.setStatus(1); // 宸插鏍?
        afterSales.setAdminId(adminId);
        afterSales.setAdminRemark("瀹㈡湇閫€娆惧鐞?);
        afterSales.setApplyTime(LocalDateTime.now());
        afterSales.setAuditTime(LocalDateTime.now());
        afterSales.setCreateTime(LocalDateTime.now());
        afterSalesService.save(afterSales);

        // 7. 璁板綍璁㈠崟鏃ュ織
        OrderLog orderLog = new OrderLog();
        orderLog.setOrderId(order.getId()); orderLog.setOrderNo(order.getOrderNo());
        orderLog.setOrderStatus(4); orderLog.setOperatorType(3); orderLog.setOperatorId(adminId);
        orderLog.setRemark("瀹㈡湇閫€娆撅細" + reason);
        orderLog.setCreateTime(LocalDateTime.now());
        orderLogService.save(orderLog);

        // 8. 鍙戦€佹秷鎭€氱煡鐢ㄦ埛
        String refundMsg = String.format("銆愰€€娆炬垚鍔熴€慭n璁㈠崟鍙凤細%s\n閫€娆鹃噾棰濓細楼%s\n閫€娆炬柟寮忥細%s\n%s",
                order.getOrderNo(), refundAmount,
                order.getPayType() == 3 ? "閫€鍥炰細鍛樺崱浣欓" : "鍘熻矾閫€鍥?,
                order.getPayType() == 3 ? "閫€娆惧凡閫€鍥炴偍鐨勪細鍛樺崱浣欓锛岃鏌ユ敹銆? : "閫€娆惧皢鍦?-3涓伐浣滄棩鍐呭師璺€€鍥炪€?);
        ChatMessage msg = new ChatMessage();
        msg.setFromUserId(adminId); msg.setToUserId(userId);
        msg.setContent(refundMsg); msg.setMsgType("refund");
        msg.setIsRead(0); msg.setCreateTime(LocalDateTime.now());
        chatService.save(msg);

        Map<String, Object> push = new HashMap<>();
        push.put("type", "new_message");
        push.put("message", buildMessageVO(msg, adminId));
        ChatWebSocketHandler.sendToUser(userId, push);

        return Result.success("閫€娆炬垚鍔?, Map.of("refundAmount", refundAmount));
    }

    // ============ 鐢ㄦ埛绔細鑾峰彇鑱婂ぉ璁板綍 ============
    @GetMapping("/api/chat/messages")
    public Result<?> userMessages() {
        Long userId = UserContext.getUserId();
        Long adminId = findChatAdmin(userId);
        final Long targetAdminId = adminId != null ? adminId : getAdminId();

        // 鏍囪宸茶
        chatService.update(new LambdaUpdateWrapper<ChatMessage>()
                .set(ChatMessage::getIsRead, 1)
                .eq(ChatMessage::getFromUserId, targetAdminId)
                .eq(ChatMessage::getToUserId, userId)
                .eq(ChatMessage::getIsRead, 0));

        // 鎴戝彂缁欑鐞嗗憳鐨?
        List<ChatMessage> list1 = chatService.list(new LambdaQueryWrapper<ChatMessage>()
                .eq(ChatMessage::getFromUserId, userId)
                .eq(ChatMessage::getToUserId, targetAdminId)
                .orderByAsc(ChatMessage::getCreateTime));

        // AI鍙戠粰鎴戠殑
        List<ChatMessage> list2 = chatService.list(new LambdaQueryWrapper<ChatMessage>()
                .eq(ChatMessage::getFromUserId, 0L)
                .eq(ChatMessage::getToUserId, userId)
                .orderByAsc(ChatMessage::getCreateTime));

        // 绠＄悊鍛樺彂缁欐垜鐨?
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

    // ============ 绠＄悊鍛樼锛氳幏鍙栦細璇濆垪琛?============
    @GetMapping("/admin/chat/conversations")
    public Result<?> adminConversations() {
        Long adminId = UserContext.getUserId();
        List<ChatMessage> allMessages = chatService.list(new LambdaQueryWrapper<ChatMessage>()
                .and(w -> w.eq(ChatMessage::getFromUserId, adminId).or().eq(ChatMessage::getToUserId, adminId))
                .orderByDesc(ChatMessage::getCreateTime));

        Map<Long, ChatMessage> convMap = new LinkedHashMap<>();
        for (ChatMessage m : allMessages) {
            Long otherUserId = m.getFromUserId().equals(adminId) ? m.getToUserId() : m.getFromUserId();
            if (otherUserId == 0L) continue; // AI娑堟伅
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
            conv.put("nickname", user != null ? (user.getNickname() != null ? user.getNickname() : user.getUsername()) : "鐢ㄦ埛" + userIdx);
            conv.put("lastMessage", lastMsg.getContent());
            conv.put("lastTime", lastMsg.getCreateTime());
            conv.put("unreadCount", unreadCount);
            conv.put("online", ChatWebSocketHandler.isOnline(userIdx));
            result.add(conv);
        }
        return Result.success(result);
    }

    // ============ 绠＄悊鍛樼锛氳幏鍙栦笌鎸囧畾鐢ㄦ埛鐨勮亰澶╄褰?============
    @GetMapping("/admin/chat/messages/{userId}")
    public Result<?> adminMessages(@PathVariable Long userId) {
        Long adminId = UserContext.getUserId();

        // 鏍囪宸茶
        chatService.update(new LambdaUpdateWrapper<ChatMessage>()
                .set(ChatMessage::getIsRead, 1)
                .eq(ChatMessage::getFromUserId, userId)
                .eq(ChatMessage::getToUserId, adminId)
                .eq(ChatMessage::getIsRead, 0));

        // 鏌ヨ锛氱敤鎴峰彂缁欐垜鐨?+ 鐢ㄦ埛鍙戠粰AI鐨?+ AI鍙戠粰鐢ㄦ埛鐨?+ 鎴戝彂缁欑敤鎴风殑
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

    // ============ 鐢ㄦ埛绔細鑾峰彇鎴戠殑璁㈠崟鍒楄〃锛堜緵瀹㈡湇浣跨敤锛?============
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

    // ============ 绠＄悊鍛樼锛氭祴璇旳I杩炴帴 ============
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
                return Result.error(responseCode, "API杩斿洖閿欒: " + errBody.substring(0, Math.min(200, errBody.length())));
            }

            String responseBody = new String(conn.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            JsonNode root = mapper.readTree(responseBody);
            String reply = root.path("choices").get(0).path("message").path("content").asText();
            return Result.success(Map.of("reply", reply.substring(0, Math.min(100, reply.length()))));
        } catch (Exception e) {
            return Result.error(500, "杩炴帴澶辫触: " + e.getMessage());
        }
    }

    // ============ 鏈娑堟伅鏁?============
    @GetMapping("/api/chat/unread")
    public Result<?> userUnread() {
        Long userId = UserContext.getUserId();
        Long adminId = findChatAdmin(userId);
        if (adminId == null) adminId = getAdminId();
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

    // ============ 鍐呴儴鏂规硶 ============

    private String callAI(String userMessage, Long userId) {
        try {
            String apiUrl = getAiConfig("ai_api_url", "https://api.xiaomimimo.com/v1/chat/completions");
            String apiKey = getAiConfig("ai_api_key", "");
            String model = getAiConfig("ai_model", "mimo-v2.5-pro");

            if (apiKey == null || apiKey.isEmpty()) {
                return "AI鏈嶅姟鏈厤缃紝璇疯仈绯荤鐞嗗憳璁剧疆API Key銆俒TRANSFER_TO_HUMAN]";
            }

            // 鑾峰彇鐢ㄦ埛鏈€杩戠殑鑱婂ぉ璁板綍浣滀负涓婁笅鏂?
            List<ChatMessage> recentMsgs = chatService.list(new LambdaQueryWrapper<ChatMessage>()
                    .and(w -> w
                            .and(x -> x.eq(ChatMessage::getFromUserId, userId).eq(ChatMessage::getToUserId, 0L))
                            .or().and(x -> x.eq(ChatMessage::getFromUserId, 0L).eq(ChatMessage::getToUserId, userId))
                    )
                    .orderByDesc(ChatMessage::getCreateTime)
                    .last("LIMIT 10"));

            // 鏋勫缓娑堟伅鍒楄〃
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
                return "AI鏈嶅姟鏆傛椂涓嶅彲鐢紝姝ｅ湪涓烘偍杞帴浜哄伐瀹㈡湇...[TRANSFER_TO_HUMAN]";
            }

            String responseBody = new String(conn.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            JsonNode root = mapper.readTree(responseBody);
            String aiContent = root.path("choices").get(0).path("message").path("content").asText();
            return aiContent;

        } catch (Exception e) {
            return "AI鏈嶅姟鏆傛椂涓嶅彲鐢紝姝ｅ湪涓烘偍杞帴浜哄伐瀹㈡湇...[TRANSFER_TO_HUMAN]";
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
        String nickname = user != null ? (user.getNickname() != null ? user.getNickname() : user.getUsername()) : "鐢ㄦ埛" + userId;

        Map<String, Object> push = new HashMap<>();
        push.put("type", "user_transfer");
        push.put("userId", userId);
        push.put("nickname", nickname);
        push.put("message", nickname + " 璇锋眰浜哄伐瀹㈡湇");
        ChatWebSocketHandler.sendToUser(getAdminId(), push);
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
        if (status == null) return "鏈煡";
        switch (status) {
            case 0: return "寰呬粯娆?;
            case 1: return "寰呭彂璐?;
            case 2: return "宸插彂璐?;
            case 3: return "宸插畬鎴?;
            case 4: return "宸插彇娑?;
            default: return "鏈煡";
        }
    }
}
