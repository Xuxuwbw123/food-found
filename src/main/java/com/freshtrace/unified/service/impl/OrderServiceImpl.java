package com.freshtrace.unified.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freshtrace.unified.common.UserContext;
import com.freshtrace.unified.dto.*;
import com.freshtrace.unified.entity.*;
import com.freshtrace.unified.mapper.*;
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

    @Override
    @Transactional
    public Map<String, Object> create(CreateOrderDTO dto) {
        Long userId = UserContext.getUserId();
        String orderNo = "FD" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
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
            BigDecimal itemPrice = item.getPrice() != null ? item.getPrice() : p.getPrice();
            totalAmount = totalAmount.add(itemPrice.multiply(new BigDecimal(item.getQuantity())));
            totalQty += item.getQuantity();
        }

        // Create order
        OrderInfo order = new OrderInfo();
        order.setOrderNo(orderNo); order.setUserId(userId);
        order.setTotalAmount(totalAmount); order.setPayAmount(totalAmount);
        order.setPlanAmount(totalAmount); order.setActualAmount(totalAmount);
        order.setFreightAmount(BigDecimal.ZERO); order.setDiscountAmount(BigDecimal.ZERO);
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

            p.setStock(p.getStock() - item.getQuantity());
            p.setSales((p.getSales() != null ? p.getSales() : 0) + item.getQuantity());
            productMapper.updateById(p);
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
        result.put("orderId", String.valueOf(order.getId()));  // 返回字符串避免JS精度丢失
        result.put("orderNo", orderNo);
        result.put("total", totalAmount);
        return result;
    }

    @Override
    public Page<OrderVO> list(Integer pageNum, Integer pageSize, Integer status) {
        LambdaQueryWrapper<OrderInfo> qw = new LambdaQueryWrapper<OrderInfo>()
                .eq(OrderInfo::getUserId, UserContext.getUserId())
                .ne(OrderInfo::getOrderStatus, 4); // 排除已取消订单
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
        if (o.getOrderStatus() != 0) throw new RuntimeException("can only pay unpaid order");
        o.setOrderStatus(1);
        o.setPayType(1);
        o.setPayTime(LocalDateTime.now());
        orderInfoMapper.updateById(o);

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
        if (o.getOrderStatus() != 0 && o.getOrderStatus() != 1) throw new RuntimeException("只能取消未发货的订单");
        // 回滚库存
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

        // 已支付的订单标记退款
        PaymentInfo pay = paymentInfoMapper.selectOne(
                new LambdaQueryWrapper<PaymentInfo>().eq(PaymentInfo::getOrderId, o.getId()));
        if (pay != null && pay.getPayStatus() == 1) {
            pay.setPayStatus(3);
            paymentInfoMapper.updateById(pay);
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
