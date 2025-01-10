package com.mycompany.cookbuddy;

import gr.hua.dit.oop2.countdown.Countdown;
import gr.hua.dit.oop2.countdown.CountdownFactory;
import gr.hua.dit.oop2.countdown.Notifier;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class RecipeExecutor {

  private final Recipe recipe;

  public RecipeExecutor(Recipe recipe) {
    this.recipe = recipe;
  }

  public void executeRecipe() {
    // Ενημέρωση για την έναρξη της εκτέλεσης της συνταγής
    System.out.println("\nΞεκινάμε την εκτέλεση της συνταγής: " + recipe.getTitle());
    List<String> steps = recipe.getSteps();
    Scanner scanner = new Scanner(System.in);

    for (int i = 0; i < steps.size(); i++) {
      // Διαβάζουμε το βήμα και αφαιρούμε τυχόν άχρηστα κενά ή αλλαγές γραμμής
      String step = steps.get(i).replace("\n", " ").trim();

      // Εμφανίζουμε το βήμα στον χρήστη
      System.out.printf("\nΒήμα %d: %s%n", i + 1, step);

      // Εξάγουμε τη διάρκεια του βήματος σε δευτερόλεπτα
      int duration = extractStepTime(step);
      if (duration > 0) {
        System.out.println("Χρόνος αναμονής: " + duration + " δευτερόλεπτα.");

        // Ζητάμε από τον χρήστη να πατήσει Enter για να ξεκινήσει η αντίστροφη μέτρηση
        System.out.println("Πατήστε Enter για να ξεκινήσει η αντίστροφη μέτρηση...");
        waitForEnter(scanner);

        // Δημιουργούμε και ξεκινάμε την αντίστροφη μέτρηση
        Countdown countdown = CountdownFactory.countdown(duration);
        countdown.addNotifier(new StepNotifier());
        countdown.start();

        System.out.println("Αντίστροφη μέτρηση ξεκινά...");

        // Ενημερώνουμε τον χρήστη για τον χρόνο που απομένει κάθε δευτερόλεπτο
        while (countdown.secondsRemaining() > 0) {
          try {
            // Αγνοούμε εισαγωγές κατά τη διάρκεια της αντίστροφης μέτρησης
            if (System.in.available() > 0) {
              scanner.nextLine(); // Καθαρίζουμε την εισαγωγή
              System.out.println("\nΔεν μπορείτε να συνεχίσετε πριν ολοκληρωθεί η αντίστροφη μέτρηση!");
            }

            int remaining = (int) countdown.secondsRemaining();
            System.out.printf("\rΧρόνος που απομένει: %02d δευτερόλεπτα", remaining);
            Thread.sleep(1000);
          } catch (InterruptedException | IOException e) {
            Thread.currentThread().interrupt();
            System.out.println("\nΗ αντίστροφη μέτρηση διακόπηκε.");
            return;
          }
        }
        // Καθαρίζουμε την οθόνη και ενημερώνουμε ότι το βήμα ολοκληρώθηκε
        System.out.print("\r");
        System.out.println("\nΟ χρόνος για αυτό το βήμα ολοκληρώθηκε!");
        countdown.stop();
      }

      // Ζητάμε από τον χρήστη να πατήσει Enter για να περάσει στο επόμενο βήμα
      System.out.print("Πατήστε Enter για να συνεχίσετε στο επόμενο βήμα...");
      waitForEnter(scanner);
    }

    // Ενημερώνουμε τον χρήστη ότι η συνταγή ολοκληρώθηκε
    System.out.println("\nΗ συνταγή ολοκληρώθηκε επιτυχώς! Καλή σας όρεξη!");
  }

  // Μέθοδος για αναμονή του Enter, αγνοώντας άλλα δεδομένα εισόδου
  private void waitForEnter(Scanner scanner) {
    while (scanner.hasNextLine()) {
      String input = scanner.nextLine();
      if (input.isEmpty()) {
        break; // Αν πατηθεί Enter, συνεχίζουμε
      } else {
        // Ενημερώνουμε τον χρήστη ότι πρέπει να πατήσει μόνο Enter
        System.out.println("Παρακαλώ πατήστε μόνο Enter για να συνεχίσετε.");
      }
    }
  }

  // Μέθοδος για εξαγωγή της χρονικής διάρκειας από το κείμενο του βήματος
  private int extractStepTime(String step) {
    // Αφαιρούμε αλλαγές γραμμής και κενά για πιο εύκολη επεξεργασία
    String normalizedStep = step.replace("\n", " ").trim();

    // Κανονικές εκφράσεις για ανίχνευση δευτερολέπτων, λεπτών και ωρών
    String timePattern = "~\\{(\\d+)%seconds}";
    String minutesPattern = "~\\{(\\d+)%minutes}";
    String minutePattern = "~\\{(\\d+)%minute}";
    String hourPattern = "~\\{(\\d+)%hour}";
    String hoursPattern = "~\\{(\\d+)%hours}";

    // Ελέγχουμε για κάθε τύπο χρόνου και επιστρέφουμε τη διάρκεια σε δευτερόλεπτα
    if (normalizedStep.matches(".*" + timePattern + ".*")) {
      return Integer.parseInt(normalizedStep.replaceAll(".*~\\{(\\d+)%seconds}.*", "$1"));
    } else if (normalizedStep.matches(".*" + minutesPattern + ".*")) {
      return Integer.parseInt(normalizedStep.replaceAll(".*~\\{(\\d+)%minutes}.*", "$1")) * 60;
    } else if (normalizedStep.matches(".*" + minutePattern + ".*")) {
      return Integer.parseInt(normalizedStep.replaceAll(".*~\\{(\\d+)%minute}.*", "$1")) * 60;
    } else if (normalizedStep.matches(".*" + hourPattern + ".*")) {
      return Integer.parseInt(normalizedStep.replaceAll(".*~\\{(\\d+)%hour}.*", "$1")) * 3600;
    } else if (normalizedStep.matches(".*" + hoursPattern + ".*")) {
      return Integer.parseInt(normalizedStep.replaceAll(".*~\\{(\\d+)%hours}.*", "$1")) * 3600;
    }
    return 0; // Αν δεν βρεθεί χρόνος, επιστρέφουμε 0
  }

  // Εσωτερική κλάση για ειδοποίηση όταν ολοκληρωθεί η αντίστροφη μέτρηση
  private static class StepNotifier implements Notifier {
    @Override
    public void finished(Countdown c) {
      System.out.println("\nΟ χρόνος για αυτό το βήμα ολοκληρώθηκε!\n");
    }
  }
}