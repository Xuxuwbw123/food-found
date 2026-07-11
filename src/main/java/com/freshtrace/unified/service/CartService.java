package com.freshtrace.unified.service;

import com.freshtrace.unified.dto.CartItemDTO;
import com.freshtrace.unified.dto.CartUpdateDTO;
import com.freshtrace.unified.dto.CartVO;
import java.util.List;

public interface CartService {
    List<CartVO> list();
    void add(CartItemDTO dto);
    void update(Long id, CartUpdateDTO dto);
    void delete(Long id);
    void selectAll(Boolean selected);
}
