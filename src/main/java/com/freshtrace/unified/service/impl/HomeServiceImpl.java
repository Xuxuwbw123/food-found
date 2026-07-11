package com.freshtrace.unified.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.freshtrace.unified.dto.HomeVO;
import com.freshtrace.unified.dto.ProductVO;
import com.freshtrace.unified.entity.Banner;
import com.freshtrace.unified.entity.Product;
import com.freshtrace.unified.entity.ProductCategory;
import com.freshtrace.unified.mapper.BannerMapper;
import com.freshtrace.unified.mapper.ProductCategoryMapper;
import com.freshtrace.unified.mapper.ProductMapper;
import com.freshtrace.unified.service.HomeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class HomeServiceImpl implements HomeService {

    @Autowired private BannerMapper bannerMapper;
    @Autowired private ProductCategoryMapper productCategoryMapper;
    @Autowired private ProductMapper productMapper;

    @Override
    public HomeVO getHomeData() {
        HomeVO home = new HomeVO();

        List<Banner> banners = bannerMapper.selectList(new LambdaQueryWrapper<Banner>()
                .eq(Banner::getStatus, 1).orderByAsc(Banner::getSort));
        home.setBanners(banners.stream().map(b -> {
            HomeVO.BannerItem item = new HomeVO.BannerItem();
            BeanUtils.copyProperties(b, item);
            return item;
        }).collect(Collectors.toList()));

        List<ProductCategory> cats = productCategoryMapper.selectList(new LambdaQueryWrapper<ProductCategory>()
                .eq(ProductCategory::getStatus, 1).orderByAsc(ProductCategory::getSort));
        Map<Long, List<ProductCategory>> group = new HashMap<>();
        for (ProductCategory c : cats) {
            Long pid = c.getParentId() != null ? c.getParentId() : 0L;
            group.computeIfAbsent(pid, k -> new ArrayList<>()).add(c);
        }
        home.setCategories(buildTree(group, 0L));

        LambdaQueryWrapper<Product> prodQ = new LambdaQueryWrapper<Product>()
                .eq(Product::getStatus, 1).eq(Product::getAuditStatus, 1);
        home.setRecommendProducts(mapProducts(productMapper.selectList(
                prodQ.clone().eq(Product::getIsRecommend, 1).last("limit 6"))));
        home.setNewProducts(mapProducts(productMapper.selectList(
                prodQ.clone().eq(Product::getIsNew, 1).last("limit 6"))));
        home.setHotProducts(mapProducts(productMapper.selectList(
                prodQ.clone().eq(Product::getIsHot, 1).last("limit 6"))));
        return home;
    }

    private List<ProductVO> mapProducts(List<Product> list) {
        return list.stream().map(p -> {
            ProductVO vo = new ProductVO();
            BeanUtils.copyProperties(p, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    private List<HomeVO.CategoryItem> buildTree(Map<Long, List<ProductCategory>> group, Long parentId) {
        return group.getOrDefault(parentId, new ArrayList<>()).stream().map(c -> {
            HomeVO.CategoryItem item = new HomeVO.CategoryItem();
            item.setId(c.getId());
            item.setCategoryName(c.getCategoryName());
            item.setCategoryIcon(c.getCategoryIcon());
            item.setChildren(buildTree(group, c.getId()));
            return item;
        }).collect(Collectors.toList());
    }
}
