package com.example.apps4good;
import Jama.SingularValueDecomposition;
import Jama.Matrix;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * This SubDatabase is capable of storing data about a group of recipes. It also
 * gives access to an ArrayList of all ingredients used in any recipe. The main
 * functionality of this class is the getRecommendation() method, which provides
 * a recommendation of what ingredients a user may enjoy based on their likes
 * and dislikes.
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
    /**
     * Default constructor. Initializes two ArrayLists, one of recipes, and one of
     * ingredients.
     */
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
        return new SingularValueDecomposition(new Matrix(toMatrix()));
    }

    /**
     * Creates a matrix representation of the ingredients in each recipe. Each row
     * represents a recipe and each column represents an ingredient. The rows
     * correspond to the order in getRecipes() and the columns correspond to the
     * order in getIngredients().
     *
     * @return recipesMatrix
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

    /**
     * Returns a list of all recipes added to this SubDatabase
     *
     * @return recipes in the order they were added
     */
    public ArrayList<Recipe> getRecipes() {
        return recipes;
    }

    /**
     * Returns an ArrayList of all the ingredients used in all of the recipes by
     * order of their corresponding recipe. It contains no repeats.
     *
     * @return the ingredients
     */
    public ArrayList<Ingredient> getIngredients() {
        return ingredients;
    }

    /**
     * This method gets the recipe from the ArrayList<Ingredient> corresponding to
     * the index provided
     *
     * @param index
     * @return the recipe corresponding to the index provided
     */
    public Recipe getRecipe(int index) {
        return recipes.get(index);
    }

    /**
     * This method uses Singular Value Decomposition(SVD) to sort ingredients based
     * on how much the provided user is predicted to like them. The first ingredient
     * in the array is predicted to be the most liked by the user and the last
     * element is the least liked. IMPORTANT NOTE:the likes and dislikes of the user
     * are included in the array.
     *
     * @param user whose likes and dislikes are being analyzed
     * @return an ArrayList sorted by how much the user likes each ingredient. null
     *         if the user does not have any likes or dislikes
     */
    public ArrayList<Ingredient> getRecommendation(User user) {
        return getRecommendation(user, getIngredients().size() - 1);
    }

    /**
     * This method uses Singular Value Decomposition(SVD) to sort ingredients based
     * on how much the provided user is predicted to like them. The first ingredient
     * in the array is predicted to be the most liked by the user and the last
     * element is the least liked. IMPORTANT NOTE: the likes and dislikes of the
     * user are included in the array.
     *
     * @param user             whose likes and dislikes are being analyzed
     * @param valuesToConsider the number of ingredients to consider must be between
     *                         0 (inclusive) and the number of ingredients
     *                         (exclusive)
     * @return an ArrayList sorted by how much the user likes each ingredient.
     *         returns null if the user does not have any likes or dislikes or if
     *         the valuesToConsider is invalid
     */
    public ArrayList<Ingredient> getRecommendation(User user, int valuesToConsider) {
        // Checking if the parameters are invalid.
        if (user.getPreferences().size() == 0 || valuesToConsider < 0 || valuesToConsider >= ingredients.size()) {
            return null;
        }

        // Get user preferences
        HashMap<Ingredient, Boolean> preferences = user.getPreferences();

        // Convert the user preferences into a double[][]. The preferencesArr indicates
        // whether or not the user likes to corresponding Ingredient in ingredients. 1
        // means they like it, -1 means they do not like it, and 0 means they have not
        // responded.
        double[][] preferencesArr = new double[1][ingredients.size()];
        for (int i = 0; i < ingredients.size(); i++) {
            try {
                Boolean like = preferences.get(ingredients.get(i));
                if (like) {
                    preferencesArr[0][i] = 1;
                } else {
                    preferencesArr[0][i] = -1;
                }
            } catch (Exception e) {
                preferencesArr[0][i] = 0;
            }
        }

        // Calculate the recommendation using the SVD
        Matrix preferencesMatrix = new Matrix(preferencesArr);
        Matrix v = getSVD().getV();
        Matrix vTrimmed = v.getMatrix(0, ingredients.size() - 1, 0, valuesToConsider);
        v.print(0, 4);
        vTrimmed.print(0, 4);
        // The userRecommendation contains a projected score for how much a suer would
        // like or dislike every ingredient.
        double[] tempRecommendationStorage = preferencesMatrix.times(vTrimmed).times(vTrimmed.transpose())
                .getArray()[0];

        // Order the ingredients from highest to lowest score from the SVD.
        ArrayList<Ingredient> userRecommendation = new ArrayList<Ingredient>();
        System.out.println(Arrays.toString(tempRecommendationStorage));
        while (!(getMaxIngredient(tempRecommendationStorage, false) == null)) {
            userRecommendation.add(getMaxIngredient(tempRecommendationStorage, true));
        }
        return userRecommendation;
    }

    /**
     * Returns a string containing a list of all ingredients used, and a list of all
     * recipes and which ingredients each contains.
     */
    @Override
    public String toString() {
        String output = "Ingredients\n" + ingredients + "\n\nRecipes:";
        for (int i = 0; i < getRecipes().size(); i++) {
            output += "\n" + getRecipes().get(i);
        }
        return output;
    }

    /**
     * A helper method for getRecommendation. It returns the Ingredient that the
     * user is most likely to like and also has the option to replace the maximum
     * value with negative infinity, ensuring that the same ingredient will not be
     * chosen twice.
     *
     * @param array       tempRecommendationStorage, the output of the SVD and
     *                    multiplication
     * @param setToNegInf if true, the greatest value in the array will be replaced
     *                    with Double.NEGATIVE_INFINITY
     * @return the Ingredient corresponding to the maximum value. If all values in
     *         the input array are Double.NEGATIVE_INFINITY, it will return null
     */
    private Ingredient getMaxIngredient(double[] array, boolean setToNegInf) {
        int index = -1;
        double max = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < array.length; i++) {
            if (array[i] > max) {
                index = i;
                max = array[i];
            }
        }
        if (setToNegInf) {
            array[index] = Double.NEGATIVE_INFINITY;
        }
        try {
            return ingredients.get(index);
        } catch (Exception e) {
            return null;
        }
    }
}
