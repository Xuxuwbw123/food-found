package com.freshtrace.unified.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.freshtrace.unified.common.Result;
import com.freshtrace.unified.common.UserContext;
import com.freshtrace.unified.entity.*;
import com.freshtrace.unified.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@RestController
public class LocationController {

    @Autowired private SysLocationService locationService;
    @Autowired private FarmerService farmerService;

    private static final String GAODE_KEY = "202efb721c5ede9efc4f1b7343cd0cdd";

    // ============ 地理编码：文字地址 → 经纬度 ============
    @GetMapping("/api/geocode")
    public Result<?> geocode(@RequestParam String address) {
        try {
            String encoded = URLEncoder.encode(address, StandardCharsets.UTF_8);
            String apiUrl = "https://restapi.amap.com/v3/geocode/geo?address=" + encoded + "&key=" + GAODE_KEY + "&output=json";
            String response = httpGet(apiUrl);

            // Parse response
            if (response.contains("\"status\":\"1\"") && response.contains("\"geocodes\"")) {
                // Extract location from geocodes
                int locIdx = response.indexOf("\"location\":\"");
                if (locIdx > 0) {
                    String loc = response.substring(locIdx + 12, response.indexOf("\"", locIdx + 12));
                    String[] parts = loc.split(",");
                    BigDecimal lng = new BigDecimal(parts[0]);
                    BigDecimal lat = new BigDecimal(parts[1]);

                    // Extract province, city, district
                    String province = extractField(response, "province");
                    String city = extractField(response, "city");
                    String district = extractField(response, "district");

                    Map<String, Object> result = new HashMap<>();
                    result.put("longitude", lng);
                    result.put("latitude", lat);
                    result.put("province", province);
                    result.put("city", city);
                    result.put("district", district);
                    result.put("formattedAddress", extractField(response, "formatted_address"));
                    return Result.success(result);
                }
            }
            return Result.error(400, "无法解析该地址，请输入更详细的地址");
        } catch (Exception e) {
            return Result.error(500, "地理编码服务异常: " + e.getMessage());
        }
    }

    // ============ 管理员：点位CRUD ============
    @GetMapping("/admin/locations")
    public Result<?> listLocations(@RequestParam(required = false) Long farmerId,
                                   @RequestParam(required = false) String locationType) {
        LambdaQueryWrapper<SysLocation> qw = new LambdaQueryWrapper<>();
        if (farmerId != null) qw.eq(SysLocation::getFarmerId, farmerId);
        if (locationType != null) qw.eq(SysLocation::getLocationType, locationType);
        qw.orderByDesc(SysLocation::getCreateTime);
        return Result.success(locationService.list(qw));
    }

    @PostMapping("/admin/locations")
    public Result<?> addLocation(@RequestBody SysLocation location) {
        location.setStatus(1);
        location.setCreateTime(LocalDateTime.now());
        locationService.save(location);
        return Result.success();
    }

    @PutMapping("/admin/locations")
    public Result<?> updateLocation(@RequestBody SysLocation location) {
        locationService.updateById(location);
        return Result.success();
    }

    @DeleteMapping("/admin/locations/{id}")
    public Result<?> deleteLocation(@PathVariable Long id) {
        locationService.removeById(id);
        return Result.success();
    }

    // ============ 农户：获取自己的点位列表 ============
    @GetMapping("/api/locations/my")
    public Result<?> myLocations() {
        Long userId = UserContext.getUserId();
        Farmer farmer = farmerService.getOne(new LambdaQueryWrapper<Farmer>().eq(Farmer::getUserId, userId));
        if (farmer == null) return Result.error(404, "农户信息不存在");
        return Result.success(locationService.list(new LambdaQueryWrapper<SysLocation>()
                .eq(SysLocation::getFarmerId, farmer.getId())
                .eq(SysLocation::getStatus, 1)
                .orderByDesc(SysLocation::getCreateTime)));
    }

    // ============ 内部方法：农户审核通过后自动创建点位 ============
    public void createLocationFromFarmer(Farmer farmer) {
        if (farmer.getLongitude() == null || farmer.getLatitude() == null) return;

        // Check if location already exists for this farmer
        Long existing = locationService.count(new LambdaQueryWrapper<SysLocation>()
                .eq(SysLocation::getFarmerId, farmer.getId()));
        if (existing > 0) return;

        SysLocation loc = new SysLocation();
        loc.setLocationName(farmer.getFarmerName());
        loc.setLocationType("farm");
        loc.setFarmerId(farmer.getId());
        loc.setUserId(farmer.getUserId());
        loc.setAddress(buildAddress(farmer));
        loc.setLongitude(farmer.getLongitude());
        loc.setLatitude(farmer.getLatitude());
        loc.setProvince(farmer.getProvince());
        loc.setCity(farmer.getCity());
        loc.setDistrict(farmer.getDistrict());
        loc.setStatus(1);
        loc.setCreateTime(LocalDateTime.now());
        locationService.save(loc);
    }

    private String buildAddress(Farmer f) {
        StringBuilder sb = new StringBuilder();
        if (f.getProvince() != null) sb.append(f.getProvince());
        if (f.getCity() != null) sb.append(f.getCity());
        if (f.getDistrict() != null) sb.append(f.getDistrict());
        if (f.getAddress() != null) sb.append(f.getAddress());
        return sb.toString();
    }

    private String extractField(String json, String field) {
        String key = "\"" + field + "\":\"";
        int idx = json.indexOf(key);
        if (idx < 0) return "";
        int start = idx + key.length();
        int end = json.indexOf("\"", start);
        return end > start ? json.substring(start, end) : "";
    }

    private String httpGet(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) sb.append(line);
        reader.close();
        return sb.toString();
    }
}
