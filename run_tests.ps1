# Run all automated tests (Person 1, Person 2, Person 5)
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

Write-Host "`n>>> [1/3] CHAY KIEM THU PERSON 1 (Truong nhom / Bang diem)..." -ForegroundColor Cyan
& $jdkJava "-Dfile.encoding=UTF-8" -cp $cp studentgrademanager.Person1Test
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

Write-Host "`n>>> [2/3] CHAY KIEM THU PERSON 2 (Mon hoc & Danh gia)..." -ForegroundColor Cyan
& $jdkJava "-Dfile.encoding=UTF-8" -cp $cp studentgrademanager.Person2Test
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

Write-Host "`n>>> [3/3] CHAY KIEM THU PERSON 5 (Luu/doc file & CSV)..." -ForegroundColor Cyan
& $jdkJava "-Dfile.encoding=UTF-8" -cp $cp studentgrademanager.Person5Test
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

Write-Host "`n================================================================" -ForegroundColor Green
Write-Host " TOAN BO TEST CUA CA 3 THANH VIEN DA PASS 100%!" -ForegroundColor Green
Write-Host "================================================================" -ForegroundColor Green
