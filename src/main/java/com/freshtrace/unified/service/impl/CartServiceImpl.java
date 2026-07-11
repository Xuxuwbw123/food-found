package com.freshtrace.unified.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.freshtrace.unified.common.UserContext;
import com.freshtrace.unified.dto.CartItemDTO;
import com.freshtrace.unified.dto.CartUpdateDTO;
import com.freshtrace.unified.dto.CartVO;
import com.freshtrace.unified.entity.Product;
import com.freshtrace.unified.entity.ShoppingCart;
import com.freshtrace.unified.mapper.ProductMapper;
import com.freshtrace.unified.mapper.ShoppingCartMapper;
import com.freshtrace.unified.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    @Autowired private ShoppingCartMapper shoppingCartMapper;
    @Autowired private ProductMapper productMapper;

    @Override
    public List<CartVO> list() {
        List<ShoppingCart> items = shoppingCartMapper.selectList(new LambdaQueryWrapper<ShoppingCart>()
                .eq(ShoppingCart::getUserId, UserContext.getUserId())
                .orderByDesc(ShoppingCart::getCreateTime));
        return items.stream().map(item -> {
            CartVO vo = new CartVO();
            vo.setId(item.getId());
            vo.setProductId(item.getProductId());
            vo.setProductName(item.getProductName());
            vo.setProductImage(item.getProductImage());
            vo.setPrice(item.getPrice());
            vo.setQuantity(item.getQuantity());
            vo.setSelected(item.getSelected());
            Product p = productMapper.selectById(item.getProductId());
            vo.setStock(p != null ? p.getStock() : 0);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public void add(CartItemDTO dto) {
        ShoppingCart exist = shoppingCartMapper.selectOne(new LambdaQueryWrapper<ShoppingCart>()
                .eq(ShoppingCart::getUserId, UserContext.getUserId())
                .eq(ShoppingCart::getProductId, dto.getProductId()));
        if (exist != null) {
            exist.setQuantity(exist.getQuantity() + dto.getQuantity());
            shoppingCartMapper.updateById(exist);
        } else {
            Product p = productMapper.selectById(dto.getProductId());
            if (p == null) throw new RuntimeException("product not found");
            ShoppingCart cart = new ShoppingCart();
            cart.setUserId(UserContext.getUserId());
            cart.setProductId(p.getId());
            cart.setProductName(p.getProductName());
            cart.setProductImage(p.getMainImage());
            cart.setPrice(p.getPrice());
            cart.setQuantity(dto.getQuantity());
            cart.setSelected(1);
            cart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insert(cart);
        }
    }

    @Override
    public void update(Long id, CartUpdateDTO dto) {
        ShoppingCart item = shoppingCartMapper.selectById(id);
        if (item == null || !item.getUserId().equals(UserContext.getUserId())) {
            throw new RuntimeException("cart item not found");
        }
        if (dto.getQuantity() != null) item.setQuantity(dto.getQuantity());
        if (dto.getSelected() != null) item.setSelected(dto.getSelected());
        shoppingCartMapper.updateById(item);
    }

    @Override
    public void delete(Long id) {
        ShoppingCart item = shoppingCartMapper.selectById(id);
        if (item == null || !item.getUserId().equals(UserContext.getUserId())) {
            throw new RuntimeException("cart item not found");
        }
        shoppingCartMapper.deleteById(id);
    }

    @Override
    public void selectAll(Boolean selected) {
        List<ShoppingCart> items = shoppingCartMapper.selectList(new LambdaQueryWrapper<ShoppingCart>()
                .eq(ShoppingCart::getUserId, UserContext.getUserId()));
        items.forEach(i -> { i.setSelected(selected ? 1 : 0); shoppingCartMapper.updateById(i); });
    }
}
