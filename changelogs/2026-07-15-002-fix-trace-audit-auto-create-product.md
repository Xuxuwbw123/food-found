# Changelog

本文档遵循 [Keep a Changelog](https://keepachangelog.com/zh-CN/1.1.0/) 规范。
本项目版本号尚未正式发布,本条目在合并到 `master` 后归入首个 `Unreleased` 段。

## [Unreleased]

### Fixed
- **溯源批次审核通过后,商品管理里看不到对应商品**
  - 场景: 农户端点击"新增批次"填写信息 → 管理员端溯源批次审核 → 点"通过" → 商品管理列表里**没有**该批次对应的商品
  - 根因: 农户端 `Dashboard.vue addTrace()` 在新建批次时,只有 `productForm.price > 0` 才调
    `/api/admin/product/publish` 创建商品。`productForm` 默认 `price: 0`,
    多数农户不填价格 → 调不到 publish → 没创建 product → 批次审核通过后商品管理空白
  - 修法: 后端 `AdminController.traceAuditApprove` 加**兜底**——
    审核通过时检查 `product WHERE trace_id = ?`,若不存在则**自动创建一个默认商品**:
    - `status = 1` (上架,审核已通过)
    - `auditStatus = 1` (已通过)
    - `categoryId = 12` (默认分类,留待商户后续编辑)
    - `mainImage = /images/products/default.png`
    - `price = 0, stock = 0` (留待商户填)
    - `isTraceable = 1` (可溯源)
    - 已有则跳过(避免重复创建)
  - 后续可优化: 前端 `addTrace` 去掉 `if (productForm.price > 0)` 条件,统一让后端兜底

### Notes
- **本修法是"后端兜底",不修前端**: 前端 `addTrace` 的 `if (productForm.price > 0)` 仍存在,
  但即使农户没填价格、没主动调 publish,后端 traceAuditApprove 也会兜底创建商品
- 验证: 用户测试的"隔壁偷来的鸡蛋"批次 (id=2077222257720950786, auditStatus=1) 之前没创建 product
  (因农户没填价格),修后审核通过时应该自动建 product
- 仍待办:
  1. 端到端测试: 农户填批次(无价格) → 管理员通过 → 验证商品管理里出现该商品
  2. 兜底商品信息是默认的(分类 12,价格 0,主图 default),商户需要进商品管理**编辑**补全
  3. 前端 addTrace 的 `if (productForm.price > 0)` 可去掉(双保险,但要去重避免重复创建)

## 回滚步骤

如本修复引入问题,可在 worktree 内:
```bash
cd D:\SHIXI\untitled-master\.worktrees\fix-trace-audit-create-product
git restore src/main/java/com/freshtrace/unified/controller/AdminController.java
git restore changelogs/2026-07-15-002-fix-trace-audit-auto-create-product.md
```
即可回退到改动前状态。如已 commit,改为:
```bash
git revert HEAD
```

## 涉及文件

| 文件 | 行数变化 |
|---|---|
| `src/main/java/com/freshtrace/unified/controller/AdminController.java` | +35 -4 |
| `changelogs/2026-07-15-002-fix-trace-audit-auto-create-product.md` | 新增 |
