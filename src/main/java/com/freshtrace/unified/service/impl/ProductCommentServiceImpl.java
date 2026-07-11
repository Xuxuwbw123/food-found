package com.freshtrace.unified.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freshtrace.unified.entity.ProductComment;
import com.freshtrace.unified.mapper.ProductCommentMapper;
import com.freshtrace.unified.service.ProductCommentService;
import org.springframework.stereotype.Service;

@Service
public class ProductCommentServiceImpl extends ServiceImpl<ProductCommentMapper, ProductComment> implements ProductCommentService {
}
