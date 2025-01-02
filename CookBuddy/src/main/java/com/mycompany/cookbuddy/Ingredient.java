
package com.mycompany.cookbuddy;
/*
 *
 * @author JAVA ASSIGNMENT 2024-2025 Βοηθός Μάγειρας
it2023101_it2023140_it2023024
 *
 */

// Η κλάση Ingredient αναπαριστά ένα υλικό συνταγής με όνομα, ποσότητα και μονάδα μέτρησης.
public class Ingredient {
    private String name;      // Όνομα του υλικ.
    private double quantity;  // Ποσότητα υλικ.
    private String unit;      // Μον. μέτρησης 

  // Κατασκευαστής της κλάσης για αρχικοποιηση
    public Ingredient(String name, double quantity, String unit) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }

   // // // Επιστρεφονται και οριζονται ονομα , ποσοτητα , και μοναδα μετρησης
    public String getName() {
        return name;
    }

    
    public void setName(String name) {
        this.name = name;
    }

    
    public double getQuantity() {
        return quantity;
    }

   
    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

   
    public String getUnit() {
        return unit;
    }

    
    public void setUnit(String unit) {
        this.unit = unit;
    }
}
