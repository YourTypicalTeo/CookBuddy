package com.mycompany.cookbuddy;

import javax.swing.*;
import java.io.File;
import java.util.*;

public class CookBuddy {
    public static void main(String[] args) {
        TextProcessor textProcessor = new TextProcessor();
        RecipeLoader recipeLoader = new RecipeLoader(textProcessor);

        // Επαληθευση οτι εχουμε παρει ορισματα
        if (args.length < 1) {
            System.out.println("Δεν δοθηκαν ορισματα. Παρακαλω προσδιοριστε ενα η περισσοτερα αρχεια συνταγων η χρησιμοποιηστε -list για δημιουργια λιστας αγορων.");
            System.exit(1);
        }

        // Σημαια για ελεγχο χρησης της επιλογης -list
        boolean isListMode = args[0].equals("-list");

        // Συλλογη διαδρομων αρχειων, επαληθευση αυτων και ελεγχος για διπλοτυπα
        Set<String> uniqueFilePaths = new HashSet<>();
        List<String> filePaths = new ArrayList<>();
        int startIndex = isListMode ? 1 : 0; // Ξεκιναμε απο το 1 αν χρησιμοποιηθει το -list

        for (int i = startIndex; i < args.length; i++) {
            String filePath = args[i];
            // Ελεγχος για διπλοτυπα αρχεια
            if (!uniqueFilePaths.add(filePath)) {
                System.out.println("Σφαλμα: Ανιχνευθηκε διπλοτυπο αρχειο: " + filePath);
                System.exit(1); // Κατευθειαν εξοδος αν ανιχνευθει διπλοτυπο
            }

            // Επαληθευση αρχειου
            if (!isValidFile(filePath)) {
                System.out.println("Σφαλμα: Παρεχομενο αρχειο δεν ειναι εγκυρο: " + filePath);
                System.exit(1); // Αμεση εξοδος αν καποιο αρχειο δεν ειναι εγκυρο
            }

            filePaths.add(filePath);
        }

        // Διαχειριση της λογικης για το -list
        if (isListMode) {
            if (filePaths.isEmpty()) {
                System.out.println("Δεν δοθηκαν εγκυρα αρχεια με το '-list'. Εξοδος...");
                System.exit(1);
            }

            // Φορτώνουμε και δημιουργούμε μια λίστα αγορών
            List<Recipe> recipes = recipeLoader.loadMultipleRecipes(filePaths);
            if (recipes.isEmpty()) {
                System.out.println("Δεν βρεθηκαν εγκυρες συνταγες για τη λιστα αγορων. Εξοδος...");
                System.exit(1);
            }

// Assuming you have a JTextArea for displaying results
            JTextArea displayArea = new JTextArea();
            displayArea.setEditable(false); // To make it non-editable

// Then, create the ShoppingList with the displayArea
            ShoppingList shoppingList = new ShoppingList(displayArea);
            shoppingList.generateShoppingList(recipes);
            return;
        }

        // Απλή επεξεργασία αρχείων (χωρίς το -list)
        List<Recipe> recipes = recipeLoader.loadMultipleRecipes(filePaths);
        if (recipes.isEmpty()) {
            System.out.println("Δεν βρεθηκαν εγκυρες συνταγες. Εξοδος...");
            System.exit(1);
        }

        // Μεταφέρουμε συνταγές στην κλάση Display για GUI
        Display display = new Display(recipes);
        display.setVisible(true); // Αυτό είναι που εμφανίζει το GUI για τις συνταγές
    }

    // Βοηθητική μέθοδος για τσεκ ύπαρξης και επέκτασης αρχείου
    private static boolean isValidFile(String filePath) {
        File file = new File(filePath);
        return file.exists() && file.isFile() && filePath.endsWith(".cook");
    }
}