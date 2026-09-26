$ErrorActionPreference = 'Stop'
$reportPath = Join-Path $PSScriptRoot 'CIT300_Project_Report.docx'
$pdfPath = Join-Path $PSScriptRoot 'CIT300_Project_Report.pdf'
$word = $null
$report = $null
try {
    $word = New-Object -ComObject Word.Application
    $word.Visible = $false
    $word.DisplayAlerts = 0
    $report = $word.Documents.Open($reportPath, $false, $false)
    foreach ($styleName in @('TOC 1', 'TOC 2', 'Table of Figures')) {
        $report.Styles.Item($styleName).Font.Name = 'Times New Roman'
        $report.Styles.Item($styleName).Font.Size = 12
        $report.Styles.Item($styleName).ParagraphFormat.LineSpacingRule = 1
    }
    $report.Fields.Update() | Out-Null
    $report.Repaginate()
    foreach ($toc in $report.TablesOfContents) { $toc.Update() }
    foreach ($figures in $report.TablesOfFigures) { $figures.Update() }
    $report.Repaginate()
    $report.Fields.Update() | Out-Null
    $report.Repaginate()
    $report.Save()
    $report.ExportAsFixedFormat($pdfPath, 17)
    Write-Output ('Rendered pages: ' + $report.ComputeStatistics(2))
    Write-Output ('Saved: ' + $reportPath)
    Write-Output ('Preview: ' + $pdfPath)
} finally {
    if ($null -ne $report) { $report.Close(0); [System.Runtime.InteropServices.Marshal]::ReleaseComObject($report) | Out-Null }
    if ($null -ne $word) { $word.Quit(); [System.Runtime.InteropServices.Marshal]::ReleaseComObject($word) | Out-Null }
}
