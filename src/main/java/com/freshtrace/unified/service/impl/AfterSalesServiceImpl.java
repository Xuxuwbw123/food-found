package com.freshtrace.unified.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freshtrace.unified.common.UserContext;
import com.freshtrace.unified.dto.*;
import com.freshtrace.unified.entity.*;
import com.freshtrace.unified.mapper.*;
import com.freshtrace.unified.service.AfterSalesService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AfterSalesServiceImpl implements AfterSalesService {

    @Autowired private AfterSalesOrderMapper afterSalesOrderMapper;
    @Autowired private OrderInfoMapper orderInfoMapper;
    @Autowired private OrderItemMapper orderItemMapper;
    @Autowired private CustomerServiceLogMapper customerServiceLogMapper;
    @Autowired private ProductMapper productMapper;

    @Override
    @Transactional
    public void apply(AfterSalesDTO dto) {
        OrderItem item = orderItemMapper.selectById(dto.getOrderItemId());
        if (item == null) throw new RuntimeException("order item not found");
        OrderInfo order = orderInfoMapper.selectById(item.getOrderId());
        if (order == null || !order.getUserId().equals(UserContext.getUserId())) throw new RuntimeException("order not found");
        if (order.getOrderStatus() != 1 && order.getOrderStatus() != 2 && order.getOrderStatus() != 3) {
            throw new RuntimeException("order status not eligible for after-sales");
        }
        // Bug #3 fix: check for existing after-sales on the same order item
        long existingCount = afterSalesOrderMapper.selectCount(new LambdaQueryWrapper<AfterSalesOrder>()
                .eq(AfterSalesOrder::getOrderItemId, dto.getOrderItemId())
                .in(AfterSalesOrder::getStatus, 0, 1, 2, 3, 4, 5, 6));
        if (existingCount > 0) {
            throw new RuntimeException("该商品已有进行中的售后申请");
        }

        String asNo = "AS" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        AfterSalesOrder as = new AfterSalesOrder();
        as.setAfterSalesNo(asNo);
        as.setOrderId(order.getId());
        as.setOrderNo(order.getOrderNo());
        as.setOrderItemId(item.getId());
        as.setUserId(UserContext.getUserId());
        as.setFarmerId(item.getFarmerId());
        as.setProductId(item.getProductId());
        as.setProductName(item.getProductName());
        as.setAfterSalesType(dto.getAfterSalesType());
        as.setReason(dto.getReason());
        as.setDescription(dto.getDescription());
        as.setEvidenceImages(dto.getEvidenceImages());
        as.setRefundAmount(dto.getRefundAmount() != null ? dto.getRefundAmount() : item.getTotalAmount());
        as.setStatus(0);
        as.setApplyTime(LocalDateTime.now());
        as.setCreateTime(LocalDateTime.now());
        afterSalesOrderMapper.insert(as);

        order.setOrderStatus(5);
        orderInfoMapper.updateById(order);
    }

    @Override
    public Page<AfterSalesVO> list(Integer pageNum, Integer pageSize) {
        Page<AfterSalesOrder> page = afterSalesOrderMapper.selectPage(new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<AfterSalesOrder>().eq(AfterSalesOrder::getUserId, UserContext.getUserId())
                        .orderByDesc(AfterSalesOrder::getCreateTime));
        Page<AfterSalesVO> result = new Page<>(pageNum, pageSize, page.getTotal());
        result.setRecords(page.getRecords().stream().map(a -> {
            AfterSalesVO vo = new AfterSalesVO();
            BeanUtils.copyProperties(a, vo);
            OrderItem item = orderItemMapper.selectById(a.getOrderItemId());
            if (item != null) vo.setProductPrice(item.getPrice());
            return vo;
        }).collect(Collectors.toList()));
        return result;
    }

    @Override
    public AfterSalesVO getDetail(Long id) {
        AfterSalesOrder a = afterSalesOrderMapper.selectById(id);
        if (a == null || !a.getUserId().equals(UserContext.getUserId())) throw new RuntimeException("after-sales not found");
        AfterSalesVO vo = new AfterSalesVO();
        BeanUtils.copyProperties(a, vo);
        OrderItem item = orderItemMapper.selectById(a.getOrderItemId());
        if (item != null) vo.setProductPrice(item.getPrice());

        List<CustomerServiceLog> logs = customerServiceLogMapper.selectList(
                new LambdaQueryWrapper<CustomerServiceLog>().eq(CustomerServiceLog::getAfterSalesId, a.getId())
                        .orderByAsc(CustomerServiceLog::getCreateTime));
        vo.setServiceLogs(logs.stream().map(l -> {
            AfterSalesVO.ServiceLogVO sv = new AfterSalesVO.ServiceLogVO();
            BeanUtils.copyProperties(l, sv);
            return sv;
        }).collect(Collectors.toList()));
        return vo;
    }

    @Override
    @Transactional
    public void update(Long id, AfterSalesUpdateDTO dto) {
        AfterSalesOrder a = afterSalesOrderMapper.selectById(id);
        if (a == null || !a.getUserId().equals(UserContext.getUserId())) throw new RuntimeException("after-sales not found");

        if (dto.getUserRemark() != null) {
            a.setUserRemark(dto.getUserRemark());
            afterSalesOrderMapper.updateById(a);
        }
        if (dto.getEvidenceImages() != null) {
            a.setEvidenceImages(dto.getEvidenceImages());
            afterSalesOrderMapper.updateById(a);
        }
        if (dto.getReturnLogisticsNo() != null) {
            a.setReturnLogisticsNo(dto.getReturnLogisticsNo());
            a.setReturnLogisticsCompany(dto.getReturnLogisticsCompany());
            a.setStatus(3);
            afterSalesOrderMapper.updateById(a);
        }
        if (dto.getCloseReason() != null) {
            a.setStatus(7);
            a.setCloseReason(dto.getCloseReason());
            a.setCloseTime(LocalDateTime.now());
            afterSalesOrderMapper.updateById(a);
        }

        String msgContent = dto.getUserRemark() != null ? dto.getUserRemark() : null;
        if (dto.getCloseReason() != null) msgContent = "Closed by user: " + dto.getCloseReason();
        if (msgContent != null) {
            CustomerServiceLog log = new CustomerServiceLog();
            log.setAfterSalesId(a.getId());
            log.setOrderId(a.getOrderId());
            log.setUserId(UserContext.getUserId());
            log.setOperatorType(1);
            log.setOperatorId(UserContext.getUserId());
            log.setOperatorName("user");
            log.setMsgType(1);
            log.setContent(msgContent);
            log.setCreateTime(LocalDateTime.now());
            customerServiceLogMapper.insert(log);
        }
    }
}