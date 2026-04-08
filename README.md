# QRCodeTools 🔲

**QRCodeTools** là một ứng dụng tiện ích độc lập (Desktop App) được xây dựng bằng công nghệ **JavaFX**, chuyên dùng để tạo, giải mã, quản lý, và quét mã QR Code trực tiếp từ máy tính mà không cần kết nối cơ sở dữ liệu.

Ứng dụng hỗ trợ giao diện song ngữ Tiếng Việt, sử dụng mô hình MVC với Dark Mode hiện đại, tối ưu hóa để có thể chạy như một tệp thực thi (`.exe`) gọn nhẹ trên mọi máy tính.

---

## Tính Năng Nổi Bật ✨

Ứng dụng được chia thành 5 phân hệ chính với 11 chức năng đáp ứng đầy đủ nhu cầu của một công cụ mã QR chuyên nghiệp:

### 1. 📝 Bộ Tạo Mã QR (Generator)
- **Đa dạng dữ liệu**: Hỗ trợ tạo mã từ Văn bản (Text), Trang web (URL), Email, Số điện thoại di động, Kết nối WiFi, Danh bạ (VCard), Địa điểm bản đồ (Maps).
- **Tùy biến nâng cao**:
  - Tùy chọn 3 kích thước: Lớn, Nhỏ, Vừa và 4 độ phức tạp khôi phục lỗi (H/Q/M/L).
  - Tùy chọn hình dáng pixel: Vuông, Tròn, Kim cương.
  - Tùy biến màu đa dạng, hỗ trợ cả màu Solid hoặc dải Gradient chéo thời thượng.
  - Cho phép chèn ảnh Logo/Watermark chính giữa mã QR.
- **Thao tác nhanh**: Copy mã vào Clipboard, Lưu trực tiếp ra `.png/.jpg`, In ảnh trực tiếp từ phần mềm.

### 2. 🔍 Giải Mã Từ Ảnh (Decoder)
- Đọc nội dung mã QR cực nhanh từ tệp ảnh sẵn có.
- Trải nghiệm tuyệt vời với **Drag & Drop** (Kéo thả ảnh thẳng vào ứng dụng để đọc).
- Sao chép ngược nội dung text vừa nhận diện.

### 3. 📷 Máy Quét Thực Tế (Webcam Reader)
- Kết nối trực tiếp vào phần cứng Webcam/Camera của máy tính (thông qua `webcam-capture`).
- Quét liên tục (Live view) - chỉ cần giơ mã QR trước ống kính, app sẽ tự động bắt diện và đọc nội dung siêu tốc.
- Tự động bỏ qua các ảnh đã quét trùng lặp trong cùng phiên làm việc.

### 4. 🗃️ Xử Lý Hàng Loạt (Batch Generator)
- Nhập mảng/danh sách ngẫu nhiên hàng ngàn hàng text, tự động cắt theo dấu xuống dòng.
- Theo dõi tiến độ sinh mã QR bằng hình ảnh trực quan (Progress Bar).
- Tự động xuất chuỗi các ảnh vừa tạo vào thư mục lưu trữ được chỉ định `qrcode_1.png...qrcode_N.png`.

### 5. 📜 Nhật Ký & Lịch Sử (Session History)
- Ghi nhận lại toàn bộ thao tác trong 1 phiên làm việc của người dùng (tạo/quét).
- Xem chi tiết nội dung, loại QR, hành động, kích thước và thời gian thực hiện.
- Trích xuất toàn bộ lịch sử ra tệp `.json` (để dev đọc/lưu) hoặc `.txt`.

---

## Công Nghệ Sử Dụng 🛠️

- **Nền tảng**: Java 11+, JavaFX 13 (OpenJFX).
- **Build Tool**: Maven (`maven-shade-plugin`, `launch4j-maven-plugin`).
- **Thư viện cốt lõi**:
  - `ZXing` (Zebra Crossing) - Xử lý giải pháp tạo và đọc mã vạch QR.
  - `webcam-capture` - Giao tiếp với thiết bị máy quay qua luồng native.
  - `Gson` - Quản lý tệp dữ liệu xuất nhập JSON.
- **Kiến trúc**: Strict MVC (Controller & Service độc lập), JavaFX Task Background Threads.
- **Giao diện**: CSS Module, Dark / Light Theme Switching (Chạm để đổi màu Sáng/Tối).

---

## Cài Đặt & Chạy 🚀

### Sử Dụng Trực Tiếp (.EXE)
Bạn có thể tìm tải và chạy `QRCodeTools.exe`.

### Phục Vụ Phát Triển (Dành Cho Dev)
1. Hãy đảm bảo bạn đã cài đặt Java JDK 11 trở lên.
2. Clone dự án và đặt tại thư mục làm việc.
3. Chạy qua **Maven Wrapper** tích hợp sẵn:
   ```bash
   # Tải plugin, biên dịch và chạy bằng JavaFX Plugin
   .\mvnw.cmd clean javafx:run
   ```
4. Build phần mềm (Gói thành file tệp tin FAT JAR và `.exe` Windows qua Launch4j):
   ```bash
   .\mvnw.cmd clean package
   ```

---
*Phát triển bởi Baoths. Bản quyền tuân thủ với cấu trúc ứng dụng tiện ích thông minh.*
