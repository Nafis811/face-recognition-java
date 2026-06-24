package com.example.facerecognitionsystem;

import com.example.facerecognitionsystem.repository.DatabaseSetup;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        DatabaseSetup.initialize(); // setup database saat pertama kali jalan
        stage.setTitle("NUSA LMS - Face Login");
        stage.setMinWidth(900);
        stage.setMinHeight(680);
        navigateTo("main-view.fxml");
        stage.show();
    }

    public static void navigateTo(String fxmlFile) {
        try {
            boolean isMaximized = primaryStage.isMaximized();
            double width = primaryStage.getWidth();
            double height = primaryStage.getHeight();
            double x = primaryStage.getX();
            double y = primaryStage.getY();

            Parent root = FXMLLoader.load(
                    HelloApplication.class.getResource(
                            "/com/example/facerecognitionsystem/" + fxmlFile));

            if (primaryStage.getScene() == null) {
                primaryStage.setScene(new Scene(root, 900, 680));
            } else {
                primaryStage.getScene().setRoot(root);
            }

            if (isMaximized) {
                primaryStage.setMaximized(true);
            } else {
                primaryStage.setWidth(width);
                primaryStage.setHeight(height);
                primaryStage.setX(x);
                primaryStage.setY(y);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}