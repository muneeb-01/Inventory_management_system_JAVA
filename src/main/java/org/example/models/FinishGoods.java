package org.example.models;

public class FinishGoods {
    private int finishedGoodsId;
    private String name;
    private int quantity;
    private int receiverId;

    public FinishGoods(int finishedGoodsId, String name, int quantity, int receiverId) {
        this.finishedGoodsId = finishedGoodsId;
        this.name = name;
        this.quantity = quantity;
        this.receiverId = receiverId;
    }

    // Getters and setters
    public int getFinishedGoodsId() { return finishedGoodsId; }
    public void setFinishedGoodsId(int finishedGoodsId) { this.finishedGoodsId = finishedGoodsId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public int getReceiverId() { return receiverId; }
    public void setReceiverId(int receiverId) { this.receiverId = receiverId; }
}

