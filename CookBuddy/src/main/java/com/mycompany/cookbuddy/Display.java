package com.mycompany.cookbuddy;
/*
 *
 * @author JAVA ASSIGNMENT 2024-2025 Βοηθός Μάγειρας
it2023101_it2023140_it2023024
 *
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class Display extends JFrame {
    private List<Recipe> recipes;
    private JTextArea displayArea;
    private JButton btnViewAllRecipes;
    private JButton btnViewRecipeDetails;
    private JButton btnCreateShoppingList;
    private JButton btnExecuteRecipe;
    private JButton btnExit;

    public Display(List<Recipe> recipes) {
        this.recipes = recipes;
        setTitle("Cook Buddy");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Περιοχή κειμένου για εμφάνιση συνταγών
        displayArea = new JTextArea();
        displayArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(displayArea);
        add(scrollPane, BorderLayout.CENTER);

        // Πάνελ για τα κουμπιά
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(5, 1));

        btnViewAllRecipes = new JButton("Προβολή Όλων των Συνταγών");
        btnViewRecipeDetails = new JButton("Προβολή Λεπτομερειών Συνταγής");
        btnCreateShoppingList = new JButton("Δημιουργία Λίστας Αγορών");
        btnExecuteRecipe = new JButton("Εκτέλεση Συνταγής");
        btnExit = new JButton("Έξοδος");
        buttonPanel.add(btnViewAllRecipes);
        buttonPanel.add(btnViewRecipeDetails);
        buttonPanel.add(btnCreateShoppingList);
        buttonPanel.add(btnExecuteRecipe);
        buttonPanel.add(btnExit);

        add(buttonPanel, BorderLayout.WEST);

        // Ενέργειες κουμπιών
        btnViewAllRecipes.addActionListener(e -> viewAllRecipes());

        btnViewRecipeDetails.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Ζητάμε από τον χρήστη να εισάγει τον αριθμό ατόμων
                String input = JOptionPane.showInputDialog(Display.this, "Για τις ανάλογες ποσότητες εισάγετε τον αριθμό των ατόμων: ","Αριθμός Ατόμων", JOptionPane.QUESTION_MESSAGE);
                try {
                    int numberOfPeople = Integer.parseInt(input);
                    if (numberOfPeople > 0) {
                        // Προτροπή στον χρήστη να επιλέξει συνταγή
                        String[] options = new String[recipes.size()];
                        for (int i = 0; i < recipes.size(); i++) {
                            options[i] = recipes.get(i).getTitle();
                        }

                        String selectedRecipe = (String) JOptionPane.showInputDialog(Display.this,
                                "Επιλέξτε μια συνταγή για προβολή λεπτομερειών:", "Λεπτομέρειες Συνταγής",
                                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

                        if (selectedRecipe != null) {
                            Recipe recipe = recipes.stream()
                                    .filter(r -> r.getTitle().equals(selectedRecipe))
                                    .findFirst().orElse(null);
                            if (recipe != null) {
                                // Εμφάνιση λεπτομερειών συνταγής
                                viewRecipeDetails(recipe, numberOfPeople);
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(Display.this, "Παρακαλώ εισάγετε έγκυρο αριθμό ατόμων.");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(Display.this, "Μη έγκυρη μορφή αριθμού. Παρακαλώ εισάγετε έγκυρο αριθμό.");
                }
            }
        });

        btnCreateShoppingList.addActionListener(e -> createShoppingList());

        btnExecuteRecipe.addActionListener(e -> executeRecipe());

        btnExit.addActionListener(e -> System.exit(0));
    }

    private void viewAllRecipes() {
        // Εμφάνιση όλων των διαθέσιμων συνταγών
        if (recipes.isEmpty()) {
            displayArea.setText("Δεν υπάρχουν διαθέσιμες συνταγές.");
        } else {
            StringBuilder recipeList = new StringBuilder("Διαθέσιμες Συνταγές:\n");
            for (int i = 0; i < recipes.size(); i++) {
                recipeList.append((i + 1) + ". " + recipes.get(i).getTitle() + "\n");
            }
            displayArea.setText(recipeList.toString());
        }
    }

    private void viewRecipeDetails(Recipe recipe, int numberOfPeople) {
        StringBuilder recipeDetails = new StringBuilder();

        recipeDetails.append("Συνταγή: " + recipe.getTitle() + "\n\n");

        // Προσαρμογή και εκτύπωση των υλικών
        if (!recipe.getIngredients().isEmpty()) {
            recipeDetails.append("Υλικά:\n");
            for (String ingredient : recipe.getIngredients()) {
                ingredient = ingredient.replaceAll("[@%{}]", ""); // Καθαρισμός ειδικών συμβόλων
                recipeDetails.append(" - " + scaleIngredientQuantity(ingredient, numberOfPeople) + "\n");
            }
        } else {
            recipeDetails.append("Υλικά: Δεν διατίθενται.\n");
        }

        // Εκτύπωση των σκευών (χωρίς προσαρμογή)
        if (!recipe.getUtensils().isEmpty()) {
            recipeDetails.append("\nΣκεύη:\n");
            for (String utensil : recipe.getUtensils()) {
                utensil = utensil.replaceAll("[#{}]", " ");
                recipeDetails.append(" - " + utensil + "\n");
            }
        } else {
            recipeDetails.append("\nΣκεύη: Δεν διατίθενται.\n");
        }

        // Εκτύπωση των βημάτων (χωρίς προσαρμογές)
        if (!recipe.getSteps().isEmpty()) {
            recipeDetails.append("\nΒήματα:\n");
            for (int i = 0; i < recipe.getSteps().size(); i++) {
                String step = recipe.getSteps().get(i).replaceAll("[@#%{}~]", " ");
                recipeDetails.append((i + 1) + ". " + step + "\n");
            }
        } else {
            recipeDetails.append("\nΒήματα: Δεν διατίθενται.\n");
        }

        // Εκτύπωση χρόνων (χωρίς προσαρμογές)
        if (!recipe.getTimes().isEmpty()) {
            int totalTime = recipe.getTotalTime();
            recipeDetails.append("\nΧρόνος:\n");
            if (totalTime < 60) {
                recipeDetails.append("Συνολικός απαιτούμενος χρόνος: " + totalTime + " λεπτά\n");
            } else if (totalTime > 60 && totalTime < 120) {
                int minutes = totalTime % 60;
                recipeDetails.append("Συνολικός απαιτούμενος χρόνος: 1 ώρα " + minutes + " λεπτά\n");
            } else if (totalTime > 120) {
                int minutes = totalTime % 60;
                int hours = totalTime / 60;
                recipeDetails.append("Συνολικός απαιτούμενος χρόνος: " + hours + " ώρες " + minutes + " λεπτά\n");
            }
        } else {
            recipeDetails.append("\nΧρόνος: Δεν διατίθενται.\n");
        }

        // Εμφάνιση της μορφοποιημένης συνταγής στην περιοχή κειμένου
        displayArea.setText(recipeDetails.toString());
    }

    private String scaleIngredientQuantity(String ingredient, int numberOfPeople) {
        String[] parts = ingredient.trim().split(" ", 2);

        // Επιστροφή του υλικού όπως είναι αν δεν υπάρχει ποσότητα
        if (parts.length < 2) {
            return ingredient;
        }

        try {
            String quantityStr = parts[0]; // Απόσπασμα του αριθμητικού μέρους
            String restOfIngredient = parts[1]; // Απόσπασμα του υπόλοιπου (π.χ. "αλεύρι gr")

            // Μετατροπή σε double και προσαρμογή
            double originalQuantity = Double.parseDouble(quantityStr);
            double scaledQuantity = originalQuantity * numberOfPeople;

            // Αποφυγή περιττών δεκαδικών για ακέραιους αριθμούς
            if (scaledQuantity == (int) scaledQuantity) {
                return (int) scaledQuantity + " " + restOfIngredient;
            } else {
                return String.format("%.2f", scaledQuantity) + " " + restOfIngredient;
            }
        } catch (NumberFormatException e) {
            // Αν η μετατροπή αποτύχει (π.χ., "μια πρέζα αλάτι"), επιστρέφουμε το υλικό όπως είναι
            return ingredient;
        }
    }

    private void createShoppingList() {
        // Έλεγχος αν υπάρχουν συνταγές για δημιουργία λίστας αγορών
        if (recipes.isEmpty()) {
            displayArea.setText("Δεν υπάρχουν συνταγές για δημιουργία λίστας αγορών.");
            return;
        }

        List<Recipe> selectedRecipes = new ArrayList<>();
        while (true) {
            // Εμφάνιση όλων των συνταγών στην περιοχή εμφάνισης
            viewAllRecipes();

            // Διάλογος για την επιλογή συνταγής για προσθήκη στη λίστα αγορών
            String[] options = new String[recipes.size()];
            for (int i = 0; i < recipes.size(); i++) {
                options[i] = recipes.get(i).getTitle();
            }

            String selectedRecipeTitle = (String) JOptionPane.showInputDialog(this,
                    "Εισάγετε τον αριθμό της συνταγής για προσθήκη στη λίστα αγορών ή πατήστε Cancel για ολοκλήρωση:",
                    "Λίστα Αγορών", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

            if (selectedRecipeTitle == null) {
                // Ο χρήστης πάτησε Cancel ή έκλεισε το παράθυρο διαλόγου, διακοπή προσθήκης
                break;
            }

            Recipe selectedRecipe = recipes.stream()
                    .filter(r -> r.getTitle().equals(selectedRecipeTitle))
                    .findFirst().orElse(null);

            // Έλεγχος αν η συνταγή είναι ήδη στη λίστα αγορών
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
            // Δημιουργία της λίστας αγορών
            ShoppingList shoppingList = new ShoppingList(displayArea);
            shoppingList.generateShoppingList(selectedRecipes);
        }
    }

    private void executeRecipe() {
        if (recipes.isEmpty()) {
            displayArea.setText("Δεν υπάρχουν διαθέσιμες συνταγές για εκτέλεση.");
            return;
        }

        String[] options = new String[recipes.size()];
        for (int i = 0; i < recipes.size(); i++) {
            options[i] = recipes.get(i).getTitle();
        }

        String selectedRecipe = (String) JOptionPane.showInputDialog(this,
                "Επιλέξτε μια συνταγή για εκτέλεση:", "Εκτέλεση Συνταγής",
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (selectedRecipe != null) {
            Recipe recipe = recipes.stream()
                    .filter(r -> r.getTitle().equals(selectedRecipe))
                    .findFirst().orElse(null);
            if (recipe != null) {
                // Εκτέλεση συνταγής ασύγχρονα μέσω SwingWorker
                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        // Ενημέρωση της JTextArea ασύγχρονα
                        RecipeExecutor recipeExecutor = new RecipeExecutor(recipe, displayArea);
                        recipeExecutor.executeRecipe();  // Εκτέλεση της συνταγής στο παρασκήνιο
                        return null;
                    }

                    @Override
                    protected void done() {
                        // Προαιρετικό: Εμφάνιση μηνύματος όταν ολοκληρωθεί η εκτέλεση της συνταγής
                        displayArea.append("\nΗ συνταγή ολοκληρώθηκε επιτυχώς! Καλή σας όρεξη!\n");
                    }
                }.execute();
            }
        }
    }
}
