package com.lyit.csd;

import java.util.Scanner;

public class Console {

  public static void run(){


    boolean systemOn = true;

    while (systemOn){

      String choice;

      Scanner userAnswer = new Scanner(System.in);  // Create a Scanner object
      System.out.println("\n+-------------------------------------------------+");
      System.out.println("|         Welcome to the Portfolio System         |");
      System.out.println("|                      Menu                       |");
      System.out.println("+-------------------------------------------------+\n");
      System.out.println("A - Purchase asset.");
      System.out.println("B - Sell an asset.");
      System.out.println("C - Get trending stock on specific region.");
      System.out.println("D - Get historical data on specified region.");
      System.out.println("E - Get realtime quote on specific assets.");
      System.out.println("F - Get total portfolio live value.");
      System.out.println("G - List of all investments.");
      System.out.println("H - List of specific portfolio type.");
      System.out.println("I - List of purchased assets in specific interval.");
      System.out.println("J - List of sold assets in specific interval.");
      System.out.println("\nQ - Exit the Portfolio System.");

      System.out.print("\nPlease type your option of choice: ");

      choice = userAnswer.next().toLowerCase();  // Read user input

      switch (choice){
        case "a":
        case "b":
        case "c":
        case "d":
        case "e":
        case "f":
        case "g":
        case "h":
        case "i":
        case "j":
          System.out.println("\nChoice = " + choice);
          break;
        case "q":
          systemOn = false;
          System.out.println("\nThank you for using the Portfolio System");
          break;
        default:
          System.out.println("\nPlease enter a valid option.\n\n");
      }

    }


  }

}
