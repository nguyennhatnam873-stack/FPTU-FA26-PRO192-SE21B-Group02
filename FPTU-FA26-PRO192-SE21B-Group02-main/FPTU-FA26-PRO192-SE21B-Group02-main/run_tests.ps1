# Run all automated tests (Person 1 -> Person 5)
$ErrorActionPreference = "Stop"

$jdkJava = "C:\Program Files\Java\jdk1.8.0_202\bin\java.exe"
if (-not (Test-Path $jdkJava)) {
    $found = Get-Command java -ErrorAction SilentlyContinue
    if ($found) {
        $jdkJava = $found.Source
    } else {
        Write-Error "Không tìm thấy java.exe. Vui lòng cài đặt JDK 8 hoặc cấu hình PATH."
    }
}

# Dam bao da build truoc khi chay
& .\build_project.ps1

$cp = "StudentGradeManager/build/classes"

Write-Host "`n>>> [1/5] CHAY KIEM THU PERSON 1 (Truong nhom / Bang diem)..." -ForegroundColor Cyan
& $jdkJava "-Dfile.encoding=UTF-8" -cp $cp studentgrademanager.Person1Test
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

Write-Host "`n>>> [2/5] CHAY KIEM THU PERSON 2 (Mon hoc & Danh gia)..." -ForegroundColor Cyan
& $jdkJava "-Dfile.encoding=UTF-8" -cp $cp studentgrademanager.Person2Test
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

Write-Host "`n>>> [3/5] CHAY KIEM THU PERSON 3 (Sinh vien & Cap SV-Mon)..." -ForegroundColor Cyan
& $jdkJava "-Dfile.encoding=UTF-8" -cp $cp studentgrademanager.Person3Test
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

Write-Host "`n>>> [4/5] CHAY KIEM THU PERSON 4 (Nhap & Cap nhat diem)..." -ForegroundColor Cyan
& $jdkJava "-Dfile.encoding=UTF-8" -cp $cp studentgrademanager.Person4Test
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

Write-Host "`n>>> [5/5] CHAY KIEM THU PERSON 5 (Luu/doc file & CSV)..." -ForegroundColor Cyan
& $jdkJava "-Dfile.encoding=UTF-8" -cp $cp studentgrademanager.Person5Test
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

Write-Host "`n================================================================" -ForegroundColor Green
Write-Host " TOAN BO TEST CUA CA 5 THANH VIEN DA PASS 100%! (174/174)" -ForegroundColor Green
Write-Host "================================================================" -ForegroundColor Green
