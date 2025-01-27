package com.mycompany.cookbuddy;
/*
 *
 * @author JAVA ASSIGNMENT 2024-2025 Βοηθός Μάγειρας
it2023101_it2023140_it2023024
 *
 */
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JTextArea;

public class ShoppingList {

    // Μοτίβο για τον εντοπισμό συστατικών στα βήματα συνταγών
    private static final Pattern INGREDIENT_PATTERN = Pattern.compile("@([\\p{L}0-9_\\- ]+)\\{([^}%]+)%?([^}]*)\\}");

    // Χάρτης για τη μετατροπή προσαρμοσμένων μονάδων σε τυπικές (γραμμάρια, λίτρα)
    private static final Map<String, Double> CUSTOM_UNIT_TO_STANDARD = createCustomUnitToStandardMap();

    // Σύνολο για μετρήσιμα αντικείμενα (χρησιμοποιείται για ποσότητες ανά τεμάχιο)
    private static final Set<String> COUNTABLE_ITEMS = Set.of("αυγα", "κομματια");

    private JTextArea displayArea; // Ενημέρωση του GUI με τη λίστα αγορών

    public ShoppingList(JTextArea displayArea) {
        this.displayArea = displayArea;
    }

    // Μέθοδος για τη δημιουργία και εμφάνιση της λίστας αγορών βάσει των συστατικών των συνταγών
    public void generateShoppingList(List<Recipe> recipes) {
        Map<String, Ingredient> ingredientTotals = new HashMap<>(); // Χάρτης για τις συνολικές ποσότητες συστατικών

        // Διαδρομή σε κάθε συνταγή για την εξαγωγή δεδομένων συστατικών
        for (Recipe recipe : recipes) {
            for (String step : recipe.getSteps()) {
                Matcher matcher = INGREDIENT_PATTERN.matcher(step);

                // Επεξεργασία κάθε αναγνωρισμένου συστατικού
                while (matcher.find()) {
                    String ingredientName = matcher.group(1).trim(); // Όνομα συστατικού
                    String quantityStr = matcher.group(2).trim();   // Ποσότητα
                    String unit = matcher.group(3).trim();          // Μονάδα μέτρησης

                    // Ανάλυση της ποσότητας και κανονικοποίηση της μονάδας
                    Ingredient ingredient = parseIngredient(ingredientName, quantityStr, unit);

                    // Συγκέντρωση ποσοτήτων για το ίδιο συστατικό
                    ingredientTotals.putIfAbsent(ingredient.getName(), new Ingredient(ingredientName, 0.0, ingredient.getUnit()));
                    Ingredient existingIngredient = ingredientTotals.get(ingredientName);

                    if (existingIngredient.getUnit().equals(ingredient.getUnit())) {
                        existingIngredient.setQuantity(existingIngredient.getQuantity() + ingredient.getQuantity());
                    } else {
                        displayArea.append("Προειδοποίηση: Ασυμφωνία μονάδων για το συστατικό: " + ingredientName + "\n");
                    }
                }
            }
        }

        // Εμφάνιση της τελικής λίστας αγορών στο JTextArea
        displayArea.setText("\nΛίστα Αγορών:\n");
        for (Ingredient ingredient : ingredientTotals.values()) {
            displayArea.append(defineUnit(ingredient.getUnit(), ingredient.getQuantity()) + " " + ingredient.getName() + "\n");
        }
    }

    // Ανάλυση των λεπτομερειών του συστατικού και κανονικοποίηση ποσότητας και μονάδας
    private Ingredient parseIngredient(String name, String quantityStr, String unit) {
        try {
            double quantity = Double.parseDouble(quantityStr); // Ανάλυση της αριθμητικής ποσότητας

            // Έλεγχος για μετρήσιμα αντικείμενα (τεμάχια)
            if (COUNTABLE_ITEMS.contains(unit.toLowerCase())) {
                return new Ingredient(name, quantity, "pcs"); // Επεξεργασία ως τεμάχια
            }

            // Έλεγχος για προσαρμοσμένες μονάδες (π.χ., πρέζα, κουταλάκι)
            if (CUSTOM_UNIT_TO_STANDARD.containsKey(unit.toLowerCase())) {
                double normalizedQuantity = quantity * CUSTOM_UNIT_TO_STANDARD.get(unit.toLowerCase()); // Μετατροπή σε γραμμάρια ή ml
                return new Ingredient(name, normalizedQuantity, getStandardUnit(unit));
            }

            return new Ingredient(name, quantity, unit); // Επιστροφή για τυπικές μονάδες
        } catch (NumberFormatException e) {
            displayArea.append("Προειδοποίηση: Μη έγκυρη μορφή ποσότητας για το συστατικό: " + name + "\n");
            return new Ingredient(name, 0.0, unit); // Επιστροφή με ποσότητα 0 σε περίπτωση σφάλματος
        }
    }

    // Μορφοποίηση της μονάδας για εμφάνιση βάσει ποσότητας και μονάδας
    private String defineUnit(String unit, double quantity) {
        switch (unit) {
            case "pcs":
                return String.format("%.0f τεμάχια", quantity); // Μορφοποίηση για τεμάχια
            case "gr":
                if (quantity > 999) {
                    return String.format("%.2f κιλά", quantity / 1000); // Μετατροπή από γραμμάρια σε κιλά
                }
                break;
            case "ml":
                if (quantity > 999) {
                    return String.format("%.2f λίτρα", quantity / 1000); // Μετατροπή από ml σε λίτρα
                }
                break;
        }
        return String.format("%.2f %s", quantity, unit); // Προεπιλεγμένη μορφοποίηση
    }

    // Επιστροφή τυπικής μονάδας για προσαρμοσμένες μονάδες μέτρησης (π.χ., πρέζα, κουταλάκι)
    private String getStandardUnit(String unit) {
        if ("πρέζα".equalsIgnoreCase(unit) || "κουταλάκι του γλυκού".equalsIgnoreCase(unit) ||
                "κουταλιά της σούπας".equalsIgnoreCase(unit)) {
            return "gr"; // Κανονικοποίηση σε γραμμάρια
        }
        return "ml"; // Προεπιλογή σε milliliters
    }

    // Δημιουργία χάρτη για προσαρμοσμένες μονάδες μέτρησης
    private static Map<String, Double> createCustomUnitToStandardMap() {
        Map<String, Double> map = new HashMap<>();
        map.put("πρέζα", 1.0); // Πρέζα → 1 γραμμάριο
        map.put("κουταλάκι του γλυκού", 5.0); // Κουταλάκι → 5 γραμμάρια/ml
        map.put("κουταλιά της σούπας", 15.0); // Κουταλιά → 15 γραμμάρια/ml
        return map;
    }
}
