package com.example;


import tech.tablesaw.api.Table;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import com.example.Analysis.StatisticalAnalysis;
import java.util.Scanner;

public class CSVLoader {

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);


        // Path to your CSV file in the resources folder
        String csvFile1 ="/data01.csv";
        
        
        System.out.println("1.Statistical Analysis for Hospital Mortality Prediction Dataset");
        System.out.println("Enter a choice");

        String choice = scan.nextLine();
        switch(choice)
        {
            case "1":
            processCSVFile(csvFile1); // Process the CSV file for statistical analysis
            break;
            default:
            System.out.println("Invalid Choice.");


        }
        scan.close(); // Close the scanner to prevent resource leaks



    }



    private static void processCSVFile(String csvFile){
        try (InputStream inputStream = CSVLoader.class.getResourceAsStream(csvFile);
         InputStreamReader reader = new InputStreamReader(inputStream);
         BufferedReader br = new BufferedReader(reader)) {


            Table table = Table.read().csv(reader); // Load CSV data into a Table object
            table = table.dropRowsWithMissingValues(); // Remove rows with missing values

            System.out.println("Column names:");
            System.out.println(table.columnNames()); // Print column names of the dataset
            StatisticalAnalysis.performAnalysis(table); // Perform statistical analysis on the table

            


}
        catch (IOException e) {
             e.printStackTrace(); // Handle any I/O exceptions that occur
}
    }
}
