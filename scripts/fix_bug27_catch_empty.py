#!/usr/bin/env python3
"""Bug #27 fix: 只把"操作型 catch {}"改为提示错误
   保留"加载型 catch {}"静默（页面加载失败静默是可接受的）

  操作型判据：try 块里出现 ElMessage.success() 但 catch 块吞了错
"""
import re
from pathlib import Path

# 操作型 —— 必须提示错误
OP_FILES = [
    ("frontend/src/views/admin/AdminManage.vue", [72, 76]),
    ("frontend/src/views/admin/ConfigCenter.vue", [36]),
    ("frontend/src/views/admin/CouponManage.vue", [69]),
    ("frontend/src/views/admin/ProductList.vue", [117, 118]),
    ("frontend/src/views/admin/MarketingManage.vue", [155]),
    ("frontend/src/views/admin/MemberManage.vue", [92]),
    ("frontend/src/views/admin/UserList.vue", [140, 143]),
    ("frontend/src/views/admin/TraceAudit.vue", [81]),
    ("frontend/src/views/farmer/Dashboard.vue", [245]),
    ("frontend/src/views/farmer/TraceDetail.vue", [290, 301]),
]

ROOT = Path("/home/sun/freshtrack-marketplace")

REPLACEMENT = "} catch (e) { ElMessage.error(e?.response?.data?.message || '操作失败，请重试') }"

total_changed = 0
for rel, lines in OP_FILES:
    fpath = ROOT / rel
    if not fpath.exists():
        print("MISSING: " + str(fpath))
        continue
    src = fpath.read_text(encoding="utf-8")
    lines_arr = src.split("\n")
    for lnum in lines:
        idx = lnum - 1
        if idx >= len(lines_arr): continue
        line = lines_arr[idx]
        # 精确替换 "} catch {}" 为 REPLACEMENT
        if "} catch {}" in line:
            new_line = line.replace("} catch {}", REPLACEMENT)
            lines_arr[idx] = new_line
            total_changed += 1
            print("[OK] " + rel + ":" + str(lnum))
        else:
            # 多行 catch {} —— 需 look-around
            if idx+1 < len(lines_arr) and lines_arr[idx].rstrip().endswith("} catch {}"):
                pass
            else:
                print("[SKIP] " + rel + ":" + str(lnum) + " —— " + line[:80])
    fpath.write_text("\n".join(lines_arr), encoding="utf-8")

print("\n共修改 " + str(total_changed) + " 处")
