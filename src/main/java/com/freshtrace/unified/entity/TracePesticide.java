package com.freshtrace.unified.entity;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data @TableName("trace_pesticide")
public class TracePesticide {
    @TableId(type = IdType.ASSIGN_ID) private Long id;
    private Long traceId;
    @JsonAlias({"use_date","useDate","sprayDate","spray_date"}) private LocalDate useDate;
    @JsonAlias({"pesticide_type","pesticideType"}) private String pesticideType;
    private String pesticideName;
    @JsonAlias({"pesticide_brand","pesticideBrand"}) private String pesticideBrand;
    private BigDecimal dosage;
    @JsonAlias({"dilution_ratio","dilutionRatio"}) private String dilutionRatio;
    @JsonAlias({"use_method","useMethod"}) private String useMethod;
    @JsonAlias({"safety_interval","safetyInterval","safeInterval"}) private Integer safetyInterval;
    private String description;
    private String operator;
    private String images;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}