(() => {
  // helper: پاک‌سازی متن
  const tx = s => {
    if (s === null || s === undefined) return '';
    return String(s).replace(/\u00A0/g, ' ').replace(/\r\n/g, '\n').replace(/\n{2,}/g, '\n').replace(/[ \t]{2,}/g, ' ').trim();
  };

  // header table
  const headerTable = document.querySelector('.npgrid-table-header table') || document.querySelector('.npgrid-table-header');
  if (!headerTable) {
    console.error('هیچ جدول هدر npgrid-table-header پیدا نشد.');
    return null;
  }

  // جمع‌آوری thها به صورت ردیف‌ها (DOM order). ما از پایین به بالا می‌نویسیم تا زیرستون‌ها (children) جای والدها را بگیرند.
  const headerRows = Array.from(headerTable.querySelectorAll('tr'));
  const headersByIndex = {}; // numeric index -> title

  for (let r = headerRows.length - 1; r >= 0; r--) {
    const ths = Array.from(headerRows[r].querySelectorAll('th[npindex]'));
    for (const th of ths) {
      const idxRaw = th.getAttribute('npindex');
      if (!idxRaw) continue;
      const idx = parseInt(idxRaw, 10);
      if (Number.isNaN(idx)) continue;
      const title = tx(th.innerText || th.textContent || '');
      // بازنویسی (زیرا از پایین به بالا می‌آییم) — زیرستون‌ها ارجح‌اند
      headersByIndex[idx] = title;
    }
  }

  // اگر بعضی اندیس‌ها بین 0 و max خالی بود، پرشان کن با رشته خالی
  const maxIdx = Math.max(...Object.keys(headersByIndex).map(x => parseInt(x,10)));
  const headerIndexList = [];
  for (let i = 0; i <= maxIdx; i++) {
    headerIndexList.push(i);
    if (!headersByIndex.hasOwnProperty(i)) headersByIndex[i] = '';
  }

  // ساخت آرایه نهایی هدرها (مرتب بر اساس اندیس)
  const headers = headerIndexList.map(i => headersByIndex[i] || '');

  // ---------- خواندن ردیف‌های بدنه ----------
  const bodyRows = Array.from(document.querySelectorAll('.npgrid-table-body tr.TR-NPGrid, .npgrid-table-body tr'));
  const getCellText = (cell) => {
    if (!cell) return '';
    // اگر جدول تو در تو دارد، آن را به خطوط مرتب تبدیل کن
    const inner = cell.querySelector('table');
    if (inner) {
      const parts = [];
      for (const tr of Array.from(inner.rows)) {
        const tds = Array.from(tr.cells).map(td => tx(td.innerText || td.textContent));
        if (tds.length === 2 && tds[0]) parts.push(`${tds[0]}: ${tds[1]}`);
        else if (tds.length) parts.push(tds.filter(Boolean).join(' | '));
      }
      const joined = parts.filter(Boolean).join('\n');
      return joined || tx(cell.innerText || cell.textContent);
    }
    return tx(cell.innerText || cell.textContent);
  };

  const rows = bodyRows.map((r) => {
    // فقط tdهای لایه بالا
    let cells = [];
    try {
      cells = Array.from(r.querySelectorAll(':scope > td'));
    } catch (e) {
      // بعضی مرورگرها ممکن است :scope پشتیبانی نکنند — fallback
      cells = Array.from(r.children).filter(ch => ch.tagName && ch.tagName.toLowerCase() === 'td');
    }

    const obj = {};
    // اگر تعداد سلول‌ها با تعداد هدرها متفاوت بود، ما سلول i را به headerIndexList[i] نگاشت می‌کنیم
    for (let i = 0; i < Math.max(headerIndexList.length, cells.length); i++) {
      const hdrIdx = headerIndexList[i] !== undefined ? headerIndexList[i] : i;
      const key = headers[hdrIdx] || `col${hdrIdx+1}`;
      obj[key] = getCellText(cells[i]);
    }
    return obj;
  });

  // ---------- پس‌پردازش: تبدیل 'کل' و 'ع' به واحد_کل/واحد_ع اگر خواسته باشی ----------
  // اگر هدرها شامل 'کل' و 'ع' هستند، آنها را به واحد_کل و واحد_ع منتقل کن و ستون‌های قدیمی را حذف کن
  const hasKol = headers.includes('کل');
  const hasA   = headers.includes('ع');
  rows.forEach(obj => {
    if (hasKol || hasA) {
      const kol = hasKol ? (obj['کل'] || '') : '';
      const a   = hasA   ? (obj['ع']  || '') : '';
      // اگر ستون 'واحد' وجود داشته باشد و 'کل' خالی باشد، از 'واحد' استفاده کن
      const vahed = obj['واحد'] || '';
      obj['واحد_کل'] = tx(kol) || tx(vahed) || '';
      obj['واحد_ع']  = tx(a) || '';
      delete obj['کل'];
      delete obj['ع'];
      delete obj['واحد'];
    }
  });

  // ---------- ترکیب هر کلید اضافی colNN در ستون "دروس پيش نياز..." اگر باقی مانده ----------
  const prereqKey = headers.find(h => /پيش|پیش|همنياز|هم‌نیاز|معادل/i.test(h)) || 'دروس پيش نياز، همنياز، متضاد و معادل';
  rows.forEach(obj => {
    const extra = Object.keys(obj).filter(k => /^col\d+$/i.test(k));
    if (extra.length) {
      extra.sort((a,b)=>parseInt(a.slice(3))-parseInt(b.slice(3)));
      let base = (obj[prereqKey] || '').trim();
      for (const k of extra) {
        if (obj[k]) {
          const v = tx(obj[k]);
          if (v) base = base ? (base + '\n' + v) : v;
        }
        delete obj[k];
      }
      obj[prereqKey] = base;
    }
  });

  // ---------- خروجی و دانلود ----------
  const out = { headers, rows, rowCount: rows.length, columnCount: headers.length };
  const filename = 'table_data_clean_final.json';
  const blob = new Blob([JSON.stringify(out, null, 2)], { type: 'application/json;charset=utf-8' });
  const a = document.createElement('a');
  a.href = URL.createObjectURL(blob);
  a.download = filename;
  document.body.appendChild(a);
  a.click();
  a.remove();
  URL.revokeObjectURL(a.href);

  console.log(`Downloaded ${filename} — rows: ${rows.length}, headers: ${headers.length}`);
  console.log('headers:', headers);
  console.log('sample row:', rows[0]);
  return out;
})();
