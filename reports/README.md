# CIT300 project report

- `CIT300_Project_Report.docx`: editable Microsoft Word report.
- `CIT300_Project_Report.pdf`: fixed-layout preview exported by Microsoft Word.
- `assets/`: the seven generated system, UML and campus diagrams.
- `build_report.py`: reproducible report and diagram source (requires python-docx and Pillow).
- `render_report.ps1`: refreshes Word fields, paginates, saves and exports the PDF using installed Microsoft Word.
- `check_report.py`: structural checks and PDF page previews (requires PyMuPDF, Pillow and lxml).

The report contains all 14 requested chapters, front matter, automatic contents and figure/table lists, page numbers, the supplied team details, verified testing results and four appendices. Student demo IDs are 0001–0004; these differ from the team members' registration numbers on the cover.

The lecturer name and submission date remain editable placeholders. Code/output and GitHub screenshot spaces are intentionally provided for the group's evidence. The lecture-material reference needs the actual lecturer/material details. Remote collaboration has not been claimed as completed: the inspected local repository has no configured remote.

After filling in the remaining fields and inserting screenshots, select all in Word and update fields (Ctrl+A, F9), choosing to update the entire table of contents. Then save. Refresh each figure/table list if Word prompts separately.

To regenerate the delivered report, run these commands from the project root in sequence:

```powershell
python reports/build_report.py
powershell -NoProfile -ExecutionPolicy Bypass -File reports/render_report.ps1
python reports/check_report.py
```

Regeneration replaces the generated DOCX and PDF; preserve any manual Word edits before doing so.
