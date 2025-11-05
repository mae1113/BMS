import java.io.*;
import java.io.Console;
import java.io.IOException;
import java.sql.*;
import oracle.jdbc.driver.*;
import oracle.sql.*;
import model.*;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


public class BanquetMenu {
    public static void main(String[] args) throws SQLException, IOException, InterruptedException {
        int BIN = 1005;
        Scanner scanner = new Scanner(System.in);
        // Login
        clearScreen();
        Console console = System.console();
        /* 
        System.out.print("Enter your username: ");    // Your Oracle ID with double quote
        String username = console.readLine();         // e.g. "98765432d"
        System.out.print("Enter your password: ");    // Password of your Oracle Account
        char[] password = console.readPassword();
        String pwd = String.valueOf(password);
        */
        // Connection
        DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
        OracleConnection conn =
                (OracleConnection)DriverManager.getConnection(
                        "jdbc:oracle:thin:@studora.comp.polyu.edu.hk:1521:dbms","\"23087882d\"@dbms","dpmcixni");
        clearScreen();


        int choice = 0;
        while (choice != 8) {
            System.out.println("\nBanquet Management System\n");
            System.out.println("1. Create Banquet");  
            //System.out.println("2. List All Banquets(for testing)");
            System.out.println("2. List All Banquets");
            System.out.println("3. Update Banquets");
            //System.out.println("4. List all attendee (for testing)");
            System.out.println("4. List all attendees");
            System.out.println("5. Update attendee Registration information");
            //System.out.println("6. List All registration(for testing)");
            System.out.println("6. List All registration");
            System.out.println("7. Generate reports");
            System.out.println("8. Exit");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    createBanquet(scanner, conn);
                    break;
                case 2:
                    clearScreen();
                    Banquet.listAll(conn);
                    break;
                case 3:
                    updateBanquet(scanner, conn);
                    break;
                case 4:
                    clearScreen();
                    Attendee.listAll(conn);
                    break;
                case 5:
                    updateRegis(scanner, conn);
                    break;
                case 6:
                    clearScreen();
                    Register.listAll(conn);
                    break;
                case 7:
                    genReport(scanner, conn);
                    break;
                case 8:
                    System.out.println("Exiting the system. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }

        scanner.close();
    }

    private static void createBanquet(Scanner scanner, Connection conn) throws SQLException, IOException, InterruptedException {
        clearScreen();
        System.out.println("Create Banquet\n");

        String name = "";
        boolean validName =false;
        while (!validName) {
            System.out.print("Enter Banquet name: ");
            name = scanner.nextLine().trim();
            if (name.isEmpty()) {System.out.println("Banquet name cannot be empty.");
            } else {validName = true;
            }
        }

        /*System.out.print("Enter Date and Time (yyyy-MM-dd HH:mm): ");
        String input = scanner.nextLine();
        LocalDateTime dateTime = LocalDateTime.parse(inp.replace(" ", "T"));*/

        LocalDateTime dateTime = null;
        boolean validDt = false;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        while (!validDt) {
            System.out.print("Enter Date and Time (yyyy-MM-dd HH:mm): ");
            String inp = scanner.nextLine().trim();
            try {dateTime = LocalDateTime.parse(inp, formatter);    validDt = true;
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date and time format. Please use the format yyyy-MM-dd HH:mm.");
            }
        }

        String address = "";
        boolean validAddr =false;
        while (!validAddr) {
            System.out.print("Enter Address: ");
            address = scanner.nextLine().trim();
            if (address.isEmpty()) {System.out.println("Address cannot be empty.");
            } else {validAddr = true;
            }
        }

        String location = "";
        boolean validLoc =false;
        while (!validLoc) {
            System.out.print("Enter Address: ");
            location = scanner.nextLine().trim();
            if (location.isEmpty()) {System.out.println("Location cannot be empty.");
            } else {validLoc = true;
            }
        }

        String contactFirstName = "";
        boolean validFN =false;
        while (!validFN) {
            System.out.print("Enter First Name of Contact Staff: ");
            contactFirstName = scanner.nextLine().trim();
            if (contactFirstName.isEmpty()) {System.out.println("Contact First Name cannot be empty.");
            } else {validFN = true;
            }
        }

        String contactLastName = "";
        boolean validLN =false;
        while (!validLN) {
            System.out.print("Enter Last Name of Contact Staff: ");
            contactLastName = scanner.nextLine().trim();
            if (contactLastName.isEmpty()) {System.out.println("Contact Last Name cannot be empty.");
            } else {validLN = true;
            }
        }

        /*System.out.print("Is the Banquet Available? (Y/N): ");
        char available = scanner.nextLine().charAt(0);*/

        char available = ' ';
        boolean validAvail = false;
        while (!validAvail) {
            System.out.print("Is the Banquet Available? (Y/N): ");
            String avail = scanner.nextLine().trim().toUpperCase();
            if (avail.length()==1 && (avail.charAt(0)=='Y' || avail.charAt(0)=='N')) {
                available = avail.charAt(0);
                validAvail = true; 
            } else {System.out.println("Invalid input. Please enter 'Y' for Yes or 'N' for No.");
            }
        }

        /*System.out.print("Enter Quota: ");
        int quota = scanner.nextInt();*/
        
        int quota = 0;
        boolean quotaValid = false;
        while (!quotaValid) {
            System.out.print("Enter positive integer for Quota: ");
            try {quota = scanner.nextInt();
                if (quota>0) {
                    quotaValid =true;
                } else {
                    System.out.println("Please enter a positive integer for Quota.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a numeric value.");
                scanner.next(); 
            }
        }
        
        scanner.nextLine();
        Banquet banquet = new Banquet(getNextBIN(conn), name, dateTime, address, location, contactFirstName, contactLastName, available, quota);
        banquet.create(conn);

        //add meals
        ArrayList<String> mealname = new ArrayList<String>();
        for (int i=0; i<4; i++) {
            System.out.println("\n--enter details for meal " +(i+1));
            mealname = addMeal(scanner,conn, banquet.getBin(), mealname);
        }
    }

    private static int getNextBIN(Connection conn) throws SQLException {
        String query = "SELECT MAX(BIN) AS MAX_BIN FROM BANQUET";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        if (!rs.next()) {
            return 1001;
        } else {
            int maxBIN = rs.getInt("MAX_BIN");
            return maxBIN + 1;
        }
    }
    
    private static ArrayList<String> addMeal(Scanner scanner, Connection conn, int bin, ArrayList<String> mealname) throws SQLException {
        String dishName ="";
        while (true){
            System.out.print("Enter Dish Name: ");
            dishName = scanner.nextLine();

            if (mealname.contains(dishName)){
                System.out.println("Dish name already exists. Please enter a unique one: ");
            }else{
                mealname.add(dishName);break;
            }
        }


        double price = 0;
        boolean priceValid = false;
        while (!priceValid) {
            System.out.print("Enter positive value for Price: ");
            try {price = scanner.nextDouble();
                if (price>0) {
                    priceValid =true;
                } else {
                    System.out.println("Please enter a positive value for Price.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a numeric value.");
                scanner.next(); 
            }
        }

        Boolean check = true;
        String mealType = "";
        String[] mealis = {"fish", "chicken", "beef", "vegetarian"};
        /*while (check){
            System.out.print("Enter Meal Type: (\"fish\", \"chicken\", \"beef\", \"vegetarian\")");
            mealType = scanner.nextLine();
            for(String i : mealis)if(i.equals(mealType))check = false;
        }*/
        while (check) {
            System.out.print("Enter Meal Type: (\"fish\", \"chicken\", \"beef\", \"vegetarian\"): ");
            mealType = scanner.nextLine().trim();
            check = true;
            for (String i : mealis) {
                if (i.equalsIgnoreCase(mealType)) {check = false;   break;
                }
            }
        }

        System.out.print("Enter Special Cuisine: ");
        String specialCuisine = scanner.nextLine();

        // insert meal into the database
        PreparedStatement pstmt = conn.prepareStatement(
                "INSERT INTO MEAL (BIN, DISHNAME, PRICE, TYPE, SPECIALCUISINE) VALUES (?, ?, ?, ?, ?)");
        pstmt.setInt(1, bin);
        pstmt.setString(2, dishName);
        pstmt.setDouble(3, price);
        pstmt.setString(4, mealType);
        pstmt.setString(5, specialCuisine);
        pstmt.executeUpdate();

        System.out.println("Meal added successfully!");
        return mealname;
    }

    public static void updateBanquet(Scanner scanner, Connection conn) throws SQLException, IOException, InterruptedException {
        clearScreen();
        System.out.println("Update Banquet\n");
        int bin=0;
        boolean existcheck = true;
        while(existcheck){
            System.out.println("Enter bin: ");
            bin = scanner.nextInt();
            //try {
                if(checkexist(conn, bin))existcheck=false;
            //} catch (SQLException e) {}
        }
        String loop = "";
        String[] collist = {"BANQUETNAME", "DATETIME", "ADDRESS", "LOCATION", "CONTACTFN", "CONTACTLN", "AVAILABLE", "QUOTA"};
        String col="";
        while(!loop.equals("-1")){
            
            while(!existcheck){
                System.err.println("Enter column name: ");
                col = scanner.nextLine().toUpperCase().trim();
                for (String i : collist)if(col.equals(i))existcheck=true;
                if(!existcheck)System.err.println("Please enter a correct column name! ");
            }
            while(existcheck){
                System.err.println("Enter new input: ");
                String input = scanner.nextLine().trim();
                try {
                    if(update(conn, bin, col, input))existcheck=false;
                } catch (SQLException e) {

                }
            }
            System.out.println("Type anything to update other column on this, or -1 for stopping this function");
			loop = scanner.nextLine().trim();

        }
    }

    public static boolean checkexist(Connection conn, int reqbin) throws SQLException {
        try {
            PreparedStatement a = conn.prepareStatement("SELECT BIN, BANQUETNAME, DATETIME, ADDRESS, LOCATION, CONTACTFN, CONTACTLN, AVAILABLE, QUOTA FROM BANQUET WHERE BIN = ?");
            a.setInt(1, reqbin);
            ResultSet rset = a.executeQuery();
            while (rset.next()) {
                int bin = rset.getInt(1);
                String name = rset.getString(2);
                Timestamp dateTime = rset.getTimestamp(3);
                String address = rset.getString(4);
                String location = rset.getString(5);
                String contactFN = rset.getString(6);
                String contactLN = rset.getString(7);
                String available = rset.getString(8);
                int quota = rset.getInt(9);
    
                System.out.printf(
                        "BIN: %d | BanquetName: %s | DateTime: %s | Address: %s | Location: %s | ContactFN: %s | ContactLN: %s | Available: %s | Quota: %d%n",
                        bin, name, dateTime.toLocalDateTime(), address, location, contactFN, contactLN, available, quota
                );
            }
            rset.wasNull();
            return true;
        }
        catch(SQLException e){
            System.err.println("This is not an exisiting bin. Please enter a correct one");
            return false;
        }
    }

    public static boolean update(Connection conn, int reqbin, String col, String input) throws SQLException {
        try {
            PreparedStatement a = conn.prepareStatement("UPDATE BANQUET SET "+col+" = ? WHERE BIN = ?");
            a.setInt(2, reqbin);
            switch (col) {
                case "BANQUETNAME, DATETIME, ADDRESS, LOCATION, CONTACTFN, CONTACTLN, AVAILABLE, QUOTA":
                    
                    break;
                case "BANQUETNAME":
                    a.setString(1, input);
                    break;
                case "DATETIME":
                    a.setTimestamp(1, Timestamp.valueOf(input+":00"));
                    break;
                case "ADDRESS":
                    a.setString(1, input);
                    break;
                case "LOCATION":
                    a.setString(1, input);
                    break;
                case "CONTACTFN":
                    a.setString(1, input);
                    break;
                case "CONTACTLN":
                    a.setString(1, input);
                    break;
                case "AVAILABLE":
                    a.setString(1, String.valueOf(input));
                    break;
                case "QUOTA":
                    a.setInt(1, (int)Integer.parseInt(input));
                    break;
            }
            a.executeUpdate();
            System.err.println("Updated version: ");
            checkexist(conn, reqbin);
            return true;
        }
        catch(SQLException e){
            System.err.println("The input is wrong. Please enter a correct one");
            return false;
        }
    }

    public static void updateRegis(Scanner scanner, Connection conn) throws SQLException, IOException, InterruptedException{
        System.out.println("Update acc registration information\n");
        String email="";
        boolean existcheck = true;
        while(existcheck){ //check email exist
            System.out.println("Enter email: ");
            email = scanner.nextLine().trim();
            //try {
                if(Attendee.checkexist(conn, "EMAIL", email)){
                    existcheck=false;
                }
            //} catch (SQLException e) {}
        }

        String loop = "";
        String[] collist = {"MEALCHOICE", "DRINKCHOICE", "REMARKS"};
        String bin = "";
        String col = "";
        while(!loop.equals("-1")){
            System.out.println("Email: "+email);
            if(!Register.printUsingEmail(conn, email))updateRegis(scanner, conn);

            while(!existcheck){
                System.err.println("Enter bin: ");
                bin = scanner.nextLine().trim();
                if(!bin.matches("[0-9]+"))continue;
                if(Register.checkexist(conn, "BIN", bin))existcheck=true;
                if(!existcheck)System.err.println("Please enter a existing bin! ");
            }
            while(existcheck){
                System.err.println("Enter column name: (MEALCHOICE, DRINKCHOICE, REMARKS)");
                col = scanner.nextLine().toUpperCase().trim();
                for (String i : collist)if(col.equals(i))existcheck=false;
                if(existcheck)System.err.println("Please enter a correct column name! ");
            }
            while(!existcheck){
                System.err.println("Enter new input: ");
                String input = scanner.nextLine().trim();
                try {
                    if(Register.updateUni(conn, email, bin, col, input))existcheck=true;
                } catch (SQLException e) {}
            }
            System.out.println("Type anything to update registration information on this email, or -1 for stopping this function");
			loop = scanner.nextLine().trim();

        }
        
    }

    public static List<String> genReport(Scanner scanner, Connection conn) throws SQLException, IOException, InterruptedException {
        clearScreen();
        System.out.println("Generate report for");
        System.out.println("1. Registration status");
        System.out.println("2. Most popular meals");
        System.out.println("3. Attendance behavior");
        System.out.println("4. Most expensive banquets");
        System.out.println("--");
        System.out.print("Enter your choice: ");
        int choice = 0;
        choice = scanner.nextInt();
        scanner.nextLine();
        switch (choice) {
            case 1:
                clearScreen();
                System.out.println("1. Registration status");
                List<String> ybq = Banquet.getAvailable(conn);
                if (ybq.isEmpty()) {
                    System.out.println("No available banquets found.");
                } else {
                    System.out.println("Available Banquets:\n--");
                    System.out.println("BIN\tREGISTERED\tBANQUET NAME\n");
                    for (String banquet : ybq) {
                        System.out.println(banquet);
                    }
                    return ybq;
                }
            case 2:
                clearScreen();
                System.out.println("2. Most popular meals:\n--");
                System.out.println("COUNT\tDISH NAME\n");
                List<String> popMeals = Meal.getPopMeal(conn);
                return popMeals;

            case 3:
                clearScreen();
                System.out.println("3. Attendance behavior:\n--");
                List<String> attB = Register.getAttBehav(conn);
                if (attB.isEmpty()) {
                    System.out.println("No available banquets found.");
                } else {
                    System.out.println("BIN\tATTENDEE COUNT\n");
                    for (String record : attB) {
                        System.out.println(record);
                    }
                    return attB;
                }
            case 4:
                clearScreen();
                System.out.println("4. Most expensive banquets:\n--");
                System.out.println("BIN\tSUM OF PRICE\tBANQUET NAME\n");
                List<String> rich = Meal.getExpensive(conn);
                return rich;
                //
            default:
                System.out.println("Invalid choice. Please try again.");
                break;
        }
        return null;
    }

    // clearScreen function -- clear screen
    static void clearScreen() throws IOException, InterruptedException
    {
        if (System.getProperty("os.name").contains("Windows"))
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        else
            System.out.print("\033[H\033[2J");
    }
}