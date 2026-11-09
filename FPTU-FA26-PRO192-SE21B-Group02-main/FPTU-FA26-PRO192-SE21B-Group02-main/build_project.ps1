# Build script for StudentGradeManager (JDK 8)
$ErrorActionPreference = "Stop"

$jdkJavac = "C:\Program Files\Java\jdk1.8.0_202\bin\javac.exe"
if (-not (Test-Path $jdkJavac)) {
    $found = Get-Command javac -ErrorAction SilentlyContinue
    if ($found) {
        $jdkJavac = $found.Source
    } else {
        Write-Error "Không tìm thấy javac.exe. Vui lòng cài đặt JDK 8 hoặc cấu hình PATH."
    }
}

Write-Host "Dang bien dich du an bang: $jdkJavac"
$buildDir = "StudentGradeManager/build/classes"
if (-not (Test-Path $buildDir)) {
    New-Item -ItemType Directory -Path $buildDir -Force | Out-Null
}

$srcFiles = Get-ChildItem -Path "StudentGradeManager/src" -Recurse -Filter "*.java" | Select-Object -ExpandProperty FullName
$testFiles = Get-ChildItem -Path "StudentGradeManager/test" -Recurse -Filter "*.java" | Select-Object -ExpandProperty FullName
$allFiles = @($srcFiles) + @($testFiles)

& $jdkJavac -encoding UTF-8 -d $buildDir -sourcepath "StudentGradeManager/src;StudentGradeManager/test" $allFiles
if ($LASTEXITCODE -eq 0) {
    Write-Host "[SUCCESS] Bien dich thanh cong toan bo ma nguon va test!" -ForegroundColor Green
} else {
    Write-Error "[FAIL] Bien dich that bai!"
}
