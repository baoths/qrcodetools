# QRCodeTools 🔲

`QRCodeTools` là ứng dụng desktop JavaFX dùng để **tạo**, **giải mã**, **quét webcam**, **batch generate** và **quản lý lịch sử** QR code.

Ứng dụng hỗ trợ giao diện tiếng Việt, theme tối/sáng, và có thể đóng gói thành installer `.exe` tự chứa runtime Java để người dùng cuối cài trực tiếp.

---

## Tính năng chính ✨

### 1) Tạo QR (Generator)
- Hỗ trợ nhiều loại dữ liệu: Text, URL, Email, Phone, WiFi, VCard, Maps.
- Tuỳ chỉnh: kích thước, mức sửa lỗi, hình module, màu/gradient, logo.
- Có guard an toàn để tránh QR khó quét:
  - chặn màu tương phản thấp,
  - giới hạn logo,
  - yêu cầu mức sửa lỗi phù hợp khi dùng logo.

### 2) Giải mã QR (Decoder)
- Giải mã từ file ảnh và kéo-thả (drag & drop).
- Dán ảnh từ clipboard để giải mã nhanh.
- Copy nội dung đã giải mã.

### 3) Quét bằng webcam
- Đọc QR trực tiếp từ webcam theo thời gian thực.

### 4) Batch generator
- Nhập danh sách nội dung và xuất hàng loạt ảnh QR.

### 5) Lịch sử phiên làm việc
- Lưu lịch sử tạo/giải mã, hỗ trợ xuất JSON/TXT.

---

## Công nghệ 🛠️

- Java 11+
- JavaFX 13
- Maven
- ZXing (generate/decode QR)
- webcam-capture
- Gson

---

## Chạy cho môi trường dev 🚀

Yêu cầu: JDK 11+

```bash
.\mvnw.cmd clean javafx:run
```

Build jar:

```bash
.\mvnw.cmd clean package
```

---

## Đóng gói installer `.exe` tự chứa Java (khuyến nghị)

Mục tiêu: người dùng chỉ tải 1 file `.exe` để cài/chạy, không cần cài Java riêng.

### Yêu cầu máy build
- Windows
- JDK có `jpackage` (khuyến nghị JDK 17+)
- WiX Toolset có `candle.exe`, `light.exe` trong `PATH`

Kiểm tra nhanh:

```bash
where.exe jpackage
where.exe candle
where.exe light
```

### Lệnh build

```bash
.\mvnw.cmd clean package -Pwindows-exe
```

### Artifact đầu ra
- Installer: `target\installer\QRCodeTools-1.0.0.exe`

> Ghi chú: profile `windows-exe` đang dùng `windows.app.version=1.0.0` trong `pom.xml` để phù hợp ràng buộc version của Windows Installer (`x.y` đến `x.y.z.w`).

---

## Fallback khi chưa có WiX

Bạn có thể tạo bản portable app-image (vẫn tự chứa runtime):

```bash
jpackage --type app-image --name QRCodeTools --input target --main-jar qrcodetools-1.jar --main-class com.baoths.Launcher --runtime-image "%JAVA_HOME%" --dest target\installer
```

---

Phát triển bởi Baoths.
