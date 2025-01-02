package com.mycompany.cookbuddy;

import gr.hua.dit.oop2.countdown.Countdown;
import gr.hua.dit.oop2.countdown.CountdownFactory;
import gr.hua.dit.oop2.countdown.Notifier;

import java.util.List;
import java.util.Scanner;

public class RecipeExecutor {

  private final Recipe recipe;

  public RecipeExecutor(Recipe recipe) {
    this.recipe = recipe;
  }

  public void executeRecipe() {
    System.out.println("\nΞεκινάμε την εκτέλεση της συνταγής: " + recipe.getTitle());
    List<String> steps = recipe.getSteps();

    Scanner scanner = new Scanner(System.in);

    for (int i = 0; i < steps.size(); i++) {
      String step = steps.get(i);

      // Εμφάνιση του βήματος
      System.out.printf("\nΒήμα %d: %s%n", i + 1, step);

      // Εντοπισμός χρόνου για το συγκεκριμένο βήμα (αν υπάρχει)
      int duration = extractStepTime(step);
      if (duration > 0) {
        System.out.println("Χρόνος αναμονής: " + duration + " δευτερόλεπτα.");

        // Έναρξη αντίστροφης μέτρησης
        Countdown countdown = CountdownFactory.countdown(duration);
        countdown.addNotifier(new StepNotifier());
        countdown.start();

        // Αναμονή μέχρι να τελειώσει η αντίστροφη μέτρηση
        while (countdown.secondsRemaining() > 0) {
          try {
            Thread.sleep(1000); // Αναμονή 1 δευτερολέπτου
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Η αντίστροφη μέτρηση διακόπηκε.");
            return;
          }
        }

        countdown.stop();
      }

      // Περιμένουμε από τον χρήστη να προχωρήσει στο επόμενο βήμα
      System.out.print("Πατήστε Enter για να συνεχίσετε στο επόμενο βήμα...");
      scanner.nextLine();
    }

    System.out.println("\nΗ συνταγή ολοκληρώθηκε επιτυχώς! Καλή σας όρεξη!");
  }

  // Εξάγει τον χρόνο από ένα βήμα (σε δευτερόλεπτα)
  private int extractStepTime(String step) {
    String timePattern = "~\\{(\\d+)%seconds\\}";
    String minutePattern = "~\\{(\\d+)%minutes\\}";

    if (step.matches(".*" + timePattern + ".*")) {
      return Integer.parseInt(step.replaceAll(".*~\\{(\\d+)%seconds\\}.*", "$1"));
    } else if (step.matches(".*" + minutePattern + ".*")) {
      return Integer.parseInt(step.replaceAll(".*~\\{(\\d+)%minutes\\}.*", "$1")) * 60;
    }

    return 0; // Αν δεν υπάρχει χρόνος στο βήμα
  }

  // Εσωτερική κλάση Notifier για ειδοποίηση τέλους χρονομέτρησης
  private static class StepNotifier implements Notifier {
    @Override
    public void finished(Countdown c) {
      System.out.println("\nΟ χρόνος για αυτό το βήμα ολοκληρώθηκε!\n");
    }
  }
}