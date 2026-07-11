package com.freshtrace.unified.dto;

import lombok.Data;
import java.util.List;

@Data
public class HomeVO {
    private List<BannerItem> banners;
    private List<CategoryItem> categories;
    private List<ProductVO> recommendProducts;
    private List<ProductVO> newProducts;
    private List<ProductVO> hotProducts;

    @Data
    public static class BannerItem {
        private Long id;
        private String title;
        private String imageUrl;
        private Integer linkType;
        private Long linkId;
        private String linkUrl;
    }

    @Data
    public static class CategoryItem {
        private Long id;
        private String categoryName;
        private String categoryIcon;
        private List<CategoryItem> children;
    }
}
