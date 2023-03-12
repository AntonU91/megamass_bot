package com.anton.uzhva.megamazz_bot;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Test {
    public static void main(String[] args) {


                // Example date object
                Date date = new Date();

                // Create a date format using the pattern "dd-MM-yyyy"
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

                // Format the date object using the created format
                String formattedDate = dateFormat.format(date);

                // Print the formatted date
                System.out.println("Formatted date: " + formattedDate);
            }
}
