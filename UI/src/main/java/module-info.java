module com.example.facerecognitionsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.xerial.sqlitejdbc;
    requires java.desktop;

    opens com.example.facerecognitionsystem to javafx.fxml;
    opens com.example.facerecognitionsystem.controller to javafx.fxml;
    opens com.example.facerecognitionsystem.model to javafx.base;
    exports com.example.facerecognitionsystem;
}