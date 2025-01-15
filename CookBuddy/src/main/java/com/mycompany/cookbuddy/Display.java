package com.mycompany.cookbuddy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class Display extends JFrame {
    private final List<Recipe> recipes;
    private final JTextArea displayArea;
    private final JButton btnViewAllRecipes;
    private final JButton btnViewRecipeDetails;
    private final JButton btnCreateShoppingList;
    private final JButton btnExecuteRecipe;
    private final JButton btnExit;

    public Display(List<Recipe> recipes) {
        this.recipes = recipes;
        setTitle("Cook Buddy");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Text area to display recipes
        displayArea = new JTextArea();
        displayArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(displayArea);
        add(scrollPane, BorderLayout.CENTER);

        // Panel for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(5, 1));

        btnViewAllRecipes = new JButton("View All Recipes");
        btnViewRecipeDetails = new JButton("View Recipe Details");
        btnCreateShoppingList = new JButton("Create Shopping List");
        btnExecuteRecipe = new JButton("Execute Recipe");
        btnExit = new JButton("Exit");

        buttonPanel.add(btnViewAllRecipes);
        buttonPanel.add(btnViewRecipeDetails);
        buttonPanel.add(btnCreateShoppingList);
        buttonPanel.add(btnExecuteRecipe);
        buttonPanel.add(btnExit);

        add(buttonPanel, BorderLayout.WEST);

        // Button actions
        btnViewAllRecipes.addActionListener(e -> viewAllRecipes());

        btnViewRecipeDetails.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Ask the user for the number of people
                String input = JOptionPane.showInputDialog(Display.this, "Enter the number of people:");
                try {
                    int numberOfPeople = Integer.parseInt(input);
                    if (numberOfPeople > 0) {
                        // Prompt user to select a recipe
                        String[] options = new String[recipes.size()];
                        for (int i = 0; i < recipes.size(); i++) {
                            options[i] = recipes.get(i).getTitle();
                        }

                        String selectedRecipe = (String) JOptionPane.showInputDialog(Display.this,
                                "Select a recipe to view details:", "Recipe Details",
                                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

                        if (selectedRecipe != null) {
                            Recipe recipe = recipes.stream()
                                    .filter(r -> r.getTitle().equals(selectedRecipe))
                                    .findFirst().orElse(null);
                            if (recipe != null) {
                                // Display recipe details
                                viewRecipeDetails(recipe, numberOfPeople);
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(Display.this, "Please enter a valid number of people.");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(Display.this, "Invalid number format. Please enter a valid number.");
                }
            }
        });

        btnCreateShoppingList.addActionListener(e -> createShoppingList());

        btnExecuteRecipe.addActionListener(e -> executeRecipe());

        btnExit.addActionListener(e -> System.exit(0));
    }

    private void viewAllRecipes() {
        if (recipes.isEmpty()) {
            displayArea.setText("No recipes available.");
        } else {
            StringBuilder recipeList = new StringBuilder("Available Recipes:\n");
            for (int i = 0; i < recipes.size(); i++) {
                recipeList.append((i + 1) + ". " + recipes.get(i).getTitle() + "\n");
            }
            displayArea.setText(recipeList.toString());
        }
    }

    private void viewRecipeDetails(Recipe recipe, int numberOfPeople) {
        StringBuilder recipeDetails = new StringBuilder();

        recipeDetails.append("Συνταγη: " + recipe.getTitle() + "\n\n");

        // Προσαρμογή και εκτύπωση των υλικών
        if (!recipe.getIngredients().isEmpty()) {
            recipeDetails.append("Υλικα:\n");
            for (String ingredient : recipe.getIngredients()) {
                ingredient = ingredient.replaceAll("[@%{}]", ""); // Καθαρισμός ειδικών συμβόλων
                recipeDetails.append(" - " + scaleIngredientQuantity(ingredient, numberOfPeople) + "\n");
            }
        } else {
            recipeDetails.append("Υλικα: Δεν διατιθενται.\n");
        }

        // Εκτύπωση των σκευών (χωρίς προσαρμογή)
        if (!recipe.getUtensils().isEmpty()) {
            recipeDetails.append("\nΣκέυη:\n");
            for (String utensil : recipe.getUtensils()) {
                utensil = utensil.replaceAll("[#{}]", " ");
                recipeDetails.append(" - " + utensil + "\n");
            }
        } else {
            recipeDetails.append("\nΣκέυη: Δεν διατιθενται.\n");
        }

        // Εκτύπωση των βημάτων (χωρίς προσαρμογές)
        if (!recipe.getSteps().isEmpty()) {
            recipeDetails.append("\nΒηματα:\n");
            for (int i = 0; i < recipe.getSteps().size(); i++) {
                String step = recipe.getSteps().get(i).replaceAll("[@#%{}~]", " ");
                recipeDetails.append((i + 1) + ". " + step + "\n");
            }
        } else {
            recipeDetails.append("\nΒηματα: Δεν διατιθενται.\n");
        }

        // Εκτύπωση χρόνων (χωρίς προσαρμογές)
        if (!recipe.getTimes().isEmpty()) {
            int totalTime = recipe.getTotalTime();
            recipeDetails.append("\nΧρόνος:\n");
            if (totalTime < 60) {
                recipeDetails.append("Συνολικος απαιτουμενος χρονος: " + totalTime + " λεπτα\n");
            } else if (totalTime > 60 && totalTime < 120) {
                int minutes = totalTime % 60;
                recipeDetails.append("Συνολικος απαιτουμενος χρονος: 1 ωρα " + minutes + " λεπτα\n");
            } else if (totalTime > 120) {
                int minutes = totalTime % 60;
                int hours = totalTime / 60;
                recipeDetails.append("Συνολικος απαιτουμενος χρονος: " + hours + " ωρες " + minutes + " λεπτα\n");
            }
        } else {
            recipeDetails.append("\nΧρονος: Δεν διατιθενται.\n");
        }

        // Display the formatted recipe in the JTextArea
        displayArea.setText(recipeDetails.toString());
    }

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

    private void createShoppingList() {
        // Check if there are any recipes for shopping list creation
        if (recipes.isEmpty()) {
            displayArea.setText("Δεν υπάρχουν συνταγές για δημιουργία λίστας αγορών.");
            return;
        }

        List<Recipe> selectedRecipes = new ArrayList<>();
        while (true) {
            // Show all recipes in the display area
            viewAllRecipes();

            // Show dialog for the user to choose a recipe to add to shopping list
            String[] options = new String[recipes.size()];
            for (int i = 0; i < recipes.size(); i++) {
                options[i] = recipes.get(i).getTitle();
            }

            String selectedRecipeTitle = (String) JOptionPane.showInputDialog(this,
                    "Εισάγετε τον αριθμό της συνταγής για προσθήκη στη λίστα αγορών ή πατήστε Cancel για ολοκλήρωση:",
                    "Λίστα Αγορών", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

            if (selectedRecipeTitle == null) {
                // User clicked Cancel or closed the dialog, stop adding to the shopping list
                break;
            }

            Recipe selectedRecipe = recipes.stream()
                    .filter(r -> r.getTitle().equals(selectedRecipeTitle))
                    .findFirst().orElse(null);

            // Check if the recipe is already in the shopping list
            if (selectedRecipe != null && selectedRecipes.contains(selectedRecipe)) {
                JOptionPane.showMessageDialog(this, "Η συνταγή \"" + selectedRecipe.getTitle() + "\" είναι ήδη στη λίστα αγορών.");
            } else if (selectedRecipe != null) {
                selectedRecipes.add(selectedRecipe);
                JOptionPane.showMessageDialog(this, "Προστέθηκε \"" + selectedRecipe.getTitle() + "\" στη λίστα αγορών.");
            }
        }

        if (selectedRecipes.isEmpty()) {
            displayArea.setText("Η λίστα αγορών είναι άδεια.");
        } else {
            // Generate the shopping list
            ShoppingList shoppingList = new ShoppingList(displayArea);
            shoppingList.generateShoppingList(selectedRecipes);
            displayArea.setText("Η λίστα αγορών δημιουργήθηκε επιτυχώς!");
        }
    }
    private void executeRecipe() {
        if (recipes.isEmpty()) {
            displayArea.setText("No recipes available to execute.");
            return;
        }

        String[] options = new String[recipes.size()];
        for (int i = 0; i < recipes.size(); i++) {
            options[i] = recipes.get(i).getTitle();
        }

        String selectedRecipe = (String) JOptionPane.showInputDialog(this,
                "Select a recipe to execute:", "Execute Recipe",
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (selectedRecipe != null) {
            Recipe recipe = recipes.stream()
                    .filter(r -> r.getTitle().equals(selectedRecipe))
                    .findFirst().orElse(null);
            if (recipe != null) {
                // Execute recipe asynchronously using SwingWorker
                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        // Update JTextArea asynchronously
                        displayArea.append("\nΞεκινάμε την εκτέλεση της συνταγής: " + recipe.getTitle() + "\n");

                        RecipeExecutor recipeExecutor = new RecipeExecutor(recipe, displayArea);
                        recipeExecutor.executeRecipe();  // Execute the recipe in the background
                        return null;
                    }

                    @Override
                    protected void done() {
                        // Optional: Show a message when the recipe execution is done
                        displayArea.append("\nΗ συνταγή ολοκληρώθηκε επιτυχώς! Καλή σας όρεξη!\n");
                    }
                }.execute();  // Start the SwingWorker to run asynchronously
            }
        }
    }
}