# AI Interaction Log - Nhóm 02 (PRO192 FA26, Đề P05)

Mỗi lần dùng AI, thành viên thêm một dòng vào bảng theo quy định minh bạch học thuật của FPT University.

| Ngày | Người dùng (họ tên, MSSV) | Công cụ AI | Mục đích / prompt chính | Phần đã kiểm tra | Phần đã tự sửa / không dùng |
|:---:|---|:---:|---|---|---|
| 26/10 | Thành viên 1 (Trưởng nhóm) | Antigravity (Gemini 3.8 Flash) | Thiết kế mô hình lớp UML, công thức GPA có trọng số tín chỉ và khung dự án NetBeans 17 / JDK 8 | Kiểm tra luồng gọi đa hình và phân chia nhiệm vụ cho 5 thành viên theo checklist | Thống nhất quy ước tên package, private 100%, dùng `this.` và `super.` tường minh |
| 26/10 | Thành viên 2 | Antigravity (Gemini 3.8 Flash) | Khung trừu tượng Course và 2 cách tính điểm đa hình TheoryCourse (0.3/0.7), PracticeCourse (0.5/0.5) | Kiểm tra với hai điểm 8 và 6: LT101 ra 6.60, TH101 ra 7.00 | Tách hàm kiểm tra tín chỉ (1-6) và chặn đổi loại môn sau khi có kết quả |
| 26/10 | Thành viên 3 | Antigravity (Gemini 3.8 Flash) | Tạo khung lớp Student, StudentManager, GradeRecord và kiểm thử ca D05, D06 chống trùng lặp kết quả | Chạy kiểm thử thành công ca D05 (từ chối mã sai, điểm -0.01, 10.01, NaN) và D06 (cập nhật điểm 8.00 không trùng dòng) với JDK 1.8 | Chuẩn hóa quy tắc private 100%, bổ sung tường minh `this.`, tách nghiệp vụ không cho xóa sinh viên đã có điểm |
| 26/10 | Thành viên 4 | Antigravity (Gemini 3.8 Flash) | Thiết kế logic nhập/cập nhật điểm, kiểm tra miền điểm [0; 10], phân biệt `Double null` và điểm 0.0 | Thử ca D03 (CHUA_DU_DIEM), D04 (quá trình 0, cuối kỳ 10), D10 (quá trình 0, cuối kỳ thiếu) | Đảm bảo không âm thầm đổi ô trống thành điểm 0 |
| 26/10 | Thành viên 5 | Antigravity (Gemini 3.8 Flash) | Cài đặt lưu/đọc file text cục bộ, xuất CSV có header và Menu Console lặp lại | Chạy thử lưu mẫu, thoát chương trình rồi đọc lại (ca D09) bảo toàn đúng loại môn và điểm thiếu | Format chuẩn phân cách dấu phẩy, dấu chấm thập phân, escape ký tự đặc biệt |
