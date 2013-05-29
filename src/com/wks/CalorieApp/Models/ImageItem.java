package com.wks.CalorieApp.Models;

/**
 * POJO representing Image row in images database
 * @author Waqqas
 *
 */
public class ImageItem {
    private String imageId;
    private long foodId;
    private long size;
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
        return String.format("[imageId: %s,foodId: %d,size: %d,finalized: %s]",getImageId(),getFoodId(),getSize(),isFinalized());
    }
    
    
}
