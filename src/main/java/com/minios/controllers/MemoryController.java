package com.minios.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.*;

public class MemoryController implements Initializable {

    @FXML private TextField txtFrames, txtRefString;
    @FXML private ComboBox<String> comboAlgo;
    @FXML private Text txtHits, txtMisses, txtHitRatio;
    @FXML private GridPane gridMemory;
    @FXML private ListView<String> logView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        comboAlgo.setItems(javafx.collections.FXCollections.observableArrayList("FIFO", "LRU"));
        comboAlgo.setValue("FIFO");
    }

    @FXML
    private void handleSimulate() {
        int framesCount = Integer.parseInt(txtFrames.getText());
        String[] refs = txtRefString.getText().split("\\s+");
        
        gridMemory.getChildren().clear();
        logView.getItems().clear();
        
        if (comboAlgo.getValue().equals("FIFO")) {
            runFIFO(refs, framesCount);
        } else {
            runLRU(refs, framesCount);
        }
    }

    private void runFIFO(String[] refs, int framesCount) {
        Queue<String> frames = new LinkedList<>();
        Set<String> set = new HashSet<>();
        int hits = 0, misses = 0;

        for (int i = 0; i < refs.length; i++) {
            String page = refs[i];
            boolean isHit = set.contains(page);
            
            if (isHit) {
                hits++;
                logView.getItems().add("Step " + (i+1) + ": Page " + page + " -> HIT");
            } else {
                misses++;
                if (frames.size() == framesCount) {
                    String removed = frames.poll();
                    set.remove(removed);
                }
                frames.add(page);
                set.add(page);
                logView.getItems().add("Step " + (i+1) + ": Page " + page + " -> MISS (Replaced)");
            }
            updateGrid(i, page, frames, isHit, framesCount);
        }
        updateStats(hits, misses, refs.length);
    }

    private void runLRU(String[] refs, int framesCount) {
        List<String> frames = new ArrayList<>();
        int hits = 0, misses = 0;

        for (int i = 0; i < refs.length; i++) {
            String page = refs[i];
            boolean isHit = frames.contains(page);
            
            if (isHit) {
                hits++;
                frames.remove(page);
                frames.add(page); // Move to most recently used
                logView.getItems().add("Step " + (i+1) + ": Page " + page + " -> HIT");
            } else {
                misses++;
                if (frames.size() == framesCount) {
                    frames.remove(0); // Remove least recently used
                }
                frames.add(page);
                logView.getItems().add("Step " + (i+1) + ": Page " + page + " -> MISS (Replaced LRU)");
            }
            updateGrid(i, page, new LinkedList<>(frames), isHit, framesCount);
        }
        updateStats(hits, misses, refs.length);
    }

    private void updateGrid(int step, String page, Queue<String> currentFrames, boolean isHit, int maxFrames) {
        // Add header for the step
        Text stepTxt = new Text("S" + (step + 1) + "\n(" + page + ")");
        stepTxt.setFill(Color.WHITE);
        gridMemory.add(stepTxt, step, 0);

        List<String> frameList = new ArrayList<>(currentFrames);
        for (int f = 0; f < maxFrames; f++) {
            StackPane cell = new StackPane();
            Rectangle rect = new Rectangle(40, 40);
            rect.setStroke(Color.WHITE);
            rect.setFill(isHit && f < frameList.size() && frameList.get(f).equals(page) ? Color.web("#4CAF50") : Color.web("#333333"));
            
            Text txt = new Text(f < frameList.size() ? frameList.get(f) : "-");
            txt.setFill(Color.WHITE);
            
            cell.getChildren().addAll(rect, txt);
            gridMemory.add(cell, step, f + 1);
        }
    }

    private void updateStats(int hits, int misses, int total) {
        txtHits.setText(String.valueOf(hits));
        txtMisses.setText(String.valueOf(misses));
        txtHitRatio.setText(String.format("%.1f%%", (double) hits / total * 100));
    }

    @FXML
    private void handleReset() {
        gridMemory.getChildren().clear();
        logView.getItems().clear();
        txtHits.setText("0");
        txtMisses.setText("0");
        txtHitRatio.setText("0%");
    }
}
