package com.wks.calorieapp.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * POJO representing Food Item in Foods database.
 *
 * @author Waqqas
 */
@Entity
@Table(name = "foods")
public class FoodEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "food_Id", nullable = false, unique = true)
    private long foodId;
    @Column(name = "name", length = 255, nullable = false)
    private String name;

    public long getFoodId() {
        return foodId;
    }

    public void setFoodId(long foodId) {
        this.foodId = foodId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String toString() {
        return String.format("[id: %d,name: %s]", foodId, name);
    }

}
