package com.mycompany.cookbuddy;
/*
 *
 * @author JAVA ASSIGNMENT 2024-2025 Βοηθός Μάγειρας
it2023101_it2023140_it2023024
 *
 */
import java.util.List;

// Η κλαση Recipe αναπαριστα μια συνταγη μαγειρικης με τιτλο, υλικα, σκευη, βηματα και χρονους.
public class Recipe {
    private String title;                  //Τιτλος
    private List<String> ingredients;     //Λιστα με τα υλικα
    private List<String> utensils;        //σκευη
    private List<String> steps;           //βηματα
    private List<String> times;           //Λιστα με χρονους

    // κατασκευαστης για την αρχικοποιηση της συνταγης 
    public Recipe(String title, List<String> ingredients, List<String> utensils, List<String> steps, List<String> times) {
        this.title = title;
        this.ingredients = ingredients;
        this.utensils = utensils;
        this.steps = steps;
        this.times = times;
    }

    // // απαραιτιτοι setters και getters
    public String getTitle() {
        return title;
    }

  
    public void setTitle(String title) {
        this.title = title;
    }

    
    public List<String> getIngredients() {
        return ingredients;
    }

   
    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    
    public List<String> getUtensils() {
        return utensils;
    }

   
    public void setUtensils(List<String> utensils) {
        this.utensils = utensils;
    }

   
    public List<String> getSteps() {
        return steps;
    }

   
    public void setSteps(List<String> steps) {
        this.steps = steps;
    }

    
    public List<String> getTimes() {
        return times;
    }

   
    public void setTimes(List<String> times) {
        this.times = times;
    }

  
    public int getTotalTime() {
        int totalTime = 0;

        for (String time : times) {
            try {
        // ορισμος των μη επιθυμητων χαρακτηρων και μετατροπη σε ακεραιο
                time = time.replaceAll("[^0-9]", ""); // Διατηρουμε μονο ψηφια
                if (!time.isEmpty()) {
                    totalTime += Integer.parseInt(time);
                }
            } catch (NumberFormatException e) {
                System.err.println("Μη εγκυρη μορφη χρονου: " + time + ". Παραλειπεται αυτη η τιμη.");
            }
        }

        return totalTime;
    }
}

