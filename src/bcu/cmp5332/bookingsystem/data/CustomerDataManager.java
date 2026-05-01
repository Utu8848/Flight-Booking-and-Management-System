package bcu.cmp5332.bookingsystem.data;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * Manages persistence of customer data to and from files.
 * 
 * @author M9
 */


public class CustomerDataManager implements DataManager {

    /**
     * Constructs a CustomerDataManager.
     */
    public CustomerDataManager() {
    }
    
    private final String RESOURCE = "./resources/data/customers.txt";
    
    @Override
    public void loadData(FlightBookingSystem fbs) throws IOException, FlightBookingSystemException {
        File file = new File(RESOURCE);
        if (!file.exists()) {
            System.out.println("Customer file not found, creating new file...");
            file.getParentFile().mkdirs();
            file.createNewFile();
            return;
        }
        
        try (Scanner sc = new Scanner(file)) {
            int line_idx = 1;
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (line.trim().isEmpty()) continue;
                
                String[] properties = line.split(SEPARATOR, -1);
                try {
                    int id = Integer.parseInt(properties[0]);
                    String name = properties[1];
                    String phone = properties[2];
                    
                    // Support for email (with default for backward compatibility)
                    String email = properties.length > 3 && !properties[3].isEmpty() ? 
                                  properties[3] : "noemail@gmail.com";
                    boolean deleted = properties.length > 4 && !properties[4].isEmpty() ? 
                                     Boolean.parseBoolean(properties[4]) : false;
                    
                    Customer customer = new Customer(id, name, phone, email);
                    customer.setDeleted(deleted);
                    fbs.addCustomer(customer);
                    
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) {
                    System.err.println("Error parsing customer on line " + line_idx + ": " + line);
                    System.err.println("Error: " + ex.getMessage());
                }
                line_idx++;
            }
        }
    }

    @Override
    public void storeData(FlightBookingSystem fbs) throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter(RESOURCE))) {
            for (Customer customer : fbs.getCustomers()) {
                out.print(customer.getId() + SEPARATOR);
                out.print(customer.getName() + SEPARATOR);
                out.print(customer.getPhone() + SEPARATOR);
                out.print(customer.getEmail() + SEPARATOR);
                out.print(customer.isDeleted() + SEPARATOR);
                out.println();
            }
        }
    }
}
