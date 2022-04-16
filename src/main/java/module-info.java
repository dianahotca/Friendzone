module com.example.socialnetworkguiapplication {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires java.desktop;

    exports com.example.socialnetworkguiapplication to javafx.base,javafx.graphics,javafx.fxml;

    opens domain;
    opens repository;
    opens service;
    opens com.example.socialnetworkguiapplication;
    requires org.apache.pdfbox;
}