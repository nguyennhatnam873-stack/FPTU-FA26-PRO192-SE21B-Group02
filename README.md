# FPTU-FA26-PRO192-SE21B-Group02
## Dự án: Quản lý sinh viên và bảng điểm (Đề tài P05)

### 1. Thông tin nhóm
- **Lớp:** SE21B
- **Môn học:** PRO192 (OOP with Java)
- **Học kỳ:** FA26
- **Giảng viên hướng dẫn:** Thầy Khoa (`khoand96`)
- **Danh sách thành viên:**
  1. Trưởng nhóm: [Họ và tên] - [MSSV] - [GitHub Username]
  2. Thành viên 2: [Họ và tên] - [MSSV] - [GitHub Username]
  3. Thành viên 3: [Họ và tên] - [MSSV] - [GitHub Username]
  4. Thành viên 4: [Họ và tên] - [MSSV] - [GitHub Username]
  5. Thành viên 5: [Họ và tên] - [MSSV] - [GitHub Username]

---

### 2. Môi trường phát triển & Yêu cầu hệ thống
- **Ngôn ngữ:** Java SE (JDK 1.8 / Java 8)
- **IDE:** Apache NetBeans 17
- **Công cụ quản lý mã nguồn:** Git / GitHub

---

### 3. Cấu trúc thư mục dự án
```text
FPTU-FA26-PRO192-SE21B-Group02/
├── docs/
│   └── ai_logs/
│       └── AI_INTERACTION_LOG.md   # Nhật ký minh bạch sử dụng AI
├── StudentGradeManager/            # NetBeans Project
│   ├── nbproject/                  # Cấu hình NetBeans
│   ├── src/
│   │   └── studentgrademanager/    # Mã nguồn chính
│   │       └── StudentGradeManager.java
│   ├── build.xml                   # Ant build script
│   └── manifest.mf
├── README.md                       # Tài liệu hướng dẫn dự án
└── .gitignore                      # Cấu hình bỏ qua file rác khi commit
```

---

### 4. Hướng dẫn Mở, Build và Chạy dự án
1. **Mở dự án trong NetBeans:**
   - Mở **NetBeans 17**.
   - Chọn menu **File** $\rightarrow$ **Open Project...**
   - Trỏ tới thư mục `StudentGradeManager` bên trong repository này.
2. **Kiểm tra phiên bản Java:**
   - Chuột phải vào project `StudentGradeManager` $\rightarrow$ **Properties** $\rightarrow$ **Sources**.
   - Đảm bảo **Source/Binary Format** là **JDK 8**.
3. **Build & Chạy:**
   - Nhấn **F6** hoặc nút **Run Project** (tam giác xanh) để chạy trực tiếp trên Console.
   - Để xuất file JAR: Chuột phải vào project $\rightarrow$ chọn **Clean and Build**. File `.jar` sẽ được tạo trong thư mục `StudentGradeManager/dist/`.
