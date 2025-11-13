import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

// Enum for alert categories
enum AlertCategory {
    WATER_LEAK, POTHOLE, LOST_PET, POWER_OUTAGE, 
    STREET_LIGHT, GARBAGE, NOISE, OTHER
}

// Enum for urgency levels
enum Urgency {
    LOW(1), MEDIUM(2), HIGH(3), CRITICAL(4);
    
    private final int level;
    
    Urgency(int level) {
        this.level = level;
    }
    
    public int getLevel() {
        return level;
    }
}

// Alert class representing a community report
class Alert implements Serializable {
    private static final long serialVersionUID = 1L;
    private static int idCounter = 1000;
    
    private int id;
    private String title;
    private String description;
    private AlertCategory category;
    private Urgency urgency;
    private String location;
    private String reportedBy;
    private LocalDateTime timestamp;
    private String status;
    
    public Alert(String title, String description, AlertCategory category, 
                 Urgency urgency, String location, String reportedBy) {
        this.id = idCounter++;
        this.title = title;
        this.description = description;
        this.category = category;
        this.urgency = urgency;
        this.location = location;
        this.reportedBy = reportedBy;
        this.timestamp = LocalDateTime.now();
        this.status = "OPEN";
    }
    
    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public AlertCategory getCategory() { return category; }
    public Urgency getUrgency() { return urgency; }
    public String getLocation() { return location; }
    public String getReportedBy() { return reportedBy; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getStatus() { return status; }
    
    public void setStatus(String status) { this.status = status; }
    
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return String.format("ID: %d | %s [%s]\n" +
                           "Category: %s | Urgency: %s\n" +
                           "Location: %s\n" +
                           "Description: %s\n" +
                           "Reported by: %s | Time: %s\n" +
                           "Status: %s\n" +
                           "----------------------------------------",
                           id, title, status, category, urgency, 
                           location, description, reportedBy, 
                           timestamp.format(formatter), status);
    }
}

// Comparator for sorting by urgency
class UrgencyComparator implements Comparator<Alert> {
    @Override
    public int compare(Alert a1, Alert a2) {
        int urgencyCompare = Integer.compare(a2.getUrgency().getLevel(), 
                                            a1.getUrgency().getLevel());
        if (urgencyCompare != 0) {
            return urgencyCompare;
        }
        return a2.getTimestamp().compareTo(a1.getTimestamp());
    }
}

// Comparator for sorting by location
class LocationComparator implements Comparator<Alert> {
    @Override
    public int compare(Alert a1, Alert a2) {
        int locationCompare = a1.getLocation().compareToIgnoreCase(a2.getLocation());
        if (locationCompare != 0) {
            return locationCompare;
        }
        return a2.getTimestamp().compareTo(a1.getTimestamp());
    }
}

// Base alert system with common functionality
class AlertSystemBase {
    protected List<Alert> alerts;
    protected final String dataFile = "alerts_data.ser";
    protected Scanner scanner;
    
    public AlertSystemBase() {
        this.alerts = new ArrayList<>();
        this.scanner = new Scanner(System.in);
        loadAlerts();
    }
    
    // Submit a new alert
    public void submitAlert() {
        System.out.println("\n=== Submit New Alert ===");
        
        System.out.print("Title: ");
        String title = scanner.nextLine();
        
        System.out.print("Description: ");
        String description = scanner.nextLine();
        
        System.out.println("\nCategories:");
        AlertCategory[] categories = AlertCategory.values();
        for (int i = 0; i < categories.length; i++) {
            System.out.printf("%d. %s\n", i + 1, categories[i]);
        }
        System.out.print("Select category (1-" + categories.length + "): ");
        int catChoice = getIntInput(1, categories.length);
        AlertCategory category = categories[catChoice - 1];
        
        System.out.println("\nUrgency Levels:");
        Urgency[] urgencies = Urgency.values();
        for (int i = 0; i < urgencies.length; i++) {
            System.out.printf("%d. %s\n", i + 1, urgencies[i]);
        }
        System.out.print("Select urgency (1-" + urgencies.length + "): ");
        int urgChoice = getIntInput(1, urgencies.length);
        Urgency urgency = urgencies[urgChoice - 1];
        
        System.out.print("Location (e.g., Main Street, Park Area): ");
        String location = scanner.nextLine();
        
        System.out.print("Your name: ");
        String reportedBy = scanner.nextLine();
        
        Alert alert = new Alert(title, description, category, urgency, location, reportedBy);
        alerts.add(alert);
        saveAlerts();
        
        System.out.println("\n✓ Alert submitted successfully! Alert ID: " + alert.getId());
    }
    
    // View all alerts
    public void viewAllAlerts() {
        if (alerts.isEmpty()) {
            System.out.println("\nNo alerts in the system.");
            return;
        }
        
        System.out.println("\n=== All Alerts ===");
        System.out.println("Total alerts: " + alerts.size());
        System.out.println();
        
        for (Alert alert : alerts) {
            System.out.println(alert);
            System.out.println();
        }
    }
    
    // Sort alerts by urgency
    public void sortByUrgency() {
        if (alerts.isEmpty()) {
            System.out.println("\nNo alerts to sort.");
            return;
        }
        
        List<Alert> sortedAlerts = new ArrayList<>(alerts);
        Collections.sort(sortedAlerts, new UrgencyComparator());
        
        System.out.println("\n=== Alerts Sorted by Urgency ===");
        for (Alert alert : sortedAlerts) {
            System.out.println(alert);
            System.out.println();
        }
    }
    
    // Sort alerts by location
    public void sortByLocation() {
        if (alerts.isEmpty()) {
            System.out.println("\nNo alerts to sort.");
            return;
        }
        
        List<Alert> sortedAlerts = new ArrayList<>(alerts);
        Collections.sort(sortedAlerts, new LocationComparator());
        
        System.out.println("\n=== Alerts Sorted by Location ===");
        for (Alert alert : sortedAlerts) {
            System.out.println(alert);
            System.out.println();
        }
    }
    
    // Filter alerts by category
    public void filterByCategory() {
        System.out.println("\n=== Filter by Category ===");
        AlertCategory[] categories = AlertCategory.values();
        for (int i = 0; i < categories.length; i++) {
            System.out.printf("%d. %s\n", i + 1, categories[i]);
        }
        System.out.print("Select category (1-" + categories.length + "): ");
        int choice = getIntInput(1, categories.length);
        AlertCategory selectedCategory = categories[choice - 1];
        
        List<Alert> filtered = alerts.stream()
            .filter(a -> a.getCategory() == selectedCategory)
            .collect(Collectors.toList());
        
        if (filtered.isEmpty()) {
            System.out.println("\nNo alerts found for category: " + selectedCategory);
            return;
        }
        
        System.out.println("\n=== " + selectedCategory + " Alerts ===");
        System.out.println("Found " + filtered.size() + " alert(s)");
        System.out.println();
        
        for (Alert alert : filtered) {
            System.out.println(alert);
            System.out.println();
        }
    }
    
    // Search alerts by location
    public void searchByLocation() {
        System.out.print("\nEnter location to search: ");
        String searchTerm = scanner.nextLine().toLowerCase();
        
        List<Alert> results = alerts.stream()
            .filter(a -> a.getLocation().toLowerCase().contains(searchTerm))
            .collect(Collectors.toList());
        
        if (results.isEmpty()) {
            System.out.println("\nNo alerts found for location: " + searchTerm);
            return;
        }
        
        System.out.println("\n=== Search Results ===");
        System.out.println("Found " + results.size() + " alert(s)");
        System.out.println();
        
        for (Alert alert : results) {
            System.out.println(alert);
            System.out.println();
        }
    }
    
    // Get statistics
    public void showStatistics() {
        if (alerts.isEmpty()) {
            System.out.println("\nNo alerts to analyze.");
            return;
        }
        
        System.out.println("\n=== Community Alert Statistics ===");
        System.out.println("Total Alerts: " + alerts.size());
        
        // Count by category
        Map<AlertCategory, Long> categoryCount = alerts.stream()
            .collect(Collectors.groupingBy(Alert::getCategory, Collectors.counting()));
        
        System.out.println("\nAlerts by Category:");
        categoryCount.forEach((cat, count) -> 
            System.out.printf("  %s: %d\n", cat, count));
        
        // Count by urgency
        Map<Urgency, Long> urgencyCount = alerts.stream()
            .collect(Collectors.groupingBy(Alert::getUrgency, Collectors.counting()));
        
        System.out.println("\nAlerts by Urgency:");
        urgencyCount.forEach((urg, count) -> 
            System.out.printf("  %s: %d\n", urg, count));
        
        // Count by status
        Map<String, Long> statusCount = alerts.stream()
            .collect(Collectors.groupingBy(Alert::getStatus, Collectors.counting()));
        
        System.out.println("\nAlerts by Status:");
        statusCount.forEach((status, count) -> 
            System.out.printf("  %s: %d\n", status, count));
    }
    
    // Save alerts to file
    protected void saveAlerts() {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(dataFile))) {
            oos.writeObject(alerts);
        } catch (IOException e) {
            System.out.println("Error saving alerts: " + e.getMessage());
        }
    }
    
    // Load alerts from file
    @SuppressWarnings("unchecked")
    protected void loadAlerts() {
        File file = new File(dataFile);
        if (!file.exists()) {
            return;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(dataFile))) {
            alerts = (List<Alert>) ois.readObject();
            System.out.println("Loaded " + alerts.size() + " existing alerts.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading alerts: " + e.getMessage());
        }
    }
    
    // Helper method for integer input validation
    protected int getIntInput(int min, int max) {
        while (true) {
            try {
                int input = Integer.parseInt(scanner.nextLine());
                if (input >= min && input <= max) {
                    return input;
                }
                System.out.print("Please enter a number between " + min + " and " + max + ": ");
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number: ");
            }
        }
    }
}

// USER VERSION - Limited features for regular community members
class UserAlertSystem extends AlertSystemBase {
    
    public void showUserMenu() {
        System.out.println("\n=== USER MENU ===");
        System.out.println("1. Submit New Alert");
        System.out.println("2. View All Alerts");
        System.out.println("3. Sort by Urgency");
        System.out.println("4. Sort by Location");
        System.out.println("5. Filter by Category");
        System.out.println("6. Search by Location");
        System.out.println("7. View Statistics");
        System.out.println("8. Exit");
        System.out.print("\nSelect option (1-8): ");
    }
    
    public void run() {
        boolean running = true;
        while (running) {
            showUserMenu();
            
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                
                switch (choice) {
                    case 1:
                        submitAlert();
                        break;
                    case 2:
                        viewAllAlerts();
                        break;
                    case 3:
                        sortByUrgency();
                        break;
                    case 4:
                        sortByLocation();
                        break;
                    case 5:
                        filterByCategory();
                        break;
                    case 6:
                        searchByLocation();
                        break;
                    case 7:
                        showStatistics();
                        break;
                    case 8:
                        System.out.println("\nThank you for using Community Alert System!");
                        System.out.println("Stay safe and help your community!");
                        running = false;
                        break;
                    default:
                        System.out.println("\nInvalid option. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("\nInvalid input. Please enter a number.");
            }
        }
    }
}

// ADMIN VERSION - Full control with additional management features
class AdminAlertSystem extends AlertSystemBase {
    
    // Update alert status (ADMIN ONLY)
    public void updateAlertStatus() {
        System.out.print("\nEnter Alert ID to update: ");
        int id = getIntInput(1000, Integer.MAX_VALUE);
        
        Alert alert = alerts.stream()
            .filter(a -> a.getId() == id)
            .findFirst()
            .orElse(null);
        
        if (alert == null) {
            System.out.println("\nAlert not found with ID: " + id);
            return;
        }
        
        System.out.println("\nCurrent alert:");
        System.out.println(alert);
        
        System.out.println("\nStatus options:");
        System.out.println("1. OPEN");
        System.out.println("2. IN_PROGRESS");
        System.out.println("3. RESOLVED");
        System.out.println("4. CLOSED");
        System.out.print("Select new status (1-4): ");
        
        int statusChoice = getIntInput(1, 4);
        String[] statuses = {"OPEN", "IN_PROGRESS", "RESOLVED", "CLOSED"};
        alert.setStatus(statuses[statusChoice - 1]);
        saveAlerts();
        
        System.out.println("\n✓ Alert status updated successfully!");
    }
    
    // Delete specific alert (ADMIN ONLY)
    public void deleteAlert() {
        if (alerts.isEmpty()) {
            System.out.println("\nNo alerts to delete.");
            return;
        }
        
        System.out.print("\nEnter Alert ID to delete: ");
        int id = getIntInput(1000, Integer.MAX_VALUE);
        
        Alert alert = alerts.stream()
            .filter(a -> a.getId() == id)
            .findFirst()
            .orElse(null);
        
        if (alert == null) {
            System.out.println("\nAlert not found with ID: " + id);
            return;
        }
        
        System.out.println("\nAlert to be deleted:");
        System.out.println(alert);
        System.out.print("\nConfirm deletion? (yes/no): ");
        
        String confirmation = scanner.nextLine().trim().toLowerCase();
        
        if (confirmation.equals("yes")) {
            alerts.remove(alert);
            saveAlerts();
            System.out.println("\n✓ Alert ID " + id + " has been deleted successfully!");
        } else {
            System.out.println("\nDeletion cancelled.");
        }
    }
    
    // Clear all data (ADMIN ONLY)
    public void clearAllData() {
        if (alerts.isEmpty()) {
            System.out.println("\nNo data to clear. System is already empty.");
            return;
        }
        
        System.out.println("\n=== Clear All Data ===");
        System.out.println("WARNING: This will permanently delete ALL alerts!");
        System.out.println("Total alerts to be deleted: " + alerts.size());
        System.out.print("\nAre you sure you want to continue? (yes/no): ");
        
        String confirmation = scanner.nextLine().trim().toLowerCase();
        
        if (confirmation.equals("yes")) {
            int deletedCount = alerts.size();
            alerts.clear();
            saveAlerts();
            
            // Also delete the data file
            File file = new File(dataFile);
            if (file.exists()) {
                file.delete();
            }
            
            System.out.println("\n✓ Successfully deleted " + deletedCount + " alert(s)!");
            System.out.println("All data has been cleared from the system.");
        } else {
            System.out.println("\nOperation cancelled. No data was deleted.");
        }
    }
    
    // Export alerts to text file (ADMIN ONLY)
    public void exportToFile() {
        if (alerts.isEmpty()) {
            System.out.println("\nNo alerts to export.");
            return;
        }
        
        String exportFile = "alerts_export.txt";
        try (PrintWriter writer = new PrintWriter(new FileWriter(exportFile))) {
            writer.println("====================================");
            writer.println("COMMUNITY ALERT SYSTEM - DATA EXPORT");
            writer.println("Export Date: " + LocalDateTime.now());
            writer.println("Total Alerts: " + alerts.size());
            writer.println("====================================\n");
            
            for (Alert alert : alerts) {
                writer.println(alert);
                writer.println();
            }
            
            System.out.println("\n✓ Successfully exported " + alerts.size() + " alert(s)!");
            System.out.println("File saved as: " + exportFile);
        } catch (IOException e) {
            System.out.println("Error exporting alerts: " + e.getMessage());
        }
    }
    
    public void showAdminMenu() {
        System.out.println("\n=== ADMIN MENU ===");
        System.out.println("1. Submit New Alert");
        System.out.println("2. View All Alerts");
        System.out.println("3. Sort by Urgency");
        System.out.println("4. Sort by Location");
        System.out.println("5. Filter by Category");
        System.out.println("6. Search by Location");
        System.out.println("7. Update Alert Status");
        System.out.println("8. Delete Specific Alert");
        System.out.println("9. View Statistics");
        System.out.println("10. Export to File");
        System.out.println("11. Clear All Data");
        System.out.println("12. Exit");
        System.out.print("\nSelect option (1-12): ");
    }
    
    public void run() {
        boolean running = true;
        while (running) {
            showAdminMenu();
            
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                
                switch (choice) {
                    case 1:
                        submitAlert();
                        break;
                    case 2:
                        viewAllAlerts();
                        break;
                    case 3:
                        sortByUrgency();
                        break;
                    case 4:
                        sortByLocation();
                        break;
                    case 5:
                        filterByCategory();
                        break;
                    case 6:
                        searchByLocation();
                        break;
                    case 7:
                        updateAlertStatus();
                        break;
                    case 8:
                        deleteAlert();
                        break;
                    case 9:
                        showStatistics();
                        break;
                    case 10:
                        exportToFile();
                        break;
                    case 11:
                        clearAllData();
                        break;
                    case 12:
                        System.out.println("\nThank you for using Community Alert System!");
                        System.out.println("Stay safe and help your community!");
                        running = false;
                        break;
                    default:
                        System.out.println("\nInvalid option. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("\nInvalid input. Please enter a number.");
            }
        }
    }
}

// Main application launcher
public class CommunityAlertApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("==========================================");
        System.out.println("  Local Community Alert System v2.0");
        System.out.println("  Report and Track Community Issues");
        System.out.println("==========================================");
        System.out.println("\nSelect User Type:");
        System.out.println("1. USER (Community Member)");
        System.out.println("2. ADMIN (System Administrator)");
        System.out.print("\nEnter choice (1 or 2): ");
        
        try {
            int userType = Integer.parseInt(scanner.nextLine());
            
            if (userType == 1) {
                System.out.println("\n>>> Launching USER Version <<<");
                System.out.println("Features: Submit, View, Search, Filter alerts\n");
                UserAlertSystem userSystem = new UserAlertSystem();
                userSystem.run();
            } else if (userType == 2) {
                System.out.println("\n>>> Launching ADMIN Version <<<");
                System.out.println("Features: Full control + Update, Delete, Export, Clear\n");
                AdminAlertSystem adminSystem = new AdminAlertSystem();
                adminSystem.run();
            } else {
                System.out.println("\nInvalid choice. Exiting...");
            }
        } catch (NumberFormatException e) {
            System.out.println("\nInvalid input. Exiting...");
        }
        
        scanner.close();
    }
}