package com.freshtrace.unified.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freshtrace.unified.entity.ProductQrcode;
import com.freshtrace.unified.mapper.ProductQrcodeMapper;
import com.freshtrace.unified.service.ProductQrcodeService;
import org.springframework.stereotype.Service;
@Service
public class ProductQrcodeServiceImpl extends ServiceImpl<ProductQrcodeMapper, ProductQrcode> implements ProductQrcodeService {
}
