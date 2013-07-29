package com.wks.calorieapp.api.fatsecret.entities;

import java.util.ArrayList;
import java.util.List;


public class FSFoods extends FSAbstractResponse
{
    private List<NutritionInfo> nutritionInfoList;
    
    public FSFoods()
    {
	nutritionInfoList = new ArrayList<NutritionInfo>();
    }
    
    public boolean add(NutritionInfo foodInfo)
    {
	return nutritionInfoList.add(foodInfo);
    }
    
    public List<NutritionInfo> getNutritionInfoList()
    {
	return this.nutritionInfoList;
    }
    
    @Override
    public String toString()
    {
        return String.format("[nutritionInfoList: %s]\n", nutritionInfoList.toString());
    }
}
