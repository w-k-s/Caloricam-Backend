package com.wks.calorieapp.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id", nullable = false, unique = true)
    private String imageId;
//    @OneToOne(
//            targetEntity = FoodEntry.class,
//            mappedBy = "foodId"
//    )
    private long foodId;
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

    public long getFoodId() {
        return foodId;
    }

    public void setFoodId(long foodId) {
        this.foodId = foodId;
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
        return String.format("[imageId: %s,foodId: %d,size: %d,finalized: %s]", getImageId(), getFoodId(), getSize(), isFinalized());
    }


}
