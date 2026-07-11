package com.freshtrace.unified.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freshtrace.unified.entity.ProductFavorite;
import com.freshtrace.unified.mapper.ProductFavoriteMapper;
import com.freshtrace.unified.service.ProductFavoriteService;
import org.springframework.stereotype.Service;

@Service
public class ProductFavoriteServiceImpl extends ServiceImpl<ProductFavoriteMapper, ProductFavorite> implements ProductFavoriteService {
}
