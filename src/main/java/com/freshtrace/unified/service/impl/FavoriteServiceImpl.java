package com.freshtrace.unified.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.freshtrace.unified.common.UserContext;
import com.freshtrace.unified.dto.ProductVO;
import com.freshtrace.unified.entity.Product;
import com.freshtrace.unified.entity.ProductFavorite;
import com.freshtrace.unified.mapper.ProductFavoriteMapper;
import com.freshtrace.unified.mapper.ProductMapper;
import com.freshtrace.unified.service.FavoriteService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    @Autowired private ProductFavoriteMapper productFavoriteMapper;
    @Autowired private ProductMapper productMapper;

    @Override
    @Transactional
    public void toggle(Long productId) {
        ProductFavorite exist = productFavoriteMapper.selectOne(new LambdaQueryWrapper<ProductFavorite>()
                .eq(ProductFavorite::getUserId, UserContext.getUserId())
                .eq(ProductFavorite::getProductId, productId));
        Product p = productMapper.selectById(productId);
        if (exist != null) {
            productFavoriteMapper.deleteById(exist.getId());
            if (p != null) { p.setCollectCount(Math.max(0, (p.getCollectCount() != null ? p.getCollectCount() : 1) - 1)); productMapper.updateById(p); }
        } else {
            ProductFavorite fav = new ProductFavorite();
            fav.setUserId(UserContext.getUserId());
            fav.setProductId(productId);
            fav.setCreateTime(LocalDateTime.now());
            productFavoriteMapper.insert(fav);
            if (p != null) { p.setCollectCount((p.getCollectCount() != null ? p.getCollectCount() : 0) + 1); productMapper.updateById(p); }
        }
    }

    @Override
    public List<ProductVO> list() {
        List<ProductFavorite> favs = productFavoriteMapper.selectList(new LambdaQueryWrapper<ProductFavorite>()
                .eq(ProductFavorite::getUserId, UserContext.getUserId()).orderByDesc(ProductFavorite::getCreateTime));
        return favs.stream().map(f -> {
            Product p = productMapper.selectById(f.getProductId());
            if (p == null) return null;
            ProductVO vo = new ProductVO();
            BeanUtils.copyProperties(p, vo);
            return vo;
        }).filter(vo -> vo != null).collect(Collectors.toList());
    }
}
