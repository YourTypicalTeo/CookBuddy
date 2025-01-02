package com.mycompany.cookbuddy;
/*
 *
 * @author JAVA ASSIGNMENT 2024-2025 Βοηθός Μάγειρας
it2023101_it2023140_it2023024
 *
 */
import java.io.File;
import java.util.ArrayList;
import java.util.List;

// η RecipeLoader υπευθυνη για τη φορτωση συνταγων
public class RecipeLoader {
    private final com.mycompany.cookbuddy.TextProcessor textProcessor; // Αντικειμενο για την επεξεργασια και αναλυση αρχειων συνταγων

    // constructor που λαμβανει εναν TextProcessor για την αναλυση αρχειων
    public RecipeLoader(com.mycompany.cookbuddy.TextProcessor textProcessor) {
        this.textProcessor = textProcessor;
    }

    // φορτωνει μια μοναδικη συνταγη απο τη διαδρομη αρχειου
    public Recipe loadSingleRecipe(String filePath) {
        File file = new File(filePath);

      // ελεγχος αν το αρχειο υπαρχει, ειναι ενα αρχειο, και εχει σωστη καταληξη
        if (!file.exists() || !file.isFile() || !file.getName().endsWith(".cook")) {
            System.out.println("Invalid recipe file: " + filePath); // Εμφανιση μηνυματος σφαλματος αν το αρχειο δεν ειναι εγκυρο
            return null;
        }

     // analise του αρχειου και επιστροφη της συνταγης
        return textProcessor.parseRecipe(file);
    }

    // Φορτωνει πολλαπλες συνταγες
    public List<Recipe> loadMultipleRecipes(List<String> filePaths) {
        List<Recipe> recipes = new ArrayList<>(); // Δημιουργια λιστας για τις συνταγες που θα φορτωθουν

        // Ελεγχος καθε διαδρομης και φορτωση συνταγης
        for (String filePath : filePaths) {
            Recipe recipe = loadSingleRecipe(filePath); // Καλει την loadSingleRecipe καθε φορα
            if (recipe != null) {
                recipes.add(recipe); // Προσθηκη της συνταγης στη λιστα αν ειναι εγκυρη
            } else {
                System.out.println("Failed to load recipe from: " + filePath); // Μηνυμα αποτυχιας :(
            }
        }

        // Επιστροφη της λιστας συνταγων
        return recipes;
    }
}
