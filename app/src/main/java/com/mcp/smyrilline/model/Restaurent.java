package com.mcp.smyrilline.model;

/**
 * Created by saiful on 5/18/17.
 */

public class Restaurent {


    private String RecipeName;
    private String RecipeDetails;

    public Restaurent(String recipeName, String recipeDetails) {
        RecipeName = recipeName;
        RecipeDetails = recipeDetails;
    }

    public String getRecipeName() {
        return RecipeName;
    }

    public void setRecipeName(String recipeName) {
        RecipeName = recipeName;
    }

    public String getRecipeDetails() {
        return RecipeDetails;
    }

    public void setRecipeDetails(String recipeDetails) {
        RecipeDetails = recipeDetails;
    }
}
