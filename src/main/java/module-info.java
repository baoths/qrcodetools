module com.baoths {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires java.desktop;
    requires java.logging;
    requires com.google.zxing;
    requires com.google.zxing.javase;
    requires com.google.gson;
    requires webcam.capture;

    opens com.baoths to javafx.fxml;
    opens com.baoths.controller to javafx.fxml;
    opens com.baoths.model to javafx.fxml, com.google.gson;

    exports com.baoths;
    exports com.baoths.controller;
    exports com.baoths.model;
    exports com.baoths.service;
    exports com.baoths.util;
    exports com.baoths.exception;
}
