import json
import xml.etree.ElementTree as ET
import csv

with open('response.json', 'r', encoding='utf-8') as json_file:
    r_json = json.load(json_file)

bmt = r_json['outpar']['BMt']

root = ET.fromstring(bmt)

headers = ['id', 'دانشكده', 'گروه آموزشي', 'شماره و گروه درس', 'نام درس', 'واحد کل', 'واحد ع', 'ظر فيت', 'ثبت نام شده', 'تعداد ليست انتظار', 'جنسيت', 'نام استاد', 'زمان و مكان ارائه/ امتحان', 'توضيحات', 'امكان اخذ درس توسط ساير مراكز', 'حذف اضطراري']

def clean_text(text):
    if text is None:
        return ''
    text = text.replace('<BR>', '\n').replace('</BR>', '').replace('&lt;BR&gt;', '\n').replace('&lt;/BR&gt;', '').replace('"', '').strip()
    return text

rows = []
for row_index, row_elem in enumerate(root.findall('row'), start=1):
    row_data = {
        'id': str(row_index),
        'دانشكده': clean_text(row_elem.get('B2')),
        'گروه آموزشي': clean_text(row_elem.get('B4')),
        'شماره و گروه درس': clean_text(row_elem.get('C1')),
        'نام درس': clean_text(row_elem.get('C2')),
        'واحد کل': clean_text(row_elem.get('C3')),
        'واحد ع': clean_text(row_elem.get('C4')),
        'ظر فيت': clean_text(row_elem.get('C5')),
        'ثبت نام شده': clean_text(row_elem.get('C6')),
        'تعداد ليست انتظار': clean_text(row_elem.get('C7')),
        'جنسيت': clean_text(row_elem.get('C8')),
        'نام استاد': clean_text(row_elem.get('C9')),
        'زمان و مكان ارائه/ امتحان': clean_text(row_elem.get('C12')),
        'توضيحات': clean_text(row_elem.get('C18')),
        'امكان اخذ درس توسط ساير مراكز': clean_text(row_elem.get('C19')),
        'حذف اضطراري': clean_text(row_elem.get('C20'))
    }
    rows.append(row_data)

with open('all_university_classes.csv', 'w', encoding='utf-8', newline='') as csv_file:
    writer = csv.DictWriter(csv_file, fieldnames=headers)
    writer.writeheader()
    writer.writerows(rows)