package com.freshtrace.unified.controller;

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

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/trace")
public class TraceController {

    @Autowired private TraceabilityService traceService;
    @Autowired private TracePlantingService plantingService;
    @Autowired private TraceFertilizerService fertilizerService;
    @Autowired private TracePesticideService pesticideService;
    @Autowired private TraceIrrigationService irrigationService;
    @Autowired private TraceHarvestService harvestService;
    @Autowired private TraceInspectionService inspectionService;
    @Autowired private TraceLogisticsService logisticsService;
    @Autowired private TraceStorageService storageService;
    @Autowired private TraceImageService imageService;
    @Autowired private TraceBreedingService breedingService;
    @Autowired private TraceProcessingService processingService;
    @Autowired private LogisticsTrackService logisticsTrackService;
    @Autowired private ProductService productService;

    // ============ 溯源主表 ============
    @GetMapping("/list")
    public Result<?> traceList(PageQuery pageQuery,
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<Traceability> qw = new LambdaQueryWrapper<Traceability>()
                .eq(Traceability::getDeleted, 0).eq(Traceability::getAuditStatus, 1);
        if (productId != null) qw.eq(Traceability::getProductId, productId);
        if (status != null) qw.eq(Traceability::getStatus, status);
        qw.orderByDesc(Traceability::getCreateTime);
        return Result.success(traceService.page(pageQuery.toPage(), qw));
    }

    @GetMapping("/detail/{id}")
    public Result<?> traceDetail(@PathVariable Long id) {
        Traceability t = traceService.getById(id);
        if (t == null) return Result.error(404, "溯源信息不存在");
        Map<String, Object> data = new HashMap<>();
        data.put("traceability", t);
        List<TracePlanting> plantings = plantingService.list(new LambdaQueryWrapper<TracePlanting>().eq(TracePlanting::getTraceId, id));
        data.put("planting", plantings.isEmpty() ? null : plantings.get(0));
        data.put("fertilizerList", fertilizerService.list(new LambdaQueryWrapper<TraceFertilizer>().eq(TraceFertilizer::getTraceId, id)));
        data.put("pesticideList", pesticideService.list(new LambdaQueryWrapper<TracePesticide>().eq(TracePesticide::getTraceId, id)));
        data.put("irrigationList", irrigationService.list(new LambdaQueryWrapper<TraceIrrigation>().eq(TraceIrrigation::getTraceId, id)));
        List<TraceHarvest> harvests = harvestService.list(new LambdaQueryWrapper<TraceHarvest>().eq(TraceHarvest::getTraceId, id));
        data.put("harvest", harvests.isEmpty() ? null : harvests.get(0));
        data.put("inspectionList", inspectionService.list(new LambdaQueryWrapper<TraceInspection>().eq(TraceInspection::getTraceId, id)));
        data.put("imageList", imageService.list(new LambdaQueryWrapper<TraceImage>().eq(TraceImage::getTraceId, id)));
        return Result.success(data);
    }

    @GetMapping("/scan/{batchNo}")
    public Result<?> scanTrace(@PathVariable String batchNo) {
        Traceability t = traceService.getOne(new LambdaQueryWrapper<Traceability>()
                .eq(Traceability::getBatchNo, batchNo).eq(Traceability::getDeleted, 0));
        if (t == null) return Result.error(404, "溯源码不存在");
        t.setScanCount(t.getScanCount() + 1); traceService.updateById(t);
        // Return full detail like /detail/{id}
        Map<String, Object> data = new HashMap<>();
        data.put("traceability", t);
        List<TracePlanting> plantings = plantingService.list(new LambdaQueryWrapper<TracePlanting>().eq(TracePlanting::getTraceId, t.getId()));
        data.put("planting", plantings.isEmpty() ? null : plantings.get(0));
        data.put("fertilizerList", fertilizerService.list(new LambdaQueryWrapper<TraceFertilizer>().eq(TraceFertilizer::getTraceId, t.getId())));
        data.put("pesticideList", pesticideService.list(new LambdaQueryWrapper<TracePesticide>().eq(TracePesticide::getTraceId, t.getId())));
        data.put("irrigationList", irrigationService.list(new LambdaQueryWrapper<TraceIrrigation>().eq(TraceIrrigation::getTraceId, t.getId())));
        List<TraceHarvest> harvests = harvestService.list(new LambdaQueryWrapper<TraceHarvest>().eq(TraceHarvest::getTraceId, t.getId()));
        data.put("harvest", harvests.isEmpty() ? null : harvests.get(0));
        data.put("inspectionList", inspectionService.list(new LambdaQueryWrapper<TraceInspection>().eq(TraceInspection::getTraceId, t.getId())));
        data.put("imageList", imageService.list(new LambdaQueryWrapper<TraceImage>().eq(TraceImage::getTraceId, t.getId())));
        return Result.success(data);
    }

    @GetMapping("/by-product/{productId}")
    public Result<?> traceByProduct(@PathVariable Long productId) {
        Traceability t = traceService.getOne(new LambdaQueryWrapper<Traceability>()
                .eq(Traceability::getProductId, productId).last("LIMIT 1"));
        if (t == null) {
            Product p = productService.getById(productId);
            if (p != null && p.getTraceId() != null) t = traceService.getById(p.getTraceId());
        }
        return Result.success(t);
    }

    @PostMapping("/create")
    public Result<?> createTrace(@RequestBody Map<String, Object> body) {
        String batchNo = (String) body.get("batchNo");
        Long existCount = traceService.count(new LambdaQueryWrapper<Traceability>().eq(Traceability::getBatchNo, batchNo));
        if (existCount > 0) return Result.error(400, "批次号已存在");
        Traceability t = new Traceability();
        t.setTraceCode("TR" + java.time.LocalDate.now().toString().replace("-", "") + String.format("%04d", new Random().nextInt(10000)));
        t.setFarmerId(Long.valueOf(body.get("farmerId").toString()));
        t.setBatchNo(batchNo); t.setProductName((String) body.get("productName"));
        t.setOriginPlace((String) body.get("originPlace")); t.setFarmName((String) body.get("farmName"));
        t.setResponsiblePerson((String) body.get("responsiblePerson"));
        t.setResponsiblePhone((String) body.getOrDefault("responsiblePhone", ""));
        t.setProductId(0L); t.setStatus(1); t.setAuditStatus(0);
        traceService.save(t);
        return Result.success("创建成功", new HashMap<String,Object>() {{ put("id", t.getId()); put("traceCode", t.getTraceCode()); }});
    }

    @Autowired private TraceabilityMapper traceabilityMapper;

    @PutMapping("/request-delete/{id}")
    public Result<?> requestDelete(@PathVariable Long id) {
        traceabilityMapper.updateDeletedStatus(id, 2);
        return Result.success();
    }

    @GetMapping("/pending-deletes")
    public Result<?> pendingDeletes() {
        return Result.success(traceabilityMapper.selectPendingDeletes());
    }

    @PutMapping("/approve-delete/{id}")
    public Result<?> approveDelete(@PathVariable Long id) {
        plantingService.remove(new LambdaQueryWrapper<TracePlanting>().eq(TracePlanting::getTraceId, id));
        fertilizerService.remove(new LambdaQueryWrapper<TraceFertilizer>().eq(TraceFertilizer::getTraceId, id));
        pesticideService.remove(new LambdaQueryWrapper<TracePesticide>().eq(TracePesticide::getTraceId, id));
        irrigationService.remove(new LambdaQueryWrapper<TraceIrrigation>().eq(TraceIrrigation::getTraceId, id));
        harvestService.remove(new LambdaQueryWrapper<TraceHarvest>().eq(TraceHarvest::getTraceId, id));
        imageService.remove(new LambdaQueryWrapper<TraceImage>().eq(TraceImage::getTraceId, id));
        productService.remove(new LambdaQueryWrapper<Product>().eq(Product::getTraceId, id));
        traceabilityMapper.updateDeletedStatus(id, 1);
        return Result.success();
    }

    @PutMapping("/reject-delete/{id}")
    public Result<?> rejectDelete(@PathVariable Long id) {
        traceabilityMapper.updateDeletedStatus(id, 0);
        return Result.success();
    }

    // ============ 种植记录 ============
    @GetMapping("/planting/{traceId}")
    public Result<?> getPlanting(@PathVariable Long traceId) {
        return Result.success(plantingService.list(new LambdaQueryWrapper<TracePlanting>().eq(TracePlanting::getTraceId, traceId)));
    }

    @PostMapping("/planting/save")
    public Result<?> savePlanting(@RequestBody TracePlanting planting) { plantingService.save(planting); return Result.success(); }

    // ============ 施肥记录 ============
    @GetMapping("/fertilizer/list/{traceId}")
    public Result<?> getFertilizers(@PathVariable Long traceId) {
        return Result.success(fertilizerService.list(new LambdaQueryWrapper<TraceFertilizer>().eq(TraceFertilizer::getTraceId, traceId)));
    }

    @PostMapping("/fertilizer/add")
    public Result<?> addFertilizer(@RequestBody TraceFertilizer f) { fertilizerService.save(f); return Result.success(); }

    @PutMapping("/fertilizer/update")
    public Result<?> updateFertilizer(@RequestBody TraceFertilizer f) { fertilizerService.updateById(f); return Result.success(); }

    @DeleteMapping("/fertilizer/delete/{id}")
    public Result<?> deleteFertilizer(@PathVariable Long id) { fertilizerService.removeById(id); return Result.success(); }

    // ============ 农药记录 ============
    @GetMapping("/pesticide/list/{traceId}")
    public Result<?> getPesticides(@PathVariable Long traceId) {
        return Result.success(pesticideService.list(new LambdaQueryWrapper<TracePesticide>().eq(TracePesticide::getTraceId, traceId)));
    }

    @PostMapping("/pesticide/add")
    public Result<?> addPesticide(@RequestBody TracePesticide p) { pesticideService.save(p); return Result.success(); }

    @PutMapping("/pesticide/update")
    public Result<?> updatePesticide(@RequestBody TracePesticide p) { pesticideService.updateById(p); return Result.success(); }

    @DeleteMapping("/pesticide/delete/{id}")
    public Result<?> deletePesticide(@PathVariable Long id) { pesticideService.removeById(id); return Result.success(); }

    // ============ 灌溉记录 ============
    @GetMapping("/irrigation/list/{traceId}")
    public Result<?> getIrrigations(@PathVariable Long traceId) {
        return Result.success(irrigationService.list(new LambdaQueryWrapper<TraceIrrigation>().eq(TraceIrrigation::getTraceId, traceId)));
    }

    @PostMapping("/irrigation/add")
    public Result<?> addIrrigation(@RequestBody TraceIrrigation i) { irrigationService.save(i); return Result.success(); }

    @PutMapping("/irrigation/update")
    public Result<?> updateIrrigation(@RequestBody TraceIrrigation i) { irrigationService.updateById(i); return Result.success(); }

    @DeleteMapping("/irrigation/delete/{id}")
    public Result<?> deleteIrrigation(@PathVariable Long id) { irrigationService.removeById(id); return Result.success(); }

    // ============ 收获记录 ============
    @GetMapping("/harvest/get-by-trace/{traceId}")
    public Result<?> getHarvest(@PathVariable Long traceId) {
        return Result.success(harvestService.list(new LambdaQueryWrapper<TraceHarvest>().eq(TraceHarvest::getTraceId, traceId)));
    }

    @PostMapping("/harvest/add")
    public Result<?> addHarvest(@RequestBody TraceHarvest h) { harvestService.save(h); return Result.success(); }

    @PutMapping("/harvest/update")
    public Result<?> updateHarvest(@RequestBody TraceHarvest h) { harvestService.updateById(h); return Result.success(); }

    @DeleteMapping("/harvest/delete/{id}")
    public Result<?> deleteHarvest(@PathVariable Long id) { harvestService.removeById(id); return Result.success(); }

    // ============ 检测报告 ============
    @GetMapping("/inspection/list/{traceId}")
    public Result<?> getInspections(@PathVariable Long traceId) {
        return Result.success(inspectionService.list(new LambdaQueryWrapper<TraceInspection>().eq(TraceInspection::getTraceId, traceId)));
    }

    @PostMapping("/inspection/add")
    public Result<?> addInspection(@RequestBody TraceInspection i) { inspectionService.save(i); return Result.success(); }

    @PutMapping("/inspection/update")
    public Result<?> updateInspection(@RequestBody TraceInspection i) { inspectionService.updateById(i); return Result.success(); }

    @DeleteMapping("/inspection/delete/{id}")
    public Result<?> deleteInspection(@PathVariable Long id) { inspectionService.removeById(id); return Result.success(); }

    // ============ 仓储记录 ============
    @GetMapping("/storage/get-by-trace/{traceId}")
    public Result<?> getStorage(@PathVariable Long traceId) {
        return Result.success(storageService.list(new LambdaQueryWrapper<TraceStorage>().eq(TraceStorage::getTraceId, traceId)));
    }

    @PostMapping("/storage/add")
    public Result<?> addStorage(@RequestBody TraceStorage s) { storageService.save(s); return Result.success(); }

    @PutMapping("/storage/update")
    public Result<?> updateStorage(@RequestBody TraceStorage s) { storageService.updateById(s); return Result.success(); }

    @DeleteMapping("/storage/delete/{id}")
    public Result<?> deleteStorage(@PathVariable Long id) { storageService.removeById(id); return Result.success(); }

    // ============ 物流信息 ============
    @GetMapping("/logistics/{traceId}")
    public Result<?> getLogistics(@PathVariable Long traceId) {
        TraceLogistics tl = logisticsService.getOne(new LambdaQueryWrapper<TraceLogistics>()
                .eq(TraceLogistics::getTraceId, traceId).orderByDesc(TraceLogistics::getCreateTime).last("LIMIT 1"));
        Map<String, Object> data = new HashMap<>();
        data.put("logistics", tl);
        data.put("tracks", tl != null ? logisticsTrackService.list(new LambdaQueryWrapper<LogisticsTrack>()
                .eq(LogisticsTrack::getLogisticsId, tl.getId())) : Collections.emptyList());
        return Result.success(data);
    }

    @PostMapping("/logistics/save")
    public Result<?> saveLogistics(@RequestBody TraceLogistics logistics) { logisticsService.save(logistics); return Result.success(); }

    @DeleteMapping("/logistics/delete/{id}")
    public Result<?> deleteLogistics(@PathVariable Long id) { logisticsService.removeById(id); return Result.success(); }

    @GetMapping("/logistics/tracks/{logisticsId}")
    public Result<?> getLogisticsTracks(@PathVariable Long logisticsId) {
        return Result.success(logisticsTrackService.list(new LambdaQueryWrapper<LogisticsTrack>()
                .eq(LogisticsTrack::getLogisticsId, logisticsId).orderByAsc(LogisticsTrack::getTrackTime)));
    }

    @PostMapping("/logistics/track/save")
    public Result<?> saveLogisticsTrack(@RequestBody LogisticsTrack track) { logisticsTrackService.save(track); return Result.success(); }

    @DeleteMapping("/logistics/track/delete/{id}")
    public Result<?> deleteLogisticsTrack(@PathVariable Long id) { logisticsTrackService.removeById(id); return Result.success(); }

    // ============ 溯源图片 ============
    @GetMapping("/image/list-by-trace/{traceId}")
    public Result<?> getTraceImages(@PathVariable Long traceId) {
        return Result.success(imageService.list(new LambdaQueryWrapper<TraceImage>().eq(TraceImage::getTraceId, traceId)));
    }

    @PostMapping("/image/add")
    public Result<?> addTraceImage(@RequestBody TraceImage img) { imageService.save(img); return Result.success(); }

    @PutMapping("/image/update")
    public Result<?> updateTraceImage(@RequestBody TraceImage img) { imageService.updateById(img); return Result.success(); }

    @DeleteMapping("/image/delete/{id}")
    public Result<?> deleteTraceImage(@PathVariable Long id) { imageService.removeById(id); return Result.success(); }

    // ============ 养殖记录 ============
    @GetMapping("/breeding/list-by-trace/{traceId}")
    public Result<?> getBreedings(@PathVariable Long traceId) {
        return Result.success(breedingService.list(new LambdaQueryWrapper<TraceBreeding>().eq(TraceBreeding::getTraceId, traceId)));
    }

    @PostMapping("/breeding/add")
    public Result<?> addBreeding(@RequestBody TraceBreeding b) { breedingService.save(b); return Result.success(); }

    @PutMapping("/breeding/update")
    public Result<?> updateBreeding(@RequestBody TraceBreeding b) { breedingService.updateById(b); return Result.success(); }

    @DeleteMapping("/breeding/delete/{id}")
    public Result<?> deleteBreeding(@PathVariable Long id) { breedingService.removeById(id); return Result.success(); }

    // ============ 加工记录 ============
    @GetMapping("/processing/list-by-trace/{traceId}")
    public Result<?> getProcessings(@PathVariable Long traceId) {
        return Result.success(processingService.list(new LambdaQueryWrapper<TraceProcessing>().eq(TraceProcessing::getTraceId, traceId)));
    }

    @PostMapping("/processing/add")
    public Result<?> addProcessing(@RequestBody TraceProcessing p) { processingService.save(p); return Result.success(); }

    @PutMapping("/processing/update")
    public Result<?> updateProcessing(@RequestBody TraceProcessing p) { processingService.updateById(p); return Result.success(); }

    @DeleteMapping("/processing/delete/{id}")
    public Result<?> deleteProcessing(@PathVariable Long id) { processingService.removeById(id); return Result.success(); }
}
