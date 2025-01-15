package com.mycompany.cookbuddy;

import gr.hua.dit.oop2.countdown.Countdown;
import gr.hua.dit.oop2.countdown.CountdownFactory;
import gr.hua.dit.oop2.countdown.Notifier;

import javax.swing.*;
import java.util.List;

public class RecipeExecutor {

  private final Recipe recipe;      // Η συνταγή που θα εκτελεστεί
  private final JTextArea displayArea;  // Περιοχή κειμένου για την εμφάνιση των πληροφοριών

  // Κατασκευαστής για την αρχικοποίηση της συνταγής και της περιοχής κειμένου
  public RecipeExecutor(Recipe recipe, JTextArea displayArea) {
    this.recipe = recipe;
    this.displayArea = displayArea;
  }

  // Μέθοδος εκτέλεσης της συνταγής
  public void executeRecipe() {
    // Εμφανίζουμε την αρχή της εκτέλεσης της συνταγής
    displayArea.append("\nΞεκινάμε την εκτέλεση της συνταγής: " + recipe.getTitle() + "\n");
    List<String> steps = recipe.getSteps();  // Παίρνουμε τα βήματα της συνταγής

    // Διατρέχουμε κάθε βήμα της συνταγής
    for (int i = 0; i < steps.size(); i++) {
      // Διαβάζουμε το βήμα και αφαιρούμε τυχόν κενά ή νέα γραμμή
      String step = steps.get(i).replace("\n", " ").trim();

      // Εμφανίζουμε το βήμα στην περιοχή κειμένου
      displayArea.append(String.format("\nΒήμα %d: %s%n", i + 1, step));

      // Εξάγουμε τη διάρκεια του βήματος σε δευτερόλεπτα
      int duration = extractStepTime(step);
      if (duration > 0) {
        displayArea.append("Χρόνος αναμονής: " + duration + " δευτερόλεπτα.\n");

        // Ρωτάμε τον χρήστη να πατήσει OK για να ξεκινήσει η αντίστροφη μέτρηση
        int choice = JOptionPane.showConfirmDialog(null, "Πατήστε OK για να ξεκινήσει η αντίστροφη μέτρηση.",
                "Start Countdown", JOptionPane.DEFAULT_OPTION);
        if (choice == JOptionPane.CLOSED_OPTION) {
          break; // Αν ο χρήστης κλείσει το παράθυρο, σταματάμε την εκτέλεση
        }

        // Δημιουργούμε και ξεκινάμε την αντίστροφη μέτρηση
        Countdown countdown = CountdownFactory.countdown(duration);
        countdown.addNotifier(new StepNotifier());
        countdown.start();

        displayArea.append("Αντίστροφη μέτρηση ξεκινά...\n");

        // Ενημερώνουμε τον χρήστη για τον υπόλοιπο χρόνο κάθε δευτερόλεπτο
        while (countdown.secondsRemaining() > 0) {
          try {
            int remaining = (int) countdown.secondsRemaining();
            // Καθαρίζουμε τα προηγούμενα δευτερόλεπτα και ενημερώνουμε τον χρόνο που απομένει
            displayArea.setText(displayArea.getText().replaceAll("(?s)(.*?)(Χρόνος που απομένει: \\d+ δευτερόλεπτα)", "$1"));
            displayArea.append(String.format("\rΧρόνος που απομένει: %02d δευτερόλεπτα", remaining));
            Thread.sleep(1000);  // Περιμένουμε 1 δευτερόλεπτο
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            displayArea.append("\nΗ αντίστροφη μέτρηση διακόπηκε.");
            return;
          }
        }

        // Καθαρίζουμε τη γραμμή και ειδοποιούμε ότι το βήμα ολοκληρώθηκε
        displayArea.append("\rΟ χρόνος για αυτό το βήμα ολοκληρώθηκε!\n");
        countdown.stop();  // Σταματάμε την αντίστροφη μέτρηση
      }

      // Ρωτάμε τον χρήστη να πατήσει OK για να προχωρήσουμε στο επόμενο βήμα
      JOptionPane.showConfirmDialog(null, "Πατήστε OK για να συνεχίσετε στο επόμενο βήμα.",
              "Next Step", JOptionPane.DEFAULT_OPTION);
    }

    // Ειδοποιούμε τον χρήστη ότι η συνταγή ολοκληρώθηκε
    displayArea.append("\nΗ συνταγή ολοκληρώθηκε επιτυχώς! Καλή σας όρεξη!\n");
  }

  // Μέθοδος για την εξαγωγή της διάρκειας (σε δευτερόλεπτα) από το κείμενο του βήματος
  private int extractStepTime(String step) {
    String normalizedStep = step.replace("\n", " ").trim();  // Κανονικοποιούμε το κείμενο του βήματος

    // Κανονικές εκφράσεις για να εντοπίσουμε τα δευτερόλεπτα, λεπτά και ώρες
    String timePattern = "~\\{(\\d+)%seconds}";
    String minutesPattern = "~\\{(\\d+)%minutes}";
    String minutePattern = "~\\{(\\d+)%minute}";
    String hourPattern = "~\\{(\\d+)%hour}";
    String hoursPattern = "~\\{(\\d+)%hours}";

    // Ελέγχουμε ποιο χρονικό διάστημα υπάρχει στο βήμα και το επιστρέφουμε
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
    return 0;  // Επιστρέφουμε 0 αν δεν βρούμε καμία χρονική πληροφορία
  }

  // Εσωτερική κλάση για να ειδοποιεί όταν ολοκληρωθεί η αντίστροφη μέτρηση
  private static class StepNotifier implements Notifier {
    @Override
    public void finished(Countdown c) {
      // Ειδοποιούμε ότι ολοκληρώθηκε ο χρόνος για το βήμα
      System.out.println("\nΟ χρόνος για αυτό το βήμα ολοκληρώθηκε!\n");
    }
  }
}