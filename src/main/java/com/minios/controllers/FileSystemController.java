package com.minios.controllers;

import com.minios.models.OSFile;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;

public class FileSystemController implements Initializable {

    @FXML private TextField txtName;
    @FXML private TreeView<OSFile> fileTreeView;
    @FXML private ProgressBar progressStorage;

    private TreeItem<OSFile> rootItem;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        OSFile rootFile = new OSFile("Root (C:)", true);
        rootItem = new TreeItem<>(rootFile);
        rootItem.setExpanded(true);
        fileTreeView.setRoot(rootItem);

        // Add some sample data
        addSampleData();
    }

    private void addSampleData() {
        TreeItem<OSFile> system = new TreeItem<>(new OSFile("System", true));
        system.getChildren().add(new TreeItem<>(new OSFile("kernel.sys", false)));
        system.getChildren().add(new TreeItem<>(new OSFile("config.ini", false)));
        
        TreeItem<OSFile> users = new TreeItem<>(new OSFile("Users", true));
        TreeItem<OSFile> guest = new TreeItem<>(new OSFile("Guest", true));
        guest.getChildren().add(new TreeItem<>(new OSFile("document.txt", false)));
        users.getChildren().add(guest);

        rootItem.getChildren().addAll(system, users);
    }

    @FXML
    private void handleNewFolder() {
        String name = txtName.getText().trim();
        if (name.isEmpty()) return;

        TreeItem<OSFile> selected = fileTreeView.getSelectionModel().getSelectedItem();
        if (selected == null) selected = rootItem;
        if (!selected.getValue().isDirectory()) selected = selected.getParent();

        selected.getChildren().add(new TreeItem<>(new OSFile(name, true)));
        selected.setExpanded(true);
        txtName.clear();
    }

    @FXML
    private void handleNewFile() {
        String name = txtName.getText().trim();
        if (name.isEmpty()) return;

        TreeItem<OSFile> selected = fileTreeView.getSelectionModel().getSelectedItem();
        if (selected == null) selected = rootItem;
        if (!selected.getValue().isDirectory()) selected = selected.getParent();

        selected.getChildren().add(new TreeItem<>(new OSFile(name, false)));
        selected.setExpanded(true);
        txtName.clear();
        updateStorageProgress();
    }

    @FXML
    private void handleRename() {
        TreeItem<OSFile> selected = fileTreeView.getSelectionModel().getSelectedItem();
        if (selected == null || selected == rootItem) return;

        String name = txtName.getText().trim();
        if (name.isEmpty()) return;

        selected.getValue().setName(name);
        fileTreeView.refresh();
        txtName.clear();
    }

    @FXML
    private void handleDelete() {
        TreeItem<OSFile> selected = fileTreeView.getSelectionModel().getSelectedItem();
        if (selected == null || selected == rootItem) return;

        selected.getParent().getChildren().remove(selected);
        updateStorageProgress();
    }

    private void updateStorageProgress() {
        // Mock storage update
        double current = progressStorage.getProgress();
        progressStorage.setProgress(Math.min(1.0, current + 0.05));
    }
}
