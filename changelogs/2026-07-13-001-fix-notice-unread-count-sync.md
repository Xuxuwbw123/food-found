# Changelog

本文档遵循 [Keep a Changelog](https://keepachangelog.com/zh-CN/1.1.0/) 规范。
本项目版本号尚未正式发布,本条目在合并到 `master` 后归入首个 `Unreleased` 段。

## [Unreleased]

### Fixed
- **顶部铃铛未读数与消息中心不同步**
  顶部导航栏( `frontend/src/layouts/UserLayout.vue` )和消息中心页面
  ( `frontend/src/views/user/NoticeCenter.vue` )原先各维护一份独立的
  `unreadCount` ref,互不通信,导致:
  1. 在消息中心点开单条消息或点"全部已读"后,顶部铃铛的徽标不会变化;
  2. 切到 `/notices` 路由时,顶部铃铛不重新拉取后端,可能停留在登录时的旧值;
  3. 顶部 `watch(route.path)` 中存在 `if (newPath !== '/notices')` 的跳过逻辑,加剧上述问题。

  现将 `unreadCount` 提升为 `useAuthStore` 中的全局 state,顶部铃铛与消息中心
  共用同一份响应式数据;铃铛与消息中心任何一侧的状态变更都会同步到另一侧。

### Changed
- **`frontend/src/stores/auth.js`**
  新增三个 action:
  - `loadUnreadCount()`:从后端 `/api/notice/unread-count` 拉取未读数;未登录时清零;
  - `decrementUnreadCount()`:未读数自减 1(有 0 守卫,不会变负);
  - `resetUnreadCount()`:未读数置 0。
  `logout()` 时同时将 `unreadCount` 重置为 0,避免登出后残值被其他组件读到。

- **`frontend/src/layouts/UserLayout.vue`**
  - 移除本地 `unreadCount` ref,改读 `auth.unreadCount`;
  - 移除本地 `loadUnreadCount()` 函数,改用 `auth.loadUnreadCount()`;
  - 移除 `import axios from 'axios'`(本文件不再直接发请求);
  - `watch(route.path)` 改为任何路径变化都触发刷新,不再跳过 `/notices`;
  - 30s 轮询继续保留,作为后端 WebSocket 推送缺位时的兜底。

- **`frontend/src/views/user/NoticeCenter.vue`**
  - 移除本地 `unreadCount` ref,改读 `auth.unreadCount`;
  - 模板中"全部已读"按钮的 `:disabled` 改用 `auth.unreadCount`;
  - `loadData()` 末尾的 `axios.get('/api/notice/unread-count')` 替换为
    `await auth.loadUnreadCount()`,减少重复请求;
  - `readAll()` 在调接口后先本地 `auth.resetUnreadCount()`(不等后端),
    再 `loadData()` 重新拉列表;
  - `clickNotice()` 把 `unreadCount.value--` 改为 `auth.decrementUnreadCount()`。

- **`.gitignore`**
  - 新增 `frontend/dist/` —— 阻止 vite 每次 build 时 3000+ 个 bundle 文件污染
    `git status`;
  - 新增 `frontend/package-lock.json` —— `npm install` 时会改它,跟 `dist/` 同样
    视作构建产物不入库(团队需要时可由 CI 锁定版本,本仓库目前尚未配置)。

### Notes
- 本次改动**不涉及后端 API**,`/api/notice/unread-count`、`/api/notice/read/:id`、
  `/api/notice/read-all` 三个接口契约保持不变。
- 顺带把 `frontend/dist/` 和 `frontend/package-lock.json` 加入 `.gitignore`,
  避免后续 `npm run build` 时 3000+ 个构建产物污染 `git status`(本次为同一 commit,
  因为两者都属"清理仓库状态"范畴)。
- **遗留问题**(本次未处理,留作后续工单):
  1. 新订单产生的通知仍依赖前端 30s 轮询才能在铃铛上反映出来,后端尚未接入
     WebSocket / SSE 实时推送,极端情况下新消息最坏延迟 30s;
  2. `frontend/src/assets/`,`logs/` 等本地调试产物仍未在 `.gitignore` 中显式列出,
     后续 `git stash` 可能误带这些未跟踪文件,建议另起一次 `chore:` commit 处理。

## 回滚步骤

如本修复引入问题,可在 worktree 内:
```bash
cd D:\SHIXI\untitled-master\.worktrees\fix-notice-unread-count-sync
git restore frontend/src/stores/auth.js \
              frontend/src/layouts/UserLayout.vue \
              frontend/src/views/user/NoticeCenter.vue
git restore changelogs/2026-07-13-001-fix-notice-unread-count-sync.md
```
即可回退到改动前状态。如已 commit,改为:
```bash
git revert HEAD
```

## 涉及文件

| 文件 | 行数变化 |
|---|---|
| `frontend/src/stores/auth.js` | +22 -1 |
| `frontend/src/layouts/UserLayout.vue` | +6 -12 |
| `frontend/src/views/user/NoticeCenter.vue` | +9 -6 |
| `.gitignore` | +2 |
| `changelogs/2026-07-13-001-fix-notice-unread-count-sync.md` | 新增 |
