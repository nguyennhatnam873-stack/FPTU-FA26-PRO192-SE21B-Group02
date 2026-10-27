# AI Interaction Log - Nhóm 02 (PRO192 FA26, Đề P05)

Mỗi lần dùng AI, thành viên tự thêm một dòng vào bảng theo quy định minh bạch học thuật của FPT University.

| Ngày | Người thực hiện (họ tên, MSSV) | Công cụ AI | Mục tiêu / prompt chính | Link hoặc trích đoạn liên quan | Phần đã kiểm tra | Phần đã tự chỉnh sửa |
|---|---|---|---|---|---|---|
| 26/10 | Nguyễn Văn A, SExxxxxx | Claude | Hỏi cách chặn NaN khi nhập điểm | Link cuộc trò chuyện hoặc đoạn trả lời ngắn của AI | Chạy thử với NaN, -0.01, 10.01 | Viết lại hàm kiểm tra cho khớp code nhóm |
| 26/10 | Thành viên 2 (Môn học & Đánh giá) | Antigravity (Gemini 3.8 Flash) | Thiết kế mô hình kế thừa Course, TheoryCourse, PracticeCourse, phương thức tính điểm đa hình và validation môn học | Trích xuất công thức 0.3/0.7 và 0.5/0.5, trả về null khi thiếu điểm, chặn đổi tín chỉ | Chạy kiểm thử thành công ca D01 (6.60 & 7.00), D07 (GPA trọng số), D11 (tín chỉ 0, 1.5, trùng mã LT101) | Tinh chỉnh kiểm tra số nguyên cho tín chỉ, bổ sung cơ chế bảo vệ immutability khi đã có kết quả |
| 27/10 | Thành viên 3 (Hồ sơ sinh viên & Cặp SV–môn) | Antigravity (Gemini 3.8 Flash) | Thiết kế lớp Student, StudentManager, khung GradeRecord, validation hồ sơ và xử lý nghiệp vụ cặp SV–môn | Kiểm tra MSSV duy nhất, trim khoảng trắng, chống tạo dòng trùng cặp SV–môn, kiểm tra tồn tại 2 chiều, chặn xóa SV đã có điểm | Chạy kiểm thử thành công ca D05 (từ chối mã SV/môn không tồn tại, giữ nguyên dữ liệu mẫu) và D06 (nhập lại An–LT101 không tạo dòng trùng) | Cài đặt hàm xóa có điều kiện đối chiếu GradeRecord, chuẩn hóa tìm kiếm equalsIgnoreCase, bổ sung từ chối đổi dữ liệu một phần khi gặp lỗi, dùng tường minh this. |
