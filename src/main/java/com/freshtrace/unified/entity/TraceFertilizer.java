package com.freshtrace.unified.entity;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data @TableName("trace_fertilizer")
public class TraceFertilizer {
    @TableId(type = IdType.ASSIGN_ID) private Long id;
    private Long traceId;
    @JsonAlias({"fertilize_date", "fertilizeDate"}) private LocalDate fertilizeDate;
    @JsonAlias({"fertilizer_type", "fertilizerType"}) private String fertilizerType;
    private String fertilizerName;
    @JsonAlias({"fertilizer_brand", "fertilizerBrand"}) private String fertilizerBrand;
    private BigDecimal dosage;
    @JsonAlias({"fertilize_method", "fertilizeMethod"}) private String fertilizeMethod;
    private String description;
    private String operator;
    private String images;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}