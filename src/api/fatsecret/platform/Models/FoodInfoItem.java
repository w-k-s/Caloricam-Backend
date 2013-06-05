package api.fatsecret.platform.Models;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FoodInfoItem {
    
    private static final String REGEX_SPACE = "\\s";
    private static final String REGEX_DESCRIPTION = "Per([0-9.]+?)g-Calories:([0-9.]+?)kcal|Fat:([0-9.]+?)g|Carbs:([0-9.]+?)g|Protein:([0-9.]+?)g";
    private static final int NUM_MATCH_GROUPS = 5;
    private static final int MATCH_GROUP_SERVING_SIZE = 1;
    private static final int MATCH_GROUP_CALORIES = 2;
    private static final int MATCH_GROUP_FAT = 3;
    private static final int MATCH_GROUP_CARBS = 4;
    private static final int MATCH_GROUP_PROTEINS = 5;
    
    private long id;
    private String name;
    private String type;
    private String description;
    private String url;
    private float kiloCaloriesPer100g = 0;
    private float gramFatPer100g = 0;
    private float gramCarbsPer100g = 0; 
    private float gramProteinsPer100g = 0;
    
    public FoodInfoItem()
    {
	
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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
        return kiloCaloriesPer100g;
    }

    public void setCaloriesPer100g(float caloriesPer100g) {
        this.kiloCaloriesPer100g = caloriesPer100g;
    }

    public float getFatPer100g() {
        return gramFatPer100g;
    }

    public void setGramFatPer100g(float fatPer100g) {
        this.gramFatPer100g = fatPer100g;
    }

    public float getGramCarbsPer100g() {
        return gramCarbsPer100g;
    }

    public void setGramCarbsPer100g(float carbsPer100g) {
        this.gramCarbsPer100g = carbsPer100g;
    }

    public float getGramProteinsPer100g() {
        return gramProteinsPer100g;
    }

    public void setGramProteinsPer100g(float proteinsPer100g) {
        this.gramProteinsPer100g = proteinsPer100g;
    }

    public void setDescription(String description) {
        this.description = description;
        parseDescription();
    }
    
    public String getUrl() {
	return url;
    }
    
    public void setUrl(String url) {
	this.url = url;
    }
    
    private boolean parseDescription(){
	if(this.description != null && description.length()!=0)
	{
	    String description = this.description.replace(REGEX_SPACE, "");
	    Pattern descriptionPattern = Pattern.compile(REGEX_DESCRIPTION);
	    Matcher matcher = descriptionPattern.matcher(description);
	    
	    
	    if(matcher.find() && matcher.groupCount()==NUM_MATCH_GROUPS){
		float _servingSize = Float.parseFloat(matcher.group(MATCH_GROUP_SERVING_SIZE));
		float _kiloCalories = Float.parseFloat(matcher.group(MATCH_GROUP_CALORIES));
		float _gramFat = Float.parseFloat(matcher.group(MATCH_GROUP_FAT));
		float _gramCarbs = Float.parseFloat(matcher.group(MATCH_GROUP_CARBS));
		float _gramProteins = Float.parseFloat(matcher.group(MATCH_GROUP_PROTEINS));
	    
		
		setCaloriesPer100g(  _kiloCalories );//_kiloCalories/_servingSize * 100 );
		setGramFatPer100g ( _gramFat);//_gramFat/_servingSize * 100 );
		setGramCarbsPer100g ( _gramCarbs);//_gramCarbs/_servingSize * 100 );
		setGramProteinsPer100g ( _gramProteins);//_gramProteins/_servingSize * 100 );
		
		return true;
	    }
	    
	}
	
	//match groups were not found so calorie information will be 0.
	return false;
    }
    
    @Override
    public String toString() {
        return String.format("[id: %d,name: %s,type: %s,description: %s]", id,name,type,description);
    }
}
