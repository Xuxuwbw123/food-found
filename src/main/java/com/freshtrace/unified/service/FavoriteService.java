package com.freshtrace.unified.service;

import com.freshtrace.unified.dto.ProductVO;
import java.util.List;

public interface FavoriteService {
    void toggle(Long productId);
    List<ProductVO> list();
}
