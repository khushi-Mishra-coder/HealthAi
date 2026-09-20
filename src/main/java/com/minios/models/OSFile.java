package com.minios.models;

import java.util.ArrayList;
import java.util.List;

public class OSFile {
    private String name;
    private boolean isDirectory;
    private final List<OSFile> children;

    public OSFile(String name, boolean isDirectory) {
        this.name = name;
        this.isDirectory = isDirectory;
        this.children = new ArrayList<>();
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public boolean isDirectory() { return isDirectory; }
    public List<OSFile> getChildren() { return children; }

    @Override
    public String toString() {
        return name;
    }
}
