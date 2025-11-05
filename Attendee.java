package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Attendee {
    private String email;
    private String FName;
    private String LName;
    private String address;
    private String attendeeType;
    private String password;
    private String phoneNo;
    private String organization;


    public Attendee(String email, String fName, String LName, String address, String attendeeType, String password, String phoneNo, String organization) {
        this.email = email;
        this.FName = fName;
        this.LName = LName;
        this.address = address;
        this.attendeeType = attendeeType;
        this.password = password;
        this.phoneNo = phoneNo;
        this.organization = organization;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getFName() {
        return FName;
    }

    public void setFName(String fName) {
        this.FName = fName;
    }

    public String getLName() {
        return LName;
    }

    public void setLName(String lName) {
        this.LName = lName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAttendeeType() {
        return attendeeType;
    }

    public void setAttendeeType(String attendeeType) {
        this.attendeeType = attendeeType;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public void create(Connection conn) throws SQLException {
        System.out.printf("Debugging Insert: Email=%s, FName=%s, LName=%s, Address=%s, AttendeeType=%s, Password=%s, PhoneNo=%s, Organization=%s%n",
                this.email, this.FName, this.LName, this.address, this.attendeeType, this.password, this.phoneNo, this.organization);

        PreparedStatement stmt = conn.prepareStatement("INSERT INTO ATTENDEE (EMAIL, FNAME, LNAME, ADDRESS, ATTENDEETYPE, PASSWORD, PHONENO, ORGANIZATION) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
        stmt.setString(1, this.email);
        stmt.setString(2, this.FName);
        stmt.setString(3, this.LName);
        stmt.setString(4, this.address);
        stmt.setString(5, this.attendeeType);
        stmt.setString(6, this.password);
        stmt.setString(7, this.phoneNo);
        stmt.setString(8, this.organization);

        stmt.executeQuery();

        System.out.println("Account created");
    }

    public static void listAll(Connection conn) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT EMAIL, FNAME, LNAME, ADDRESS, ATTENDEETYPE, PASSWORD, PHONENO, ORGANIZATION FROM ATTENDEE ORDER BY FNAME ");
        ResultSet rset = stmt.executeQuery();

        while (rset.next()) {
            String email = rset.getString(1);
            String fname = rset.getString(2);
            String lname = rset.getString(3);
            String address = rset.getString(4);
            String attendeetype = rset.getString(5);
            String password = rset.getString(6);
            String phoneno = rset.getString(7);
            String organisation = rset.getString(8);

            System.out.printf(
                    "Email: %s | Name: %s %s | Address: %s | AttendeeType: %s | Password: %s | PhoneNumber: %s | Organization: %s %n",
                    email, fname, lname, address, attendeetype, password, phoneno, organisation
            );
        }

    }

    public static boolean checkexist(Connection conn, String col, String input) throws SQLException {
        try{
            PreparedStatement a = conn.prepareStatement("SELECT "+col+ " FROM ATTENDEE WHERE "+col+" = ?");
            a.setString(1, input);
            ResultSet rset = a.executeQuery();
            while (rset.next())System.err.println(rset.getString(1)+" was found succesfully\n");
            rset.wasNull();
            return true;
        }
        catch(SQLException e){
            System.err.println("The input is incorrect or empty output. Please enter a correct one\n");
            return false;
        }
    }

    public static void listusingEmail(Connection conn, String reqemail) throws SQLException{
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM ATTENDEE WHERE EMAIL = ?");
        stmt.setString(1, reqemail);
        ResultSet rset = stmt.executeQuery();

        while (rset.next()) {
            String email = rset.getString(1);
            String fname = rset.getString(2);
            String lname = rset.getString(3);
            String address = rset.getString(4);
            String attendeetype = rset.getString(5);
            String password = rset.getString(6);
            String phoneno = rset.getString(7);
            String organisation = rset.getString(8);

            System.out.printf(
                    "Email: %s | FName: %s | LName: %s | Address: %s | AttendeeType: %s | Password: %s | PhoneNo: %s | Organization: %s %n",
                    email, fname, lname, address, attendeetype, password, phoneno, organisation
            );
        }
    }

    public static boolean update(Connection conn, String email, String col, String input)throws SQLException{
        try {
            if(col.equals("EMAIL")){
                Statement b = conn.createStatement();
                b.executeUpdate("alter table registration disable constraint regemail".toUpperCase());
                PreparedStatement att = conn.prepareStatement("UPDATE ATTENDEE SET "+col+" = ? WHERE EMAIL = ?");
                PreparedStatement reg = conn.prepareStatement("UPDATE REGISTRATION SET "+col+" = ? WHERE EMAIL = ?");
                att.setString(2, email);
                att.setString(1, input);
                reg.setString(2, email);
                reg.setString(1, input);
                att.executeUpdate();
                reg.executeUpdate();
                b = conn.createStatement();
                b.executeUpdate("alter table registration enable constraint regemail".toUpperCase());

            }
            else{
                PreparedStatement a = conn.prepareStatement("UPDATE ATTENDEE SET "+col+" = ? WHERE EMAIL = ?");
                a.setString(2, email);
                a.setString(1, input);
                a.executeUpdate();
            } 
                System.err.println("update successful");
                return true;
            
        } catch (SQLException e) {
            System.out.println("The input is wrong. Please enter a correct one\n");
            return false;
        }
    }
}