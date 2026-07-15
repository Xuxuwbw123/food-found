package com.freshtrace.unified.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freshtrace.unified.common.UserContext;
import com.freshtrace.unified.dto.*;
import com.freshtrace.unified.entity.*;
import com.freshtrace.unified.mapper.*;
import com.freshtrace.unified.mapper.UserCouponMapper;
import com.freshtrace.unified.mapper.CouponMapper;
import com.freshtrace.unified.service.OrderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired private OrderInfoMapper orderInfoMapper;
    @Autowired private OrderItemMapper orderItemMapper;
    @Autowired private PaymentInfoMapper paymentInfoMapper;
    @Autowired private OrderLogMapper orderLogMapper;
    @Autowired private ShoppingCartMapper shoppingCartMapper;
    @Autowired private UserAddressMapper userAddressMapper;
    @Autowired private ProductMapper productMapper;
    @Autowired private FarmerMapper farmerMapper;
    @Autowired private SysUserMapper userMapper;
    @Autowired private UserCouponMapper userCouponMapper;
    @Autowired private CouponMapper couponMapper;
    @Autowired private MemberLevelConfigMapper memberLevelConfigMapper;
    @Autowired private MemberPointMapper memberPointMapper;
    @Autowired private PointLogMapper pointLogMapper;

    @Override
    @Transactional
    public Map<String, Object> create(CreateOrderDTO dto) {
        Long userId = UserContext.getUserId();
        // Bug #34 fix: 璁㈠崟鍙峰姞4浣嶉殢鏈烘暟闃叉挒杞?
        String orderNo = "FD" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"))
                + String.format("%04d", java.util.concurrent.ThreadLocalRandom.current().nextInt(10000));
        BigDecimal totalAmount = BigDecimal.ZERO;
        int totalQty = 0;

        // Resolve address
        String receiverName = "", receiverPhone = "", province = "", city = "", district = "", detailAddress = "";
        if (dto.getAddress() != null) {
            Map<String, Object> a = dto.getAddress();
            receiverName = String.valueOf(a.getOrDefault("receiver_name", a.getOrDefault("receiverName", "")));
            receiverPhone = String.valueOf(a.getOrDefault("receiver_phone", a.getOrDefault("receiverPhone", "")));
            province = String.valueOf(a.getOrDefault("province", ""));
            city = String.valueOf(a.getOrDefault("city", ""));
            district = String.valueOf(a.getOrDefault("district", ""));
            detailAddress = String.valueOf(a.getOrDefault("detail_address", a.getOrDefault("detailAddress", "")));
        } else if (dto.getAddressId() != null) {
            UserAddress addr = userAddressMapper.selectById(dto.getAddressId());
            if (addr == null || !addr.getUserId().equals(userId)) throw new RuntimeException("address not found");
            receiverName = addr.getReceiverName(); receiverPhone = addr.getReceiverPhone();
            province = addr.getProvince(); city = addr.getCity();
            district = addr.getDistrict(); detailAddress = addr.getDetailAddress();
        }

        // Build order items from either cart or direct items
        List<CreateOrderDTO.OrderItemDTO> orderItems;
        if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            // Direct order (buy now)
            orderItems = dto.getItems();
        } else if (dto.getCartItemIds() != null && !dto.getCartItemIds().isEmpty()) {
            // Cart order
            List<ShoppingCart> cartItems = shoppingCartMapper.selectList(
                    new LambdaQueryWrapper<ShoppingCart>().in(ShoppingCart::getId, dto.getCartItemIds()));
            cartItems = cartItems.stream().filter(c -> c.getSelected() == 1).collect(Collectors.toList());
            if (cartItems.isEmpty()) throw new RuntimeException("no selected items");
            orderItems = cartItems.stream().map(c -> {
                CreateOrderDTO.OrderItemDTO item = new CreateOrderDTO.OrderItemDTO();
                item.setProductId(c.getProductId()); item.setProductName(c.getProductName());
                item.setProductImage(c.getProductImage()); item.setPrice(c.getPrice());
                item.setQuantity(c.getQuantity());
                return item;
            }).collect(Collectors.toList());
        } else {
            throw new RuntimeException("no items provided");
        }

        // Validate stock and calculate total
        for (CreateOrderDTO.OrderItemDTO item : orderItems) {
            Product p = productMapper.selectById(item.getProductId());
            if (p == null) throw new RuntimeException("product not found: " + item.getProductId());
            if (p.getStock() < item.getQuantity()) throw new RuntimeException("out of stock: " + p.getProductName());
            // Bug #7 fix: validate product status before order
            if (p.getDeleted() != null && p.getDeleted() == 1)
                throw new RuntimeException("product has been deleted: " + p.getProductName());
            if (p.getStatus() == null || p.getStatus() != 1)
                throw new RuntimeException("product is not available: " + p.getProductName());
            if (p.getAuditStatus() == null || p.getAuditStatus() != 1)
                throw new RuntimeException("product not approved: " + p.getProductName());
            if (p.getIsPresale() != null && p.getIsPresale() == 1)
                throw new RuntimeException("presale product cannot be ordered directly: " + p.getProductName());
            BigDecimal itemPrice = item.getPrice() != null ? item.getPrice() : p.getPrice();
            totalAmount = totalAmount.add(itemPrice.multiply(new BigDecimal(item.getQuantity())));
            totalQty += item.getQuantity();
        }

        // 1. 鍏堢畻浼氬憳鎶樻墸
        SysUser currentUser = userMapper.selectById(userId);
        BigDecimal memberDiscountRate = BigDecimal.ONE;
        if (currentUser != null && currentUser.getMemberLevel() != null && currentUser.getMemberLevel() > 0
                && currentUser.getStatus() != null && currentUser.getStatus() == 1) {
            MemberLevelConfig levelConfig = memberLevelConfigMapper.selectOne(
                    new LambdaQueryWrapper<MemberLevelConfig>().eq(MemberLevelConfig::getLevel, currentUser.getMemberLevel()));
            if (levelConfig != null && levelConfig.getDiscountRate() != null) {
                memberDiscountRate = levelConfig.getDiscountRate();
            }
        }
        BigDecimal memberDiscountAmount = totalAmount.multiply(BigDecimal.ONE.subtract(memberDiscountRate)).setScale(2, java.math.RoundingMode.HALF_UP);
        BigDecimal afterMemberDiscount = totalAmount.subtract(memberDiscountAmount);

        // 2. 鍐嶅噺浼樻儬鍒?
        BigDecimal couponDiscountAmount = BigDecimal.ZERO;
        if (dto.getCouponId() != null) {
            UserCoupon uc = userCouponMapper.selectOne(new LambdaQueryWrapper<UserCoupon>()
                    .eq(UserCoupon::getUserId, userId)
                    .eq(UserCoupon::getCouponId, dto.getCouponId())
                    .eq(UserCoupon::getStatus, "unused"));
            if (uc != null) {
                Coupon coupon = couponMapper.selectById(dto.getCouponId());
                if (coupon != null && Integer.valueOf(1).equals(coupon.getStatus())) {
                    String couponType = coupon.getType();
                    if ("full_reduce".equals(couponType)) {
                        // 婊″噺鍒?
                        if (afterMemberDiscount.compareTo(coupon.getMinAmount()) >= 0) {
                            couponDiscountAmount = coupon.getFaceValue();
                        }
                    } else if ("discount".equals(couponType)) {
                        // 鎶樻墸鍒?
                        couponDiscountAmount = afterMemberDiscount.multiply(BigDecimal.ONE.subtract(coupon.getFaceValue().divide(new BigDecimal("10"), 2, java.math.RoundingMode.HALF_UP))).setScale(2, java.math.RoundingMode.HALF_UP);
                    } else {
                        // 鍏朵粬绫诲瀷锛坣ew_user, general绛夛級鐩存帴鍑忛潰鍊?
                        couponDiscountAmount = coupon.getFaceValue();
                    }
                    uc.setStatus("used");
                    uc.setUseTime(LocalDateTime.now());
                    userCouponMapper.updateById(uc);
                }
            }
        }

        // 鎬讳紭鎯?= 浼氬憳鎶樻墸 + 浼樻儬鍒?
        BigDecimal totalDiscount = memberDiscountAmount.add(couponDiscountAmount);
        BigDecimal payAmount = totalAmount.subtract(totalDiscount);
        if (payAmount.compareTo(BigDecimal.ZERO) < 0) payAmount = BigDecimal.ZERO;

        // Create order
        OrderInfo order = new OrderInfo();
        order.setOrderNo(orderNo); order.setUserId(userId);
        order.setTotalAmount(totalAmount); order.setPayAmount(payAmount);
        order.setPlanAmount(payAmount); order.setActualAmount(payAmount);
        order.setFreightAmount(BigDecimal.ZERO); order.setDiscountAmount(totalDiscount);
        order.setTotalQuantity(totalQty); order.setOrderStatus(0);
        order.setReceiverName(receiverName); order.setReceiverPhone(receiverPhone);
        order.setReceiverProvince(province); order.setReceiverCity(city);
        order.setReceiverDistrict(district); order.setReceiverAddress(detailAddress);
        order.setOrderRemark(dto.getRemark()); order.setIsComment(0);
        order.setCouponId(dto.getCouponId()); order.setCreateTime(LocalDateTime.now());
        orderInfoMapper.insert(order);

        // Create order items
        for (CreateOrderDTO.OrderItemDTO item : orderItems) {
            Product p = productMapper.selectById(item.getProductId());
            BigDecimal itemPrice = item.getPrice() != null ? item.getPrice() : p.getPrice();
            OrderItem oi = new OrderItem();
            oi.setOrderId(order.getId()); oi.setOrderNo(orderNo);
            oi.setProductId(p.getId()); oi.setProductName(item.getProductName() != null ? item.getProductName() : p.getProductName());
            oi.setProductImage(item.getProductImage() != null ? item.getProductImage() : p.getMainImage());
            oi.setProductNo(p.getProductNo()); oi.setCategoryId(p.getCategoryId());
            oi.setFarmerId(p.getFarmerId()); oi.setPrice(itemPrice);
            oi.setOriginalPrice(p.getOriginalPrice()); oi.setQuantity(item.getQuantity());
            oi.setTotalAmount(itemPrice.multiply(new BigDecimal(item.getQuantity())));
            oi.setUnit(item.getUnit() != null ? item.getUnit() : p.getUnit());
            oi.setTraceId(item.getTraceId() != null ? item.getTraceId() : p.getTraceId());
            oi.setIsComment(0); oi.setCreateTime(LocalDateTime.now());
            orderItemMapper.insert(oi);

            // Bug #33/#35 fix: 鍘熷瓙UPDATE鎵ｅ簱瀛?
            int affected = productMapper.deductStock(item.getProductId(), item.getQuantity());
            if (affected == 0) {
                throw new RuntimeException("out of stock: " + p.getProductName());
            }
        }

        // Clear cart items if cart order
        if (dto.getCartItemIds() != null && !dto.getCartItemIds().isEmpty()) {
            shoppingCartMapper.deleteBatchIds(dto.getCartItemIds());
        }

        // Create payment record
        PaymentInfo pay = new PaymentInfo();
        pay.setOrderId(order.getId()); pay.setOrderNo(orderNo);
        pay.setUserId(userId); pay.setPaymentNo("PAY" + orderNo);
        pay.setPayType(1); pay.setPayAmount(totalAmount); pay.setPayStatus(0);
        pay.setCreateTime(LocalDateTime.now());
        paymentInfoMapper.insert(pay);

        // Create order log
        OrderLog log = new OrderLog();
        log.setOrderId(order.getId()); log.setOrderNo(orderNo);
        log.setOrderStatus(0); log.setOperatorType(4);
        log.setRemark("order created"); log.setCreateTime(LocalDateTime.now());
        orderLogMapper.insert(log);

        Map<String, Object> result = new java.util.HashMap<>();
        result.put("orderId", String.valueOf(order.getId()));  // 杩斿洖瀛楃涓查伩鍏岼S绮惧害涓㈠け
        result.put("orderNo", orderNo);
        result.put("total", payAmount);  // 杩斿洖鎶樺悗浠凤紙浼氬憳鎶樻墸+浼樻儬鍒革級
        result.put("originalTotal", totalAmount);  // 鍘熶环
        result.put("discount", totalDiscount);  // 鎬讳紭鎯?
        return result;
    }

    @Override
    public Page<OrderVO> list(Integer pageNum, Integer pageSize, Integer status) {
        LambdaQueryWrapper<OrderInfo> qw = new LambdaQueryWrapper<OrderInfo>()
                .eq(OrderInfo::getUserId, UserContext.getUserId())
                .ne(OrderInfo::getOrderStatus, 4); // 鎺掗櫎宸插彇娑堣鍗?
        if (status != null) qw.eq(OrderInfo::getOrderStatus, status);
        qw.orderByDesc(OrderInfo::getCreateTime);
        Page<OrderInfo> page = orderInfoMapper.selectPage(new Page<>(pageNum, pageSize), qw);
        Page<OrderVO> result = new Page<>(pageNum, pageSize, page.getTotal());
        result.setRecords(page.getRecords().stream().map(o -> {
            OrderVO vo = new OrderVO();
            BeanUtils.copyProperties(o, vo);
            vo.setOrderStatusName(statusName(o.getOrderStatus()));
            Farmer f = farmerMapper.selectById(o.getFarmerId());
            if (f != null) vo.setFarmerName(f.getFarmerName());
            List<OrderItem> items = orderItemMapper.selectList(
                    new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, o.getId()));
            vo.setItems(items.stream().map(i -> {
                OrderVO.OrderItemVO iv = new OrderVO.OrderItemVO();
                BeanUtils.copyProperties(i, iv);
                return iv;
            }).collect(Collectors.toList()));
            return vo;
        }).collect(Collectors.toList()));
        return result;
    }

    @Override
    public OrderDetailVO getDetail(Long id) {
        OrderInfo o = orderInfoMapper.selectById(id);
        if (o == null || !o.getUserId().equals(UserContext.getUserId())) throw new RuntimeException("order not found");
        OrderDetailVO vo = new OrderDetailVO();
        BeanUtils.copyProperties(o, vo);
        vo.setOrderStatusName(statusName(o.getOrderStatus()));

        Farmer f = farmerMapper.selectById(o.getFarmerId());
        if (f != null) vo.setFarmerName(f.getFarmerName());

        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, o.getId()));
        vo.setItems(items.stream().map(i -> {
            OrderDetailVO.OrderItemVO iv = new OrderDetailVO.OrderItemVO();
            BeanUtils.copyProperties(i, iv);
            return iv;
        }).collect(Collectors.toList()));

        PaymentInfo pay = paymentInfoMapper.selectOne(
                new LambdaQueryWrapper<PaymentInfo>().eq(PaymentInfo::getOrderId, o.getId()));
        if (pay != null) {
            OrderDetailVO.PaymentVO pv = new OrderDetailVO.PaymentVO();
            BeanUtils.copyProperties(pay, pv);
            vo.setPayment(pv);
        }

        List<OrderLog> logs = orderLogMapper.selectList(
                new LambdaQueryWrapper<OrderLog>().eq(OrderLog::getOrderId, o.getId()).orderByAsc(OrderLog::getCreateTime));
        vo.setLogs(logs.stream().map(l -> {
            OrderDetailVO.OrderLogVO lv = new OrderDetailVO.OrderLogVO();
            BeanUtils.copyProperties(l, lv);
            return lv;
        }).collect(Collectors.toList()));

        return vo;
    }

    @Override
    @Transactional
    public void pay(Long id) {
        OrderInfo o = orderInfoMapper.selectById(id);
        if (o == null || !o.getUserId().equals(UserContext.getUserId())) throw new RuntimeException("order not found");
        // Bug #22 fix: 鏀粯骞傜瓑
        if (o.getOrderStatus() == 1) { return; }
        if (o.getOrderStatus() != 0) throw new RuntimeException("璁㈠崟鐘舵€佷笉鍏佽鏀粯");
        int updated = orderInfoMapper.update(null,
                new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<OrderInfo>()
                        .set(OrderInfo::getOrderStatus, 1).set(OrderInfo::getPayType, 1)
                        .set(OrderInfo::getPayTime, LocalDateTime.now())
                        .eq(OrderInfo::getId, id).eq(OrderInfo::getOrderStatus, 0));
        if (updated == 0) { return; }

        PaymentInfo pay = paymentInfoMapper.selectOne(
                new LambdaQueryWrapper<PaymentInfo>().eq(PaymentInfo::getOrderId, o.getId()));
        if (pay != null) {
            pay.setPayStatus(1);
            pay.setPayTime(LocalDateTime.now());
            paymentInfoMapper.updateById(pay);
        }

        OrderLog log = new OrderLog();
        log.setOrderId(o.getId()); log.setOrderNo(o.getOrderNo()); log.setOrderStatus(1);
        log.setOperatorType(1); log.setOperatorId(UserContext.getUserId());
        log.setRemark("user paid");
        log.setCreateTime(LocalDateTime.now());
        orderLogMapper.insert(log);
    }

    @Override
    @Transactional
    public void cancel(Long id) {
        OrderInfo o = orderInfoMapper.selectById(id);
        if (o == null || !o.getUserId().equals(UserContext.getUserId())) throw new RuntimeException("order not found");
        if (o.getOrderStatus() != 0 && o.getOrderStatus() != 1) throw new RuntimeException("鍙兘鍙栨秷鏈彂璐х殑璁㈠崟");
        // 鍥炴粴搴撳瓨
        List<OrderItem> items = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, id));
        for (OrderItem item : items) {
            Product p = productMapper.selectById(item.getProductId());
            if (p != null) {
                p.setStock(p.getStock() + item.getQuantity());
                p.setSales(Math.max(0, (p.getSales() != null ? p.getSales() : 0) - item.getQuantity()));
                productMapper.updateById(p);
            }
        }
        o.setOrderStatus(4);
        o.setCancelTime(LocalDateTime.now());
        orderInfoMapper.updateById(o);

        // Bug #8 fix: 返还优惠券——仅当订单使用了优惠券且状态为"used"时恢复
        if (o.getCouponId() != null) {
            UserCoupon uc = userCouponMapper.selectOne(new LambdaQueryWrapper<UserCoupon>()
                    .eq(UserCoupon::getUserId, o.getUserId())
                    .eq(UserCoupon::getCouponId, o.getCouponId())
                    .eq(UserCoupon::getStatus, "used"));
            if (uc != null) {
                uc.setStatus("unused");
                uc.setUseTime(null);
                userCouponMapper.updateById(uc);
                // 回退优惠券领取数
                couponMapper.update(null, new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<Coupon>()
                        .setSql("taken_count = GREATEST(taken_count - 1, 0)").eq(Coupon::getId, o.getCouponId()));
            }
        }

        // 宸叉敮浠樼殑璁㈠崟鏍囪閫€娆?
        PaymentInfo pay = paymentInfoMapper.selectOne(
                new LambdaQueryWrapper<PaymentInfo>().eq(PaymentInfo::getOrderId, o.getId()));
        if (pay != null && pay.getPayStatus() == 1) {
            pay.setPayStatus(3);
            paymentInfoMapper.updateById(pay);

            // 浼氬憳鍗℃敮浠樼殑璁㈠崟锛岄€€娆惧埌浣欓
            if (o.getPayType() != null && o.getPayType() == 3) {
                SysUser user = userMapper.selectById(o.getUserId());
                if (user != null) {
                    BigDecimal refundAmount = o.getPayAmount() != null ? o.getPayAmount() : o.getTotalAmount();
                    user.setBalance(user.getBalance().add(refundAmount));
                    userMapper.updateById(user);
                }
            }

            // 鎵ｉ櫎璇ヨ鍗曡幏寰楃殑绉垎
            BigDecimal paidAmount = o.getPayAmount() != null ? o.getPayAmount() : o.getTotalAmount();
            SysUser paidUser = userMapper.selectById(o.getUserId());
            int memberLevel = paidUser != null && paidUser.getMemberLevel() != null ? paidUser.getMemberLevel() : 0;
            int pointsRate = 1;
            if (memberLevel > 0) {
                MemberLevelConfig config = memberLevelConfigMapper.selectOne(
                        new LambdaQueryWrapper<MemberLevelConfig>().eq(MemberLevelConfig::getLevel, memberLevel));
                if (config != null && config.getPointsRate() != null) pointsRate = config.getPointsRate();
            }
            int deductPoints = paidAmount.intValue() * pointsRate;
            if (deductPoints > 0) {
                MemberPoint mp = memberPointMapper.selectById(o.getUserId());
                if (mp != null && mp.getAvailablePoint() != null) {
                    int actualDeduct = Math.min(deductPoints, mp.getAvailablePoint());
                    mp.setAvailablePoint(mp.getAvailablePoint() - actualDeduct);
                    mp.setTotalPoint(Math.max(0, mp.getTotalPoint() - actualDeduct));
                    memberPointMapper.updateById(mp);

                    PointLog log = new PointLog();
                    log.setUserId(o.getUserId()); log.setType("refund");
                    log.setPoint(-actualDeduct); log.setBalance(mp.getAvailablePoint());
                    log.setRemark("\u53d6\u6d88\u8ba2\u5355\u6263\u9664\u79ef\u5206");
                    log.setCreateTime(LocalDateTime.now());
                    pointLogMapper.insert(log);
                }
            }
        }

        OrderLog log = new OrderLog();
        log.setOrderId(o.getId()); log.setOrderNo(o.getOrderNo()); log.setOrderStatus(4);
        log.setOperatorType(1); log.setOperatorId(UserContext.getUserId());
        log.setRemark("user cancelled");
        log.setCreateTime(LocalDateTime.now());
        orderLogMapper.insert(log);
    }

    @Override
    @Transactional
    public void confirmReceive(Long id) {
        OrderInfo o = orderInfoMapper.selectById(id);
        if (o == null || !o.getUserId().equals(UserContext.getUserId())) throw new RuntimeException("order not found");
        if (o.getOrderStatus() != 2) throw new RuntimeException("order not shipped");
        o.setOrderStatus(3);
        o.setReceiveTime(LocalDateTime.now());
        o.setFinishTime(LocalDateTime.now());
        orderInfoMapper.updateById(o);

        OrderLog log = new OrderLog();
        log.setOrderId(o.getId()); log.setOrderNo(o.getOrderNo()); log.setOrderStatus(3);
        log.setOperatorType(1); log.setOperatorId(UserContext.getUserId());
        log.setRemark("user confirmed receipt");
        log.setCreateTime(LocalDateTime.now());
        orderLogMapper.insert(log);
    }

    private String statusName(Integer s) {
        switch (s != null ? s : -1) {
            case 0: return "pending payment";
            case 1: return "pending shipment";
            case 2: return "shipped";
            case 3: return "completed";
            case 4: return "cancelled";
            case 5: return "after-sales";
            default: return "unknown";
        }
    }
}
