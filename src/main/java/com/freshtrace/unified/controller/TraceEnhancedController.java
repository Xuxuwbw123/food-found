package com.freshtrace.unified.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.freshtrace.unified.common.Result;
import com.freshtrace.unified.common.UserContext;
import com.freshtrace.unified.entity.*;
import com.freshtrace.unified.mapper.TraceabilityMapper;
import com.freshtrace.unified.service.*;
import com.freshtrace.unified.mapper.SysConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/trace")
public class TraceEnhancedController {

    @Autowired private TraceabilityService traceService;
    @Autowired private TraceabilityMapper traceabilityMapper;
    @Autowired private TraceLocationService locationService;
    @Autowired private ProductQrcodeService qrcodeService;
    @Autowired private QrcodeScanLogService scanLogService;
    @Autowired private GrowthTimelineService growthService;
    @Autowired private FarmUpdateService farmUpdateService;
    @Autowired private QualificationCertService certService;
    @Autowired private TracePlantingService plantingService;
    @Autowired private TraceFertilizerService fertilizerService;
    @Autowired private TracePesticideService pesticideService;
    @Autowired private TraceIrrigationService irrigationService;
    @Autowired private TraceHarvestService harvestService;
    @Autowired private TraceImageService imageService;
    @Autowired private ProductService productService;
    @Autowired private SysConfigMapper configMapper;

    // ============ 溯源地理位置 ============
    @GetMapping("/locations/{traceId}")
    public Result<?> getLocations(@PathVariable Long traceId) {
        return Result.success(locationService.list(new LambdaQueryWrapper<TraceLocation>()
                .eq(TraceLocation::getTraceId, traceId).orderByAsc(TraceLocation::getSort)));
    }

    @PostMapping("/locations")
    public Result<?> addLocation(@RequestBody TraceLocation location) {
        if (location.getLocationType() == null) location.setLocationType("farm");
        location.setCreateTime(LocalDateTime.now());
        locationService.save(location);
        return Result.success();
    }

    @DeleteMapping("/locations/{id}")
    public Result<?> deleteLocation(@PathVariable Long id) {
        locationService.removeById(id);
        return Result.success();
    }

    // ============ 一物一码：获取服务器地址配置 ============
    @GetMapping("/qrcode/config")
    public Result<?> getQrcodeConfig() {
        SysConfig config = configMapper.selectOne(new LambdaQueryWrapper<SysConfig>()
                .eq(SysConfig::getConfigKey, "qrcode_base_url"));
        String url = config != null ? config.getConfigValue() : "http://localhost:8088";
        return Result.success(Map.of("baseUrl", url));
    }

    // ============ 一物一码：批量生成 ============
    @PostMapping("/qrcode/batch-generate")
    public Result<?> batchGenerateQrcode(@RequestBody Map<String, Object> body) {
        Long traceId = Long.valueOf(body.get("traceId").toString());
        int count = Integer.parseInt(body.get("count").toString());
        if (count <= 0 || count > 500) return Result.error(400, "数量范围1-500");

        Traceability trace = traceService.getById(traceId);
        if (trace == null) return Result.error(404, "溯源批次不存在");

        String baseUrl = body.containsKey("baseUrl") ? body.get("baseUrl").toString() : "http://localhost:8088";

        List<Map<String, Object>> codes = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String content = "TRACE-" + traceId + "-" + System.nanoTime() + "-" + i;
            ProductQrcode qr = new ProductQrcode();
            qr.setProductId(trace.getProductId());
            qr.setTraceId(traceId);
            qr.setQrcodeContent(content);
            qr.setScanCount(0);
            qr.setStatus(1);
            qr.setCreateTime(LocalDateTime.now());
            qrcodeService.save(qr);

            Map<String, Object> item = new HashMap<>();
            item.put("id", qr.getId());
            item.put("content", content);
            item.put("url", baseUrl + "/qrcode-scan/" + content);
            codes.add(item);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("traceId", traceId);
        result.put("batchNo", trace.getBatchNo());
        result.put("productName", trace.getProductName());
        result.put("count", codes.size());
        result.put("codes", codes);
        return Result.success(result);
    }

    // ============ 一物一码：查询批次的QR码列表 ============
    @GetMapping("/qrcode/list/{traceId}")
    public Result<?> listQrcodes(@PathVariable Long traceId) {
        return Result.success(qrcodeService.list(new LambdaQueryWrapper<ProductQrcode>()
                .eq(ProductQrcode::getTraceId, traceId).orderByDesc(ProductQrcode::getCreateTime)));
    }

    // ============ 一物一码：扫码（记录扫描） ============
    @PostMapping("/qrcode/scan")
    public Result<?> scanQrcode(@RequestBody Map<String, String> body) {
        String content = body.get("content");
        ProductQrcode qr = qrcodeService.getOne(new LambdaQueryWrapper<ProductQrcode>()
                .eq(ProductQrcode::getQrcodeContent, content));
        if (qr == null) return Result.error(404, "溯源码不存在");

        qr.setScanCount(qr.getScanCount() + 1);
        if (qr.getFirstScanTime() == null) {
            qr.setFirstScanTime(LocalDateTime.now());
        }
        qrcodeService.updateById(qr);

        QrcodeScanLog log = new QrcodeScanLog();
        log.setQrcodeId(qr.getId());
        log.setScanIp("");
        log.setScanTime(LocalDateTime.now());
        scanLogService.save(log);

        Map<String, Object> result = new HashMap<>();
        result.put("firstScan", qr.getScanCount() == 1);
        result.put("scanCount", qr.getScanCount());
        result.put("productId", qr.getProductId());
        return Result.success(result);
    }

    // ============ 一物一码：扫码获取完整溯源信息（公开接口） ============
    @GetMapping("/qrcode/detail/{content}")
    public Result<?> getQrcodeDetail(@PathVariable String content) {
        ProductQrcode qr = qrcodeService.getOne(new LambdaQueryWrapper<ProductQrcode>()
                .eq(ProductQrcode::getQrcodeContent, content));
        if (qr == null) return Result.error(404, "溯源码不存在");

        // 记录扫描
        qr.setScanCount(qr.getScanCount() + 1);
        boolean firstScan = qr.getFirstScanTime() == null;
        if (firstScan) qr.setFirstScanTime(LocalDateTime.now());
        qrcodeService.updateById(qr);

        QrcodeScanLog log = new QrcodeScanLog();
        log.setQrcodeId(qr.getId());
        log.setScanIp("");
        log.setScanTime(LocalDateTime.now());
        scanLogService.save(log);

        // 构建完整溯源信息
        Map<String, Object> data = new HashMap<>();
        data.put("content", content);
        data.put("firstScan", firstScan);
        data.put("scanCount", qr.getScanCount());
        data.put("productId", qr.getProductId());

        // 查找溯源批次
        Long traceId = qr.getTraceId();
        if (traceId != null) {
            Traceability trace = traceService.getById(traceId);
            if (trace != null) {
                Map<String, Object> traceInfo = new HashMap<>();
                traceInfo.put("id", trace.getId());
                traceInfo.put("productName", trace.getProductName());
                traceInfo.put("batchNo", trace.getBatchNo());
                traceInfo.put("originPlace", trace.getOriginPlace());
                traceInfo.put("traceCode", trace.getTraceCode());
                data.put("trace", traceInfo);

                // 种植记录
                List<TracePlanting> plantings = plantingService.list(new LambdaQueryWrapper<TracePlanting>()
                        .eq(TracePlanting::getTraceId, traceId));
                data.put("planting", plantings.isEmpty() ? null : plantings.get(0));

                // 施肥记录
                data.put("fertilizerList", fertilizerService.list(new LambdaQueryWrapper<TraceFertilizer>()
                        .eq(TraceFertilizer::getTraceId, traceId)));

                // 农药记录
                data.put("pesticideList", pesticideService.list(new LambdaQueryWrapper<TracePesticide>()
                        .eq(TracePesticide::getTraceId, traceId)));

                // 灌溉记录
                data.put("irrigationList", irrigationService.list(new LambdaQueryWrapper<TraceIrrigation>()
                        .eq(TraceIrrigation::getTraceId, traceId)));

                // 收获记录
                List<TraceHarvest> harvests = harvestService.list(new LambdaQueryWrapper<TraceHarvest>()
                        .eq(TraceHarvest::getTraceId, traceId));
                data.put("harvest", harvests.isEmpty() ? null : harvests.get(0));

                // 溯源图片
                data.put("imageList", imageService.list(new LambdaQueryWrapper<TraceImage>()
                        .eq(TraceImage::getTraceId, traceId)));

                // 溯源地理位置
                data.put("locationList", locationService.list(new LambdaQueryWrapper<TraceLocation>()
                        .eq(TraceLocation::getTraceId, traceId).orderByAsc(TraceLocation::getSort)));
            }
        }

        return Result.success(data);
    }

    // ============ 生长周期 ============
    @GetMapping("/growth/{traceId}")
    public Result<?> getGrowthTimeline(@PathVariable Long traceId) {
        return Result.success(growthService.list(new LambdaQueryWrapper<GrowthTimeline>()
                .eq(GrowthTimeline::getTraceId, traceId).orderByAsc(GrowthTimeline::getCreateTime)));
    }

    @PostMapping("/growth")
    public Result<?> addGrowthRecord(@RequestBody GrowthTimeline record) {
        record.setCreateTime(LocalDateTime.now());
        record.setUpdateTime(LocalDateTime.now());
        growthService.save(record);
        return Result.success();
    }

    // ============ 农场动态 ============
    @GetMapping("/farm-update/{farmerId}")
    public Result<?> getFarmUpdates(@PathVariable Long farmerId) {
        return Result.success(farmUpdateService.list(new LambdaQueryWrapper<FarmUpdate>()
                .eq(FarmUpdate::getFarmerId, farmerId).orderByDesc(FarmUpdate::getCreateTime)));
    }

    @PostMapping("/farm-update")
    public Result<?> addFarmUpdate(@RequestBody FarmUpdate update) {
        update.setCreateTime(LocalDateTime.now());
        farmUpdateService.save(update);
        return Result.success();
    }

    // ============ 资质证书 ============
    @GetMapping("/certs/{farmerId}")
    public Result<?> getCerts(@PathVariable Long farmerId) {
        return Result.success(certService.list(new LambdaQueryWrapper<QualificationCert>()
                .eq(QualificationCert::getFarmerId, farmerId)));
    }

    @PostMapping("/certs")
    public Result<?> addCert(@RequestBody QualificationCert cert) {
        cert.setCreateTime(LocalDateTime.now());
        certService.save(cert);
        return Result.success();
    }
}
