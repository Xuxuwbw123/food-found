package com.freshtrace.unified.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * SPA fallback: serve index.html for all frontend routes.
 * API routes (/api/**, /admin/**, /auth/**) are handled by RestControllers
 * and matched before this controller.
 */
@Controller
public class SpaController {

    @RequestMapping({
            "/", "/login", "/register",
            "/cart", "/orders", "/pay", "/profile", "/address",
            "/favorites", "/search", "/trace", "/notices",
            "/points", "/footprints", "/coupons", "/after-sales",
            "/qrcode-verify", "/qrcode-scan",
            "/presale", "/presale-confirm", "/my-presale",
            "/chat"
    })
    public String forward() {
        return "forward:/index.html";
    }

    @RequestMapping("/product/{id}")
    public String forwardProduct() {
        return "forward:/index.html";
    }

    @RequestMapping("/farmer/{id}")
    public String forwardFarmer() {
        return "forward:/index.html";
    }

    @RequestMapping("/logistics/{id}")
    public String forwardLogistics() {
        return "forward:/index.html";
    }

    @RequestMapping("/trace/{batchNo}")
    public String forwardTrace() {
        return "forward:/index.html";
    }

    // Admin SPA pages
    @RequestMapping({
            "/admin/dashboard", "/admin/users", "/admin/categories",
            "/admin/products", "/admin/banners", "/admin/orders",
            "/admin/comments", "/admin/payments", "/admin/after-sales",
            "/admin/addresses", "/admin/trace-audit", "/admin/product-audit", "/admin/trace-delete-audit",
            "/admin/operation-logs", "/admin/admins", "/admin/farmer-audit",
            "/admin/config", "/admin/member-level", "/admin/points-exchange",
            "/admin/coupons", "/admin/seckill",
            "/admin/members", "/admin/marketing", "/admin/chat"
    })
    public String forwardAdmin() {
        return "forward:/index.html";
    }

    @RequestMapping("/admin/product/edit/{id}")
    public String forwardProductEdit() {
        return "forward:/index.html";
    }

    @RequestMapping("/admin/product/edit")
    public String forwardProductAdd() {
        return "forward:/index.html";
    }

    @RequestMapping("/admin/order/detail/{id}")
    public String forwardOrderDetail() {
        return "forward:/index.html";
    }

    @RequestMapping("/admin/after-sales/detail/{id}")
    public String forwardAfterSalesDetail() {
        return "forward:/index.html";
    }

    @RequestMapping("/farmer")
    public String forwardFarmerDashboard() {
        return "forward:/index.html";
    }

    @RequestMapping("/farmer/trace/{id}")
    public String forwardFarmerTrace() {
        return "forward:/index.html";
    }

    @RequestMapping("/farmer/order/{id}")
    public String forwardFarmerOrder() {
        return "forward:/index.html";
    }

    @RequestMapping("/qrcode-scan/{code}")
    public String forwardQrcodeScan() {
        return "forward:/index.html";
    }

    @RequestMapping({
            "/admin/group-buy", "/admin/themes", "/admin/mystery-boxes",
            "/admin/recipes", "/admin/stock-alerts", "/admin/green-points-rules",
            "/admin/qualification-certs", "/admin/farm-updates", "/admin/trace-location",
            "/admin/trace-settings"
    })
    public String forwardAdminNew() {
        return "forward:/index.html";
    }
}
