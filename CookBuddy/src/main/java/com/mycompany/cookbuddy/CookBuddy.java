package com.mycompany.cookbuddy;
/*
 *
 * @author JAVA ASSIGNMENT 2024-2025 Βοηθός Μάγειρας
it2023101_it2023140_it2023024
 *
 */

import java.io.File;
import java.util.*;
import java.util.List;

import static java.awt.SystemColor.window;

public class CookBuddy {
    public static void main(String[] args) {
        TextProcessor textProcessor = new TextProcessor();
        RecipeLoader recipeLoader = new RecipeLoader(textProcessor);

        // Επαληθευση οτι εχουμε παρει ορισματα
        if (args.length < 1) {
            System.out.println("Δεν δοθηκαν ορισματα. Παρακαλω προσδιοριστε ενα η περισσοτερα αρχεια συνταγων.");
            System.exit(1);
        }

        // Συλλογη διαδρομων αρχειων, επαληθευση αυτων και ελεγχος για διπλοτυπα
        Set<String> uniqueFilePaths = new HashSet<>();
        List<String> filePaths = new ArrayList<>();

        for (String filePath : args) {
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

        // Απλή επεξεργασία αρχείων
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
