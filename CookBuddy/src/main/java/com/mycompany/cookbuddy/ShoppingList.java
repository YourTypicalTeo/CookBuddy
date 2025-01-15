package com.mycompany.cookbuddy;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JTextArea;

public class ShoppingList {

    // Pattern for identifying ingredients in recipe steps
    private static final Pattern INGREDIENT_PATTERN = Pattern.compile("@([\\p{L}0-9_\\- ]+)\\{([^}%]+)%?([^}]*)\\}");

    // Map for converting custom units to standard ones (grams, liters)
    private static final Map<String, Double> CUSTOM_UNIT_TO_STANDARD = createCustomUnitToStandardMap();

    // Set for countable items (used for piece-based quantities)
    private static final Set<String> COUNTABLE_ITEMS = Set.of("αυγα", "κομματια");

    private JTextArea displayArea; // To update the GUI with the shopping list

    public ShoppingList(JTextArea displayArea) {
        this.displayArea = displayArea;
    }

    // Method to generate and display the shopping list based on recipe ingredients
    public void generateShoppingList(List<Recipe> recipes) {
        Map<String, Ingredient> ingredientTotals = new HashMap<>(); // Map for total quantities of ingredients

        // Loop through each recipe to extract ingredient data
        for (Recipe recipe : recipes) {
            for (String step : recipe.getSteps()) {
                Matcher matcher = INGREDIENT_PATTERN.matcher(step);

                // Process each identified ingredient
                while (matcher.find()) {
                    String ingredientName = matcher.group(1).trim(); // Ingredient name
                    String quantityStr = matcher.group(2).trim();   // Quantity
                    String unit = matcher.group(3).trim();          // Measurement unit

                    // Parse the ingredient's quantity and normalize its unit
                    Ingredient ingredient = parseIngredient(ingredientName, quantityStr, unit);

                    // Accumulate quantities for the same ingredient
                    ingredientTotals.putIfAbsent(ingredient.getName(), new Ingredient(ingredientName, 0.0, ingredient.getUnit()));
                    Ingredient existingIngredient = ingredientTotals.get(ingredientName);

                    if (existingIngredient.getUnit().equals(ingredient.getUnit())) {
                        existingIngredient.setQuantity(existingIngredient.getQuantity() + ingredient.getQuantity());
                    } else {
                        displayArea.append("Warning: Mismatched units for ingredient: " + ingredientName + "\n");
                    }
                }
            }
        }

        // Display the final shopping list in the JTextArea
        displayArea.append("\nShopping List:\n");
        for (Ingredient ingredient : ingredientTotals.values()) {
            displayArea.append(defineUnit(ingredient.getUnit(), ingredient.getQuantity()) + " " + ingredient.getName() + "\n");
        }
    }

    // Parse the ingredient details and normalize the quantity and unit
    private Ingredient parseIngredient(String name, String quantityStr, String unit) {
        try {
            double quantity = Double.parseDouble(quantityStr); // Parse numeric quantity

            // Check for countable items (pieces)
            if (COUNTABLE_ITEMS.contains(unit.toLowerCase())) {
                return new Ingredient(name, quantity, "pcs"); // Handle as pieces
            }

            // Check for custom units (e.g., pinch, teaspoon)
            if (CUSTOM_UNIT_TO_STANDARD.containsKey(unit.toLowerCase())) {
                double normalizedQuantity = quantity * CUSTOM_UNIT_TO_STANDARD.get(unit.toLowerCase()); // Convert to grams or milliliters
                return new Ingredient(name, normalizedQuantity, getStandardUnit(unit));
            }

            return new Ingredient(name, quantity, unit); // Return for standard units
        } catch (NumberFormatException e) {
            displayArea.append("Warning: Invalid quantity format for ingredient: " + name + "\n");
            return new Ingredient(name, 0.0, unit); // Return with 0 quantity on error
        }
    }

    // Format the unit for display based on quantity and unit
    private String defineUnit(String unit, double quantity) {
        switch (unit) {
            case "pcs":
                return String.format("%.0f pieces", quantity); // Format for pieces
            case "gr":
                if (quantity > 999) {
                    return String.format("%.2f kg", quantity / 1000); // Convert from grams to kilograms
                }
                break;
            case "ml":
                if (quantity > 999) {
                    return String.format("%.2f L", quantity / 1000); // Convert from ml to liters
                }
                break;
        }
        return String.format("%.2f %s", quantity, unit); // Default formatting
    }

    // Return the standard unit for custom measurement units (e.g., pinch, teaspoon)
    private String getStandardUnit(String unit) {
        if ("πρέζα".equalsIgnoreCase(unit) || "κουταλάκι του γλυκού".equalsIgnoreCase(unit) ||
                "κουταλιά της σούπας".equalsIgnoreCase(unit)) {
            return "gr"; // Normalize to grams
        }
        return "ml"; // Default to milliliters
    }

    // Create the map for custom measurement units
    private static Map<String, Double> createCustomUnitToStandardMap() {
        Map<String, Double> map = new HashMap<>();
        map.put("πρέζα", 1.0); // Pinch → 1 gram
        map.put("κουταλάκι του γλυκού", 5.0); // Teaspoon → 5 grams/milliliters
        map.put("κουταλιά της σούπας", 15.0); // Tablespoon → 15 grams/milliliters
        return map;
    }
}