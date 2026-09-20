package com.minios.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ProgressBar;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML private Text txtCpuLoad, txtRamUsage, txtProcessCount;
    @FXML private ProgressBar progressCpu, progressRam;
    @FXML private AreaChart<Number, Number> cpuChart;
    @FXML private PieChart taskChart;

    private XYChart.Series<Number, Number> cpuSeries;
    private int time = 0;
    private final Random random = new Random();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupCharts();
        startSimulation();
    }

    private void setupCharts() {
        cpuSeries = new XYChart.Series<>();
        cpuSeries.setName("CPU Load");
        cpuChart.getData().add(cpuSeries);
        cpuChart.setAnimated(false);

        taskChart.getData().addAll(
                new PieChart.Data("System", 20),
                new PieChart.Data("Processes", 45),
                new PieChart.Data("I/O", 15),
                new PieChart.Data("Idle", 20)
        );
    }

    private void startSimulation() {
        Timeline simulation = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            updateMetrics();
        }));
        simulation.setCycleCount(Timeline.INDEFINITE);
        simulation.play();
    }

    private void updateMetrics() {
        double cpuLoad = 20 + random.nextDouble() * 40;
        double ramUsage = 1000 + random.nextInt(2000); // 1-3 GB
        int processes = 50 + random.nextInt(20);

        txtCpuLoad.setText(String.format("%.1f%%", cpuLoad));
        progressCpu.setProgress(cpuLoad / 100.0);

        txtRamUsage.setText(String.format("%.0f MB", ramUsage));
        progressRam.setProgress(ramUsage / 8192.0); // Assuming 8GB total

        txtProcessCount.setText(String.valueOf(processes));

        cpuSeries.getData().add(new XYChart.Data<>(time++, cpuLoad));
        if (cpuSeries.getData().size() > 20) {
            cpuSeries.getData().remove(0);
        }
    }
}
