package com.example.apps4good;

import Jama.SingularValueDecomposition;
import Jama.Matrix;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This SubDatabase is capable of storing
 *
 * @author Venkat
 *
 */
public class SubDatabase {
    // Data
    private double[][] recipesMatrix;
    private ArrayList<Recipe> recipes;
    private ArrayList<Ingredient> ingredients;

    // Constructors
    public SubDatabase() {
        recipes = new ArrayList<Recipe>();
        ingredients = new ArrayList<Ingredient>();
    }

    // Methods
    /**
     * Adds a recipe to the SubDatabase
     *
     * @param newRecipe recipe to add
     */
    public void addRecipe(Recipe newRecipe) {
        if (!recipes.contains(newRecipe)) {
            for (Ingredient i : newRecipe.getIngredients()) {
                if (!ingredients.contains(i)) {
                    ingredients.add(i);
                }
            }
            recipes.add(newRecipe);
        }
    }

    /**
     * Gets the singular value decomposition of the int[][] generated by the
     * toMatrix() method
     *
     * @param matrix
     * @return
     */
    public SingularValueDecomposition getSVD() {
        return new SingularValueDecomposition(new Matrix(recipesMatrix));
    }

    /**
     * Creates a matrix representation of the ingredients in each recipe
     *
     * @return
     */
    public double[][] toMatrix() {
        recipesMatrix = new double[getRecipes().size()][getIngredients().size()];
        for (int i = 0; i < getRecipes().size(); i++) {
            Recipe r = getRecipe(i);
            for (int j = 0; j < getIngredients().size(); j++) {
                Ingredient ing = getIngredients().get(j);
                if (r.getIngredients().contains(ing)) {
                    recipesMatrix[i][j] = 1;
                } else {
                    recipesMatrix[i][j] = 0;
                }
            }
        }
        return recipesMatrix;
    }

    public ArrayList<Recipe> getRecipes() {
        return recipes;
    }

    public ArrayList<Ingredient> getIngredients() {
        return ingredients;
    }

    /**
     *
     * @param index
     * @return the Recipe corresponding to the provided index
     */
    public Recipe getRecipe(int index) {
        return recipes.get(index);
    }

    public ArrayList<Ingredient> getRecommendation(User user) {
        return null;
    }

    /**
     * @return the recipesMatrix
     */
    public double[][] getRecipesMatrix() {
        return recipesMatrix;
    }

    @Override
    public String toString() {
        String output = "Ingredients\n" + ingredients + "\n\nRecipes:";
        for (int i = 0; i < getRecipes().size(); i++) {
            output += "\n" + getRecipes().get(i);
        }
        return output;
    }
}
