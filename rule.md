# QRCodeTools - JavaFX Standalone Application Rules

## 1. Tổng Quan Dự Án

| Thuộc tính | Giá trị |
|---|---|
| **Tên ứng dụng** | QRCodeTools |
| **Loại** | Ứng dụng Desktop độc lập (Standalone JavaFX) |
| **Group ID** | `com.baoths` |
| **Artifact ID** | `qrcodetools` |
| **Java Version** | 11+ |
| **JavaFX Version** | 13+ |
| **Build Tool** | Apache Maven |
| **Main Class** | `com.baoths.App` |
| **Lệnh chạy** | `mvn clean javafx:run` |

## 2. Cấu Trúc Thư Mục

```
qrcodetools/
├── pom.xml
├── rule.md
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/baoths/
│   │   │       ├── App.java                  # Entry point - Application class
│   │   │       ├── controller/               # FXML Controllers
│   │   │       │   ├── PrimaryController.java
│   │   │       │   └── ...Controller.java
│   │   │       ├── model/                    # Data models / POJOs
│   │   │       │   └── QRCodeData.java
│   │   │       ├── service/                  # Business logic
│   │   │       │   ├── QRCodeGenerator.java
│   │   │       │   └── QRCodeReader.java
│   │   │       └── util/                     # Tiện ích dùng chung
│   │   │           └── FileUtils.java
│   │   └── resources/
│   │       └── com/baoths/
│   │           ├── fxml/                     # FXML layout files
│   │           │   ├── primary.fxml
│   │           │   └── secondary.fxml
│   │           ├── css/                      # Stylesheet files
│   │           │   └── styles.css
│   │           ├── images/                   # Hình ảnh, icon
│   │           │   └── app-icon.png
│   │           └── i18n/                     # Đa ngôn ngữ (nếu cần)
│   │               └── messages.properties
│   └── test/
│       └── java/
│           └── com/baoths/
│               ├── service/
│               └── util/
└── target/
```

> [!IMPORTANT]
> Các file FXML **phải** nằm cùng package path với resources (`com/baoths/`) hoặc trong sub-folder `fxml/` để `FXMLLoader` có thể load được. Khi di chuyển FXML vào sub-folder, cần cập nhật đường dẫn trong `App.java`.

## 3. Kiến Trúc & Design Pattern

### 3.1. MVC (Model-View-Controller)

Áp dụng **MVC** nghiêm ngặt:

| Layer | Trách nhiệm | Vị trí |
|---|---|---|
| **Model** | Data class, business entity | `com.baoths.model` |
| **View** | Giao diện người dùng | FXML files + CSS trong `resources/` |
| **Controller** | Xử lý sự kiện UI, kết nối View ↔ Service | `com.baoths.controller` |
| **Service** | Business logic, xử lý QR code | `com.baoths.service` |
| **Util** | Helper, tiện ích dùng chung | `com.baoths.util` |

```
┌──────────────────────────────────────────┐
│              View (FXML + CSS)           │
│         primary.fxml, styles.css         │
└──────────────┬───────────────────────────┘
               │ fx:controller
┌──────────────▼───────────────────────────┐
│           Controller Layer               │
│   PrimaryController, QRController, ...   │
│   - Nhận event từ UI                     │
│   - Gọi Service để xử lý                │
│   - Cập nhật UI với kết quả             │
└──────────────┬───────────────────────────┘
               │ delegates
┌──────────────▼───────────────────────────┐
│           Service Layer                  │
│   QRCodeGenerator, QRCodeReader, ...     │
│   - Business logic thuần                 │
│   - KHÔNG phụ thuộc JavaFX              │
│   - Có thể test độc lập                 │
└──────────────┬───────────────────────────┘
               │ uses
┌──────────────▼───────────────────────────┐
│           Model Layer                    │
│   QRCodeData, QRCodeConfig, ...          │
│   - POJO / Data class                   │
│   - Có thể dùng JavaFX Properties       │
└──────────────────────────────────────────┘
```

### 3.2. Quy tắc phân tách

- **Controller** KHÔNG chứa business logic — chỉ điều phối giữa View và Service.
- **Service** KHÔNG import bất kỳ class nào từ `javafx.*` — đảm bảo test được mà không cần JavaFX runtime.
- **Model** có thể dùng `javafx.beans.property.*` khi cần data binding, nhưng ưu tiên POJO thuần cho đơn giản.

## 4. Quy Tắc Code

### 4.1. Quy ước đặt tên

| Loại | Convention | Ví dụ |
|---|---|---|
| Package | lowercase, dot-separated | `com.baoths.controller` |
| Class | PascalCase | `QRCodeGenerator` |
| Interface | PascalCase, prefix `I` hoặc suffix mô tả | `QRCodeService` |
| Method | camelCase, bắt đầu bằng động từ | `generateQRCode()` |
| Biến | camelCase | `qrContent` |
| Constant | UPPER_SNAKE_CASE | `MAX_QR_SIZE` |
| FXML file | kebab-case hoặc lowercase | `primary.fxml`, `qr-generator.fxml` |
| CSS file | kebab-case | `styles.css`, `dark-theme.css` |
| FXML `fx:id` | camelCase, prefix theo loại control | `btnGenerate`, `txtContent`, `lblStatus` |

### 4.2. FXML `fx:id` Prefix Convention

| Control Type | Prefix | Ví dụ |
|---|---|---|
| Button | `btn` | `btnGenerate`, `btnSave` |
| Label | `lbl` | `lblStatus`, `lblResult` |
| TextField | `txt` | `txtContent`, `txtFilePath` |
| TextArea | `txa` | `txaInput` |
| ImageView | `img` | `imgQRCode`, `imgPreview` |
| ComboBox | `cmb` | `cmbFormat` |
| CheckBox | `chk` | `chkTransparent` |
| ListView | `lst` | `lstHistory` |
| TableView | `tbl` | `tblResults` |
| ProgressBar | `prg` | `prgLoading` |
| Pane/VBox/HBox | `pnl` | `pnlMain`, `pnlSidebar` |

### 4.3. Javadoc & Comments

```java
/**
 * Tạo mã QR từ nội dung text.
 *
 * @param content nội dung cần mã hóa
 * @param width   chiều rộng (pixel)
 * @param height  chiều cao (pixel)
 * @return BufferedImage chứa mã QR
 * @throws QRGenerationException khi không thể tạo QR code
 */
public BufferedImage generateQRCode(String content, int width, int height)
        throws QRGenerationException {
    // ...
}
```

- **Mọi public class và public method** phải có Javadoc.
- Comment inline chỉ khi logic phức tạp, không comment những thứ hiển nhiên.
- Viết comment bằng **tiếng Việt hoặc tiếng Anh** — nhất quán trong toàn dự án.

## 5. JavaFX Specific Rules

### 5.1. Threading — JavaFX Application Thread

> [!CAUTION]
> **KHÔNG BAO GIỜ** thực hiện tác vụ nặng (I/O, encode/decode QR, network) trên JavaFX Application Thread. Sẽ gây đơ UI.

```java
// ✅ ĐÚNG - Dùng Task cho tác vụ nặng
Task<BufferedImage> generateTask = new Task<>() {
    @Override
    protected BufferedImage call() throws Exception {
        return qrService.generateQRCode(content, width, height);
    }
};

generateTask.setOnSucceeded(event -> {
    // Cập nhật UI ở đây — tự động trên FX Thread
    imgQRCode.setImage(SwingFXUtils.toFXImage(generateTask.getValue(), null));
    lblStatus.setText("Tạo QR thành công!");
});

generateTask.setOnFailed(event -> {
    lblStatus.setText("Lỗi: " + generateTask.getException().getMessage());
});

new Thread(generateTask).start();
```

```java
// ❌ SAI - Block UI thread
BufferedImage img = qrService.generateQRCode(content, 500, 500); // Block!
imgQRCode.setImage(SwingFXUtils.toFXImage(img, null));
```

```java
// ✅ Cập nhật UI từ background thread
Platform.runLater(() -> {
    lblStatus.setText("Đang xử lý...");
    prgLoading.setVisible(true);
});
```

### 5.2. FXML & Controller Binding

```java
public class QRGeneratorController implements Initializable {

    @FXML private TextField txtContent;
    @FXML private ImageView imgQRCode;
    @FXML private Button btnGenerate;
    @FXML private Label lblStatus;
    @FXML private ProgressBar prgLoading;

    private final QRCodeService qrService = new QRCodeService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Khởi tạo UI state
        prgLoading.setVisible(false);

        // Binding: disable button khi chưa nhập nội dung
        btnGenerate.disableProperty().bind(
            txtContent.textProperty().isEmpty()
        );
    }

    @FXML
    private void handleGenerate(ActionEvent event) {
        // Xử lý event
    }
}
```

**Quy tắc:**
- Controller **phải** implement `Initializable` khi cần setup ban đầu.
- Dùng `@FXML` annotation cho **mọi** element bind từ FXML.
- Các `@FXML` field phải `private`.
- Handler method format: `handle` + `Tên hành động` (ví dụ: `handleGenerate`, `handleSave`).

### 5.3. Scene Navigation

```java
// Trong App.java - quản lý chuyển scene tập trung
public class App extends Application {

    private static Scene scene;
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        scene = new Scene(loadFXML("primary"), 800, 600);
        scene.getStylesheets().add(
            getClass().getResource("css/styles.css").toExternalForm()
        );

        stage.setTitle("QRCodeTools");
        stage.setScene(scene);
        stage.setMinWidth(640);
        stage.setMinHeight(480);
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
            App.class.getResource("fxml/" + fxml + ".fxml")
        );
        return fxmlLoader.load();
    }
}
```

### 5.4. CSS Styling

```css
/* styles.css - Ví dụ theme cho QRCodeTools */
.root {
    -fx-font-family: "Segoe UI", "Arial", sans-serif;
    -fx-font-size: 14px;
    -fx-background-color: #f5f5f5;
}

.button-primary {
    -fx-background-color: #2196F3;
    -fx-text-fill: white;
    -fx-padding: 8 16;
    -fx-background-radius: 4;
    -fx-cursor: hand;
}

.button-primary:hover {
    -fx-background-color: #1976D2;
}
```

**Quy tắc CSS:**
- Dùng CSS class thay vì inline style trong FXML.
- Tên class CSS dùng `kebab-case`.
- Nhóm style theo component/section.
- Tạo file CSS riêng cho theme (nếu hỗ trợ dark/light mode).

## 6. Quản Lý Dependencies (pom.xml)

### 6.1. Dependencies cần thiết cho QRCodeTools

```xml
<dependencies>
    <!-- JavaFX -->
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-controls</artifactId>
        <version>${javafx.version}</version>
    </dependency>
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-fxml</artifactId>
        <version>${javafx.version}</version>
    </dependency>
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-swing</artifactId>
        <version>${javafx.version}</version>
    </dependency>

    <!-- QR Code Generation - ZXing -->
    <dependency>
        <groupId>com.google.zxing</groupId>
        <artifactId>core</artifactId>
        <version>${zxing.version}</version>
    </dependency>
    <dependency>
        <groupId>com.google.zxing</groupId>
        <artifactId>javase</artifactId>
        <version>${zxing.version}</version>
    </dependency>

    <!-- Testing -->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>5.9.3</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

### 6.2. Properties quản lý version tập trung

```xml
<properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <maven.compiler.source>11</maven.compiler.source>
    <maven.compiler.target>11</maven.compiler.target>
    <javafx.version>13</javafx.version>
    <zxing.version>3.5.2</zxing.version>
</properties>
```

> [!TIP]
> Luôn dùng `<properties>` để quản lý version — tránh hard-code version trong từng dependency.

### 6.3. Plugin để đóng gói Standalone App

Để build app độc lập (standalone), sử dụng các plugin sau:

```xml
<plugins>
    <!-- Compile -->
    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-compiler-plugin</artifactId>
        <version>3.11.0</version>
        <configuration>
            <release>11</release>
        </configuration>
    </plugin>

    <!-- Chạy JavaFX -->
    <plugin>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-maven-plugin</artifactId>
        <version>0.0.8</version>
        <configuration>
            <mainClass>com.baoths.App</mainClass>
        </configuration>
    </plugin>

    <!-- Đóng gói FAT JAR (standalone) -->
    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-shade-plugin</artifactId>
        <version>3.5.1</version>
        <executions>
            <execution>
                <phase>package</phase>
                <goals>
                    <goal>shade</goal>
                </goals>
                <configuration>
                    <transformers>
                        <transformer implementation=
                            "org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                            <mainClass>com.baoths.App</mainClass>
                        </transformer>
                    </transformers>
                </configuration>
            </execution>
        </executions>
    </plugin>
</plugins>
```

## 7. Build & Run

### 7.1. Lệnh thường dùng

| Hành động | Lệnh |
|---|---|
| Chạy app | `mvn clean javafx:run` |
| Compile | `mvn clean compile` |
| Chạy test | `mvn clean test` |
| Đóng gói JAR | `mvn clean package` |
| Đóng gói + skip test | `mvn clean package -DskipTests` |

### 7.2. Đóng gói Standalone

Để tạo file JAR chạy độc lập:

```bash
# Build fat JAR
mvn clean package

# Chạy standalone JAR
java -jar target/qrcodetools-1.jar
```

> [!WARNING]
> JavaFX 11+ không bundle cùng JDK. Khi phân phối standalone, cần đảm bảo:
> 1. Dùng `maven-shade-plugin` để bundle tất cả dependencies vào 1 JAR, **hoặc**
> 2. Dùng `jlink`/`jpackage` để tạo custom runtime image kèm JavaFX.

## 8. Xử Lý Lỗi & Logging

### 8.1. Exception Handling

```java
// ✅ Custom exception cho domain-specific errors
public class QRCodeException extends Exception {
    public QRCodeException(String message) {
        super(message);
    }
    public QRCodeException(String message, Throwable cause) {
        super(message, cause);
    }
}

// ✅ Trong Controller — hiển thị lỗi cho user
@FXML
private void handleGenerate() {
    try {
        BufferedImage qrImage = qrService.generateQRCode(txtContent.getText(), 300, 300);
        imgQRCode.setImage(SwingFXUtils.toFXImage(qrImage, null));
    } catch (QRCodeException e) {
        showAlert(Alert.AlertType.ERROR, "Lỗi tạo QR Code", e.getMessage());
    }
}

// ✅ Utility method hiển thị Alert
private void showAlert(Alert.AlertType type, String title, String content) {
    Alert alert = new Alert(type);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(content);
    alert.showAndWait();
}
```

### 8.2. Logging

```java
import java.util.logging.Logger;
import java.util.logging.Level;

public class QRCodeService {
    private static final Logger LOGGER = Logger.getLogger(QRCodeService.class.getName());

    public BufferedImage generateQRCode(String content, int w, int h)
            throws QRCodeException {
        LOGGER.info("Generating QR code: " + content.substring(0, Math.min(50, content.length())));
        try {
            // ... logic
        } catch (WriterException e) {
            LOGGER.log(Level.SEVERE, "Failed to generate QR code", e);
            throw new QRCodeException("Không thể tạo mã QR: " + e.getMessage(), e);
        }
    }
}
```

- Dùng `java.util.logging` (built-in) hoặc SLF4J nếu cần mạnh hơn.
- **KHÔNG** dùng `System.out.println()` cho logging.
- Log level: `SEVERE` cho lỗi, `WARNING` cho cảnh báo, `INFO` cho thông tin, `FINE/FINER` cho debug.

## 9. File I/O & FileChooser

```java
// Lưu QR code ra file
@FXML
private void handleSaveQRCode() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Lưu mã QR");
    fileChooser.setInitialFileName("qrcode.png");
    fileChooser.getExtensionFilters().addAll(
        new FileChooser.ExtensionFilter("PNG Image", "*.png"),
        new FileChooser.ExtensionFilter("JPEG Image", "*.jpg", "*.jpeg"),
        new FileChooser.ExtensionFilter("All Files", "*.*")
    );

    File file = fileChooser.showSaveDialog(App.getPrimaryStage());
    if (file != null) {
        try {
            ImageIO.write(currentQRImage, "PNG", file);
            lblStatus.setText("Đã lưu: " + file.getName());
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể lưu file: " + e.getMessage());
        }
    }
}

// Đọc QR code từ file ảnh
@FXML
private void handleLoadImage() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Chọn ảnh QR Code");
    fileChooser.getExtensionFilters().addAll(
        new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.bmp", "*.gif")
    );

    File file = fileChooser.showOpenDialog(App.getPrimaryStage());
    if (file != null) {
        // Xử lý đọc QR từ ảnh
    }
}
```

## 10. Testing

### 10.1. Cấu trúc Test

```
src/test/java/com/baoths/
├── service/
│   ├── QRCodeGeneratorTest.java
│   └── QRCodeReaderTest.java
├── util/
│   └── FileUtilsTest.java
└── model/
    └── QRCodeDataTest.java
```

### 10.2. Quy tắc Test

```java
class QRCodeGeneratorTest {

    private QRCodeService qrService;

    @BeforeEach
    void setUp() {
        qrService = new QRCodeService();
    }

    @Test
    @DisplayName("Tạo QR code thành công với nội dung hợp lệ")
    void generateQRCode_withValidContent_returnsImage() throws QRCodeException {
        BufferedImage result = qrService.generateQRCode("https://example.com", 300, 300);

        assertNotNull(result);
        assertEquals(300, result.getWidth());
        assertEquals(300, result.getHeight());
    }

    @Test
    @DisplayName("Throw exception khi nội dung rỗng")
    void generateQRCode_withEmptyContent_throwsException() {
        assertThrows(QRCodeException.class, () ->
            qrService.generateQRCode("", 300, 300)
        );
    }
}
```

**Quy tắc:**
- Tên method test: `methodName_condition_expectedResult`.
- Dùng `@DisplayName` mô tả bằng tiếng Việt hoặc tiếng Anh.
- **Chỉ test Service và Util** — không test Controller trực tiếp (cần TestFX nếu muốn).
- Mỗi test case phải **độc lập**, không phụ thuộc thứ tự chạy.

## 11. Quy Tắc Chung

### 11.1. KHÔNG làm

- ❌ KHÔNG hard-code đường dẫn file (`C:\Users\...`), dùng `FileChooser` hoặc relative path.
- ❌ KHÔNG catch `Exception` chung chung — catch exception cụ thể.
- ❌ KHÔNG để field `public` trong model — dùng getter/setter.
- ❌ KHÔNG dùng `System.out.println` cho logging.
- ❌ KHÔNG block JavaFX Application Thread với tác vụ nặng.
- ❌ KHÔNG import `*` (wildcard import).

### 11.2. NÊN làm

- ✅ Dùng `try-with-resources` cho mọi I/O operation.
- ✅ Validate input trước khi xử lý.
- ✅ Hiển thị trạng thái (loading, success, error) cho user.
- ✅ Dùng `Optional` thay vì return `null`.
- ✅ Đặt `minWidth`/`minHeight` cho Stage để tránh UI bị vỡ.
- ✅ Dùng CSS cho styling, không inline style.
- ✅ Close resources (streams, connections) đúng cách.
- ✅ Responsive layout — dùng `AnchorPane`, `GridPane`, `VBox`/`HBox` với constraint phù hợp.
