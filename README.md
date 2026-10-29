# FPTU-FA26-PRO192-SE21B-Group02
## Dự án: Quản lý sinh viên và bảng điểm (Đề tài P05)

### 1. Thông tin nhóm
- **Lớp:** SE21B
- **Môn học:** PRO192 (OOP with Java)
- **Học kỳ:** FA26
- **Giảng viên hướng dẫn:** Thầy Khoa (`khoand96`)
- **Phân công nhiệm vụ (5 thành viên):**
  1. **Người 1 — Trưởng nhóm / Bảng điểm:** Tổng hợp bảng điểm, GPA có trọng số tín chỉ, xử lý môn đủ điểm (Ca D02, D04, D06, D07).
  2. **Người 2 — Môn học & Đánh giá:** Mô hình `Course`, `TheoryCourse`, `PracticeCourse`, tính điểm đa hình, validation tín chỉ (Ca D01, D07, D11).
  3. **Người 3 — Sinh viên & Cặp SV–Môn:** Lớp `Student`, `GradeRecord`, kiểm tra mã duy nhất, chống tạo dòng trùng (Ca D05, D06).
  4. **Người 4 — Nhập & Cập nhật điểm:** Quản lý điểm thành phần, kiểm tra miền [0, 10], xử lý điểm thiếu `null` (Ca D03, D04, D05, D10).
  5. **Người 5 — Lưu/đọc file & CSV:** Lưu/đọc dữ liệu bền vững `FileDataStore`, xuất CSV RFC 4180 `CsvExporter`, menu console `MainMenu` (Ca D08, D09, D10).

---

### 2. Môi trường phát triển & Yêu cầu hệ thống
- **Ngôn ngữ:** Java SE (JDK 1.8 / Java 8)
- **IDE:** Apache NetBeans 17
- **Bộ mã hóa ký tự:** UTF-8
- **Công cụ quản lý mã nguồn:** Git / GitHub

---

### 3. Cấu trúc thư mục dự án
```text
Final-Project/
├── docs/
│   ├── ai_logs/
│   │   └── AI_INTERACTION_LOG.md       # Nhật ký minh bạch sử dụng AI
│   ├── BAO_CAO_NGUOI_1.md              # Báo cáo chi tiết của Người 1 (Trưởng nhóm / Bảng điểm)
│   ├── BAO_CAO_NGUOI_2.md              # Báo cáo chi tiết của Người 2 (Môn học & Đánh giá)
│   ├── BAO_CAO_NGUOI_4.md              # Báo cáo chi tiết của Người 4 (Nhập & Cập nhật điểm)
│   ├── BAO_CAO_NGUOI_5.md              # Báo cáo chi tiết của Người 5 (Lưu/đọc file & CSV)
│   └── checklist.html                  # Checklist 14 ngày & 11 task/thành viên
├── data/
│   └── data_store.txt                  # File lưu trữ dữ liệu bền vững (Người 5)
├── StudentGradeManager/                # NetBeans Project
│   ├── nbproject/                      # Cấu hình NetBeans
│   ├── src/studentgrademanager/        # Mã nguồn chính
│   │   ├── models/                     # Các lớp thực thể (Course, Student, GradeRecord, Transcript)
│   │   ├── services/                   # Dịch vụ nghiệp vụ (CourseManager, StudentManager, GradeManager, TranscriptService)
│   │   ├── storage/                    # Lưu/đọc file & xuất CSV (FileDataStore, CsvExporter)
│   │   ├── ui/                         # Giao diện Console (MainMenu)
│   │   └── StudentGradeManager.java    # Lớp chứa hàm main()
│   ├── test/studentgrademanager/       # Bộ kiểm thử tự động (100% PASS - 174/174 assertions)
│   │   ├── Person1Test.java            # Test Người 1 (D02, D04, D06, D07, D03)
│   │   ├── Person2Test.java            # Test Người 2 (D01, D07, D11)
│   │   ├── Person3Test.java            # Test Người 3 (D05, D06)
│   │   ├── Person4Test.java            # Test Người 4 (D03, D04, D05, D10)
│   │   └── Person5Test.java            # Test Người 5 (D08, D09, D10)
│   ├── dist/
│   │   └── StudentGradeManager.jar     # File thực thi JAR chuẩn JDK 8
│   ├── build.xml                       # Ant build script
│   └── manifest.mf
├── build_project.ps1                   # Script build dự án tự động
├── package_jar.ps1                     # Script đóng gói JAR
├── run_tests.ps1                       # Script chạy toàn bộ test tự động
└── README.md                           # Hướng dẫn chi tiết dự án
```

---

### 4. Hướng dẫn Mở, Build và Chạy dự án

#### Cách 1: Sử dụng NetBeans 17
1. Mở **NetBeans 17**.
2. Chọn menu **File** $\rightarrow$ **Open Project...**
3. Trỏ tới thư mục `StudentGradeManager`.
4. Nhấn **F6** (hoặc Run) để khởi chạy giao diện Console.
5. Để xuất file JAR: Chuột phải vào project $\rightarrow$ chọn **Clean and Build**. File `.jar` sẽ được tạo tại `StudentGradeManager/dist/StudentGradeManager.jar`.

#### Cách 2: Chạy trực tiếp qua Command Line / Terminal
- **Biên dịch toàn bộ mã nguồn:**
  ```powershell
  powershell -ExecutionPolicy Bypass -File .\build_project.ps1
  ```
- **Chạy toàn bộ các ca kiểm thử tự động (Person 1 -> Person 5):**
  ```powershell
  powershell -ExecutionPolicy Bypass -File .\run_tests.ps1
  ```
- **Đóng gói file JAR thực thi:**
  ```powershell
  powershell -ExecutionPolicy Bypass -File .\package_jar.ps1
  ```
- **Chạy ứng dụng từ file JAR:**
  ```bash
  java -Dfile.encoding=UTF-8 -jar StudentGradeManager/dist/StudentGradeManager.jar
  ```

---

### 5. Quy ước Lưu trữ & Xuất CSV (Người 5)
- **Vị trí file lưu trữ dữ liệu:** `data/data_store.txt` (đường dẫn tương đối).
- **Cơ chế lưu trữ:** File văn bản phân mục (`[STUDENTS]`, `[COURSES]`, `[GRADES]`), áp dụng cơ chế **Atomic Save** (ghi file tạm `.tmp` trước, đảm bảo an toàn dữ liệu 100%).
- **Xuất CSV:** Chuẩn RFC 4180, phân cách dấu phẩy `,`, dấu chấm thập phân `.`, có UTF-8 BOM để Excel hiển thị tiếng Việt chính xác.
- **Quy ước điểm thiếu & điểm 0:**
  - Điểm thiếu: Biểu diễn bằng `null` trên RAM, lưu thành `EMPTY` trong file dữ liệu, để ô trống `""` trong file CSV, trạng thái môn là `CHUA_DU_DIEM`.
  - Điểm 0: Biểu diễn bằng `0.0`, lưu thành `0.00`, xuất `0.00` trong CSV, tham gia tính điểm bình thường.
