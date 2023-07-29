package com.wks.calorieapp.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * POJO representing Image row in images database
 *
 * @author Waqqas
 */
@Entity
@Table(name = "images")
public class ImageEntry {
    @Id
    @Column(name = "image_id", nullable = false, unique = true)
    private String imageId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_id", referencedColumnName = "food_id", nullable = true)
    private FoodEntry food;
    @Column(name = "size")
    private long size;

    @Column(name = "is_finalized")
    private boolean finalized;

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public FoodEntry getFood() {
        return food;
    }

    public void setFood(FoodEntry food) {
        this.food = food;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public boolean isFinalized() {
        return finalized;
    }

    public void setFinalized(boolean finalized) {
        this.finalized = finalized;
    }

    @Override
    public String toString() {
        return String.format("[imageId: %s,foodId: %d,size: %d,finalized: %s]", getImageId(), getFood(), getSize(), isFinalized());
    }


}
