package com.mycompany.cookbuddy;
/*
 *
 * @author JAVA ASSIGNMENT 2024-2025 Βοηθος Μαγειρας
it2023101_it2023140_it2023024
 *
 */
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//   δημιουργια λιστας αγορων με βαση τα υλικα των συνταγων.
public class ShoppingList {

 // χρηση του προτυπου απο τον TextProcessor για την αναγνωριση των  υλικων.
    private static final Pattern INGREDIENT_PATTERN = Pattern.compile("@([\\p{L}0-9_\\- ]+)\\{([^}%]+)%?([^}]*)\\}");

    // Χαρτης που μετατρεπει τις προσαρμοσμενες μοναδες μετρησης σε γραμμαρια η χιλιολιτρα.
    private static final Map<String, Double> CUSTOM_UNIT_TO_STANDARD = createCustomUnitToStandardMap();

      // Συνολο με μοναδες που χρησιμοποιουνται για μετρηση τεμαχιων αντι για βαρος η ογκο.
    private static final Set<String> COUNTABLE_ITEMS = Set.of("αυγα", "κομματια");

    // method που δημιουργει τη λιστα αγορων απο  συνταγες.
    public void generateShoppingList(List<Recipe> recipes) {
        Map<String, Ingredient> ingredientTotals = new HashMap<>(); // Χαρτης για τις συνολικες ποσοτητες των υλικων.

        // Τρεχει σε  καθε συνταγη και περνει  δεδομενα υλικων.
        for (Recipe recipe : recipes) {
            for (String step : recipe.getSteps()) {
                Matcher matcher = INGREDIENT_PATTERN.matcher(step);

                // Επεξεργασια του καθε ταυτοποιημενου υλικου
                while (matcher.find()) {
                    String ingredientName = matcher.group(1).trim(); // Ονομα 
                    String quantityStr = matcher.group(2).trim();   // Ποσοτητα 
                    String unit = matcher.group(3).trim();          // Μοναδα μετρησης 

                    // Αναλυση ποσοτητας και κανονικοποιηση μοναδας.
                    Ingredient ingredient = parseIngredient(ingredientName, quantityStr, unit);

           // Συγκεντρωση των ποσοτητων για το ιδιο υλικο.
                    ingredientTotals.putIfAbsent(ingredient.getName(), new Ingredient(ingredientName, 0.0, ingredient.getUnit()));
                    Ingredient existingIngredient = ingredientTotals.get(ingredientName);

                    if (existingIngredient.getUnit().equals(ingredient.getUnit())) {
                        existingIngredient.setQuantity(existingIngredient.getQuantity() + ingredient.getQuantity());
                    } else {
                        System.out.println("Warning: Mismatched units for ingredient: " + ingredientName);
                    }
                }
            }
        }

        // Εκτυπωση της τελικης λιστας αγορων.
        System.out.println("\nShopping List:");
        for (Ingredient ingredient : ingredientTotals.values()) {
            System.out.println(defineUnit(ingredient.getUnit(), ingredient.getQuantity()) + " " + ingredient.getName());
        }
    }

    // Αναλυση και κανονικοποιηση ποσοτητας και μοναδας μετρησης.
    private Ingredient parseIngredient(String name, String quantityStr, String unit) {
        try {
            double quantity = Double.parseDouble(quantityStr); // Εξαγωγη αριθμητικης ποσοτητας.

     // ελεγχος για μοναδες μετρησης τεμαχιων 
            if (COUNTABLE_ITEMS.contains(unit.toLowerCase())) {
                return new Ingredient(name, quantity, "pcs"); // Μεταχειριση ως τεμαχια.
            }

      // ελεγχος για προσαρμοσμενες μοναδες μετρησης 
            if (CUSTOM_UNIT_TO_STANDARD.containsKey(unit.toLowerCase())) {
                double normalizedQuantity = quantity * CUSTOM_UNIT_TO_STANDARD.get(unit.toLowerCase()); // Μετατροπη σε gr/ml.
                return new Ingredient(name, normalizedQuantity, getStandardUnit(unit));
            }

            return new Ingredient(name, quantity, unit); // Επιστροφη για τις τυπικες μοναδες 
        } catch (NumberFormatException e) {
            System.out.println("Warning: Invalid quantity format for ingredient: " + name);
            return new Ingredient(name, 0.0, unit); // Επιστροφη  σε περιπτωση λαθους.
        }
    }

    // Διαμορφωση των μοναδων 
    private String defineUnit(String unit, double quantity) {
        switch (unit) {
            case "pcs":
                return String.format("%.0f pieces", quantity); // Διαμορφωση για τεμαχια.
            case "gr":
                if (quantity > 999) {
                    return String.format("%.2f kg", quantity / 1000); // Μετατροπη απο γραμμ. σε κιλα.
                }
                break;
            case "ml":
                if (quantity > 999) {
                    return String.format("%.2f L", quantity / 1000); // Μετατροπη απο εμ-ελ σε λιτρα.
                }
                break;
        }
        return String.format("%.2f %s", quantity, unit); 
    }

    // Επιστροφη 'τυπικης' μοναδας για προσαρμοσμενες μετρησεις
    private String getStandardUnit(String unit) {
        if ("πρέζα".equalsIgnoreCase(unit) || "κουταλάκι του γλυκού".equalsIgnoreCase(unit) ||
                "κουταλιά της σούπας".equalsIgnoreCase(unit)) {
            return "gr"; // Κανονικοποιηση σε γραμ.
        }
        return "ml"; // Προεπιλογη σε μλ
    }

    // Δημιουργια χαρτη για προσαρμοσμενες μοναδες μετρησης.
    private static Map<String, Double> createCustomUnitToStandardMap() {
        Map<String, Double> map = new HashMap<>();
        map.put("πρέζα", 1.0); // πρεζα → 1 γραμμαριο.
        map.put("κουταλάκι του γλυκού", 5.0); // κουταλακι → 5 γραμμαρια/χιλιολιτρα.
        map.put("κουταλιά της σούπας", 15.0); // κουταλια → 15 γραμμαρια/χιλιολιτρα.
        return map;
    }
}
