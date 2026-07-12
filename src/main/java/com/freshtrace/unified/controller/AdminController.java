package com.freshtrace.unified.controller;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freshtrace.unified.common.PageQuery;
import com.freshtrace.unified.common.Result;
import com.freshtrace.unified.entity.*;
import com.freshtrace.unified.mapper.TraceabilityMapper;
import com.freshtrace.unified.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping({"/admin", "/api/admin"})
public class AdminController {

    @Autowired private UserService userService;
    @Autowired private ProductService productService;
    @Autowired private ProductImageService productImageService;
    @Autowired private CategoryService categoryService;
    @Autowired private BannerService bannerService;
    @Autowired private OrderInfoService orderService;
    @Autowired private OrderItemService orderItemService;
    @Autowired private OrderLogService orderLogService;
    @Autowired private ProductCommentService commentService;
    @Autowired private AfterSalesOrderService afterSalesService;
    @Autowired private PaymentInfoService paymentService;
    @Autowired private CustomerServiceLogService csLogService;
    @Autowired private FarmerService farmerService;
    @Autowired private FarmerAuditService farmerAuditService;
    @Autowired private CouponService couponService;
    @Autowired private com.freshtrace.unified.service.TraceabilityService traceabilityService;
    @Autowired private com.freshtrace.unified.mapper.ProductFavoriteMapper productFavoriteMapper;
    @Autowired private UserCouponService userCouponService;
    @Autowired private SeckillService seckillService;
    @Autowired private MarketingActivityService marketingService;
    @Autowired private SysConfigService configService;
    @Autowired private SysNoticeService noticeService;
    @Autowired private SysOperationLogService operationLogService;

    // ============ 首页统计 ============
    @GetMapping("/statistics/home")
    public Result<?> homeStats() {
        Map<String, Object> data = new HashMap<>();
        data.put("userCount", userService.count());
        data.put("productCount", productService.count(new LambdaQueryWrapper<Product>().eq(Product::getDeleted, 0)));
        data.put("orderCount", orderService.count(new LambdaQueryWrapper<OrderInfo>().eq(OrderInfo::getDeleted, 0)));
        data.put("todayOrders", 0);
        data.put("totalSales", 0);
        data.put("pendingCommentCount", commentService.count(new LambdaQueryWrapper<ProductComment>().eq(ProductComment::getStatus, 0)));
        data.put("pendingFarmerCount", farmerAuditService.count(new LambdaQueryWrapper<FarmerAudit>().eq(FarmerAudit::getAuditStatus, 0)));
        data.put("onlineProductCount", productService.count(new LambdaQueryWrapper<Product>().eq(Product::getStatus, 1).eq(Product::getDeleted, 0)));
        return Result.success(data);
    }

    @GetMapping("/statistics/dashboard")
    public Result<?> dashboard() {
        Map<String, Object> data = new HashMap<>();
        data.put("userCount", userService.count());
        data.put("farmerCount", farmerService.count(new LambdaQueryWrapper<Farmer>().eq(Farmer::getAuditStatus, 1)));
        data.put("productCount", productService.count(new LambdaQueryWrapper<Product>().eq(Product::getDeleted, 0)));
        data.put("todayOrders", 0); data.put("todaySales", 0); data.put("totalSales", 0);
        data.put("trend7", Collections.emptyList()); data.put("topProducts", Collections.emptyList());
        return Result.success(data);
    }

    // ============ 用户管理 ============
    @GetMapping("/user/list")
    public Result<?> userList(PageQuery pageQuery,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer userType,
            @RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<User> qw = new LambdaQueryWrapper<User>().eq(User::getDeleted, 0);
        if (keyword != null && !keyword.isEmpty())
            qw.and(w -> w.like(User::getUsername, keyword).or().like(User::getPhone, keyword).or().like(User::getNickname, keyword));
        if (userType != null) qw.eq(User::getUserType, userType);
        if (status != null) qw.eq(User::getStatus, status);
        qw.orderByDesc(User::getCreateTime);
        Page<User> page = userService.page(pageQuery.toPage(), qw);
        page.getRecords().forEach(u -> u.setPassword(null));
        return Result.success(page);
    }

    @GetMapping("/user/{id}")
    public Result<?> userDetail(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user != null) user.setPassword(null);
        return Result.success(user);
    }

    @PostMapping("/user/add")
    public Result<?> userAdd(@RequestBody User user) {
        if (user.getUsername() == null || user.getPassword() == null) return Result.error(400, "用户名和密码不能为空");
        Long count = userService.count(new LambdaQueryWrapper<User>().eq(User::getUsername, user.getUsername()));
        if (count > 0) return Result.error(400, "用户名已被使用");
        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        user.setDeleted(0);
        userService.save(user);
        return Result.success("创建成功", null);
    }

    @PutMapping("/user/update")
    public Result<?> userUpdate(@RequestBody User user) {
        user.setPassword(null);
        userService.updateById(user);
        return Result.success();
    }

    @DeleteMapping("/user/delete/{id}")
    public Result<?> userDelete(@PathVariable Long id) {
        userService.removeById(id);
        return Result.success();
    }

    @PutMapping("/user/change-status/{id}")
    public Result<?> userChangeStatus(@PathVariable Long id, @RequestParam Integer status) {
        User u = new User(); u.setId(id); u.setStatus(status);
        userService.updateById(u);
        return Result.success();
    }

    // ============ 分类管理 ============
    @GetMapping("/category/list")
    public Result<?> categoryList(PageQuery pageQuery) {
        return Result.success(categoryService.page(pageQuery.toPage(), new LambdaQueryWrapper<Category>().orderByAsc(Category::getSort)));
    }

    @GetMapping("/category/tree")
    public Result<?> categoryTree() {
        return Result.success(categoryService.list(new LambdaQueryWrapper<Category>().eq(Category::getDeleted, 0).orderByAsc(Category::getSort)));
    }

    @GetMapping("/category/first-level")
    public Result<?> firstLevel() {
        return Result.success(categoryService.list(new LambdaQueryWrapper<Category>()
                .and(w -> w.isNull(Category::getParentId).or().eq(Category::getParentId, 0))
                .eq(Category::getDeleted, 0).orderByAsc(Category::getSort)));
    }

    @PostMapping("/category/add")
    public Result<?> categoryAdd(@RequestBody Category cat) { categoryService.save(cat); return Result.success(); }

    @PutMapping("/category/update")
    public Result<?> categoryUpdate(@RequestBody Category cat) { categoryService.updateById(cat); return Result.success(); }

    @DeleteMapping("/category/delete/{id}")
    public Result<?> categoryDelete(@PathVariable Long id) { categoryService.removeById(id); return Result.success(); }

    @PutMapping("/category/change-status/{id}")
    public Result<?> categoryChangeStatus(@PathVariable Long id, @RequestParam Integer status) {
        Category c = new Category(); c.setId(id); c.setStatus(status);
        categoryService.updateById(c);
        return Result.success();
    }

    // ============ 商品管理 ============
    @GetMapping("/product/list")
    public Result<?> productList(PageQuery pageQuery,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<Product> qw = new LambdaQueryWrapper<Product>().eq(Product::getDeleted, 0);
        if (keyword != null && !keyword.isEmpty())
            qw.and(w -> w.like(Product::getProductName, keyword).or().like(Product::getProductNo, keyword));
        if (categoryId != null) qw.eq(Product::getCategoryId, categoryId);
        if (status != null) qw.eq(Product::getStatus, status);
        qw.orderByAsc(Product::getSort).orderByDesc(Product::getCreateTime);
        return Result.success(productService.page(pageQuery.toPage(), qw));
    }

    @GetMapping("/product/{id}")
    public Result<?> productDetail(@PathVariable Long id) {
        Product p = productService.getById(id);
        if (p == null || p.getDeleted() == 1) return Result.error(404, "商品不存在");
        List<ProductImage> images = productImageService.list(new LambdaQueryWrapper<ProductImage>()
                .eq(ProductImage::getProductId, id));
        Map<String, Object> data = new HashMap<>();
        data.put("product", p); data.put("images", images);
        return Result.success(data);
    }

    @PostMapping("/product/add")
    public Result<?> productAdd(@RequestBody Map<String, Object> body) {
        Product p = new Product();
        p.setProductName((String) body.get("productName"));
        p.setProductNo((String) body.getOrDefault("productNo", ""));
        p.setCategoryId(body.containsKey("categoryId") ? Long.valueOf(body.get("categoryId").toString()) : 12);
        p.setMainImage((String) body.getOrDefault("mainImage", "/images/products/default.png"));
        p.setPrice(new java.math.BigDecimal(body.getOrDefault("price", "0").toString()));
        p.setOriginalPrice(new java.math.BigDecimal(body.getOrDefault("originalPrice", "0").toString()));
        p.setStock(Integer.parseInt(body.getOrDefault("stock", "0").toString()));
        p.setUnit((String) body.getOrDefault("unit", "kg"));
        p.setOriginPlace((String) body.getOrDefault("originPlace", ""));
        p.setDescription((String) body.getOrDefault("description", ""));
        p.setIsRecommend(Integer.parseInt(body.getOrDefault("isRecommend", "0").toString()));
        p.setIsNew(Integer.parseInt(body.getOrDefault("isNew", "0").toString()));
        p.setIsHot(Integer.parseInt(body.getOrDefault("isHot", "0").toString()));
        p.setSort(Integer.parseInt(body.getOrDefault("sort", "0").toString()));
        p.setStatus(0); p.setAuditStatus(1); p.setDeleted(0);
        productService.save(p);
        return Result.success("创建成功", new HashMap<String,Object>() {{ put("id", p.getId()); }});
    }

    @PostMapping("/product/publish")
    public Result<?> productPublish(@RequestBody Map<String, Object> body) {
        Product p = new Product();
        p.setProductName((String) body.get("productName"));
        p.setProductNo((String) body.getOrDefault("productNo", "P" + System.currentTimeMillis()));
        p.setCategoryId(body.containsKey("categoryId") ? Long.valueOf(body.get("categoryId").toString()) : 12);
        p.setFarmerId(body.containsKey("farmerId") ? Long.valueOf(body.get("farmerId").toString()) : 0L);
        p.setMainImage((String) body.getOrDefault("mainImage", "/images/products/default.png"));
        p.setPrice(new java.math.BigDecimal(body.getOrDefault("price", "0").toString()));
        p.setOriginalPrice(new java.math.BigDecimal(body.getOrDefault("originalPrice", "0").toString()));
        p.setStock(Integer.parseInt(body.getOrDefault("stock", "0").toString()));
        p.setUnit((String) body.getOrDefault("unit", "kg"));
        p.setWeight(new java.math.BigDecimal(body.getOrDefault("weight", "0").toString()));
        p.setOriginPlace((String) body.getOrDefault("originPlace", ""));
        p.setDescription((String) body.getOrDefault("description", ""));
        p.setTraceId(body.containsKey("traceId") ? Long.valueOf(body.get("traceId").toString()) : null);
        p.setStatus(0); p.setAuditStatus(0); p.setDeleted(0); // 待审核
        p.setCreateTime(LocalDateTime.now());
        productService.save(p);
        return Result.success("提交成功，等待审核", new HashMap<String,Object>() {{ put("id", p.getId()); }});
    }

    @PutMapping("/product/update")
    public Result<?> productUpdate(@RequestBody Product product) {
        productService.updateById(product);
        return Result.success();
    }

    @DeleteMapping("/product/delete/{id}")
    @org.springframework.transaction.annotation.Transactional
    public Result<?> productDelete(@PathVariable Long id) {
        // Bug #7: 级联删除溯源
        traceabilityService.update(new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<com.freshtrace.unified.entity.Traceability>()
                .set(com.freshtrace.unified.entity.Traceability::getDeleted, 1)
                .eq(com.freshtrace.unified.entity.Traceability::getProductId, id));
        // Bug #21: 级联删除收藏
        productFavoriteMapper.delete(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.freshtrace.unified.entity.ProductFavorite>()
                .eq(com.freshtrace.unified.entity.ProductFavorite::getProductId, id));
        productService.removeById(id);
        return Result.success();
    }

    @PutMapping("/product/change-status/{id}")
    public Result<?> productChangeStatus(@PathVariable Long id, @RequestParam Integer status) {
        Product p = new Product(); p.setId(id); p.setStatus(status);
        productService.updateById(p);
        return Result.success();
    }

    @PutMapping("/product/change-recommend/{id}")
    public Result<?> productChangeRecommend(@PathVariable Long id, @RequestParam Integer recommend) {
        Product p = new Product(); p.setId(id); p.setIsRecommend(recommend);
        productService.updateById(p);
        return Result.success();
    }

    // ============ 轮播图 ============
    @GetMapping("/banner/list")
    public Result<?> bannerList(PageQuery pageQuery) {
        return Result.success(bannerService.page(pageQuery.toPage(), new LambdaQueryWrapper<Banner>().orderByAsc(Banner::getSort)));
    }

    @PostMapping("/banner/add")
    public Result<?> bannerAdd(@RequestBody Banner banner) { bannerService.save(banner); return Result.success(); }

    @PutMapping("/banner/update")
    public Result<?> bannerUpdate(@RequestBody Banner banner) { bannerService.updateById(banner); return Result.success(); }

    @DeleteMapping("/banner/delete/{id}")
    public Result<?> bannerDelete(@PathVariable Long id) { bannerService.removeById(id); return Result.success(); }

    @PutMapping("/banner/change-status/{id}")
    public Result<?> bannerChangeStatus(@PathVariable Long id, @RequestParam Integer status) {
        Banner b = new Banner(); b.setId(id); b.setStatus(status);
        bannerService.updateById(b);
        return Result.success();
    }

    // ============ 商品图片管理 ============
    @GetMapping("/product-image/list/{productId}")
    public Result<?> productImageList(@PathVariable Long productId) {
        return Result.success(productImageService.list(new LambdaQueryWrapper<ProductImage>()
                .eq(ProductImage::getProductId, productId).orderByAsc(ProductImage::getSort)));
    }

    @PostMapping("/product-image/add")
    public Result<?> productImageAdd(@RequestBody ProductImage image) {
        productImageService.save(image);
        return Result.success();
    }

    @DeleteMapping("/product-image/delete/{id}")
    public Result<?> productImageDelete(@PathVariable Long id) {
        productImageService.removeById(id);
        return Result.success();
    }

    @DeleteMapping("/product-image/delete-by-product/{productId}")
    public Result<?> productImageDeleteByProduct(@PathVariable Long productId) {
        productImageService.remove(new LambdaQueryWrapper<ProductImage>().eq(ProductImage::getProductId, productId));
        return Result.success();
    }

    // ============ 订单日志 ============
    @Autowired private OrderLogService orderLogService2;

    @GetMapping("/order-log/list/{orderId}")
    public Result<?> orderLogList(@PathVariable Long orderId) {
        return Result.success(orderLogService2.list(new LambdaQueryWrapper<OrderLog>()
                .eq(OrderLog::getOrderId, orderId).orderByAsc(OrderLog::getCreateTime)));
    }

    @PostMapping("/order-log/add")
    public Result<?> orderLogAdd(@RequestBody OrderLog log) {
        log.setCreateTime(LocalDateTime.now());
        orderLogService2.save(log);
        return Result.success();
    }

    // ============ 订单管理 ============
    @GetMapping("/order/list")
    public Result<?> orderList(PageQuery pageQuery,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<OrderInfo> qw = new LambdaQueryWrapper<OrderInfo>().eq(OrderInfo::getDeleted, 0);
        if (keyword != null && !keyword.isEmpty())
            qw.and(w -> w.like(OrderInfo::getOrderNo, keyword).or().like(OrderInfo::getReceiverName, keyword)
                    .or().like(OrderInfo::getReceiverPhone, keyword));
        if (status != null) qw.eq(OrderInfo::getOrderStatus, status);
        qw.orderByDesc(OrderInfo::getCreateTime);
        Page<OrderInfo> page = orderService.page(pageQuery.toPage(), qw);
        List<Map<String, Object>> enrichedRecords = new ArrayList<>();
        for (OrderInfo o : page.getRecords()) {
            Map<String, Object> row = new HashMap<>();
            row.put("id", o.getId()); row.put("orderNo", o.getOrderNo());
            row.put("userId", o.getUserId()); row.put("farmerId", o.getFarmerId());
            row.put("totalAmount", o.getTotalAmount()); row.put("payAmount", o.getPayAmount());
            row.put("planAmount", o.getPlanAmount()); row.put("actualAmount", o.getActualAmount());
            row.put("freightAmount", o.getFreightAmount()); row.put("discountAmount", o.getDiscountAmount());
            row.put("totalQuantity", o.getTotalQuantity()); row.put("payType", o.getPayType());
            row.put("payTime", o.getPayTime()); row.put("orderStatus", o.getOrderStatus());
            row.put("deliveryType", o.getDeliveryType()); row.put("deliveryTime", o.getDeliveryTime());
            row.put("receiveTime", o.getReceiveTime()); row.put("finishTime", o.getFinishTime());
            row.put("cancelTime", o.getCancelTime()); row.put("cancelReason", o.getCancelReason());
            row.put("receiverName", o.getReceiverName()); row.put("receiverPhone", o.getReceiverPhone());
            row.put("receiverProvince", o.getReceiverProvince()); row.put("receiverCity", o.getReceiverCity());
            row.put("receiverDistrict", o.getReceiverDistrict()); row.put("receiverAddress", o.getReceiverAddress());
            row.put("logisticsNo", o.getLogisticsNo()); row.put("logisticsCompany", o.getLogisticsCompany());
            row.put("orderRemark", o.getOrderRemark()); row.put("adminRemark", o.getAdminRemark());
            row.put("isComment", o.getIsComment()); row.put("couponId", o.getCouponId());
            row.put("createTime", o.getCreateTime()); row.put("updateTime", o.getUpdateTime());
            // Buyer info
            User buyer = userService.getById(o.getUserId());
            if (buyer != null) {
                row.put("buyerUsername", buyer.getUsername());
                row.put("buyerNickname", buyer.getNickname());
                row.put("buyerPhone", buyer.getPhone());
            }
            // Order items
            List<OrderItem> items = orderItemService.list(new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, o.getId()));
            row.put("items", items);
            enrichedRecords.add(row);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("records", enrichedRecords);
        result.put("total", page.getTotal());
        result.put("size", page.getSize());
        result.put("current", page.getCurrent());
        result.put("pages", page.getPages());
        return Result.success(result);
    }

    @GetMapping("/order/{id}")
    public Result<?> orderDetail(@PathVariable Long id) {
        OrderInfo order = orderService.getById(id);
        if (order == null) return Result.error(404, "订单不存在");
        Map<String, Object> data = new HashMap<>();
        data.put("order", order);
        data.put("orderItems", orderItemService.list(new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, id)));
        data.put("orderLogs", orderLogService.list(new LambdaQueryWrapper<OrderLog>().eq(OrderLog::getOrderId, id)));
        return Result.success(data);
    }

    @PutMapping("/order/delivery/{id}")
    public Result<?> orderDelivery(@PathVariable Long id, @RequestBody Map<String, String> body) {
        OrderInfo order = orderService.getById(id);
        if (order == null || order.getOrderStatus() != 1) return Result.error(400, "订单状态不允许发货");
        order.setOrderStatus(2); order.setLogisticsNo(body.get("logisticsNo"));
        order.setLogisticsCompany(body.get("logisticsCompany")); order.setDeliveryTime(LocalDateTime.now());
        orderService.updateById(order);
        OrderLog log = new OrderLog();
        log.setOrderId(id); log.setOrderNo(order.getOrderNo()); log.setOrderStatus(2);
        log.setOperatorType(1); log.setRemark("商家发货:" + body.getOrDefault("logisticsNo", ""));
        orderLogService.save(log);
        return Result.success("发货成功", null);
    }

    @PutMapping("/order/change-status/{id}")
    public Result<?> orderChangeStatus(@PathVariable Long id, @RequestParam Integer status) {
        OrderInfo order = orderService.getById(id);
        if (order == null) return Result.error(404, "订单不存在");
        order.setOrderStatus(status); orderService.updateById(order);
        return Result.success();
    }

    @DeleteMapping("/order/delete/{id}")
    public Result<?> orderDelete(@PathVariable Long id) {
        orderService.update(new LambdaUpdateWrapper<OrderInfo>()
                .eq(OrderInfo::getId, id).set(OrderInfo::getDeleted, 1));
        return Result.success();
    }

    // ============ 评论管理 ============
    @GetMapping("/comment/list")
    public Result<?> commentList(PageQuery pageQuery,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<ProductComment> qw = new LambdaQueryWrapper<>();
        if (status != null) qw.eq(ProductComment::getStatus, status);
        if (productId != null) qw.eq(ProductComment::getProductId, productId);
        if (keyword != null && !keyword.isEmpty()) qw.like(ProductComment::getContent, keyword);
        qw.orderByDesc(ProductComment::getCreateTime);
        Page<ProductComment> page = commentService.page(pageQuery.toPage(), qw);
        // Enrich with user and product info
        List<Map<String, Object>> enrichedRecords = new ArrayList<>();
        for (ProductComment c : page.getRecords()) {
            Map<String, Object> row = new HashMap<>();
            row.put("id", c.getId());
            row.put("productId", c.getProductId());
            row.put("userId", c.getUserId());
            row.put("orderId", c.getOrderId());
            row.put("rating", c.getRating());
            row.put("content", c.getContent());
            row.put("images", c.getImages());
            row.put("status", c.getStatus());
            row.put("createTime", c.getCreateTime());
            User u = userService.getById(c.getUserId());
            if (u != null) {
                row.put("username", u.getUsername());
                row.put("nickname", u.getNickname());
            }
            Product p = productService.getById(c.getProductId());
            if (p != null) {
                row.put("productName", p.getProductName());
                row.put("mainImage", p.getMainImage());
            }
            enrichedRecords.add(row);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("records", enrichedRecords);
        result.put("total", page.getTotal());
        return Result.success(result);
    }

    @PutMapping("/comment/approve/{id}")
    public Result<?> commentApprove(@PathVariable Long id) {
        commentService.update(new LambdaUpdateWrapper<ProductComment>().eq(ProductComment::getId, id).set(ProductComment::getStatus, 1));
        return Result.success();
    }

    @PutMapping("/comment/reject/{id}")
    public Result<?> commentReject(@PathVariable Long id) {
        commentService.removeById(id);
        return Result.success();
    }

    @DeleteMapping("/comment/delete/{id}")
    public Result<?> commentDelete(@PathVariable Long id) { commentService.removeById(id); return Result.success(); }

    // ============ 售后管理 ============
    @GetMapping("/after-sales/list")
    public Result<?> afterSalesList(PageQuery pageQuery) {
        return Result.success(afterSalesService.page(pageQuery.toPage(), new LambdaQueryWrapper<AfterSalesOrder>().orderByDesc(AfterSalesOrder::getApplyTime)));
    }

    @GetMapping("/after-sales/{id}")
    public Result<?> afterSalesDetail(@PathVariable Long id) { return Result.success(afterSalesService.getById(id)); }

    @PutMapping("/after-sales/audit/{id}")
    public Result<?> auditAfterSales(@PathVariable Long id, @RequestParam Integer status,
            @RequestParam(required = false) String adminRemark) {
        AfterSalesOrder aso = afterSalesService.getById(id);
        if (aso == null) return Result.error(404, "售后单不存在");
        aso.setStatus(status);
        aso.setAdminRemark(adminRemark != null ? adminRemark : "");
        aso.setAuditTime(LocalDateTime.now());
        afterSalesService.updateById(aso);
        return Result.success();
    }

    @PutMapping("/after-sales/close/{id}")
    public Result<?> closeAfterSales(@PathVariable Long id, @RequestParam String closeReason) {
        AfterSalesOrder aso = afterSalesService.getById(id);
        if (aso == null) return Result.error(404, "售后单不存在");
        aso.setStatus(4);
        aso.setCloseReason(closeReason);
        aso.setCloseTime(LocalDateTime.now());
        afterSalesService.updateById(aso);
        return Result.success();
    }

    @DeleteMapping("/after-sales/delete/{id}")
    public Result<?> deleteAfterSales(@PathVariable Long id) {
        afterSalesService.removeById(id);
        return Result.success();
    }

    // ============ 支付管理 ============
    @GetMapping("/payment/list")
    public Result<?> paymentList(PageQuery pageQuery) {
        return Result.success(paymentService.page(pageQuery.toPage(), new LambdaQueryWrapper<PaymentInfo>().orderByDesc(PaymentInfo::getCreateTime)));
    }

    // ============ 用户地址 ============
    @Autowired private AddressService addressService;

    @GetMapping("/addresses")
    public Result<?> adminAddresses() {
        List<Map<String, Object>> list = new ArrayList<>();
        List<UserAddress> addrs = addressService.list(new LambdaQueryWrapper<UserAddress>()
                .eq(UserAddress::getDeleted, 0).orderByDesc(UserAddress::getCreateTime));
        for (UserAddress a : addrs) {
            Map<String, Object> row = new HashMap<>();
            row.put("id", a.getId());
            row.put("user_id", a.getUserId());
            row.put("receiverName", a.getReceiverName());
            row.put("receiverPhone", a.getReceiverPhone());
            row.put("province", a.getProvince());
            row.put("city", a.getCity());
            row.put("district", a.getDistrict());
            row.put("detailAddress", a.getDetailAddress());
            row.put("isDefault", a.getIsDefault());
            row.put("createTime", a.getCreateTime());
            // Get username
            User u = userService.getById(a.getUserId());
            if (u != null) row.put("username", u.getUsername());
            list.add(row);
        }
        return Result.success(list);
    }

    // ============ 农户审核 ============
    @GetMapping("/farmer/audit/list")
    public Result<?> farmerAuditList(PageQuery pageQuery, @RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<FarmerAudit> qw = new LambdaQueryWrapper<>();
        if (status != null) qw.eq(FarmerAudit::getAuditStatus, status);
        qw.orderByDesc(FarmerAudit::getCreateTime);
        Page<FarmerAudit> page = farmerAuditService.page(pageQuery.toPage(), qw);
        // Enrich with farmer info
        List<Map<String, Object>> enrichedRecords = new ArrayList<>();
        for (FarmerAudit fa : page.getRecords()) {
            Map<String, Object> row = new HashMap<>();
            row.put("id", fa.getId());
            row.put("farmer_id", fa.getFarmerId());
            row.put("auditStatus", fa.getAuditStatus());
            row.put("auditRemark", fa.getAuditRemark());
            row.put("auditTime", fa.getAuditTime());
            row.put("createTime", fa.getCreateTime());
            Farmer f = farmerService.getById(fa.getFarmerId());
            if (f != null) {
                row.put("farmerName", f.getFarmerName());
                row.put("contactPerson", f.getContactPerson());
                row.put("contactPhone", f.getContactPhone());
                row.put("province", f.getProvince());
                row.put("city", f.getCity());
                row.put("district", f.getDistrict());
                row.put("address", f.getAddress());
                row.put("farmArea", f.getFarmArea());
                row.put("mainProducts", f.getMainProducts());
            }
            enrichedRecords.add(row);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("records", enrichedRecords);
        result.put("total", page.getTotal());
        return Result.success(result);
    }

    @GetMapping("/farmer/audit/detail/{id}")
    public Result<?> farmerAuditDetail(@PathVariable Long id) {
        FarmerAudit fa = farmerAuditService.getById(id);
        if (fa == null) return Result.error(404, "审核记录不存在");
        Map<String, Object> data = new HashMap<>();
        data.put("id", fa.getId());
        data.put("farmerId", fa.getFarmerId());
        data.put("auditStatus", fa.getAuditStatus());
        data.put("auditRemark", fa.getAuditRemark());
        data.put("auditTime", fa.getAuditTime());
        data.put("createTime", fa.getCreateTime());
        Farmer f = farmerService.getById(fa.getFarmerId());
        if (f != null) {
            data.put("farmerName", f.getFarmerName());
            data.put("contactPerson", f.getContactPerson());
            data.put("contactPhone", f.getContactPhone());
            data.put("province", f.getProvince());
            data.put("city", f.getCity());
            data.put("district", f.getDistrict());
            data.put("address", f.getAddress());
            data.put("farmArea", f.getFarmArea());
            data.put("mainProducts", f.getMainProducts());
        }
        return Result.success(data);
    }

    @PutMapping("/farmer/audit/{id}")
    public Result<?> farmerAudit(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        FarmerAudit fa = farmerAuditService.getById(id);
        if (fa == null) return Result.error(404, "审核记录不存在");
        boolean approve = Boolean.TRUE.equals(body.get("approve"));
        fa.setAuditStatus(approve ? 1 : 2);
        fa.setAuditRemark((String) body.getOrDefault("remark", ""));
        fa.setAuditTime(LocalDateTime.now());
        farmerAuditService.updateById(fa);
        Farmer farmer = farmerService.getById(fa.getFarmerId());
        if (farmer != null) {
            farmer.setAuditStatus(approve ? 1 : 2);
            farmer.setAuditRemark(fa.getAuditRemark());
            farmer.setAuditTime(LocalDateTime.now());
            farmerService.updateById(farmer);
            if (approve) {
                User u = new User(); u.setId(farmer.getUserId()); u.setUserType(2);
                userService.updateById(u);
            }
        }
        return Result.success(approve ? "已通过" : "已驳回", null);
    }

    @PutMapping("/farmer/expire/{id}")
    public Result<?> expireFarmer(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Farmer farmer = farmerService.getById(id);
        if (farmer == null) return Result.error(404, "农户不存在");
        String remark = (String) body.getOrDefault("remark", "资质过期");
        farmer.setAuditStatus(2);
        farmer.setAuditRemark(remark);
        farmer.setAuditTime(LocalDateTime.now());
        farmerService.updateById(farmer);
        // Restore user to normal
        User u = new User(); u.setId(farmer.getUserId()); u.setUserType(1);
        userService.updateById(u);
        // Create audit record
        FarmerAudit fa = new FarmerAudit();
        fa.setFarmerId(id); fa.setAuditStatus(2); fa.setAuditRemark(remark);
        fa.setAuditTime(LocalDateTime.now()); fa.setCreateTime(LocalDateTime.now());
        farmerAuditService.save(fa);
        return Result.success("已撤销农户资质", null);
    }

    // ============ 溯源批次审核 ============
    @Autowired private TraceabilityService traceService;
    @Autowired private TraceabilityMapper traceabilityMapper;
    @Autowired private TraceImageService traceImageService;

    @GetMapping("/trace/audit/list")
    public Result<?> traceAuditList(PageQuery pageQuery, @RequestParam(required = false) Integer status) {
        if (status != null && status == 0) {
            // 待审核：查询 audit_status=0
            return Result.success(traceabilityMapper.selectList(
                    new LambdaQueryWrapper<Traceability>().eq(Traceability::getAuditStatus, 0)
                            .eq(Traceability::getDeleted, 0).orderByDesc(Traceability::getCreateTime)));
        }
        return Result.success(traceabilityMapper.selectList(
                new LambdaQueryWrapper<Traceability>().eq(Traceability::getDeleted, 0)
                        .orderByDesc(Traceability::getCreateTime)));
    }

    @GetMapping("/trace/audit/detail/{id}")
    public Result<?> traceAuditDetail(@PathVariable Long id) {
        Traceability t = traceService.getById(id);
        if (t == null) return Result.error(404, "溯源批次不存在");
        Map<String, Object> data = new HashMap<>();
        data.put("id", t.getId()); data.put("traceCode", t.getTraceCode());
        data.put("batchNo", t.getBatchNo()); data.put("productName", t.getProductName());
        data.put("originPlace", t.getOriginPlace()); data.put("farmName", t.getFarmName());
        data.put("responsiblePerson", t.getResponsiblePerson());
        data.put("auditStatus", t.getAuditStatus()); data.put("createTime", t.getCreateTime());
        data.put("planting", traceService.list(new LambdaQueryWrapper<Traceability>())); // placeholder
        data.put("images", traceImageService.list(new LambdaQueryWrapper<TraceImage>().eq(TraceImage::getTraceId, id)));
        return Result.success(data);
    }

    @PutMapping("/trace/audit/approve/{id}")
    public Result<?> traceAuditApprove(@PathVariable Long id) {
        traceabilityMapper.updateDeletedStatus(id, 0); // ensure deleted=0
        Traceability t = new Traceability(); t.setId(id); t.setAuditStatus(1);
        traceService.updateById(t);
        return Result.success("审核通过", null);
    }

    @PutMapping("/trace/audit/reject/{id}")
    public Result<?> traceAuditReject(@PathVariable Long id) {
        traceabilityMapper.updateDeletedStatus(id, 1); // soft delete
        return Result.success("已拒绝", null);
    }

    // ============ 商品发布审核 ============
    @GetMapping("/product/audit/list")
    public Result<?> productAuditList(PageQuery pageQuery, @RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<Product> qw = new LambdaQueryWrapper<Product>().eq(Product::getDeleted, 0);
        if (status != null) qw.eq(Product::getAuditStatus, status);
        qw.orderByDesc(Product::getCreateTime);
        return Result.success(productService.page(pageQuery.toPage(), qw));
    }

    @PutMapping("/product/audit/approve/{id}")
    public Result<?> productAuditApprove(@PathVariable Long id) {
        Product p = new Product(); p.setId(id); p.setAuditStatus(1); p.setStatus(1);
        productService.updateById(p);
        return Result.success("审核通过", null);
    }

    @PutMapping("/product/audit/reject/{id}")
    public Result<?> productAuditReject(@PathVariable Long id) {
        productService.removeById(id);
        return Result.success("已拒绝", null);
    }

    // ============ 管理员账号 ============
    @GetMapping("/admins")
    public Result<?> adminList() {
        return Result.success(userService.list(new LambdaQueryWrapper<User>()
                .eq(User::getUserType, 3).eq(User::getDeleted, 0).orderByDesc(User::getCreateTime)));
    }

    @PostMapping("/admins")
    public Result<?> adminAdd(@RequestBody User user) {
        if (user.getUsername() == null || user.getPassword() == null) return Result.error(400, "用户名和密码不能为空");
        Long count = userService.count(new LambdaQueryWrapper<User>().eq(User::getUsername, user.getUsername()));
        if (count > 0) return Result.error(400, "用户名已被使用");
        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        user.setUserType(3); user.setStatus(1); user.setDeleted(0);
        userService.save(user);
        return Result.success("创建成功", new HashMap<String,Object>() {{ put("id", user.getId()); }});
    }

    @PutMapping("/admins")
    public Result<?> adminUpdate(@RequestBody User user) {
        user.setPassword(null); userService.updateById(user);
        return Result.success();
    }

    @PutMapping("/admins/resetPassword/{id}")
    public Result<?> adminResetPassword(@PathVariable Long id) {
        User u = new User(); u.setId(id);
        u.setPassword(BCrypt.hashpw("Abc123!@", BCrypt.gensalt()));
        userService.updateById(u);
        return Result.success("密码已重置为 Abc123!@", null);
    }

    // ============ 优惠券管理 ============
    @GetMapping("/coupons")
    public Result<?> couponList() {
        return Result.success(couponService.list(new LambdaQueryWrapper<Coupon>().orderByDesc(Coupon::getCreateTime)));
    }

    @PostMapping("/coupons")
    public Result<?> couponAdd(@RequestBody Coupon coupon) { couponService.save(coupon); return Result.success(); }

    @PutMapping("/coupons")
    public Result<?> couponUpdate(@RequestBody Coupon coupon) { couponService.updateById(coupon); return Result.success(); }

    @DeleteMapping("/coupons/{id}")
    public Result<?> couponDelete(@PathVariable Long id) { couponService.removeById(id); return Result.success(); }

    @GetMapping("/coupons/{id}/records")
    public Result<?> couponRecords(@PathVariable Long id, PageQuery pageQuery) {
        Page<UserCoupon> page = userCouponService.page(pageQuery.toPage(),
                new LambdaQueryWrapper<UserCoupon>().eq(UserCoupon::getCouponId, id).orderByDesc(UserCoupon::getTakeTime));
        return Result.success(page);
    }

    // ============ 秒杀管理 ============
    @GetMapping("/seckill")
    public Result<?> seckillList() { return Result.success(seckillService.list()); }

    @PostMapping("/seckill")
    public Result<?> seckillAdd(@RequestBody Seckill seckill) { seckillService.save(seckill); return Result.success(); }

    @PutMapping("/seckill")
    public Result<?> seckillUpdate(@RequestBody Seckill seckill) { seckillService.updateById(seckill); return Result.success(); }

    @DeleteMapping("/seckill/{id}")
    public Result<?> seckillDelete(@PathVariable Long id) { seckillService.removeById(id); return Result.success(); }

    // ============ 营销活动 ============
    @GetMapping("/marketing")
    public Result<?> marketingList() { return Result.success(marketingService.list()); }

    @PostMapping("/marketing")
    public Result<?> marketingAdd(@RequestBody MarketingActivity activity) { marketingService.save(activity); return Result.success(); }

    @PutMapping("/marketing")
    public Result<?> marketingUpdate(@RequestBody MarketingActivity activity) { marketingService.updateById(activity); return Result.success(); }

    @DeleteMapping("/marketing/{id}")
    public Result<?> marketingDelete(@PathVariable Long id) { marketingService.removeById(id); return Result.success(); }

    // ============ 会员管理 ============
    @GetMapping("/members")
    public Result<?> memberList(PageQuery pageQuery, @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<User> qw = new LambdaQueryWrapper<User>()
                .eq(User::getUserType, 1).eq(User::getDeleted, 0);
        if (keyword != null && !keyword.isEmpty())
            qw.and(w -> w.like(User::getUsername, keyword).or().like(User::getPhone, keyword).or().like(User::getNickname, keyword));
        qw.orderByDesc(User::getCreateTime);
        Page<User> page = userService.page(pageQuery.toPage(), qw);
        List<Map<String, Object>> enrichedRecords = new ArrayList<>();
        for (User u : page.getRecords()) {
            Map<String, Object> row = new HashMap<>();
            row.put("id", u.getId()); row.put("username", u.getUsername());
            row.put("nickname", u.getNickname()); row.put("phone", u.getPhone());
            row.put("email", u.getEmail()); row.put("avatar", u.getAvatar());
            row.put("gender", u.getGender()); row.put("status", u.getStatus());
            row.put("createTime", u.getCreateTime());
            // Calculate totalSpent and orderCount
            List<OrderInfo> orders = orderService.list(new LambdaQueryWrapper<OrderInfo>()
                    .eq(OrderInfo::getUserId, u.getId()).eq(OrderInfo::getDeleted, 0));
            double totalSpent = orders.stream().filter(o -> o.getOrderStatus() == 1 || o.getOrderStatus() == 2 || o.getOrderStatus() == 3)
                    .mapToDouble(o -> o.getPayAmount() != null ? o.getPayAmount().doubleValue() : 0).sum();
            row.put("totalSpent", totalSpent);
            row.put("orderCount", orders.size());
            enrichedRecords.add(row);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("records", enrichedRecords);
        result.put("total", page.getTotal());
        return Result.success(result);
    }

    @GetMapping("/members/{id}")
    public Result<?> memberDetail(@PathVariable Long id) {
        User u = userService.getById(id);
        if (u == null) return Result.error(404, "用户不存在");
        Map<String, Object> data = new HashMap<>();
        data.put("id", u.getId()); data.put("username", u.getUsername());
        data.put("nickname", u.getNickname()); data.put("phone", u.getPhone());
        data.put("email", u.getEmail()); data.put("avatar", u.getAvatar());
        data.put("gender", u.getGender()); data.put("status", u.getStatus());
        data.put("createTime", u.getCreateTime());
        List<OrderInfo> orders = orderService.list(new LambdaQueryWrapper<OrderInfo>()
                .eq(OrderInfo::getUserId, u.getId()).eq(OrderInfo::getDeleted, 0));
        double totalSpent = orders.stream().filter(o -> o.getOrderStatus() == 1 || o.getOrderStatus() == 2 || o.getOrderStatus() == 3)
                .mapToDouble(o -> o.getPayAmount() != null ? o.getPayAmount().doubleValue() : 0).sum();
        data.put("totalSpent", totalSpent);
        data.put("orderCount", orders.size());
        return Result.success(data);
    }

    @PutMapping("/members/{id}/status")
    public Result<?> memberToggleStatus(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        User u = new User();
        u.setId(id);
        u.setStatus(Integer.parseInt(body.get("status").toString()));
        userService.updateById(u);
        return Result.success();
    }

    // ============ 系统配置 ============
    @GetMapping("/config/list")
    public Result<?> configList() { return Result.success(configService.list()); }

    @PutMapping("/config/update")
    public Result<?> configUpdate(@RequestBody Map<String, String> body) {
        String key = body.get("configKey");
        String value = body.get("configValue");
        configService.update(new LambdaUpdateWrapper<SysConfig>().eq(SysConfig::getConfigKey, key).set(SysConfig::getConfigValue, value));
        return Result.success("配置已更新", null);
    }

    // ============ 操作日志 ============
    @GetMapping("/operationLog/page")
    public Result<?> operationLogPage(PageQuery pageQuery,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        LambdaQueryWrapper<SysOperationLog> qw = new LambdaQueryWrapper<>();
        if (module != null && !module.isEmpty()) qw.eq(SysOperationLog::getModule, module);
        if (username != null && !username.isEmpty()) qw.like(SysOperationLog::getUsername, username);
        if (startTime != null && !startTime.isEmpty()) qw.ge(SysOperationLog::getCreateTime, startTime);
        if (endTime != null && !endTime.isEmpty()) qw.le(SysOperationLog::getCreateTime, endTime + " 23:59:59");
        qw.orderByDesc(SysOperationLog::getCreateTime);
        return Result.success(operationLogService.page(pageQuery.toPage(), qw));
    }

    // ============ 批量操作 ============
    @PutMapping("/batch/users")
    public Result<?> batchUsers(@RequestBody Map<String, Object> body) {
        @SuppressWarnings("unchecked")
        List<Long> ids = ((List<Number>) body.get("ids")).stream().map(Number::longValue).collect(java.util.stream.Collectors.toList());
        String action = (String) body.get("action");
        int status = "enable".equals(action) ? 1 : 0;
        userService.update(new LambdaUpdateWrapper<User>().in(User::getId, ids).set(User::getStatus, status));
        return Result.success();
    }

    @PutMapping("/batch/products")
    public Result<?> batchProducts(@RequestBody Map<String, Object> body) {
        @SuppressWarnings("unchecked")
        List<Long> ids = ((List<Number>) body.get("ids")).stream().map(Number::longValue).collect(java.util.stream.Collectors.toList());
        String action = (String) body.get("action");
        int status = "online".equals(action) ? 1 : 0;
        productService.update(new LambdaUpdateWrapper<Product>().in(Product::getId, ids).set(Product::getStatus, status));
        return Result.success();
    }

    // ============ Excel导出 ============
    @GetMapping("/export/{table}")
    public void exportData(@PathVariable String table, HttpServletResponse response) throws IOException {
        response.setContentType("text/csv;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + table + "_" + System.currentTimeMillis() + ".csv");
        response.setCharacterEncoding("UTF-8");
        // BOM for Excel
        response.getWriter().write("﻿");

        StringBuilder csv = new StringBuilder();
        switch (table) {
            case "users":
                csv.append("ID,用户名,昵称,手机号,邮箱,用户类型,状态,注册时间\n");
                userService.list(new LambdaQueryWrapper<User>().eq(User::getDeleted, 0)).forEach(u ->
                    csv.append(u.getId()).append(",").append(u.getUsername()).append(",")
                       .append(u.getNickname()).append(",").append(u.getPhone()).append(",")
                       .append(u.getEmail()).append(",").append(u.getUserType()).append(",")
                       .append(u.getStatus()).append(",").append(u.getCreateTime()).append("\n"));
                break;
            case "products":
                csv.append("ID,商品编号,商品名称,价格,库存,销量,状态,创建时间\n");
                productService.list(new LambdaQueryWrapper<Product>().eq(Product::getDeleted, 0)).forEach(p ->
                    csv.append(p.getId()).append(",").append(p.getProductNo()).append(",")
                       .append(p.getProductName()).append(",").append(p.getPrice()).append(",")
                       .append(p.getStock()).append(",").append(p.getSales()).append(",")
                       .append(p.getStatus()).append(",").append(p.getCreateTime()).append("\n"));
                break;
            case "orders":
                csv.append("ID,订单号,用户ID,总金额,实付金额,订单状态,收货人,电话,创建时间\n");
                orderService.list(new LambdaQueryWrapper<OrderInfo>().eq(OrderInfo::getDeleted, 0)).forEach(o ->
                    csv.append(o.getId()).append(",").append(o.getOrderNo()).append(",")
                       .append(o.getUserId()).append(",").append(o.getTotalAmount()).append(",")
                       .append(o.getPayAmount()).append(",").append(o.getOrderStatus()).append(",")
                       .append(o.getReceiverName()).append(",").append(o.getReceiverPhone()).append(",")
                       .append(o.getCreateTime()).append("\n"));
                break;
            default:
                response.setStatus(400);
                response.getWriter().write("unsupported table");
                return;
        }
        response.getWriter().write(csv.toString());
    }
}
