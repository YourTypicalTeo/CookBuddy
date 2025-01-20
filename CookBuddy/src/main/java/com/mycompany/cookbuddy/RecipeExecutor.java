package com.mycompany.cookbuddy;
/*
 *
 * @author JAVA ASSIGNMENT 2024-2025 Βοηθός Μάγειρας
it2023101_it2023140_it2023024
 *
 */
import gr.hua.dit.oop2.countdown.Countdown;
import gr.hua.dit.oop2.countdown.CountdownFactory;
import gr.hua.dit.oop2.countdown.Notifier;
import javax.swing.*;
import java.util.List;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class RecipeExecutor {

  private final Recipe recipe;      // Η συνταγή που θα εκτελεστεί
  private final JTextArea displayArea;  // Περιοχή κειμένου για την εμφάνιση των πληροφοριών
  private boolean keyPressed = false;  // Για να παρακολουθούμε αν έχει πατηθεί κάποιο πλήκτρο

  // Κατασκευαστής για την αρχικοποίηση της συνταγής και της περιοχής κειμένου
  public RecipeExecutor(Recipe recipe, JTextArea displayArea) {
    this.recipe = recipe;
    this.displayArea = displayArea;

    // Προσθήκη KeyListener στην περιοχή κειμένου
    displayArea.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        keyPressed = true;  // Όταν πατηθεί ένα πλήκτρο, ορίζουμε την τιμή σε true
      }
    });
    displayArea.setFocusable(true);  // Διασφαλίζουμε ότι η περιοχή κειμένου μπορεί να δεχτεί εισροές πληκτρολογίου
  }

  // Μέθοδος εκτέλεσης της συνταγής
  public void executeRecipe() {
    // Εμφανίζουμε την αρχή της εκτέλεσης της συνταγής
    displayArea.setText("");
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

        // Περιμένουμε για το πάτημα ενός πλήκτρου για να ξεκινήσει η αντίστροφη μέτρηση
        displayArea.append("Πατήστε οποιοδήποτε πλήκτρο για να ξεκινήσει η αντίστροφη μέτρηση...\n");
        waitForKeyPress();  // Περιμένουμε για το πάτημα πλήκτρου

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
        displayArea.append("\r\nΟ χρόνος για αυτό το βήμα ολοκληρώθηκε!\n");
        countdown.stop();  // Σταματάμε την αντίστροφη μέτρηση
      }

      // Εισαγωγή αναμονής για το επόμενο βήμα με το πάτημα ενός πλήκτρου
      displayArea.append("Πατήστε οποιοδήποτε πλήκτρο για να συνεχίσετε στο επόμενο βήμα...\n");

      // Περιμένουμε το πάτημα πλήκτρου
      waitForKeyPress();
    }

    // Ειδοποιούμε τον χρήστη ότι η συνταγή ολοκληρώθηκε
    displayArea.append("\nΗ συνταγή ολοκληρώθηκε επιτυχώς! Καλή σας όρεξη!\n");
  }

  // Μέθοδος για να περιμένει το πάτημα οποιουδήποτε πλήκτρου
  private void waitForKeyPress() {
    // Περιμένουμε μέχρι το πλήκτρο να πατηθεί
    while (!keyPressed) {
      try {
        Thread.sleep(100);  // Περιμένουμε 100ms για να ελέγξουμε αν έχει πατηθεί κάποιο πλήκτρο
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }
    keyPressed = false;  // Επαναφέρουμε το flag για την επόμενη χρήση
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