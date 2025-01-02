package com.mycompany.cookbuddy;
/*
 *
 * @author JAVA ASSIGNMENT 2024-2025 Βοηθος Μαγειρας
it2023101_it2023140_it2023024
 *
 */
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Display {
    private final List<com.mycompany.cookbuddy.Recipe> recipes;

    public Display(List<com.mycompany.cookbuddy.Recipe> recipes) {
        this.recipes = recipes;
    }

    public void displayMenu() {
        // Ελεγχει αν υπαρχουν συνταγες για προβολη η εκτελεση
        if (recipes.isEmpty()) {
            System.out.println("Δεν υπαρχουν συνταγες για προβολη η εκτελεση.");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            // Εμφανιση βασικου μενου επιλογων
            System.out.println("\nΚαλως ηρθατε στον Βοηθο Μαγειρα! Παρακαλω επιλεξτε μια επιλογη:");
            System.out.println("1. Προβολη Ολων των Συνταγων");
            System.out.println("2. Προβολη Λεπτομερειων Συνταγης");
            System.out.println("3. Δημιουργια Λιστας Αγορων");
            System.out.println("4. Εκτελεση Συνταγης");
            System.out.println("5. Εξοδος");
            System.out.print("Εισαγεται την επιλογη σας: ");

            // ejasfalish  εγκυρης εισαγωγης επιλογης
            int choice = readIntegerInput(scanner, 1, 5, "Μη εγκυρη επιλογη. Παρακαλω εισαγεται εναν αριθμο απο 1 εως 5.");

            // Επεξεργασια επιλογων χρηστη
            switch (choice) {
                case 1 -> viewAllRecipes();
                case 2 -> viewRecipeDetails(scanner);
                case 3 -> createShoppingList(scanner);
                case 4 -> executeRecipe(scanner);
                case 5 -> {
                    System.out.println("Αντιο! Καλο μαγειρεμα!");
                    exit = true;
                }
                default -> // Δεν θα επρεπε να συμβει  :(
                        System.out.println("Μη εγκυρη επιλογη. Προσπαθηστε ξανα!");
            }
        }
    }

    // method για προβολη ολων των συνταγων
    private void viewAllRecipes() {
        if (recipes.isEmpty()) {
            System.out.println("Δεν υπαρχουν συνταγες διαθεσιμες.");
        } else {
            System.out.println("\nΔιαθεσιμες συνταγες:");
            for (int i = 0; i < recipes.size(); i++) {
                System.out.printf("%d. %s%n", i + 1, recipes.get(i).getTitle());
            }
        }
    }

    // Μεθοδος για προβολη λεπτομερειων μιας συνταγης
    private void viewRecipeDetails(Scanner scanner) {
        if (recipes.isEmpty()) {
            System.out.println("Δεν υπαρχουν συνταγες διαθεσιμες.");
            return;
        }

        while (true) {
            // Εμφανιση ολων των συνταγων
            viewAllRecipes();
            System.out.print("Εισαγεται τον αριθμο της συνταγης για προβολη λεπτομερειων, η πατηστε -1 για επιστροφη στο βασικο μενου: ");
            int recipeNumber = readIntegerInput(scanner, -1, recipes.size(),
                    "Μη εγκυρη εισαγωγη. Παρακαλω εισαγεται εναν εγκυρο αριθμο συνταγης η -1 για επιστροφη.");

            if (recipeNumber == -1) { // Επιστροφη στο μενου
                System.out.println("Επιστροφη στο βασικο μενου...");
                break;
            }

            // Επιλογη ποιας συνταγης
            Recipe selectedRecipe = recipes.get(recipeNumber - 1);

            // Ζηταει αριθμο ατομων για τους οποιους προοριζεται η συνταγη
            System.out.print("Εισαγεται τον αριθμο ατομων για τους οποιους μαγειρευετε: ");
            int numberOfPeople = readIntegerInput(scanner, 1, Integer.MAX_VALUE,
                    "Μη εγκυρη εισαγωγη. Παρακαλω εισαγεται εναν εγκυρο αριθμο ατομων (τουλαχιστον 1).");

            // Προβολη λεπτομερειων της συνταγης, προσαρμοσμενων στον αριθμο των ατομων
            System.out.println("Συνταγη για " + numberOfPeople + " ατομα:");
            displayScaledRecipe(selectedRecipe, numberOfPeople);
        }
    }

    // methodς για προβολη της συνταγης προσαρμοσμενης για συγκεκριμενο αριθμο ατομων
    private void displayScaledRecipe(Recipe recipe, int numberOfPeople) {
        System.out.println("Συνταγη: " + recipe.getTitle());

        // Προσαρμογη και εκτυπωση των υλικων
        if (!recipe.getIngredients().isEmpty()) {
            System.out.println("Υλικα:");
            for (String ingredient : recipe.getIngredients()) {
                ingredient = ingredient.replaceAll("[@%{}]", ""); // Καθαρισμος ειδικων συμβολων
                System.out.println(" - " + scaleIngredientQuantity(ingredient, numberOfPeople));
            }
        } else {
            System.out.println("Υλικα: Δεν διατιθενται.");
        }

        //  // Εκτυπωση των εργαλειων (χωρις προσαρμογη)
        if (!recipe.getUtensils().isEmpty()) {
            System.out.println("Σκέυη:");
            for (String utensil : recipe.getUtensils()) {
                utensil = utensil.replaceAll("[#{}]", " ");
                System.out.println(" - " + utensil);
            }
        } else {
            System.out.println("Σκέυη: Δεν διατιθενται.");
        }
        // Εκτυπωση των βηματων (χωρις προσαρμογς)
        if (!recipe.getSteps().isEmpty()) {
            System.out.println("Βηματα:");
            for (int i = 0; i < recipe.getSteps().size(); i++) {
                //βαλαμε replaceALL για να ειναι πιο Clean το output
                String step = recipe.getSteps().get(i).replaceAll("[@#%{}~]", " ");
                System.out.println((i + 1) + ". " + step);
            }
        } else {
            System.out.println("Βηματα: Δεν διατιθενται.");
        }

        // Εκτυπωση χρονων (χωρις προσαρμογες)
        if (!recipe.getTimes().isEmpty()) {
            // Συνολικος χρονος
            int totalTime = recipe.getTotalTime();
            System.out.println("Χρόνος:");
            if (totalTime<60) {
                System.out.println("Συνολικος απαιτουμενος χρονος: " + totalTime + " λεπτα");
            }
            if(totalTime>60 && totalTime<120) {
                int minutes = totalTime % 60;
                System.out.println("Συνολικος απαιτουμενος χρονος: 1 ωρα " + minutes + " λεπτα");
            }
            else if(totalTime>120) {
                int minutes = totalTime % 60;
                int hours = totalTime / 60;
                System.out.println("Συνολικος απαιτουμενος χρονος:" + hours +  " ωρες" + minutes + " λεπτα");
            }
        } else {
            System.out.println("Χρονος: Δεν διατιθενται.");
        }
    }

    // Μεθοδος για προσαρμογη ποσοτητας υλικων στον αριθμο των ατομων
    private String scaleIngredientQuantity(String ingredient, int numberOfPeople) {
        String[] parts = ingredient.trim().split(" ", 2);

        // Return the ingredient as-is if no quantity is present
        if (parts.length < 2) {
            return ingredient;
        }

        try {
            String quantityStr = parts[0]; // Extract the numeric part
            String restOfIngredient = parts[1]; // Extract the rest (e.g., "αλεύρι gr")

            // Convert to double and scale
            double originalQuantity = Double.parseDouble(quantityStr);
            double scaledQuantity = originalQuantity * numberOfPeople;

            // Avoid unnecessary decimals for integers
            if (scaledQuantity == (int) scaledQuantity) {
                return (int) scaledQuantity + " " + restOfIngredient;
            } else {
                return String.format("%.2f", scaledQuantity) + " " + restOfIngredient;
            }
        } catch (NumberFormatException e) {
            // If parsing fails (e.g., "μια πρέζα αλάτι"), return the ingredient as-is
            return ingredient;
        }
    }

    private void createShoppingList(Scanner scanner) {
        // ελεγχος για το εαν υπαρχουν συνταγες για δημιουργια λιστας αγορων
        if (recipes.isEmpty()) {
            System.out.println("Δεν υπαρχουν συνταγες για δημιουργια λιστας αγορων.");
            return;
        }

        List<com.mycompany.cookbuddy.Recipe> selectedRecipes = new ArrayList<>();
        while (true) {
            // Εμφανιση ολων των συνταγων
            viewAllRecipes();
            System.out.print("Εισαγεται τον αριθμο της συνταγης για προσθηκη στη λιστα αγορων, η πατηστε -1 για ολοκληρωση: ");

            int recipeNumber = readIntegerInput(scanner, -1, recipes.size(),
                    "Μη εγκυρη εισαγωγη. Παρακαλω εισαγεται εναν εγκυρο αριθμο συνταγης η -1 για ολοκληρωση.");

            if (recipeNumber == -1) { // Ολοκληρωση δημιουργιας της λιστας αγορων
                break;
            }

            com.mycompany.cookbuddy.Recipe selectedRecipe = recipes.get(recipeNumber - 1);

            // Ελεγχος αν η συνταγη ειναι ηδη στη λιστα
            if (selectedRecipes.contains(selectedRecipe)) {
                System.out.println("Η συνταγη \"" + selectedRecipe.getTitle() + "\" ειναι ηδη στη λιστα αγορων.");
            } else {
                selectedRecipes.add(selectedRecipe);
                System.out.println("Προσθεθηκε \"" + selectedRecipe.getTitle() + "\" στη λιστα αγορων.");
            }
        }

        if (selectedRecipes.isEmpty()) {
            System.out.println("Η λιστα αγορων ειναι αδεια.");
        } else {
            ShoppingList shoppingList = new ShoppingList();
            shoppingList.generateShoppingList(selectedRecipes);
            System.out.println("Η λιστα αγορων δημιουργηθηκε επιτυχως!");
        }
    }

    // TODO μερος 2 εργασιας
    private void executeRecipe(Scanner scanner) {
        // Ελεγχει αν υπαρχουν συνταγες για εκτελεση
        if (recipes.isEmpty()) {
            System.out.println("Δεν υπαρχουν συνταγες για εκτελεση.");
            return;
        }

        while (true) {
            // εμφανιση ολων των συνταγων
            viewAllRecipes();
            System.out.print("Εισαγεται τον αριθμο της συνταγης που θελετε να εκτελεσετε, η πατηστε -1 για επιστροφη στο βασικο μενου: ");
            int recipeNumber = readIntegerInput(scanner, -1, recipes.size(),
                    "Μη εγκυρη εισαγωγη. Παρακαλω εισαγεται εναν εγκυρο αριθμο συνταγης η -1 για επιστροφη.");

            if (recipeNumber == -1) { // Επιστροφη στο μενου :(
                System.out.println("Επιστροφη στο βασικο μενου...");
                break;
            }

            com.mycompany.cookbuddy.Recipe selectedRecipe = recipes.get(recipeNumber - 1);
            System.out.println("\nΕκτελεση Συνταγης: " + selectedRecipe.getTitle());
            System.out.println("\nΒηματα:");

            List<String> steps = selectedRecipe.getSteps();
            for (int i = 0; i < steps.size(); i++) {
                System.out.printf("%d. %s%n", i + 1, steps.get(i));
            }
        }
    }
    // Βοηθητικη method για αναγνωση και επικηρωση εισαγωγης ακεραιου απο τον χρηστη
    private int readIntegerInput(Scanner scanner, int min, int max, String errorMessage) {
        while (true) {
            try {
                int input = Integer.parseInt(scanner.nextLine().trim());
                if (input >= min && input <= max) {
                    return input;
                }
                System.out.println(errorMessage);
            } catch (NumberFormatException e) {
                System.out.println("Μη εγκυρη εισαγωγη. Παρακαλω εισαγεται εναν εγκυρο αριθμο.");
            }
        }
    }
}