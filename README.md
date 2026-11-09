# FPTU-FA26-PRO192-SE21B-Group02
## Dự án: Quản lý sinh viên và bảng điểm (Đề tài P05)
> **Trường Đại học FPT (FPT University - Quy Nhon AI Campus)**  
> **Môn học:** PRO192 — Object-Oriented Programming with Java  
> **Học kỳ:** Fall 2026 (FA26) | **Lớp:** SE21B | **Nhóm:** 02  
> **Giảng viên hướng dẫn:** Thầy Khoa (`khoand96`)  
> **Trạng thái:**  **HOÀN THÀNH 100%** (123/123 Automated Tests Passing)

---

### 1. Phân công nhiệm vụ & Thành viên nhóm

| Thành viên | Vai trò | Trách nhiệm chính trong hệ thống | Test Suite phụ trách | Kết quả |
| :--- | :--- | :--- | :--- | :---: |
| **Người 1** | **Trưởng nhóm**<br>Bảng điểm & GPA | • Lớp `Transcript`, `TranscriptService`<br>• Tính GPA có trọng số tín chỉ, xử lý môn đủ điểm<br>• Bảo vệ tính bất biến danh sách điểm (`Collections.unmodifiableList`) | `Person1Test.java`<br>*(Ca D02, D04, D06, D07)* | **4/4 PASS** (100%) |
| **Người 2** | Thành viên<br>Môn học & Đa hình | • Lớp trừu tượng `Course`, `TheoryCourse`, `PracticeCourse`<br>• Đa hình tính điểm tổng kết (LT: 30%-70%, TH: 50%-50%)<br>• Validation tín chỉ nguyên [1, 5], chặn sửa tín chỉ khi đã có điểm | `Person2Test.java`<br>*(Ca D01, D07, D11)* | **23/23 PASS** (100%) |
| **Người 3** | Thành viên<br>Sinh viên & Ràng buộc | • Lớp `Student`, `StudentManager`<br>• Kiểm tra tính duy nhất MSSV, tên hợp lệ<br>• Quản lý liên kết cặp Sinh viên – Môn học, chặn xóa SV đã có điểm | `Person3Test.java`<br>*(Ca D05, D06)* | **5/5 PASS** (100%) |
| **Người 4** | Thành viên<br>Nhập & Cập nhật điểm | • Lớp `GradeRecord`, `GradeManager`<br>• Quản lý điểm QT, CK; kiểm tra ràng buộc miền [0.0, 10.0]<br>• Xử lý triệt để trạng thái điểm thiếu (`null`) và phân biệt với điểm `0.00` | `Person4Test.java`<br>*(Ca D03, D04, D05, D10)* | **62/62 PASS** (100%) |
| **Người 5** | Thành viên<br>Lưu trữ & Menu CLI | • Lớp `FileDataStore` (Lưu/đọc nguyên tử Atomic Save `.tmp`, Transactional Load)<br>• Lớp `CsvExporter` (Xuất file CSV chuẩn RFC 4180, UTF-8 BOM, thuần OOP)<br>• Lớp `MainMenu` (Giao diện dòng lệnh 8 chức năng, cảnh báo chưa lưu) | `Person5Test.java`<br>*(Ca D08, D09, D10)* | **29/29 PASS** (100%) |
| **TỔNG CỘNG**| **Cả nhóm 02** | **Tích hợp toàn diện, chuẩn hóa kiến trúc OOP, kiểm thử tự động 100%** | **5 Test Suites** | **123/123 PASS** (100%) |

---

### 2. Môi trường phát triển & Tiêu chuẩn kỹ thuật
- **Ngôn ngữ lập trình:** Java SE 8 (JDK 1.8.0_202)
- **Môi trường IDE khuyến nghị:** Apache NetBeans 17
- **Hệ thống Build:** Apache Ant & PowerShell Automation Scripts
- **Bộ mã hóa (Encoding):** UTF-8 không BOM trên toàn bộ mã nguồn Java; UTF-8 BOM (`\uFEFF`) khi xuất file CSV để tương thích hoàn hảo với Microsoft Excel.
- **Tiêu chuẩn thiết kế:** 
  - Tuân thủ nghiêm ngặt 4 tính chất OOP (Đóng gói, Kế thừa, Đa hình, Trừu tượng).
  - Không lạm dụng `static` ở các tầng xử lý nghiệp vụ; các Service được khởi tạo và kết hợp theo quan hệ Aggregation / Dependency Injection.

---

### 3. Cấu trúc thư mục dự án
```text
FPTU-FA26-PRO192-SE21B-Group2/
├── docs/                               # Tài liệu thiết kế & báo cáo từng thành viên
│   ├── ai_logs/
│   │   └── AI_INTERACTION_LOG.md       # Nhật ký minh bạch sử dụng trợ lý AI
│   ├── BAO_CAO_NGUOI_1.md              # Báo cáo chi tiết Người 1 (Trưởng nhóm / Bảng điểm)
│   ├── BAO_CAO_NGUOI_2.md              # Báo cáo chi tiết Người 2 (Môn học & Đánh giá)
│   ├── BAO_CAO_NGUOI_3.md              # Báo cáo chi tiết Người 3 (Sinh viên & Cặp SV–Môn)
│   ├── BAO_CAO_NGUOI_4.md              # Báo cáo chi tiết Người 4 (Nhập & Cập nhật điểm)
│   ├── BAO_CAO_NGUOI_5.md              # Báo cáo chi tiết Người 5 (Lưu/đọc file & CSV)
│   └── checklist.html                  # Checklist 14 ngày & tiến độ công việc
├── data/
│   └── data_store.txt                  # File lưu trữ dữ liệu văn bản phân mục của hệ thống
├── StudentGradeManager/                # Mã nguồn dự án NetBeans
│   ├── nbproject/                      # Cấu hình dự án NetBeans
│   ├── src/studentgrademanager/        # Mã nguồn nghiệp vụ
│   │   ├── models/                     # Thực thể dữ liệu: Course, TheoryCourse, PracticeCourse, Student, GradeRecord, Transcript
│   │   ├── services/                   # Dịch vụ xử lý: StudentManager, CourseManager, GradeManager, TranscriptService
│   │   ├── storage/                    # Tầng lưu trữ: FileDataStore, CsvExporter
│   │   ├── ui/                         # Giao diện Console tương tác: MainMenu
│   │   └── StudentGradeManager.java    # Điểm khởi chạy chương trình (main entry point)
│   ├── test/studentgrademanager/       # Bộ kiểm thử tự động 5 thành viên (123 test cases)
│   │   ├── Person1Test.java            # Kiểm thử nghiệp vụ Bảng điểm & GPA (4/4 PASS)
│   │   ├── Person2Test.java            # Kiểm thử Đa hình Môn học & Ràng buộc tín chỉ (23/23 PASS)
│   │   ├── Person3Test.java            # Kiểm thử Quản lý Sinh viên & Toàn vẹn (5/5 PASS)
│   │   ├── Person4Test.java            # Kiểm thử Nhập/Sửa điểm & Ràng buộc [0, 10] (62/62 PASS)
│   │   └── Person5Test.java            # Kiểm thử Lưu/Đọc file, RFC 4180 CSV & Ngoại lệ (29/29 PASS)
│   ├── dist/
│   │   └── StudentGradeManager.jar     # Gói JAR thực thi độc lập (Standalone Executable JAR)
│   ├── build.xml                       # Ant build script
│   └── manifest.mf
├── build_project.ps1                   # Script tự động biên dịch toàn bộ mã nguồn & tests
├── package_jar.ps1                     # Script tự động đóng gói ứng dụng thành file JAR
├── run_tests.ps1                       # Script chạy toàn bộ 5 bộ kiểm thử tự động
└── README.md                           # Tài liệu tổng quan dự án (tài liệu này)
```

---

### 4. Điểm nhấn Kiến trúc & Kỹ thuật cài đặt

#### 4.1. Đa hình và Kế thừa môn học (Polymorphism)
- Lớp cha trừu tượng `Course` định nghĩa phương thức `calculateFinalScore(Double processScore, Double examScore)`.
- Hai lớp con cụ thể ghi đè logic tính toán:
  - `TheoryCourse`: Điểm tổng kết = `(Quá trình * 0.3) + (Cuối kỳ * 0.7)`.
  - `PracticeCourse`: Điểm tổng kết = `(Quá trình * 0.5) + (Cuối kỳ * 0.5)`.
- Nếu thiếu bất kỳ cột điểm nào (chứa `null`), phương thức tự động trả về `null` thay vì tính sai lệch.

#### 4.2. Lưu trữ tệp tin nguyên tử (Atomic Save) & Giao dịch an toàn (Transactional Load)
- **Atomic Save (`FileDataStore.java`):** Khi lưu dữ liệu, hệ thống không ghi đè trực tiếp lên `data_store.txt`. Thay vào đó, dữ liệu được ghi toàn bộ ra file tạm `.tmp`. Sau khi hoàn tất và flush sạch buffer, hệ thống mới thực hiện thao tác đổi tên (atomic rename) thay thế file chính. Điều này triệt tiêu hoàn toàn rủi ro hỏng file nếu xảy ra mất điện hoặc ứng dụng bị dừng đột ngột.
- **Transactional Load:** Khi nạp dữ liệu, hệ thống phân tích cú pháp vào các danh sách tạm (staging area). Nếu phát hiện bất kỳ dòng nào hỏng cấu trúc hoặc sai định dạng, một ngoại lệ sẽ được ném ra và toàn bộ trạng thái dữ liệu hiện hành trên RAM của hệ thống được bảo toàn nguyên vẹn, không bị xóa rỗng nửa vời.

#### 4.3. Xuất báo cáo CSV chuẩn quốc tế RFC 4180 (`CsvExporter.java`)
- **Thiết kế thuần OOP:** Không sử dụng phương thức `static` cho việc xử lý nghiệp vụ, chuyển thành `instance method` kết hợp trong `MainMenu`.
- **RFC 4180 Quoting & Escaping:** Mọi ô dữ liệu chứa dấu phẩy `,`, dấu ngoặc kép `"`, hoặc ký tự xuống dòng `\n` đều được tự động bọc trong cặp dấu ngoặc kép, và các dấu ngoặc kép nội dung được nhân đôi `""`.
- **Định dạng số chuẩn:** Dùng `Locale.US` để dấu thập phân luôn là dấu chấm `.`, tránh nhầm lẫn với dấu phân cách cột.
- **Tương thích Excel tiếng Việt:** Đầu file CSV được chèn mã ký tự BOM UTF-8 (`\uFEFF`), giúp Microsoft Excel mở file hiển thị đúng 100% tiếng Việt có dấu mà không cần cấu hình thêm.

#### 4.4. Phân biệt tường minh giữa "Điểm 0.00" và "Điểm thiếu" (D10)
- **Điểm thiếu:** Đại diện bằng giá trị `null` trong bộ nhớ, hiển thị trạng thái `CHUA_DU_DIEM`, lưu trữ dưới dạng từ khóa `EMPTY` trong tệp `data_store.txt`, xuất thành ô trống `""` trong file CSV, và không tham gia tính GPA.
- **Điểm 0:** Đại diện bằng số thực `0.0`, trạng thái có thể là `DU_DIEM` nếu đủ 2 đầu điểm, lưu trữ dưới dạng `0.00`, xuất `0.00` trong CSV, và được tính vào GPA có trọng số như bình thường.

---

### 5. Hướng dẫn Mở, Biên dịch & Chạy chương trình

#### Cách 1: Sử dụng NetBeans 17 (Khuyến nghị cho chấm bài)
1. Mở phần mềm **Apache NetBeans 17**.
2. Chọn menu **File** $\rightarrow$ **Open Project...**
3. Tìm và mở thư mục `StudentGradeManager`.
4. Nhấn **F6** (hoặc nút **Run Project**) để khởi động menu Console tương tác.
5. Để xuất bản file JAR: Nhấp chuột phải vào tên project `StudentGradeManager` $\rightarrow$ chọn **Clean and Build**. File thực thi sẽ nằm tại `StudentGradeManager/dist/StudentGradeManager.jar`.

#### Cách 2: Sử dụng dòng lệnh qua PowerShell / Terminal
Các script tiện ích được cấu hình sẵn tại thư mục gốc của đồ án:
- **Biên dịch toàn bộ hệ thống:**
  ```powershell
  powershell -ExecutionPolicy Bypass -File .\build_project.ps1
  ```
- **Chạy toàn bộ 123 ca kiểm thử tự động của 5 thành viên:**
  ```powershell
  powershell -ExecutionPolicy Bypass -File .\run_tests.ps1
  ```
- **Đóng gói file JAR thực thi chuẩn:**
  ```powershell
  powershell -ExecutionPolicy Bypass -File .\package_jar.ps1
  ```
- **Khởi chạy ứng dụng từ file JAR (hỗ trợ UTF-8 đầy đủ):**
  ```powershell
  java -Dfile.encoding=UTF-8 -jar StudentGradeManager/dist/StudentGradeManager.jar
  ```

---

### 6. Danh mục chức năng Menu Console (`MainMenu.java`)

Khi chạy chương trình, người dùng được cung cấp giao diện tương tác với 8 nhóm nghiệp vụ chính:

```text
----------------------------- MENU CHINH -----------------------------
1. Quan ly Sinh vien (Them, tim, xem danh sach)
2. Quan ly Mon hoc (Them LT/TH, tim, xem danh sach)
3. Tao / Cap nhat ket qua (MSSV + Ma mon + Diem QT, CK)
4. Xem ket qua mon hoc (Trang thai du / chua du diem)
5. Xem bang diem sinh vien (Chi tiet cac mon, diem tong ket)
6. Xem diem trung binh co trong so (GPA) & tin chi
7. Xuat bang diem sinh vien ra file CSV (RFC 4180)
8. Quan ly luu tru file (Luu, Tai lai, Khoi phuc mau)
0. Thoat chuong trinh
----------------------------------------------------------------------
```

> [!IMPORTANT]
> **Cơ chế Cảnh báo An toàn khi Thoát:**  
> Nếu người dùng có thay đổi dữ liệu trong phiên làm việc mà chưa thực hiện chức năng lưu file (Mục 8), khi chọn `0. Thoat`, hệ thống sẽ đưa ra thông báo xác nhận:  
> *"Du lieu hien tai co thay doi chua duoc luu vao file! Ban co chac chan muon thoat? (y/n)"*  
> Giúp ngăn chặn tuyệt đối tình trạng mất dữ liệu do sơ ý.
