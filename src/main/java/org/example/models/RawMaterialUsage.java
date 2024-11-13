package org.example.models;

import java.sql.Connection;

public class RawMaterialUsage {
    private int usageId;
    private int finishedGoodsId;
    private int materialId;
    private int quantityUsed;

    public RawMaterialUsage(int usageId, int finishedGoodsId, int materialId, int quantityUsed) {
        this.usageId = usageId;
        this.finishedGoodsId = finishedGoodsId;
        this.materialId = materialId;
        this.quantityUsed = quantityUsed;
    }

    public int getUsageId() { return usageId; }
    public void setUsageId(int usageId) { this.usageId = usageId; }

    public int getFinishedGoodsId() { return finishedGoodsId; }
    public void setFinishedGoodsId(int finishedGoodsId) { this.finishedGoodsId = finishedGoodsId; }

    public int getMaterialId() { return materialId; }
    public void setMaterialId(int materialId) { this.materialId = materialId; }

    public int getQuantityUsed() { return quantityUsed; }
    public void setQuantityUsed(int quantityUsed) { this.quantityUsed = quantityUsed; }
}

