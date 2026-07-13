import os
import re

base = "D:/实习/项目后端/fresh-trace-unified/src/main/java/com/freshtrace/unified"

entities = {
    "TraceLocation": {"table": "trace_location", "fields": [("Long", "traceId"), ("String", "locationType"), ("String", "locationName"), ("java.math.BigDecimal", "latitude"), ("java.math.BigDecimal", "longitude"), ("String", "description"), ("Integer", "sort"), ("java.time.LocalDateTime", "createTime")]},
    "ProductQrcode": {"table": "product_qrcode", "fields": [("Long", "productId"), ("Long", "orderId"), ("String", "qrcodeContent"), ("String", "qrcodeImage"), ("Integer", "scanCount"), ("java.time.LocalDateTime", "firstScanTime"), ("String", "firstScanIp"), ("Integer", "status"), ("java.time.LocalDateTime", "createTime")]},
    "QrcodeScanLog": {"table": "qrcode_scan_log", "fields": [("Long", "qrcodeId"), ("Long", "userId"), ("String", "scanIp"), ("java.time.LocalDateTime", "scanTime")]},
    "GrowthTimeline": {"table": "growth_timeline", "fields": [("Long", "traceId"), ("String", "stage"), ("String", "title"), ("String", "content"), ("String", "imageUrls"), ("java.time.LocalDateTime", "updateTime"), ("java.time.LocalDateTime", "createTime")]},
    "FarmUpdate": {"table": "farm_update", "fields": [("Long", "farmerId"), ("String", "updateType"), ("String", "title"), ("String", "content"), ("String", "imageUrls"), ("String", "videoUrl"), ("java.time.LocalDateTime", "createTime")]},
    "QualificationCert": {"table": "qualification_cert", "fields": [("Long", "farmerId"), ("Long", "productId"), ("Long", "traceId"), ("String", "certType"), ("String", "certName"), ("String", "certImage"), ("String", "certNo"), ("String", "issueOrg"), ("java.time.LocalDate", "issueDate"), ("java.time.LocalDate", "expireDate"), ("Integer", "status"), ("java.time.LocalDateTime", "createTime")]},
    "ShopFollow": {"table": "shop_follow", "fields": [("Long", "userId"), ("Long", "farmerId"), ("java.time.LocalDateTime", "createTime")]},
    "HarvestCalendar": {"table": "harvest_calendar", "fields": [("Long", "farmerId"), ("String", "productName"), ("Long", "categoryId"), ("String", "season"), ("Integer", "harvestMonth"), ("String", "description"), ("String", "imageUrl"), ("Integer", "status"), ("java.time.LocalDateTime", "createTime")]},
    "HarvestSubscribe": {"table": "harvest_subscribe", "fields": [("Long", "userId"), ("Long", "calendarId"), ("Integer", "notified"), ("java.time.LocalDateTime", "createTime")]},
    "GroupBuy": {"table": "group_buy", "fields": [("Long", "productId"), ("java.math.BigDecimal", "groupPrice"), ("java.math.BigDecimal", "originalPrice"), ("Integer", "groupSize"), ("Integer", "currentCount"), ("Integer", "status"), ("java.time.LocalDateTime", "startTime"), ("java.time.LocalDateTime", "endTime"), ("java.time.LocalDateTime", "createTime")]},
    "GroupBuyRecord": {"table": "group_buy_record", "fields": [("Long", "groupBuyId"), ("Long", "userId"), ("Long", "orderId"), ("String", "status"), ("java.time.LocalDateTime", "createTime")]},
    "ThemeZone": {"table": "theme_zone", "fields": [("String", "name"), ("String", "description"), ("String", "bannerImage"), ("String", "bgColor"), ("String", "season"), ("String", "festival"), ("Integer", "status"), ("java.time.LocalDateTime", "startTime"), ("java.time.LocalDateTime", "endTime"), ("java.time.LocalDateTime", "createTime")]},
    "ThemeGoods": {"table": "theme_goods", "fields": [("Long", "themeId"), ("Long", "productId"), ("Integer", "sort")]},
    "MysteryBox": {"table": "mystery_box", "fields": [("String", "name"), ("String", "description"), ("java.math.BigDecimal", "price"), ("String", "imageUrl"), ("String", "categoryHint"), ("String", "avoidHint"), ("Integer", "status"), ("java.time.LocalDateTime", "createTime")]},
    "Recipe": {"table": "recipe", "fields": [("String", "title"), ("String", "description"), ("String", "coverImage"), ("String", "ingredients"), ("String", "steps"), ("Integer", "difficulty"), ("Integer", "cookTime"), ("String", "tags"), ("Integer", "status"), ("java.time.LocalDateTime", "createTime")]},
    "RecipeIngredient": {"table": "recipe_ingredient", "fields": [("Long", "recipeId"), ("Long", "productId"), ("String", "quantity"), ("Integer", "sort")]},
    "StockAlert": {"table": "stock_alert", "fields": [("Long", "productId"), ("Integer", "alertQuantity"), ("java.time.LocalDateTime", "lastNotified"), ("java.time.LocalDateTime", "createTime")]},
    "SalesStatistics": {"table": "sales_statistics", "fields": [("Long", "productId"), ("Long", "farmerId"), ("java.time.LocalDate", "statDate"), ("Integer", "quantity"), ("java.math.BigDecimal", "amount"), ("java.time.LocalDateTime", "createTime")]},
    "GreenPointsRule": {"table": "green_points_rule", "fields": [("String", "ruleName"), ("String", "actionType"), ("Integer", "points"), ("Integer", "status"), ("java.time.LocalDateTime", "createTime")]},
    "CharityCert": {"table": "charity_cert", "fields": [("Long", "userId"), ("String", "certNo"), ("String", "title"), ("String", "content"), ("String", "imageUrl"), ("java.math.BigDecimal", "totalAmount"), ("java.time.LocalDateTime", "createTime")]},
}

for name, config in entities.items():
    table = config["table"]
    fields = config["fields"]

    imports = ["com.baomidou.mybatisplus.annotation.*", "lombok.Data"]
    types = set(f[0] for f in fields)
    if any("BigDecimal" in t for t in types): imports.append("java.math.BigDecimal")
    if any("LocalDateTime" in t for t in types): imports.append("java.time.LocalDateTime")
    if any("LocalDate" in t for t in types): imports.append("java.time.LocalDate")

    import_lines = "\n".join(f"import {i};" for i in imports)
    field_lines = "\n".join(f"    private {t} {n};" for t, n in fields)

    entity_code = f"""package com.freshtrace.unified.entity;

{import_lines}

@Data
@TableName("{table}")
public class {name} {{
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
{field_lines}
}}
"""

    with open(f"{base}/entity/{name}.java", "w") as f:
        f.write(entity_code)

    mapper_code = f"""package com.freshtrace.unified.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.freshtrace.unified.entity.{name};
import org.apache.ibatis.annotations.Mapper;
@Mapper
public interface {name}Mapper extends BaseMapper<{name}> {{
}}
"""
    with open(f"{base}/mapper/{name}Mapper.java", "w") as f:
        f.write(mapper_code)

    svc_code = f"""package com.freshtrace.unified.service;
import com.baomidou.mybatisplus.extension.service.IService;
import com.freshtrace.unified.entity.{name};
public interface {name}Service extends IService<{name}> {{
}}
"""
    with open(f"{base}/service/{name}Service.java", "w") as f:
        f.write(svc_code)

    impl_code = f"""package com.freshtrace.unified.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freshtrace.unified.entity.{name};
import com.freshtrace.unified.mapper.{name}Mapper;
import com.freshtrace.unified.service.{name}Service;
import org.springframework.stereotype.Service;
@Service
public class {name}ServiceImpl extends ServiceImpl<{name}Mapper, {name}> implements {name}Service {{
}}
"""
    with open(f"{base}/service/impl/{name}ServiceImpl.java", "w") as f:
        f.write(impl_code)

print(f"Created {len(entities)} entities + mappers + services")
