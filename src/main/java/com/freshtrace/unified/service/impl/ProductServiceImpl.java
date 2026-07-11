package com.freshtrace.unified.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freshtrace.unified.dto.ProductVO;
import com.freshtrace.unified.entity.*;
import com.freshtrace.unified.mapper.*;
import com.freshtrace.unified.service.ProductService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Autowired private ProductImageMapper productImageMapper;
    @Autowired private ProductCategoryMapper productCategoryMapper;
    @Autowired private FarmerMapper farmerMapper;

    @Override
    public Page<Product> list(Integer pageNum, Integer pageSize, Long categoryId, String keyword, String sort) {
        LambdaQueryWrapper<Product> qw = new LambdaQueryWrapper<Product>()
                .eq(Product::getStatus, 1).eq(Product::getAuditStatus, 1);
        if (categoryId != null) qw.eq(Product::getCategoryId, categoryId);
        if (keyword != null && !keyword.isEmpty()) qw.like(Product::getProductName, keyword);
        if ("price_asc".equals(sort)) qw.orderByAsc(Product::getPrice);
        else if ("price_desc".equals(sort)) qw.orderByDesc(Product::getPrice);
        else qw.orderByDesc(Product::getSales);
        return baseMapper.selectPage(new Page<>(pageNum, pageSize), qw);
    }

    @Override
    public ProductVO getDetail(Long id) {
        Product p = baseMapper.selectById(id);
        if (p == null || p.getStatus() == 0) throw new RuntimeException("product not found");
        ProductVO vo = new ProductVO();
        BeanUtils.copyProperties(p, vo);

        var cat = productCategoryMapper.selectById(p.getCategoryId());
        if (cat != null) vo.setCategoryName(cat.getCategoryName());

        var farmer = farmerMapper.selectById(p.getFarmerId());
        if (farmer != null) vo.setFarmerName(farmer.getFarmerName());

        var images = productImageMapper.selectList(new LambdaQueryWrapper<ProductImage>()
                .eq(ProductImage::getProductId, id).orderByAsc(ProductImage::getSort));
        vo.setImages(images.stream().map(ProductImage::getImageUrl).collect(Collectors.toList()));

        return vo;
    }
}
