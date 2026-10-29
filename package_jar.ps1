# Package JAR script for StudentGradeManager (JDK 8)
$ErrorActionPreference = "Stop"

$jdkJar = "C:\Program Files\Java\jdk1.8.0_202\bin\jar.exe"
if (-not (Test-Path $jdkJar)) {
    $found = Get-Command jar -ErrorAction SilentlyContinue
    if ($found) {
        $jdkJar = $found.Source
    } else {
        Write-Error "Không tìm thấy jar.exe. Vui lòng cài đặt JDK 8 hoặc cấu hình PATH."
    }
}

# Dam bao da build truoc khi dong goi
& .\build_project.ps1

$baseDir = Get-Location
$distDir = Join-Path $baseDir "StudentGradeManager/dist"
if (-not (Test-Path $distDir)) {
    New-Item -ItemType Directory -Path $distDir -Force | Out-Null
}

$jarFile = Join-Path $distDir "StudentGradeManager.jar"
$manifest = Join-Path $baseDir "StudentGradeManager/manifest.mf"
$classesDir = Join-Path $baseDir "StudentGradeManager/build/classes"

Push-Location $classesDir
try {
    & $jdkJar cvfm $jarFile $manifest studentgrademanager
    if ($LASTEXITCODE -eq 0) {
        Write-Host "[SUCCESS] Da tao file JAR thanh cong tai: $jarFile" -ForegroundColor Green
    } else {
        Write-Error "[FAIL] Tao file JAR that bai!"
    }
} finally {
    Pop-Location
}
