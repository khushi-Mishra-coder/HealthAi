package com.minios.controllers;

import com.minios.algorithms.Scheduler;
import com.minios.models.OSProcess;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ProcessController implements Initializable {

    @FXML private TextField txtName, txtArrival, txtBurst, txtPriority, txtQuantum;
    @FXML private ComboBox<String> comboAlgo;
    @FXML private TableView<OSProcess> processTable;
    @FXML private TableColumn<OSProcess, String> colName, colStatus;
    @FXML private TableColumn<OSProcess, Integer> colArrival, colBurst, colPriority, colWaiting, colTAT;
    @FXML private HBox ganttChartArea, timeAxisArea;

    private ObservableList<OSProcess> processList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        comboAlgo.setItems(FXCollections.observableArrayList("FCFS", "SJF", "Round Robin", "Priority"));
        comboAlgo.setValue("FCFS");
    }

    private void setupTable() {
        colName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        colArrival.setCellValueFactory(cellData -> cellData.getValue().arrivalTimeProperty().asObject());
        colBurst.setCellValueFactory(cellData -> cellData.getValue().burstTimeProperty().asObject());
        colPriority.setCellValueFactory(cellData -> cellData.getValue().priorityProperty().asObject());
        colWaiting.setCellValueFactory(cellData -> cellData.getValue().waitingTimeProperty().asObject());
        colTAT.setCellValueFactory(cellData -> cellData.getValue().turnaroundTimeProperty().asObject());
        colStatus.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        
        processTable.setItems(processList);
    }

    @FXML
    private void handleAddProcess() {
        try {
            String name = txtName.getText();
            int arrival = Integer.parseInt(txtArrival.getText());
            int burst = Integer.parseInt(txtBurst.getText());
            int priority = Integer.parseInt(txtPriority.getText());
            
            processList.add(new OSProcess(name, arrival, burst, priority));
            clearInputs();
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter valid numbers for time and priority.");
        }
    }

    private void clearInputs() {
        txtName.clear();
        txtArrival.clear();
        txtBurst.clear();
        txtPriority.clear();
    }

    @FXML
    private void handleRunSimulation() {
        if (processList.isEmpty()) return;

        String algo = comboAlgo.getValue();
        List<OSProcess> input = new ArrayList<>(processList);
        List<OSProcess> result;

        switch (algo) {
            case "SJF": result = Scheduler.runSJF(input); break;
            case "Round Robin": 
                int q = Integer.parseInt(txtQuantum.getText());
                result = Scheduler.runRoundRobin(input, q); 
                break;
            case "Priority": result = Scheduler.runPriority(input); break;
            default: result = Scheduler.runFCFS(input); break;
        }

        renderGanttChart(result);
        processTable.refresh();
    }

    @FXML
    private void handleClear() {
        processList.clear();
        ganttChartArea.getChildren().clear();
        timeAxisArea.getChildren().clear();
    }

    private void renderGanttChart(List<OSProcess> result) {
        ganttChartArea.getChildren().clear();
        timeAxisArea.getChildren().clear();

        int lastTime = 0;
        for (OSProcess p : result) {
            // Idle time if gap between processes
            if (p.getCompletionTime() - p.getBurstTime() > lastTime) {
                int idleTime = (p.getCompletionTime() - p.getBurstTime()) - lastTime;
                addGanttBlock("Idle", idleTime, Color.GRAY);
            }
            
            addGanttBlock(p.getName(), p.getBurstTime(), getColorForProcess(p.getName()));
            lastTime = p.getCompletionTime();
        }
    }

    private void addGanttBlock(String label, int duration, Color color) {
        VBox block = new VBox(5);
        block.setAlignment(javafx.geometry.Pos.CENTER);
        
        Rectangle rect = new Rectangle(duration * 40, 50);
        rect.setFill(color);
        rect.setStroke(Color.WHITE);
        rect.setArcWidth(10);
        rect.setArcHeight(10);
        
        Text txt = new Text(label);
        txt.setFill(Color.WHITE);
        txt.setStyle("-fx-font-weight: bold;");
        
        StackPane stack = new StackPane(rect, txt);
        ganttChartArea.getChildren().add(stack);
    }

    private Color getColorForProcess(String name) {
        int hash = name.hashCode();
        return Color.hsb(Math.abs(hash % 360), 0.7, 0.9);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
