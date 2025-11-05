import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import oracle.jdbc.driver.*;

import java.util.Scanner;

import model.*;

public class AttendeeMenu {
    private static String loggedInEmail = null;

    public static void main(String[] args) throws SQLException, IOException, InterruptedException {
        Scanner scanner = new Scanner(System.in);

        DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
        OracleConnection conn =
                (OracleConnection) DriverManager.getConnection(
                        "jdbc:oracle:thin:@studora.comp.polyu.edu.hk:1521:dbms", "\"23087882d\"@dbms", "dpmcixni");

        int choice = 0;
        clearScreen();
        while (choice != 5) {
            System.out.println("\nAttendee Registration System\n");
            System.out.println("1. Create Account");
            System.out.println("2. Login");
            if (loggedInEmail != null) {
                System.out.println("3. Update Profile");
                System.out.println("4. Register for Banquet");
            }
            //System.out.println("6. List all attendees(for testing)");
            if (loggedInEmail != null){
                //System.out.println("7. List all registration(for testing)");
                System.out.println("5. Search with criteria");
                System.out.println("6. List all Available Banquets");
                System.out.println("7. Log out");
                System.out.println("8. Exit");
            }

            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1:
                    if(loggedInEmail == null) {
                        createAccount(scanner, conn);
                    }
                    else {
                        System.out.println("You are logged in");
                    }
                    break;
                case 2:
                    if(loggedInEmail == null) {
                        login(scanner, conn);
                    }
                    else {
                        System.out.println("You are logged in");
                    }
                    break;
                case 3:
                    if(loggedInEmail != null) {
                        updateProfile(scanner, conn);
                    }
                    else {
                        System.out.println("Please login first");
                    }
                    break;
                case 4:
                    if(loggedInEmail != null) {
                        clearScreen();
                        registerForBanquet(scanner, conn);
                    }
                    else {
                        System.out.println("Please login first");
                    }
                    break;
                case 8:
                    System.out.println("Exiting the system. Goodbye!");
                    break;

                /*case 6:
                    Attendee.listAll(conn);
                    break;
                case 7:
                    clearScreen();
                    Register.listAll(conn);
                    break;*/
                case 5:
                    if(loggedInEmail != null) {
                        clearScreen();
                        search(scanner, conn);
                    }
                    else {
                        System.out.println("Please login first");
                    }
                    break;
                case 6:
                    if(loggedInEmail != null) {
                        clearScreen();
                        System.out.println("Available Banquets:");
                        Statement stmt = conn.createStatement();
                        ResultSet rset = stmt.executeQuery("SELECT BIN, BANQUETNAME, DATETIME, LOCATION, QUOTA, MAXQUOTA FROM BANQUET WHERE AVAILABLE = 'Y' AND QUOTA > 0");

                        while (rset.next()) {
                            int bin = rset.getInt("BIN");
                            String banquetName = rset.getString("BANQUETNAME");
                            Timestamp datetime = rset.getTimestamp("DATETIME");
                            String location = rset.getString("LOCATION");
                            int quota = rset.getInt("QUOTA");
                            int mquota = rset.getInt("MAXQUOTA");

                            System.out.printf("BIN: %d, Name: %s, Date: %s, Location: %s, Quota: %d, MaxQuota: %d\n",
                                    bin, banquetName, datetime.toString(), location, quota, mquota);
                        }
                    }
                    else {
                        System.out.println("Please login first");
                    }
                    break;
                case 7:
                    clearScreen();
                    loggedInEmail = null;
                    System.out.println("Logged out. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }

        scanner.close();
    }

    private static void createAccount(Scanner scanner, Connection conn) throws SQLException, IOException, InterruptedException {
        clearScreen();
        System.out.println("\nCreate Account\n");
        boolean check = true;
        String firstName ="";
        while(check){
            System.out.print("Enter First Name: ");
            firstName = scanner.nextLine().trim();
            check = false;
            if (!firstName.matches("[a-zA-Z]+")) {
                System.out.println("Invalid First Name. Only English characters are accepted.");
                check = true;
            }
        }

        String lastName ="";
        while(!check){
            System.out.print("Enter Last Name: ");
            lastName = scanner.nextLine().trim();
            check = true;
            if (!lastName.matches("[a-zA-Z]+")) {
                System.out.println("Invalid Last Name. Only English characters are accepted.");
                check = false;
            }
        }
        
        String address = "";
        boolean validAddr =false;
        while (!validAddr) {
            System.out.print("Enter Address: ");
            address = scanner.nextLine().trim();
            if (address.isEmpty()) {System.out.println("Address cannot be empty. Please enter a valid address.");
            } else {validAddr = true;
            }
        }

        String attendeeType ="";
        String[] type = {"staff", "student", "alumni", "guest"}; 
        while(check){
            System.out.print("Enter Attendee Type (staff, student, alumni, guest): ");
            attendeeType = scanner.nextLine().trim().toLowerCase();
            for (String i : type)if(i.equals(attendeeType)){check = false;break;}
        }check = true;

        String email ="";
        while (check){
            System.out.print("Enter Email Address: ");
            email = scanner.nextLine().trim();
            check = false;
            try{
                PreparedStatement a = conn.prepareStatement("SELECT EMAIL FROM ATTENDEE WHERE EMAIL = ?");
                a.setString(1, email);
                ResultSet rset = a.executeQuery();
                while (rset.next())System.err.println(rset.getString(1)+" was used by other people\n");
                rset.wasNull();
                check = true;
            }
            catch(SQLException e){}

            if (!email.contains("@")) {
                System.out.println("Invalid Email. It must include '@'.");
                check = true;
            }
        }

        String password = "";
        boolean validPw =false;
        while (!validPw) {
            System.out.print("Enter Password: ");
            password = scanner.nextLine().trim();
            if (password.isEmpty()) {System.out.println("Password cannot be empty. Please enter a valid password.");
            } else {validPw = true;
            }
        }

        String phoneNumber ="";
        while(!check){
            System.out.print("Enter Phone Number: ");
            phoneNumber = scanner.nextLine().trim();
            check = true;
            if (!phoneNumber.matches("\\d{8}")) {
                System.out.println("Invalid Mobile Number. It must be an 8-digit number.");
                check = false;
            }
        }

        String organization ="";
        String[] org = {"PolyU", "SPEED", "HKCC", "Others"};
        while(check){
            System.out.print("Enter Organization: (PolyU, SPEED, HKCC, Others)");
            organization = scanner.nextLine();
            for (String i : org)if(i.equals(organization)){check = false;break;}
        }
        Attendee attendee = new Attendee(email, firstName, lastName, address, attendeeType, password, phoneNumber, organization);
        attendee.create(conn);
    }

    private static void login(Scanner scanner, Connection conn) throws SQLException, IOException, InterruptedException  {
        clearScreen();
        System.out.println("Login\n");

        boolean check = true;
        while (check){
            System.out.print("Enter Email: ");
            String email = scanner.nextLine().trim();
            System.out.print("Enter Password: ");
            String password = scanner.nextLine().trim();

            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM ATTENDEE WHERE EMAIL = ? AND PASSWORD = ?");
            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rset = stmt.executeQuery();

            if (rset.next()) {
                loggedInEmail = email;
                check = false;
                System.out.println("\nLogin successful! Welcome, " + rset.getString("FNAME") + " " + rset.getString("LNAME") + ".");
            } else {
                System.out.println("\nInvalid email or password.");
            }
        }
    }

    private static void updateProfile(Scanner scanner, Connection conn) throws SQLException, IOException, InterruptedException {
        clearScreen();
        String reqEmail = loggedInEmail; //should have this once logged in
        System.out.println("Update Account details\n");
        boolean existcheck = true;

        String loop = "";
        String[] collist = {"EMAIL", "FNAME", "LNAME", "ADDRESS", "ATTENDEETYPE", "PASSWORD", "PHONENO", "ORGANIZATION"};
        String col="";
        while(!loop.equals("-1")){
            Attendee.listusingEmail(conn, reqEmail);
            while(existcheck){
                System.err.println("Enter column name: ");
                col = scanner.nextLine().toUpperCase().trim();
                for (String i : collist)if(col.equals(i))existcheck=false;
                if(existcheck)System.err.println("Please enter a correct column name! ");
            }
            while(!existcheck){
                System.err.println("Enter new input: ");
                String input = scanner.nextLine().trim();
                if (col.equals("PHONENO")&&!input.matches("\\d{8}")){
                    System.out.println("The input is wrong. Please enter a correct one\n");
                    continue;
                }
                try {
                    if(Attendee.update(conn, reqEmail, col, input))existcheck=true;
                } catch (SQLException e) {

                }
            }
            System.err.println("Updated version: ");
            Attendee.listusingEmail(conn, reqEmail);
            System.err.println("");
            System.out.println("Type anything to update other column on this, or -1 for stopping this function");
            loop = scanner.nextLine().trim();
        }
    }

    private static void registerForBanquet(Scanner scanner, Connection conn) throws SQLException {
        System.out.println("\nRegister for a Banquet\n");

        Statement stmt = conn.createStatement();
        ResultSet rset = stmt.executeQuery("SELECT BIN, BANQUETNAME, DATETIME, LOCATION, QUOTA, MAXQUOTA FROM BANQUET WHERE AVAILABLE = 'Y' AND QUOTA > 0");

        System.out.println("Available Banquets:");
        while (rset.next()) {
            int bin = rset.getInt("BIN");
            String banquetName = rset.getString("BANQUETNAME");
            Timestamp datetime = rset.getTimestamp("DATETIME");
            String location = rset.getString("LOCATION");
            int quota = rset.getInt("QUOTA");
            int mquota = rset.getInt("MAXQUOTA");

            System.out.printf("BIN: %d, Name: %s, Date: %s, Location: %s, Quota: %d, MaxQuota: %d\n",
                    bin, banquetName, datetime.toString(), location, quota, mquota);
        } 


        System.out.print("Enter the BIN of the banquet you want to register for: ");
        int bin = scanner.nextInt();
        scanner.nextLine();

        PreparedStatement stmtCheck = conn.prepareStatement("SELECT * FROM REGISTRATION WHERE BIN = ? AND EMAIL = ?");
        stmtCheck.setInt(1, bin);
        stmtCheck.setString(2, loggedInEmail);
        ResultSet rsetCheck = stmtCheck.executeQuery();
        if (rsetCheck.next()) {
            System.out.println("You have already registered this banquet.");
            return;
        }


        System.out.print("Enter your drink choice (e.g., Tea, Coffee, Lemon Tea): ");
        String drinkChoice = scanner.nextLine();

        System.out.println("Available meal options:");
        PreparedStatement mealStmt = conn.prepareStatement("SELECT DISHNAME FROM MEAL WHERE BIN = ?");
        mealStmt.setInt(1, bin);
        ResultSet rsMeal = mealStmt.executeQuery();
        List<String> mealOptions = new ArrayList<>();
        while (rsMeal.next()) {
            String dishName = rsMeal.getString("DISHNAME");
            mealOptions.add(dishName);
            System.out.println("- " + dishName);
        }

        Boolean check = true;
        String mealChoice = "";
        while (check) {
            System.out.print("Enter your meal choice\n(or press [ENTER] for the first available option): ");
            mealChoice = scanner.nextLine().trim();

            if (mealChoice.equalsIgnoreCase("")) {
                if (!mealOptions.isEmpty()) { 
                    mealChoice = mealOptions.get(0);
                    System.out.println("You have chosen: " + mealChoice);   check = false;
                } else {
                    System.out.println("No available meal options to choose from.");
                }
            } else if (Meal.checkexistUni(conn, bin, "DISHNAME", mealChoice)) { check = false;
            } else {System.out.println("Invalid meal choice. Please try again.");
            }
        }

        System.out.print("Enter any remarks (e.g., seating preference, allergies): ");
        String remarks = scanner.nextLine();

        PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO REGISTRATION (BIN, EMAIL, REMARKS, DRINKCHOICE, MEALCHOICE, SEATNUM) VALUES (?, ?, ?, ?, ?, ?)");
        insertStmt.setInt(1, bin);
        insertStmt.setString(2, loggedInEmail);
        insertStmt.setString(3, remarks);
        insertStmt.setString(4, drinkChoice);
        insertStmt.setString(5, mealChoice);
        int seatnumber=Banquet.getnum(conn, "MAXQUOTA", bin);
        seatnumber -= Banquet.getnum(conn, "QUOTA", bin) -1;
        
        insertStmt.setInt(6, seatnumber);

        PreparedStatement updateQuotaStmt = conn.prepareStatement("UPDATE BANQUET SET QUOTA = QUOTA - 1 WHERE BIN = ?");
        updateQuotaStmt.setInt(1, bin);
        updateQuotaStmt.executeUpdate();
        insertStmt.executeQuery();
        if(Banquet.getnum(conn, "QUOTA", bin)==0){
            PreparedStatement updateAvailableStmt = conn.prepareStatement("UPDATE BANQUET SET AVAILABLE = 'N' WHERE BIN = "+bin);
            updateAvailableStmt.executeUpdate();
        }

        System.out.println("You have successfully registered for the banquet!");
    }

    public static void search(Scanner scanner, Connection conn) throws SQLException, IOException, InterruptedException  {
        clearScreen();
        System.err.println("Search registered banquets with criteria\n");
        System.err.println("Criteria: bin, banquetname, datetime, address, location, contactfn, contactln\n");
        ArrayList<String> usedCri = new ArrayList<String>();
        String[] cri = {"bin", "banquetname", "datetime", "address", "location", "contactfn", "contactln"};
        String loop = "";
        String col= "";
        String input = "";
        boolean check = true;
        String sql1 = "SELECT  B.BIN, B.BANQUETNAME, B.DATETIME, B.ADDRESS, B.LOCATION, B.CONTACTFN, B.CONTACTLN, R.MEALCHOICE, R.DRINKCHOICE, R.REMARKS FROM (SELECT BIN, BANQUETNAME, DATETIME, ADDRESS, LOCATION, CONTACTFN, CONTACTLN FROM BANQUET WHERE ";
        String sql2 = ") B INNER JOIN (SELECT BIN, MEALCHOICE, DRINKCHOICE, REMARKS FROM REGISTRATION WHERE EMAIL = '"+loggedInEmail+"') R " + 
                        "ON B.BIN = R.BIN ORDER BY B.BIN ";
        while (!loop.equals("done")){
            if(usedCri.size()==cri.length){
                System.err.println("All criteria are used!");
                break;
            }
            while(check){
                System.err.println("Enter the criteria name: ");
                col = scanner.nextLine().trim().toLowerCase();
                for (String i : cri)if (i.equals(col)){check=false;break;}
                if(!check)break;
                System.err.println("Please enter the correct criteria name! ");
            }
            
            while(!check){
                if (col.equals("datetime")){
                    System.err.println("Enter the input: (day-month-year) such as 13-NOV-00 -> 13th November 2000\n");
                    input = scanner.nextLine().trim().toUpperCase();
                }
                else {
                    System.err.println("Enter the input: ");
                    input = scanner.nextLine().trim();
                }    
                PreparedStatement a = conn.prepareStatement("SELECT "+col+" FROM BANQUET WHERE "+col+" LIKE ?");
                
                if(col.equals("bin"))a.setString(1, input);
                else a.setString(1, "%"+input+"%");
                //show sql of testing attribute
                //System.err.println(("select "+col+" from banquet where "+col+" like "+"'%"+input.toUpperCase()+"%'").toUpperCase());

                try{
                    a.executeQuery();
                }catch(SQLException e){
                    System.err.println("Please enter a correct format of input.");
                    continue;
                }
                if (usedCri.size()<1){
                    if(col.equals("BIN"))sql1 += col.toUpperCase() + " LIKE '"+ input+"'";
                    else sql1 += col.toUpperCase() + " LIKE '%"+ input+"%'";
                    usedCri.add(col);
                }
                else{
                    if(col.equals("BIN"))sql1 += " AND "+col.toUpperCase() + " LIKE '"+input+"'";
                    else sql1 += " AND "+col.toUpperCase() + " LIKE '%"+input+"%'";
                    usedCri.add(col);
                }
                check = true;
            }
            System.err.println("If you want to add another criteria, enter anything other than done");
            loop = scanner.nextLine().trim().toLowerCase();
        }
        //show final sql :)
        //System.err.println(sql1+sql2);
        ArrayList<Integer> searchedbin = new ArrayList<Integer>();
        PreparedStatement a = conn.prepareStatement(sql1+sql2);
        try{
            ResultSet rset = a.executeQuery();
            System.err.println("\nResult with criteria\n");
            while (rset.next()) {
                int bin = rset.getInt(1);
                searchedbin.add(bin);
                String name = rset.getString(2);
                Timestamp dateTime = rset.getTimestamp(3);
                String address = rset.getString(4);
                String location = rset.getString(5);
                String contactFN = rset.getString(6);
                String contactLN = rset.getString(7);
                String mealchoice = rset.getString(8);
                String drinkchoice = rset.getString(9);
                String remarks = rset.getString(10);

                System.out.printf(
                        "BIN: %d | Name: %s | DateTime: %s | Address: %s | Location: %s | Contact: %s %s | MealChoice: %s | DrinkChoice: %s | Remarks: %s%n",
                        bin, name, dateTime.toLocalDateTime(), address, location, contactFN, contactLN, mealchoice, drinkchoice, remarks
                );
            }
            rset.wasNull();
        }
        catch(SQLException e){
            System.err.println("\nEmpty outcome!\n");
        }
        System.err.println("\nIf you want to update registration information upon these results, enter \"update\" and anything else for exit");
        loop = scanner.nextLine().trim().toLowerCase();
        if(loop.equals("update")){
            if(searchedbin.isEmpty()){
                System.err.println("The searched result is empty.");
            }
            else{
                update(scanner, conn, searchedbin, loggedInEmail);
            }
        }
    }

    public static void update(Scanner scanner, Connection conn, ArrayList<Integer> list,String email) throws SQLException {
        boolean check = true;
        String col = "";
        String[] cancol = {"MEALCHOICE", "DRINKCHOICE", "REMARKS"};
        while (check){
            System.err.println("Enter column name: (MealChoice, DrinkChoice, Remarks)\n");
            col = scanner.nextLine().trim().toUpperCase();
            for (String i : cancol)if(col.equals(i)){check = false;break;}
        }
        String input = "";
        if (col.equals("MEALCHOICE")){
            for(int i : list){
                System.err.println("Meal names for reference: \n");
                Meal.listwithbin(conn, i);
                check = false;
                while (!check){
                    System.err.println("Enter an input:");
                    input = scanner.nextLine().trim();
                    if(Meal.checkexistUni(conn, i, "DISHNAME", input))check = true;
                }
                Register.updateUni(conn, email, String.valueOf(i), col, input);
            }
        }
        else{
            for(int i : list){
                System.err.println("Enter an input:");
                input = scanner.nextLine().trim();
                Register.updateUni(conn, email, String.valueOf(i), col, input);
            }
        }
    }
    
    public static void clearScreen() throws IOException, InterruptedException {
        if (System.getProperty("os.name").contains("Windows"))
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        else
            System.out.print("\033[H\033[2J");
    }
}