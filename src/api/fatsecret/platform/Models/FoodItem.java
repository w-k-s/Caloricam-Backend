package api.fatsecret.platform.Models;

public class FoodItem {
    private int id;
    private String name;
    private String type;
    private String description;
    private float caloriesPer100g;
    private float fatPer100g;
    private float carbsPer100g;
    private float proteinsPer100g;
    
    public FoodItem()
    {
	
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public float getCaloriesPer100g() {
        return caloriesPer100g;
    }

    public void setCaloriesPer100g(float caloriesPer100g) {
        this.caloriesPer100g = caloriesPer100g;
    }

    public float getFatPer100g() {
        return fatPer100g;
    }

    public void setFatPer100g(float fatPer100g) {
        this.fatPer100g = fatPer100g;
    }

    public float getCarbsPer100g() {
        return carbsPer100g;
    }

    public void setCarbsPer100g(float carbsPer100g) {
        this.carbsPer100g = carbsPer100g;
    }

    public float getProteinsPer100g() {
        return proteinsPer100g;
    }

    public void setProteinsPer100g(float proteinsPer100g) {
        this.proteinsPer100g = proteinsPer100g;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    

}
