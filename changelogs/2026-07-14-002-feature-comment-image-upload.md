# Changelog

本文档遵循 [Keep a Changelog](https://keepachangelog.com/zh-CN/1.1.0/) 规范。
本项目版本号尚未正式发布,本条目在合并到 `master` 后归入首个 `Unreleased` 段。

## [Unreleased]

### Fixed
- **评论列表中文乱码(`?????`)** — 根因:`product_comment.content` 列的脏数据
  (charset 切换前写的)。MySQL server 已是 utf8mb4,JDBC 连接 `connectionCollation=utf8mb4_unicode_ci`
  + Hikari `connection-init-sql: "SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci"`
  双重保险,新写评论中文正常(用 node 客户端测 `客户端测试中文-好吃` HEX=`E5AEA2E688B7E7ABAFE6B58BE8AF95E4B8ADE696872DE5A5BDE59083` 完整存进库)。
  已 DELETE 一条历史脏数据(id=2076895472353513473,content=`?????? 2026-07-14`)。
  注意:之前用 PowerShell `Invoke-RestMethod` + `ConvertTo-Json` 测的也是 `??`,
  是 PowerShell 把 JSON 编成 UTF-16 LE、Tomcat 收到后做了 `?` 替换,
  **不是 application bug**——前端 axios 用 `Content-Type: application/json;charset=UTF-8`
  不会有这个问题。
- **评论状态过滤方向反了 + UX 差** — 最早我写的 `.ne(status, 1)`(排除已通过的)
  改成 `.eq(status, 1)`(只显示已通过)后,用户提交的新评论(默认 `status=0` 待审核)
  自己也看不到,体验很迷。
  最终方案:`.ne(status, 2)` —— 显示待审核(0) + 已通过(1),只过滤掉被拒绝的(2)。
  这样用户提交后立刻能看到自己那条,管理员拒绝掉的才隐藏。
  约定:`0=待审核, 1=已通过, 2=已拒绝`,跟管理后台 CommentList 的展示标签一致。
- **前端 el-upload 图片丢失**(超隐蔽 bug) — 提交评论时,虽然上传图片 `option.file.url`
  被设上了,但 `commentForm.fileList.filter(f => f.url)` 过滤时拿到的是空数组,
  后端收到的 `images: []`,评论存了但没图。
  根因:el-upload 内部维护的 file 对象不一定走 Vue 的 reactive 代理,
  在自定义 `:http-request` 回调里给 `option.file.url` 赋值,
  `filter(f => f.url)` 读到的可能是老对象。
  修法:用独立的 `commentForm.uploadedUrls` 数组跟踪已上传成功的 URL,
  提交时直接读这个数组,避开 el-upload 内部 file 对象的 reactivity 问题;
  `removeCommentImage` 时同步从 `uploadedUrls` 移除。
- **`/api/notice/unread-count` 仍被 AuthInterceptor exclude** — 跟 2d6bca1 同源,
  本 worktree 从 master 拉出时是旧配置,导致顶部铃铛 unread 数永远 0。
  本次顺手从 `excludePathPatterns` 移除。

### Added
- **商品评论支持上传图片(用户端)**
  商品详情页"写评价"弹窗新增 `<el-upload picture-card>`,支持多图上传(最多 6 张,
  每张 ≤ 5MB),上传成功后图片以小方块形式展示在弹窗内,提交评论时随 `images`
  字段(URL 数组)一并提交到后端。评论列表中已有图片的评论会在文字下方
  展示 80×80 缩略图,点击可放大预览。
- **管理后台评价列表显示图片(管理端)**
  `frontend/src/views/admin/CommentList.vue` 在"评论内容"列右侧新增"图片"列,
  用 `el-image` 渲染前 3 张图(40×40 缩略图),超出显示 `+N`,
  点击触发 `preview-src-list` 预览大图。

### Changed
- **`src/main/java/com/freshtrace/unified/controller/CommentController.java`**
  - 抽出 `extractImages(Object raw)` 私有方法,统一处理 `images` 字段
    (`List<String>` / `String` / `null` 三种输入),两端点共用;
  - `POST /api/comment/direct` 从写死 `setImages("")` 改为
    `setImages(extractImages(body.get("images")))`,与 `/add` 端点行为对齐;
  - `GET /api/comment/product/{productId}` 公开评论列表过滤条件
    从 `.ne(status, 1)` 改为 `.eq(status, 1)` —— 只显示已通过审核的;
  - 新增 `enrichWithUser(List<ProductComment>)` 私有方法,批量 join `sys_user`
    表补 `nickname` / `username` / `avatar`,避免前端展示"匿名用户";
    评论作者开了匿名(isAnonymous=1)时强制覆盖 nickname 为"匿名用户";
  - `productComments` / `myComments` 返回类型从 `List<ProductComment>` 改为
    `List<Map<String, Object>>`,因为要带 user 信息(原 entity 没有 user 字段)。
- **`src/main/java/com/freshtrace/unified/config/WebMvcConfig.java`**
  - 从 `excludePathPatterns` 中移除 `"/api/notice/unread-count"`(同 2d6bca1)。
- **`src/main/resources/application.yml`**
  - JDBC URL 增加 `connectionCollation=utf8mb4_unicode_ci`;
  - `spring.datasource.hikari` 增加 `connection-init-sql: "SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci"`。
    双重保险,确保新连接 character_set_client/connection/results 都是 utf8mb4,
    不会被 Connector/J 8.x 的 `SET NAMES utf8` 退化到 3 字节 utf8。
- **`frontend/src/views/user/ProductDetail.vue`**
  - 弹窗内 `<textarea>` 下新增图片上传区块;
  - `commentForm` 增加 `fileList: []` 字段保存已上传图片;
  - 新增 `beforeCommentUpload` / `uploadCommentImage` / `removeCommentImage`
    三个上传相关函数,`uploadCommentImage` 调后端
    `POST /api/upload/product-image` 拿 `imageUrl` 存到 file.url
    (el-upload 会用它渲染缩略图);
  - `submitComment` 在提交前过滤出 `fileList` 里 url 非空的项
    作为 `images` 数组发给后端;成功后重置 `fileList`;
  - 评论列表项 `c.content` 下方增加 `<el-image>` 缩略图列表
    (逗号分隔的 `c.images` 拆开渲染,点击触发预览大图)。
- **`frontend/src/views/admin/CommentList.vue`**
  - "评论内容"列右侧新增"图片"列(宽 120px),渲染 `row.images` 拆开的缩略图数组;
    超过 3 张显示 `+N` 提示;无图显示灰色 `-`。

### Notes
- 本次改动**不涉及数据库表结构**,`product_comment.images` 字段本来就存在
  (类型 `VARCHAR(1000)`,collation `utf8mb4_unicode_ci`),
  后端 `setImages(extractImages(body.get("images")))` 写入,前端按逗号分隔读,
  前后端约定一致。
- 上传文件走 `POST /api/upload/product-image`,5MB 限制 + MIME + 魔数三重
  校验已经存在(UploadController.validateImageUpload),不需要新增校验。
- 上传文件落地在 `${user.dir}/uploads/products/`,URL 形如
  `/uploads/products/{uuid}.{ext}`,由 Spring Boot `classpath:/static/`
  + `file:./uploads/` 静态资源服务暴露(详见 WebMvcConfig)。
- **MyBatis-Plus 全局 `logic-delete-field: deleted` 是"半对的"配置**:
  `product_comment` 表确实有 `deleted` 列(默认 0),所以 `commentService.removeById()`
  会自动 `UPDATE ... SET deleted=1` 而不是真删。但其他没 `deleted` 列的表
  (比如 `sys_notice`)遇到这个全局配置时,MySQL 会返回 NULL 给 `deleted=0` 的判断,
  `NULL=0` 是 unknown,但因为 `MyBatis-Plus` 默认对 unknown 也当 false,
  所以这些表的 query 会**不过滤**(相当于 `logic-delete-field` 没用)。
  不算 bug,但配置不严谨。本次未处理,记录在此供后续关注。
- **Charset 修复踩坑总结**(给未来的我):
  1. 改 MySQL `my.ini` 是不够的,JDBC 连接也要 `SET NAMES utf8mb4`;
  2. MySQL Connector/J 8.x 的 `characterEncoding=utf-8` 会被翻译成 `SET NAMES utf8`(3 字节),
     写 3 字节的中文 BMP 字符理论上应该 OK,但实测仍然被替换成 `?` —— 安全做法是
     `connectionCollation=utf8mb4_unicode_ci` + Hikari `connection-init-sql: SET NAMES utf8mb4` 双重保险;
  3. **PowerShell 客户端测接口是个陷阱**:`ConvertTo-Json` 默认 UTF-16 LE,
     Tomcat 接收到后会把无效字节替换成 `?`,导致测试时误以为是 application bug。
     用 `node` / `curl` / 浏览器 DevTools 才靠谱。
- **el-upload 自定义 http-request 踩坑总结**:
  不要依赖 `option.file.url = response.imageUrl` 来传递已上传的 URL 给提交逻辑。
  el-upload 内部 file 对象在自定义回调里设置属性,Vue 的 reactive 可能追踪不到,
  提交时 `filter(f => f.url)` 拿到空数组。**用独立的数组自己跟踪已上传的 URL**。
- **顺手修了一个 admin 商品管理 bug**:
  `GET /api/admin/product/{id}` 后端返回 `{product, images}` 包装结构,
  但前端 `getProductDetail` 直接 `res.data` 当 product 对象用,导致:
  - ProductEdit.vue 编辑页 form 字段全 undefined
  - ProductList.vue 详情 dialog value 列全空
  - 接着 `getProductImages(undefined)` 触发后端 `Long` 转换失败
    (`For input string: "undefined"`)
  修法:`getProductDetail` 在 admin.js API 层解包 `res.data.product`,
  调用方透明。后端不动(保持契约稳定)。
- 仍待办(本次未处理):
  1. 评论图片没有水印 / 压缩,大图直接展示,后续可加 ImageMagick / Thumbnailator 压缩;
  2. 全局 `logic-delete-field: deleted` 对没 deleted 列的表是噪声配置,
     可改为只对需要的 entity 用 `@TableLogic` 注解,关掉全局;
  3. 管理后台删除评论的语义(改 status=1 vs 改 deleted=1)未统一,本次只用了 status。

## 回滚步骤

如本修复引入问题,可在 worktree 内:
```bash
cd D:\SHIXI\untitled-master\.worktrees\fix-comment-image-upload
git restore src/main/java/com/freshtrace/unified/controller/CommentController.java
git restore src/main/java/com/freshtrace/unified/config/WebMvcConfig.java
git restore src/main/resources/application.yml
git restore frontend/src/views/user/ProductDetail.vue
git restore frontend/src/views/admin/CommentList.vue
git restore pom.xml
git restore changelogs/2026-07-14-002-feature-comment-image-upload.md
```
即可回退到改动前状态。如已 commit,改为:
```bash
git revert HEAD
```

## 涉及文件

| 文件 | 行数变化 |
|---|---|
| `src/main/java/com/freshtrace/unified/controller/CommentController.java` | +60 -10 |
| `src/main/java/com/freshtrace/unified/config/WebMvcConfig.java` | +1 -1 |
| `src/main/resources/application.yml` | +6 -1 |
| `frontend/src/views/user/ProductDetail.vue` | +57 -3 |
| `frontend/src/views/admin/CommentList.vue` | +24 -1 |
| `pom.xml` | (lombok 1.18.46 + `<release>17</release>`) |
| `changelogs/2026-07-14-002-feature-comment-image-upload.md` | 新增 |
