import java.util.ArrayList;
import java.util.Random;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.*;
import java.util.Scanner;
import javax.swing.JOptionPane;

public class Part2 {
    static class Message {
        private String messageID;
        private int numMessagesSent;
        private String recipient;
        private String messageText;
        private String messageHash;

        public Message(String messageID, int numMessagesSent, String recipient, String messageText) {
            this.messageID = messageID;
            this.numMessagesSent = numMessagesSent;
            this.recipient = recipient;
            this.messageText = messageText;
            this.messageHash = createMessageHash();
        }

        public boolean checkMessageID() {
            return messageID.length() <= 10; // this si where the message ID will be checked.
        }

        public int checkRecipientCell() {
            if (recipient.length() <= 10 || !recipient.startsWith("+")) return 0;
            return 1;
        }

        public String createMessageHash() {
            if (messageText.length() == 0) return "00:0:";
            String firstTwo = messageID.substring(0, Math.min(2, messageID.length()));
            String messageNum = String.valueOf(numMessagesSent);
            String[] words = messageText.trim().split("\\s+");
            String firstWord = words.length > 0 ? words[0].toUpperCase() : "";
            String lastWord = words.length > 1 ? words[words.length - 1].toUpperCase() : firstWord;
            return firstTwo + ":" + messageNum + ":" + firstWord + lastWord;
        }

        public String sentMessage() {
            String[] options = {"Send Message", "Disregard Message", "Store Message to send later"};
            int choice = JOptionPane.showOptionDialog(null, "Once the message is completed, choose one of the following options:", "Message Options", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            switch (choice) {
                case 0: return "Send Message";
                case 1: return "Disregard Message";
                case 2: return "Store Message";
                default: return "Send Message"; 
            }
        }

        public String printMessages() {
            return "MessageID: " + messageID + ", Messages Sent: " + numMessagesSent + ", Recipient: " + recipient + ", Message: " + messageText + ", Hash: " + messageHash;
        }

        public int returnTotalMessages() {
            return numMessagesSent;
        }

        public String storeMessage() {
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()); // used POE part 2 helper as reference. Took information from there.
            return "{\n" +
                   "  \"messageID\": \"" + messageID + "\",\n" +
                   "  \"numMessagesSent\": " + numMessagesSent + ",\n" +
                   "  \"recipient\": \"" + recipient + "\",\n" +
                   "  \"messageText\": \"" + escapeJson(messageText) + "\",\n" +
                   "  \"messageHash\": \"" + messageHash + "\",\n" +
                   "  \"timestamp\": \"" + timestamp + "\"\n" +
                   "}";
        }

        public String getMessageID() { return messageID; }
        public int getNumMessagesSent() { return numMessagesSent; }
        public String getRecipient() { return recipient; }
        public String getMessageText() { return messageText; }
        public String getMessageHash() { return messageHash; }
        public void incrementMessageCount() { this.numMessagesSent++; }
    }

    private static final File FILE = new File("messages.json");
    private static ArrayList<Message> allMessages = new ArrayList<>();
    private static int totalMessagesCount = 0;
    private static int maxMessages = 0;

    public static void main(String[] args) {
        JOptionPane.showMessageDialog(null, "Welcome to the QuickChat.", "Welcome", JOptionPane.INFORMATION_MESSAGE);
        String maxMessagesStr = JOptionPane.showInputDialog(null, "How many messages do you want to enter or send?", "Message Limit", JOptionPane.QUESTION_MESSAGE);
        if (maxMessagesStr == null || maxMessagesStr.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "No limit entered. Exiting.", "Cancelled", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        try {
            maxMessages = Integer.parseInt(maxMessagesStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid number. Setting limit to 1.", "Error", JOptionPane.ERROR_MESSAGE);
            maxMessages = 1;
        }

        if (!FILE.exists() || FILE.length() == 0) {
            JOptionPane.showMessageDialog(null, "No messages found..", "Info", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Saved Messages: " + readAllMessages(), "Stored Messages", JOptionPane.INFORMATION_MESSAGE);
        }

        while (true) {
            int choice = showMainMenu();
            switch (choice) {
                case 0:
                    if (totalMessagesCount < maxMessages) {
                        sendMessage();
                    } else {
                        JOptionPane.showMessageDialog(null, "You have reached the maximum number of messages (" + maxMessages + ").", "Limit Reached", JOptionPane.WARNING_MESSAGE);
                    }
                    break;
                case 1:
                    JOptionPane.showMessageDialog(null, "Coming Soon.", "Feature", JOptionPane.INFORMATION_MESSAGE);
                    break;
                case 2:
                    JOptionPane.showMessageDialog(null, "Thank you for using QuickChat. Bye!", "Bye", JOptionPane.INFORMATION_MESSAGE);
                    return;
                default:
                    JOptionPane.showMessageDialog(null, "Invalid option. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static int showMainMenu() {
        String[] options = {"Send Messages", "Show recently sent messages", "Quit"};
        return JOptionPane.showOptionDialog(null, "QuickChat Main Menu  Choose an option:", "Main Menu", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
    }

    private static void sendMessage() {
        JOptionPane.showMessageDialog(null, "Starting message creation...", "Send Message", JOptionPane.INFORMATION_MESSAGE);
        String messageID = generateRandomMessageID();
        JOptionPane.showMessageDialog(null, "Message ID generated: " + messageID, "Message ID", JOptionPane.INFORMATION_MESSAGE);
        String recipient = getRecipientNumber(); if (recipient == null) return;
        String messageText = getMessageText(); if (messageText == null) return;
        totalMessagesCount++;
        Message message = new Message(messageID, totalMessagesCount, recipient, messageText);
        if (!validateMessage(message)) {
            totalMessagesCount--;
            return;
        }
        displayMessageDetails(message);
        String action = message.sentMessage();
        handleMessageAction(message, action);
    }

    private static String generateRandomMessageID() {
        Random random = new Random();
        StringBuilder messageID = new StringBuilder();
        for (int i = 0; i < 10; i++) messageID.append(random.nextInt(10));
        return messageID.toString();
    }

    private static String getRecipientNumber() {
        while (true) {
            String recipient = JOptionPane.showInputDialog(null, "Enter recipient cell number (with international code, e.g., +27718693002):", "Recipient Number", JOptionPane.QUESTION_MESSAGE);
            if (recipient == null) return null;
            if (recipient.startsWith("+") && recipient.length() > 10) {
                JOptionPane.showMessageDialog(null, "Cell phone number successfully captured.", "Success", JOptionPane.INFORMATION_MESSAGE);
                return recipient;
            } else {
                JOptionPane.showMessageDialog(null, "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.", "Invalid Format", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static String getMessageText() {
        while (true) {
            String messageText = JOptionPane.showInputDialog(null, "Enter your message (max 250 characters):", "Message Text", JOptionPane.QUESTION_MESSAGE);
            if (messageText == null) return null;
            if (messageText.length() <= 250) {
                JOptionPane.showMessageDialog(null, "Message ready to send.", "Success", JOptionPane.INFORMATION_MESSAGE);
                return messageText;
            } else {
                JOptionPane.showMessageDialog(null, "Message exceeds 250 characters by " + (messageText.length() - 250) + ", please reduce size.", "Message Too Long", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static boolean validateMessage(Message message) {
        if (!message.checkMessageID()) {
            JOptionPane.showMessageDialog(null, "Error: Message ID exceeds 10 characters.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (message.checkRecipientCell() == 0) {
            JOptionPane.showMessageDialog(null, "Error: Recipient number format is invalid.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private static void displayMessageDetails(Message message) {
        String details = "Message Details:\n" +
                         "Message ID: " + message.getMessageID() + "\n" +
                         "Number of messages sent: " + message.getNumMessagesSent() + "\n" +
                         "Recipient: " + message.getRecipient() + "\n" +
                         "Message: " + message.getMessageText() + "\n" +
                         "Message Hash: " + message.getMessageHash();
        JOptionPane.showMessageDialog(null, details, "Message Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void handleMessageAction(Message message, String action) {
        switch (action) {
            case "Send Message":
                allMessages.add(message);
                JOptionPane.showMessageDialog(null, "Message successfully sent.Total messages sent: " + totalMessagesCount, "Message Sent", JOptionPane.INFORMATION_MESSAGE);
                break;
            case "Disregard Message":
                totalMessagesCount--;
                JOptionPane.showMessageDialog(null, "Press 0 to delete message. Message discarded.", "Message Discarded", JOptionPane.INFORMATION_MESSAGE);
                break;
            case "Store Message":
                allMessages.add(message);
                appendToJsonFile(message.storeMessage());
                JOptionPane.showMessageDialog(null, "Message successfully stored.JSON Format: " + message.storeMessage(), "Message Stored", JOptionPane.INFORMATION_MESSAGE);
                break;
        }

        if (!allMessages.isEmpty()) {
            StringBuilder allMessagesStr = new StringBuilder("All Messages: ");
            for (Message msg : allMessages) allMessagesStr.append(msg.printMessages()).append(" ");
            JOptionPane.showMessageDialog(null, allMessagesStr.toString(), "All Messages", JOptionPane.INFORMATION_MESSAGE);
        }

        if (FILE.exists() && FILE.length() > 0) {
            JOptionPane.showMessageDialog(null, "All Stored Messages:\n" + readAllMessages(), "Updated Messages", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private static void appendToJsonFile(String data) {
        try (FileWriter fw = new FileWriter(FILE, true)) {
            if (FILE.length() != 0) fw.write(",\n");
            fw.write(data);
            JOptionPane.showMessageDialog(null, "Message saved to file successfully!", "File Saved", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Failed to write: " + e.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static String readAllMessages() {
        try (Scanner sc = new Scanner(FILE)) {
            StringBuilder all = new StringBuilder();
            while (sc.hasNextLine()) all.append(sc.nextLine()).append("\n");
            return all.toString();
        } catch (IOException e) {
            return "Error reading messages.";
        }
    }

    private static String escapeJson(String input) {
        return input.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
/*
For this whole part 2, i literally used the poe helper, and just changed a few things here and there, hence the code is longer than the helper you provided for us.
*/

