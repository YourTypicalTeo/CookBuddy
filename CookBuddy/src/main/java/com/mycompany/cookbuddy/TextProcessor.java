package com.mycompany.cookbuddy;
/*
 *
 * @author JAVA ASSIGNMENT 2024-2025 Βοηθός Μάγειρας
it2023101_it2023140_it2023024
 *
 */
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextProcessor {

    // Αναγνωση για τα συστατικα, σκευη και χρονο
    private static final Pattern INGREDIENT_PATTERN = Pattern.compile(
            "@([\\p{L}0-9_\\-]+(?:\\s+[\\p{L}0-9_\\-]+)*)(\\{[^}]*\\})?" // Αναγνωριζει συστατικα με ή χωρις ποσοτητα
    );

    private static final Pattern UTENSIL_PATTERN = Pattern.compile(
            "#([\\p{L}0-9_\\-]+(?:\\s+[\\p{L}0-9_\\-]+)*)(\\{[^}]*\\})?" // Αναγνωριζει σκευη με ή χωρις λεπτομερειες
    );
    private static final Pattern TIME_PATTERN = Pattern.compile(
            "~\\{([\\d]+)%\\s*(minutes?|hours?|minute|hour|second|seconds?)\\}" // Αναγνωριζει τον χρονο με ποσοστο (πχ. 25%λεπτα, 10%ωρες)
    );


    // Μεθοδος για την επεξεργασια του αρχειου
    public Recipe parseRecipe(File recipeFile) {
        Recipe recipe = new Recipe(recipeFile.getName().replace(".cook", ""), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        Map<String, Ingredient> ingredients = new HashMap<>(); // Χαρτης για τα συστατικα
        Set<String> utensils = new HashSet<>(); // Συνολο για τα σκευη
        List<String> steps = new ArrayList<>(); // Λιστα για τα βηματα
        List<String> times = new ArrayList<>(); // Λιστα για τον χρονο

        try {
            // Διαβαζει το περιεχομενο του αρχειου
            String content = new String(Files.readAllBytes(recipeFile.toPath()));

            // Χωριζει το περιεχομενο με βαση τα διπλα νεα γραμματα
            String[] parts = content.split("\\n\\n");

            // Επεξεργασια καθε μερους της συνταγης
            for (String part : parts) {
                // Αναγνωριση συστατικων
                Matcher ingredientMatcher = INGREDIENT_PATTERN.matcher(part);
                while (ingredientMatcher.find()) {
                    String rawName = ingredientMatcher.group(1).trim(); // Ονομα συστατικου
                    String quantityStr = ingredientMatcher.group(2);   // Προαιρετικη ποσοτητα μεσα στο {}

                    // Αν δεν υπαρχει ποσοτητα (δηλαδη δεν υπαρχει {} μπλοκ), θεωρειται συστατικο μιας λεξης
                    if (quantityStr == null) {
                        // Εξασφαλιζει οτι θα πιασουμε ολοκληρη τη λεξη
                        if (rawName.length() > 1 && rawName.charAt(rawName.length() - 1) != '}') {
                            int spaceIndex = rawName.indexOf(' ');
                            if (spaceIndex > 0) {
                                rawName = rawName.substring(0, spaceIndex).trim(); // Σταματησε στο πρωτο κενο
                            }
                        }
                    } else {
                        // Αν υπαρχει ποσοτητα, αφαιρει τα `{}` και αποθηκευει το συστατικο
                        quantityStr = quantityStr.substring(1, quantityStr.length() - 1); // Αφαιρει τα αγκυλες
                    }

                    Ingredient ingredient = parseIngredient(quantityStr != null ? quantityStr : "");

                    // Προσθετει το συστατικο στον χαρτη
                    ingredients.put(rawName, ingredient);
                }

                // Αναγνωριση σκευων
                Matcher utensilMatcher = UTENSIL_PATTERN.matcher(part);
                while (utensilMatcher.find()) {
                    String rawName = utensilMatcher.group(1).trim(); // Ονομα σκευους

                    // Ελεγχος αν υπαρχει μπλοκ {} και αν ναι, πιανει τα παντα μεσα σε αυτο
                    String quantityStr = utensilMatcher.group(2); // Προαιρετικη ποσοτητα ή λεπτομερειες μεσα στο {}

                    if (quantityStr != null) {
                        quantityStr = quantityStr.substring(1, quantityStr.length() - 1); // Αφαιρει τα αγκυλες
                        rawName = rawName + " " + quantityStr; // Συνδυαζει το ονομα σκευους με το περιεχομενο μεσα στο {}
                    } else {
                        if (rawName.length() > 1 && rawName.charAt(rawName.length() - 1) != '}') {
                            int spaceIndex = rawName.indexOf(' ');
                            if (spaceIndex > 0) {
                                rawName = rawName.substring(0, spaceIndex).trim(); // Σταματησε στο πρωτο κενο
                            }
                        }
                    }

                    // Προσθετει το ονομα του σκευους στο συνολο
                    utensils.add(rawName);
                }

                // Αναγνωριση χρονου
                Matcher timeMatcher = TIME_PATTERN.matcher(part);
                while (timeMatcher.find()) {
                    String time = timeMatcher.group(1).trim();
                    String unit = timeMatcher.group(2).trim();
                    times.add(time + " " + unit);
                }

                // Προσθηκη βηματων
                steps.add(part.trim());
            }

            // Ορισμος των ιδιοτητων της συνταγης
            recipe.setTitle(recipeFile.getName().replace(".cook", ""));
            recipe.setIngredients(formatIngredients(ingredients)); // Μορφοποιηση συστατικων
            recipe.setUtensils(new ArrayList<>(utensils)); // Μετατροπη απο Set σε List
            recipe.setSteps(steps);
            recipe.setTimes(times);

        } catch (IOException e) {
            System.out.println("Σφαλμα κατα την αναγνωση του αρχειου συνταγης: " + recipeFile.getName());
            return null;
        }

        return recipe;
    }

    // Βοηθητικη μεθοδος για την ανάλυση ποσοτητων και μοναδων απο String
    private Ingredient parseIngredient(String quantityStr) {
        String numericPart = quantityStr.replaceAll("[^0-9.]", "").trim(); // Εξαγωγη του αριθμητικου μερους
        String unitPart = quantityStr.replaceAll("[0-9.]", "").trim();    // Εξαγωγη της μοναδας

        try {
            double quantity = Double.parseDouble(numericPart);
            return new Ingredient(quantity, unitPart.isEmpty() ? "" : unitPart);
        } catch (NumberFormatException e) {
            return new Ingredient(0.0, ""); // Προεπιλεγμενη τιμη
        }
    }

    // Μεθοδος βοηθειας για τη μορφοποιηση των συστατικων
    private List<String> formatIngredients(Map<String, Ingredient> ingredients) {
        List<String> formattedIngredients = new ArrayList<>();
        for (Map.Entry<String, Ingredient> entry : ingredients.entrySet()) {
            Ingredient ingredient = entry.getValue();
            if (ingredient.getQuantity() > 0) {
                formattedIngredients.add(ingredient.getQuantity() + " " + ingredient.getUnit() + " " + entry.getKey());
            } else {
                formattedIngredients.add(entry.getKey()); // Υλικο χωρις ποσοτητα
            }
        }
        return formattedIngredients;
    }

    // Κλαση για την αποθηκευση της ποσοτητας και των μοναδων συστατικων
    public static class Ingredient {
        private double quantity;
        private final String unit;

        public Ingredient(double quantity, String unit) {
            this.quantity = quantity;
            this.unit = unit;
        }

        public double getQuantity() {
            return quantity;
        }

        public String getUnit() {
            return unit;
        }

        public void addQuantity(double additionalQuantity) {
            this.quantity += additionalQuantity;
        }
        public void subQuantity(double additionalQuantity) {
            this.quantity -= additionalQuantity;
        }
    }
}
