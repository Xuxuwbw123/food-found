package com.freshtrace.unified.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.freshtrace.unified.common.Result;
import com.freshtrace.unified.common.UserContext;
import com.freshtrace.unified.entity.*;
import com.freshtrace.unified.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
public class GroupBuyController {

    @Autowired private GroupBuyService groupBuyService;
    @Autowired private GroupBuyRecordService recordService;
    @Autowired private ProductService productService;
    @Autowired private SysNoticeService noticeService;

    // ============ 管理员：拼团管理 ============
    @GetMapping("/admin/group-buy/list")
    public Result<?> list() {
        return Result.success(groupBuyService.list(new LambdaQueryWrapper<GroupBuy>()
                .orderByDesc(GroupBuy::getCreateTime)));
    }

    @PostMapping("/admin/group-buy")
    public Result<?> add(@RequestBody GroupBuy gb) {
        gb.setCurrentCount(0);
        gb.setCreateTime(LocalDateTime.now());
        groupBuyService.save(gb);
        return Result.success();
    }

    @PutMapping("/admin/group-buy")
    public Result<?> update(@RequestBody GroupBuy gb) {
        groupBuyService.updateById(gb);
        return Result.success();
    }

    @DeleteMapping("/admin/group-buy/{id}")
    public Result<?> delete(@PathVariable Long id) {
        groupBuyService.removeById(id);
        return Result.success();
    }

    // ============ 用户端：拼团列表 ============
    @GetMapping("/api/group-buy/list")
    public Result<?> userList() {
        return Result.success(groupBuyService.list(new LambdaQueryWrapper<GroupBuy>()
                .eq(GroupBuy::getStatus, 1)
                .le(GroupBuy::getStartTime, LocalDateTime.now())
                .ge(GroupBuy::getEndTime, LocalDateTime.now())));
    }

    // ============ 用户端：参与拼团 ============
    @PostMapping("/api/group-buy/join/{id}")
    public Result<?> join(@PathVariable Long id) {
        Long userId = UserContext.getUserId();
        GroupBuy gb = groupBuyService.getById(id);
        if (gb == null || gb.getStatus() != 1) return Result.error(404, "拼团不存在");

        long count = recordService.count(new LambdaQueryWrapper<GroupBuyRecord>()
                .eq(GroupBuyRecord::getGroupBuyId, id).eq(GroupBuyRecord::getUserId, userId));
        if (count > 0) return Result.error(400, "已参与");

        GroupBuyRecord record = new GroupBuyRecord();
        record.setGroupBuyId(id); record.setUserId(userId);
        record.setStatus("pending"); record.setCreateTime(LocalDateTime.now());
        recordService.save(record);

        gb.setCurrentCount(gb.getCurrentCount() + 1);
        if (gb.getCurrentCount() >= gb.getGroupSize()) {
            gb.setStatus(2); // 拼团成功
            // 通知所有参与者
            List<GroupBuyRecord> records = recordService.list(new LambdaQueryWrapper<GroupBuyRecord>()
                    .eq(GroupBuyRecord::getGroupBuyId, id));
            for (GroupBuyRecord r : records) {
                r.setStatus("success"); recordService.updateById(r);
                try {
                    SysNotice notice = new SysNotice();
                    notice.setUserId(r.getUserId()); notice.setNoticeType("group_buy");
                    notice.setTitle("拼团成功");
                    notice.setContent("您参与的拼团已成功，商品将尽快发出");
                    notice.setIsRead(0); notice.setCreateTime(LocalDateTime.now());
                    noticeService.save(notice);
                } catch (Exception ignored) {}
            }
        }
        groupBuyService.updateById(gb);
        return Result.success("参与成功");
    }

    // ============ 用户端：我的拼团 ============
    @GetMapping("/api/group-buy/my")
    public Result<?> myGroupBuys() {
        Long userId = UserContext.getUserId();
        return Result.success(recordService.list(new LambdaQueryWrapper<GroupBuyRecord>()
                .eq(GroupBuyRecord::getUserId, userId).orderByDesc(GroupBuyRecord::getCreateTime)));
    }
}
