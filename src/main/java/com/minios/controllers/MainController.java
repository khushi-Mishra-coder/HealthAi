package com.minios.controllers;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML private StackPane contentArea;
    @FXML private Text txtClock;
    @FXML private Button btnDashboard, btnProcess, btnMemory, btnFileSystem, btnDeadlock, btnMultithreading, btnReports;

    private Button currentActiveButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currentActiveButton = btnDashboard;
        startClock();
        loadView("/com/minios/view/dashboard.fxml");
    }

    private void startClock() {
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            txtClock.setText(LocalDateTime.now().format(formatter));
        }), new KeyFrame(Duration.seconds(1)));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    private void setActiveButton(Button button) {
        currentActiveButton.getStyleClass().remove("nav-button-active");
        button.getStyleClass().add("nav-button-active");
        currentActiveButton = button;
    }

    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node node = loader.load();
            contentArea.getChildren().setAll(node);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleNavDashboard() {
        setActiveButton(btnDashboard);
        loadView("/com/minios/view/dashboard.fxml");
    }

    @FXML
    private void handleNavProcess() {
        setActiveButton(btnProcess);
        loadView("/com/minios/view/process_scheduling.fxml");
    }

    @FXML
    private void handleNavMemory() {
        setActiveButton(btnMemory);
        loadView("/com/minios/view/memory_management.fxml");
    }

    @FXML
    private void handleNavFileSystem() {
        setActiveButton(btnFileSystem);
        loadView("/com/minios/view/file_system.fxml");
    }

    @FXML
    private void handleNavDeadlock() {
        setActiveButton(btnDeadlock);
        loadView("/com/minios/view/deadlock_detection.fxml");
    }

    @FXML
    private void handleNavMultithreading() {
        setActiveButton(btnMultithreading);
        loadView("/com/minios/view/multithreading.fxml");
    }

    @FXML
    private void handleNavReports() {
        setActiveButton(btnReports);
        loadView("/com/minios/view/reports.xml"); // Wait, fxml or xml? Usually .fxml
    }
}
