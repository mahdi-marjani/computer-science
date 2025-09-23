# save as json_to_csv_tag_unit_a.py
import json
import re
from pathlib import Path
import pandas as pd

# ---------- تنظیمات ----------
json_path = Path("table_data_clean_final(for-me).json")   # نام فایل JSON ورودی (خروجیِ JS)
out_tagged_csv = Path("table_data_tagged.csv")
out_real_csv   = Path("table_data_only_real.csv")

# ---------- بارگذاری ----------
data = json.loads(json_path.read_text(encoding="utf-8"))
rows = data.get("rows") if isinstance(data, dict) else data
if not isinstance(rows, list):
    raise SystemExit("فرمت JSON غیرمنتظره است: انتظار rows به صورت لیست داشتم.")

df = pd.DataFrame(rows)

# ---------- پیدا کردن ستونِ 'واحد_ع' (یا معادل‌های ممکن) ----------
def detect_unit_a_column(columns):
    # اول سعی کن نام دقیق یا شبیه 'واحد_ع' پیدا کنی
    for c in columns:
        if re.search(r'واحد[_\s\-]*ع', c) or c.strip() == 'واحد_ع':
            return c
    # سپس نام تک‌حرفی 'ع'
    for c in columns:
        if c.strip() == 'ع':
            return c
    # سپس هر ستون که فقط شامل حرف 'ع' در کلمه باشد
    for c in columns:
        if re.search(r'\bع\b', c):
            return c
    return None

unit_a_col = detect_unit_a_column(df.columns.tolist())

# ---------- تشخیص فیک بر اساس واحد_ع خالی ----------
# منطق: اگر ستون unit_a وجود داشته باشد => ردیف فیک است اگر مقدار آن خالی باشد.
# اگر ستون وجود نداشت => fallback: ردیف‌هایی که "شماره و گروه درس" == "معادل" یا خالی باشند را فیک بگیر.
def is_empty_like(x):
    return (x is None) or (str(x).strip() == '')

if unit_a_col:
    df['_unit_a_col_used'] = unit_a_col
    df['is_fake'] = df[unit_a_col].apply(lambda v: is_empty_like(v))
    df['fake_reason'] = df.apply(
        lambda r: ("unit_a_empty" if is_empty_like(r.get(unit_a_col)) else ""),
        axis=1
    )
else:
    # fallback heuristic
    id_col = None
    # سعی کن ستون شناسه را پیدا کنی
    for cand in ["شماره و گروه درس", "شماره", "id"]:
        if cand in df.columns:
            id_col = cand
            break
    if id_col is None:
        id_col = df.columns[0] if len(df.columns) else None

    df['_unit_a_col_used'] = ''
    df['is_fake'] = df[id_col].apply(lambda v: is_empty_like(v) or (str(v).strip() == 'معادل'))
    df['fake_reason'] = df.apply(
        lambda r: ("no_unit_a_column; id_equivalent_or_empty" if is_empty_like(r.get(id_col)) or str(r.get(id_col)).strip()=='معادل' else "no_unit_a_column"),
        axis=1
    )

# ---------- (اختیاری) اگر خواستی ردیف‌های continuation ای که 'معادل' هستند را هم به ردیف قبلی بچسبانیم ----------
# این بخش اختیاری است. الان فقط تگ می‌کنیم؛ اگر خواستی ادغام واقعی انجام بدم بگو.
# ---------- ذخیره CSV ----------
df.to_csv(out_tagged_csv, index=False, encoding='utf-8-sig')
# فقط ردیف‌های غیر فیک
df_real = df[~df['is_fake']].copy()
df_real.to_csv(out_real_csv, index=False, encoding='utf-8-sig')

# ---------- گزارش خلاصه ----------
total = len(df)
fake_count = int(df['is_fake'].sum())
print(f"Total rows: {total}")
print(f"Marked as fake (is_fake==True): {fake_count}")
print(f"Wrote: {out_tagged_csv} (all rows, tagged) and {out_real_csv} (only non-fake rows).")
if unit_a_col:
    print(f"Detected unit_a column: '{unit_a_col}' (used to mark fakes by emptiness).")
else:
    print("No unit_a column detected; used 'شماره و گروه درس' fallback (mark rows with 'معادل' or empty id as fake).")

# نمایش چند نمونه برای بررسی سریع (در محیط تعاملی)
print("\nSample fake rows:")
print(df[df['is_fake']].head(6).to_string(index=False))

print("\nSample non-fake rows:")
print(df_real.head(6).to_string(index=False))
