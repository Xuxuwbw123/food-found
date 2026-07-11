package com.freshtrace.unified.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freshtrace.unified.common.UserContext;
import com.freshtrace.unified.dto.CommentCreateDTO;
import com.freshtrace.unified.dto.CommentVO;
import com.freshtrace.unified.entity.*;
import com.freshtrace.unified.mapper.*;
import com.freshtrace.unified.service.CommentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl extends ServiceImpl<ProductCommentMapper, ProductComment> implements CommentService {

    @Autowired private ProductCommentMapper productCommentMapper;
    @Autowired private SysUserMapper sysUserMapper;
    @Autowired private ProductMapper productMapper;
    @Autowired private OrderItemMapper orderItemMapper;

    @Override
    public Page<CommentVO> listByProduct(Long productId, Integer pageNum, Integer pageSize, Integer rating) {
        LambdaQueryWrapper<ProductComment> qw = new LambdaQueryWrapper<ProductComment>()
                .eq(ProductComment::getProductId, productId).eq(ProductComment::getStatus, 1);
        if (rating != null) qw.eq(ProductComment::getRating, rating);
        qw.orderByDesc(ProductComment::getCreateTime);
        Page<ProductComment> page = productCommentMapper.selectPage(new Page<>(pageNum, pageSize), qw);
        Page<CommentVO> result = new Page<>(pageNum, pageSize, page.getTotal());
        result.setRecords(page.getRecords().stream().map(c -> {
            CommentVO vo = new CommentVO();
            BeanUtils.copyProperties(c, vo);
            SysUser u = sysUserMapper.selectById(c.getUserId());
            if (c.getIsAnonymous() == 1) {
                vo.setNickname("anonymous");
                vo.setAvatar("");
            } else if (u != null) {
                vo.setNickname(u.getNickname());
                vo.setAvatar(u.getAvatar());
            }
            return vo;
        }).collect(Collectors.toList()));
        return result;
    }

    @Override
    @Transactional
    public void create(CommentCreateDTO dto) {
        ProductComment c = new ProductComment();
        c.setProductId(dto.getProductId());
        c.setUserId(UserContext.getUserId());
        c.setOrderId(dto.getOrderId());
        c.setOrderItemId(dto.getOrderItemId());
        c.setRating(dto.getRating());
        c.setContent(dto.getContent());
        c.setImages(dto.getImages());
        c.setIsAnonymous(dto.getIsAnonymous() != null ? dto.getIsAnonymous() : 0);
        c.setLikeCount(0);
        c.setReplyCount(0);
        c.setStatus(1);
        c.setCreateTime(LocalDateTime.now());
        productCommentMapper.insert(c);

        if (dto.getOrderItemId() != null) {
            OrderItem oi = orderItemMapper.selectById(dto.getOrderItemId());
            if (oi != null) { oi.setIsComment(1); orderItemMapper.updateById(oi); }
        }

        Product p = productMapper.selectById(dto.getProductId());
        if (p != null) {
            p.setCommentCount(p.getCommentCount() != null ? p.getCommentCount() + 1 : 1);
            productMapper.updateById(p);
        }
    }

    @Override
    public Page<CommentVO> myComments(Integer pageNum, Integer pageSize) {
        Page<ProductComment> page = productCommentMapper.selectPage(new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<ProductComment>().eq(ProductComment::getUserId, UserContext.getUserId())
                        .orderByDesc(ProductComment::getCreateTime));
        Page<CommentVO> result = new Page<>(pageNum, pageSize, page.getTotal());
        result.setRecords(page.getRecords().stream().map(c -> {
            CommentVO vo = new CommentVO();
            BeanUtils.copyProperties(c, vo);
            return vo;
        }).collect(Collectors.toList()));
        return result;
    }
}
