package com.freshtrace.unified.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.freshtrace.unified.dto.ProductVO;
import com.freshtrace.unified.entity.Product;

public interface ProductService extends IService<Product> {
    Page<Product> list(Integer pageNum, Integer pageSize, Long categoryId, String keyword, String sort);
    ProductVO getDetail(Long id);
}
