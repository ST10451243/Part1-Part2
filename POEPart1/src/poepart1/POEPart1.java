package poepart1;

import java.util.Scanner;
import javax.swing.JOptionPane;

public class POEPart1 {

    // This is where all the saved details will be stored.
    static String savedUsername;
    static String savedPassword;
    static String savedPhoneNumber;
    static String savedFirstName;
    static String savedLastName;

    
    static Scanner scanner = new Scanner(System.in);

    // This is the part that will check if the username that was entered is valid or not.
    public static boolean isUsernameValid(String username) {
       
        if (username.length() > 5 || !username.contains("_")) {
            return false;
        }
        return true;
    }

    // This is where the password is checked if it meets the conditions.
    public static boolean isPasswordValid(String password) {
        if (password.length() < 8) {
            return false;
        }

        boolean hasCapitalLetter = false;
        boolean hasNumbers = false;
        boolean hasSpecialChars = false;

        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);

            if (Character.isUpperCase(c)) {
                hasCapitalLetter = true;
            }
            if (Character.isDigit(c)) {
                hasNumbers = true;
            }
            if (!Character.isLetterOrDigit(c)) {
                hasSpecialChars = true;
            }
        }

        return hasCapitalLetter && hasNumbers && hasSpecialChars;
    }

    public static boolean isPhoneNumberValid(String phoneNumber) {
        if (!phoneNumber.startsWith("+")) {
            return false;
        }
        
        String digitsOnly = phoneNumber.substring(1);
        if (digitsOnly.length() > 10) {
            return false;
        }

        for (int i = 1; i < phoneNumber.length(); i++) {
            if (!Character.isDigit(phoneNumber.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    public static String registerUser(String username, String password, String phoneNumber, String firstName, String lastName) {
        if (!isUsernameValid(username)) {
            return "Username is not correctly formatted, please ensure that your username contains an underscore and is no more than five characters in length.";
        }

        if (!isPasswordValid(password)) {
            return "Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.";
        }

        if (!isPhoneNumberValid(phoneNumber)) {
            return "Cell number is incorrectly formatted or does not contain international code, please correct the number and try again.";
        }

      
        savedUsername = username;
        savedPassword = password;
        savedPhoneNumber = phoneNumber;
        savedFirstName = firstName;
        savedLastName = lastName;

        return "Username successfully captured.Password successfully captured.Cell phone number successfully added.";
    }

    public static boolean checkLoginDetails(String username, String password) {
        if (savedUsername == null || savedPassword == null) {
            return false;
        }

        return username.equals(savedUsername) && password.equals(savedPassword);
    }

    public static String getLoginMessage(boolean isSuccessful) {
        if (isSuccessful) {
            return "Welcome " + savedFirstName + " " + savedLastName + ", it is great to see you again.";
        } else {
            return "Username or password incorrect, please try again.";
        }
    }
     // This is the part that will be dealing with the registration process.
    public static void register() {
        JOptionPane.showMessageDialog(null, "---Registration---");

        String accountName = JOptionPane.showInputDialog("Enter username:");

        String secretPassword = JOptionPane.showInputDialog("Enter password:");

        String contactNumber = JOptionPane.showInputDialog("Enter phone number with international code (e.g., +27831234567):");

        String forename = JOptionPane.showInputDialog("Enter your first name:");

        String userLastName = JOptionPane.showInputDialog("Enter your last name:");

        String registrationMessage = registerUser(accountName, secretPassword, contactNumber, forename, userLastName);
        JOptionPane.showMessageDialog(null, registrationMessage);

        if (registrationMessage.contains("successfully")) {
            JOptionPane.showMessageDialog(null, "Please login with your new account.");
            login();
        } else {
            JOptionPane.showMessageDialog(null, "Please try registering again.");
            register();
        }
    }

    public static void login() {
        JOptionPane.showMessageDialog(null, "---Login---");

        String accountName = JOptionPane.showInputDialog("Enter username:");

        String secretPassword = JOptionPane.showInputDialog("Enter password:");

        boolean loginSuccess = checkLoginDetails(accountName, secretPassword);
        String message = getLoginMessage(loginSuccess);
        JOptionPane.showMessageDialog(null, message);
        
        if (loginSuccess) {
            launchPart2();
        }
    }
    /*
    method that will take you to part 2 of the poe
    */
    public static void launchPart2() {
        try {
            Class<?> part2Class = Class.forName("Part2");
            java.lang.reflect.Method mainMethod = part2Class.getMethod("main", String[].class);
            mainMethod.invoke(null, (Object) new String[0]);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error launching Part 2: " + e.getMessage());
        }
    }
    
    
     // This is the option part where the user will be able to choose the options they want to do first.
    public static void execute() {
        String[] options = {"1.Register", "2.Login"};
        int choice = JOptionPane.showOptionDialog(null, 
            "Welcome to the Login System. Enter your choice:", 
            "Login System", 
            JOptionPane.DEFAULT_OPTION, 
            JOptionPane.INFORMATION_MESSAGE, 
            null, 
            options, 
            options[0]);

        if (choice == 0) {
            register();
        } else if (choice == 1) {
            login();
        } else {
            JOptionPane.showMessageDialog(null, "Invalid choice. Please try again.");
        }
    }

    public static void main(String[] args) {
        execute();
    }
}
/*
used the banking as reference for the menu option on my code.
and i also used the structure of the of the banking app and the enhanced scenario "Volunteer porta; with the role assignment and login secure as the structure as well"
*/