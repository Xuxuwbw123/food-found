# Changelog

本文档遵循 [Keep a Changelog](https://keepachangelog.com/zh-CN/1.1.0/) 规范。
本项目版本号尚未正式发布,本条目在合并到 `master` 后归入首个 `Unreleased` 段。

## [Unreleased]

### Fixed
- **新增地址保存"成功"但实际没存**(用户报告:tmdd233 注册账号后新增地址,页面显示
  "保存成功"但前端列表不更新)
  - 根因:前端 `el-checkbox v-model` 给 `isDefault: false` (Boolean),后端 `UserAddress.isDefault`
    字段是 `Integer`,Jackson 反序列化 `false` → Integer 失败
  - 连锁反应:
    1. controller 抛 `HttpMessageNotReadableException`
    2. 全局 `@RestControllerAdvice` 把它包成 `Result.error()` → HTTP 200 + body `code: 500`
    3. 前端 `await axios.post()` 看到 HTTP 200 → 当成功
    4. `ElMessage.success('保存成功')` 触发,`loadData()` 调用
    5. GET 返回 `data: []` (DB 真的没存) → list 一直空
    6. 用户看到"保存成功" + 列表没更新,但不知道后端实际失败了
  - 修法:前端 `AddressList.vue handleSave()` 显式 `isDefault: form.isDefault ? 1 : 0`
  - 注意:即使把后端 `Integer isDefault` 改成 `Boolean` 也不够,Jackson 反序列化发生在
    controller 方法**之前**,所以前端必须发对类型

### Added
- **全局 axios 业务层拦截器**(`frontend/src/main.js`)
  - 在现有 401/refresh 拦截器**之前**注册,只对 HTTP 2xx 但 body `code !== 200` 的情况生效
  - 自动 `Promise.reject(new Error(data.message))` —— 之前 `await axios.post()` 不写 catch
    会当成功,现在 throw Error 会进用户 catch 分支
  - 白名单 `/auth/*` 路径(留给 `auth store` 自己处理,Login.vue 有详细错误分支)
  - 解决用户报告 bug 的**根本原因**:所有 vue 文件直接 `import axios` 而不用
    `utils/request.js`(后者已有类似拦截器),导致业务异常被当成功

### Notes
- **项目广泛问题**:`utils/request.js` 早就有完整 response interceptor,自动检查
  `code === 200` 才返回 data。但项目里几乎所有 vue 文件都直接 `import axios from 'axios'`,
  没走 request instance → 拦截器失效。本次新增全局拦截器覆盖了这种情况
- **未做**(本次未处理,留作 follow-up):
  1. 逐步把 `import axios from 'axios'` 改成 `import axios from '@/utils/request'`,
     让 `request.js` 里的 token 刷新和业务检查都生效
  2. 全局拦截器只 reject,没自动 `ElMessage.error` —— 留给用户代码自己弹,
     避免和已有的 `Login.vue` 等的错误展示逻辑重复
  3. 上传/地图/AI 等第三方 API 未来可能不返回 `Result<T>` 结构,
     如果发现要白名单(目前项目所有 API 都用 Result,无需白名单)

## 回滚步骤

如本修复引入问题,可在 worktree 内:
```bash
cd D:\SHIXI\untitled-master\.worktrees\fix-address-add-isdefault
git restore frontend/src/main.js frontend/src/views/user/AddressList.vue
git restore changelogs/2026-07-15-001-fix-address-isdefault-type-bug.md
```
即可回退到改动前状态。如已 commit,改为:
```bash
git revert HEAD
```

## 涉及文件

| 文件 | 行数变化 |
|---|---|
| `frontend/src/main.js` | +14 -1 |
| `frontend/src/views/user/AddressList.vue` | +6 -1 |
| `changelogs/2026-07-15-001-fix-address-isdefault-type-bug.md` | 新增 |
