package org.example.models;

public class Items {
    private int itemId;
    private String name;
    private Raw_Material[] Raw_Materials;
    private int num_of_Required_Materials = 10;

    public Items(int itemId, String name, Raw_Material[] Raw_Materials) {
        this.itemId = itemId;
        this.name = name;
        this.Raw_Materials = Raw_Materials;
    }

    // Getters
    public int getItemId() { return itemId; }
    public String getName() { return name; }
    public Raw_Material[] getRaw_Materials() { return Raw_Materials; }
    public void setNum_of_Required_Materials(int num_of_Required_Materials) {}

    // Method to display the raw materials needed
    public void displayRaw_Materials() {
        System.out.println("Raw material requirements for " + name + " (Item ID: " + itemId + "):");
    }
}
