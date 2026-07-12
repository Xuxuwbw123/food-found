"""Bug #11 · 明文密码批量加密到 BCrypt
读明文 → 生成 BCrypt hash → UPDATE 数据库
用户下次登录用原密码即可（因为 hash 是原密码算的）
"""
import bcrypt, pymysql

MYSQL = dict(host="127.0.0.1", user="root", password="123456",
             database="fresh_trace_shop", charset="utf8mb4")

conn = pymysql.connect(**MYSQL)
cur = conn.cursor()
cur.execute("SELECT id, username, password FROM sys_user WHERE password NOT LIKE '$2%'")
rows = cur.fetchall()
print("需要加密的账户数：" + str(len(rows)))

for uid, uname, plain in rows:
    # Java hutool BCrypt.gensalt() 默认 cost=10, 前缀 $2a$
    salt = bcrypt.gensalt(rounds=10, prefix=b"2a")
    hashed = bcrypt.hashpw(plain.encode("utf-8"), salt).decode("utf-8")
    cur.execute("UPDATE sys_user SET password=%s WHERE id=%s", (hashed, uid))
    print("[OK] " + uname + " (id=" + str(uid) + ") 原密码=" + plain + " -> " + hashed[:29] + "...")

conn.commit()

# 验证
cur.execute("SELECT username, password FROM sys_user WHERE password NOT LIKE '$2%'")
remaining = cur.fetchall()
print("\n剩余明文账户数：" + str(len(remaining)))
if remaining:
    for r in remaining:
        print("  " + str(r))
cur.close()
conn.close()
