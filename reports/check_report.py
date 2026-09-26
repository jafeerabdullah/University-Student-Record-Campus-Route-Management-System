"""Inspect Word/PDF structure and make small page previews for layout review."""
from pathlib import Path
from zipfile import ZipFile
import json
from lxml import etree
import fitz
from PIL import Image, ImageDraw

folder = Path(__file__).resolve().parent
preview = folder / "preview"
preview.mkdir(exist_ok=True)
ns = {"w": "http://schemas.openxmlformats.org/wordprocessingml/2006/main"}
with ZipFile(folder / "CIT300_Project_Report.docx") as archive:
    root = etree.fromstring(archive.read("word/document.xml"))
    instructions = root.xpath("//w:instrText/text()", namespaces=ns)
    text = "\n".join(root.xpath("//w:t/text()", namespaces=ns))
    assert any('TOC \\o "1-2"' in item for item in instructions)
    assert any('\\c "Figure"' in item for item in instructions)
    assert any('\\c "Table"' in item for item in instructions)
    for number in range(1, 15):
        assert "CHAPTER " + str(number) + ":" in text
    for sid in ["23DA2-0575", "23DA2-0826", "23DA2-0576", "23DA2-0729"]:
        assert sid in text
    for sid in ["0001", "0002", "0003", "0004"]:
        assert sid in text
    assert "Error!" not in text

pdf = fitz.open(folder / "CIT300_Project_Report.pdf")
pages = []
for i, page in enumerate(pdf):
    text = page.get_text()
    trimmed = [line for line in text.splitlines() if "CIT300  |" not in line]
    pages.append({"page": i + 1, "chars": len(text), "first_lines": trimmed[:3], "last_lines": trimmed[-3:]})
    assert "Error!" not in text, f"Field error on page {i+1}"
    assert len(text.strip()) > 20, f"Blank page {i+1}"
    pix = page.get_pixmap(matrix=fitz.Matrix(.65, .65), alpha=False)
    pix.save(preview / f"page-{i+1:02d}.png")

for start in range(0, len(pdf), 12):
    sheet = Image.new("RGB", (1260, 1840), "#DDE3E9")
    draw = ImageDraw.Draw(sheet)
    for offset in range(min(12, len(pdf)-start)):
        tile = Image.open(preview / f"page-{start+offset+1:02d}.png")
        tile.thumbnail((395, 425))
        x, y = 15 + (offset % 3)*420, 25 + (offset // 3)*460
        sheet.paste(tile, (x, y))
        draw.text((x,y-16), "Page " + str(start+offset+1), fill="black")
    sheet.save(preview / f"contact-{start//12+1}.png")

(preview / "layout.json").write_text(json.dumps(pages, indent=2), encoding="utf-8")
print("Pages:", len(pdf))
print("Short pages:", json.dumps([p for p in pages if p["chars"] < 350]))
print("DOCX fields, chapters, member IDs and sample data verified.")
print("Page previews:", preview)
