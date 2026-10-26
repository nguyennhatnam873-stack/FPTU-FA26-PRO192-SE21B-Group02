# Đồ án PRO192 FA26 - Nhóm 02 (Đề P05: Quản lý sinh viên và bảng điểm)

## 1. Thông tin đồ án và Bảng phân công 5 Thành viên (Theo Checklist)

* **Học phần:** PRO192 (Object-Oriented Programming with Java)
* **Lớp:** SE21B - Học kỳ FA26 - FPT University
* **Đề tài:** P05 - Quản lý sinh viên và bảng điểm (Java Console, JDK 1.8, NetBeans 17)
* **Kho mã nguồn:** [nguyennhatnam873-stack/FPTU-FA26-PRO192-SE21B-Group02](https://github.com/nguyennhatnam873-stack/FPTU-FA26-PRO192-SE21B-Group02)

| STT | Thành viên | MSSV | Vai trò chính theo Checklist | Các Class phụ trách chính | Ca kiểm thử bắt buộc |
|:---:|---|:---:|---|---|:---:|
| **1** | **Người 1 (Trưởng nhóm)** | SExxxxxx | Quản lý repo, điều phối; Tổng hợp bảng điểm, lọc môn đủ điểm, tính trung bình có trọng số tín chỉ. | `model/Transcript.java`<br>`service/TranscriptService.java` | **D02, D04, D06, D07** |
| **2** | **Người 2** | SExxxxxx | Mô hình môn học, 2 công thức đánh giá (đa hình), kiểm tra tín chỉ và mã duy nhất. | `model/Course.java`<br>`model/TheoryCourse.java`<br>`model/PracticeCourse.java`<br>`manager/CourseManager.java` | **D01, D07, D11** |
| **3** | **Người 3** | SExxxxxx | Hồ sơ sinh viên, quản lý cặp SV–môn, chống trùng kết quả, chặn xóa sinh viên đã có điểm. | `model/Student.java`<br>`manager/StudentManager.java`<br>`model/GradeRecord.java` | **D05, D06** |
| **4** | **Người 4** | SExxxxxx | Nhập và cập nhật điểm, kiểm tra miền điểm [0; 10], phân biệt `Double null` vs điểm 0.0, trạng thái `CHUA_DU_DIEM`. | `model/GradeRecord.java` (logic điểm)<br>`manager/GradeManager.java` | **D03, D04, D05, D10** |
| **5** | **Người 5** | SExxxxxx | Lưu và đọc file cục bộ, xuất file CSV chuẩn quy cách, Menu Console lặp lại. | `service/FileDataStore.java`<br>`service/CsvExporter.java`<br>`ui/MainMenu.java`<br>`app/Main.java` | **D08, D09, D10** |

---

## 2. Cấu trúc thư mục dự án (Chuẩn NetBeans 17 / JDK 8)

```text
FPTU-FA26-PRO192-SE21B-Group02/
├── nbproject/                          # Cấu hình dự án NetBeans IDE 17
│   ├── project.xml
│   └── project.properties              # Cấu hình javac source/target = 1.8
├── build.xml                           # Script Ant build dự án chuẩn NetBeans
├── .gitignore                          # Chặn file rác build/ và dist/
├── README.md                           # Hướng dẫn chi tiết dự án và phân công
├── data/
│   ├── sample_data.txt                 # Dữ liệu mẫu khởi đầu (2 SV, 3 môn, 3 kết quả)
│   └── app_data.txt                    # Dữ liệu thực tế lưu khi chạy chương trình
├── docs/
│   ├── ai_logs/
│   │   └── AI_INTERACTION_LOG.md       # Nhật ký minh bạch sử dụng AI của cả 5 thành viên
│   └── report.pdf                      # Báo cáo đồ án (nộp giai đoạn D12-D14)
└── src/
    ├── app/
    │   └── Main.java                   # Điểm khởi chạy chương trình (Member 5 & All)
    ├── model/                          # Tầng dữ liệu / Thực thể (OOP Model)
    │   ├── Student.java                # Sinh viên (Member 3)
    │   ├── Course.java                 # Lớp trừu tượng Môn học (Member 2)
    │   ├── TheoryCourse.java           # Môn Lý thuyết: 0.3 QT + 0.7 CK (Member 2)
    │   ├── PracticeCourse.java         # Môn Thực hành: 0.5 QT + 0.5 CK (Member 2)
    │   ├── GradeRecord.java            # Cặp SV - Môn và 2 điểm (Member 3 & 4)
    │   └── Transcript.java             # Bảng điểm & tính GPA trọng số (Member 1)
    ├── manager/                        # Tầng xử lý nghiệp vụ (Business Logic)
    │   ├── StudentManager.java         # Quản lý SV, kiểm tra trùng MSSV (Member 3)
    │   ├── CourseManager.java          # Quản lý Môn học (Member 2)
    │   └── GradeManager.java           # Quản lý kết quả học tập (Member 3 & 4)
    ├── service/                        # Tầng dịch vụ & I/O
    │   ├── TranscriptService.java      # Tổng hợp bảng điểm (Member 1)
    │   ├── CsvExporter.java            # Xuất bảng điểm CSV (Member 5)
    │   └── FileDataStore.java          # Lưu/đọc file cục bộ (Member 5)
    ├── ui/
    │   └── MainMenu.java               # Giao diện Console điều hướng (Member 5)
    └── test/
        └── Member3TestDriver.java      # Driver kiểm thử độc lập (D05, D06)
```

---

## 3. Quy tắc làm việc trên GitHub cho 5 Thành viên

Để được giảng viên đánh giá cao và đạt điểm tối đa ở phần lịch sử làm việc:

1. **Mỗi thành viên clone repo về máy và làm việc trên branch riêng:**
   * Trưởng nhóm (Member 1): branch `feature/member1-transcript`
   * Member 2: branch `feature/member2-course`
   * Member 3: branch `feature/member3-student`
   * Member 4: branch `feature/member4-grade`
   * Member 5: branch `feature/member5-io-menu`
2. **Commit từ tài khoản cá nhân:** Tuyệt đối không để 1 người commit thay cả nhóm. Giảng viên kiểm tra mục **Insights ➔ Contributors**.
3. **Mô tả commit rõ ràng (Semantic Commits):**
   * `feat(student): add validation for unique studentId and not-empty name`
   * `feat(course): implement polymorphism for TheoryCourse and PracticeCourse`
   * `feat(grade): implement student-course pair validation and update mode`
   * `feat(transcript): calculate weighted GPA from completed courses`
   * `feat(io): save and load local text file preserving course types`
   * `test: verify test cases D01 to D11`

---

## 4. Hướng dẫn mở và chạy dự án trên NetBeans IDE 17

1. Khởi động **NetBeans IDE 17**.
2. Chọn `File` ➔ `Open Project...` và chọn thư mục `FPTU-FA26-PRO192-SE21B-Group02`.
3. NetBeans sẽ nhận diện ngay dự án có biểu tượng tách cafe Java.
4. Bấm `F6` hoặc nhấp chuột phải vào dự án chọn **Run** để khởi chạy ứng dụng Console.
5. Để chạy kiểm thử riêng của từng thành viên: mở file trong package `test`, bấm `Shift + F6` (Run File).
