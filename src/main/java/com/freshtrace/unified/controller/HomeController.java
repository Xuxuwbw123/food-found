package com.freshtrace.unified.controller;

import com.freshtrace.unified.common.Result;
import com.freshtrace.unified.dto.HomeVO;
import com.freshtrace.unified.service.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/home")
public class HomeController {

    @Autowired private HomeService homeService;

    @GetMapping
    public Result<HomeVO> home() {
        try { return Result.ok(homeService.getHomeData()); }
        catch (Exception e) { return Result.fail(e.getMessage()); }
    }
}
