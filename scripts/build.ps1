param(
    [ValidateSet('build', 'run', 'test')]
    [string]$Mode = 'build',
    [switch]$Demo
)

$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
Push-Location -LiteralPath $projectRoot
try {
    if (-not (Get-Command javac -ErrorAction SilentlyContinue)) {
        throw 'JDK 17 or above is required. Make javac and java available on PATH.'
    }
    # Remove only this project's verified output directory, so removed modules
    # cannot remain available as stale class files after a branch change.
    $outputPath = [System.IO.Path]::GetFullPath((Join-Path $projectRoot 'out'))
    if (Test-Path -LiteralPath $outputPath) {
        $outputItem = Get-Item -LiteralPath $outputPath -Force
        if (($outputItem.Attributes -band [System.IO.FileAttributes]::ReparsePoint) -ne 0) {
            throw 'Refusing to clean an output directory that is a symbolic link or junction.'
        }
        if ((Resolve-Path -LiteralPath $outputPath).Path -ne $outputPath -or
            (Split-Path -Parent $outputPath) -ne $projectRoot) {
            throw 'Output directory is outside the expected project location.'
        }
        Remove-Item -LiteralPath $outputPath -Recurse -Force
    }
    New-Item -ItemType Directory -Path $outputPath | Out-Null
    $sourceFiles = @(Get-ChildItem -LiteralPath 'src' -Filter '*.java' -Recurse | ForEach-Object { '"' + $_.FullName.Replace('\', '/') + '"' })
    if ($Mode -eq 'test') {
        $sourceFiles += @(Get-ChildItem -LiteralPath 'tests' -Filter '*.java' -Recurse | ForEach-Object { '"' + $_.FullName.Replace('\', '/') + '"' })
    }
    # javac argument files on Windows require UTF-8 without a byte-order mark.
    $utf8 = New-Object System.Text.UTF8Encoding($false)
    [System.IO.File]::WriteAllLines((Join-Path $projectRoot 'out/sources.txt'), [string[]]$sourceFiles, $utf8)
    & javac --release 17 -encoding UTF-8 -Xlint:all -d out '@out/sources.txt'
    if ($LASTEXITCODE -ne 0) { throw 'Java compilation failed.' }
    if ($Mode -eq 'test') {
        & java -cp out TestRunner
        if ($LASTEXITCODE -ne 0) { throw 'Tests failed.' }
    } elseif ($Mode -eq 'run') {
        if ($Demo) { & java -cp out Main --demo }
        else { & java -cp out Main }
        if ($LASTEXITCODE -ne 0) { throw 'Application exited with an error.' }
    } else {
        Write-Output 'Build successful (Java 17 target). Run: java -cp out Main --demo'
    }
} finally {
    Pop-Location
}
