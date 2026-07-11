package com.freshtrace.unified.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.freshtrace.unified.dto.CommentCreateDTO;
import com.freshtrace.unified.dto.CommentVO;
import com.freshtrace.unified.entity.ProductComment;

public interface CommentService extends IService<ProductComment> {
    Page<CommentVO> listByProduct(Long productId, Integer pageNum, Integer pageSize, Integer rating);
    void create(CommentCreateDTO dto);
    Page<CommentVO> myComments(Integer pageNum, Integer pageSize);
}
