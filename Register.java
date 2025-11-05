package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Register {
    private int bin;
    private String email;
    private String mealChoice;
    private String drinkChoice;
    private String remarks;

    public Register(int bin, String email, String mealChoice, String drinkChoice, String remarks) {
        this.bin = bin;
        this.email = email;
        this.mealChoice = mealChoice;
        this.drinkChoice = drinkChoice;
        this.remarks = remarks;
    }


    public int getBin() {
        return bin;
    }
    public void setBin(int bin) {
        this.bin = bin;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getMealChoice() {
        return mealChoice;
    }
    public void setMealChoice(String mealChoice) {
        this.mealChoice = mealChoice;
    }

    public String getDrinkChoice() {
        return drinkChoice;
    }
    public void setDrinkChoice(String drinkChoice) {
        this.drinkChoice = drinkChoice;
    }

    public String getRemarks() {
        return remarks;
    }
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
    public static boolean printUsingEmail(Connection conn, String reqemail)throws SQLException{
        try{
            PreparedStatement a = conn.prepareStatement("SELECT * FROM REGISTRATION WHERE EMAIL = ?");
            a.setString(1, reqemail);
            ResultSet rset = a.executeQuery();
            while (rset.next()) {
                int bin = rset.getInt(1);
                String email = rset.getString(2);
                String remarks = rset.getString(3);
                String drinkchoice = rset.getString(4);
                String mealchoice = rset.getString(5);

                System.out.printf(
                        "   Bin: %d | Email: %s | MealChoice: %s | DrinkChoice: %s | Remarks: %s%n",
                        bin, email, mealchoice, drinkchoice, remarks
                );
            }
            rset.wasNull();
            return true;
        }catch (SQLException e){
            System.err.println("This attendee did not register any banquet");
            return false;
        }
    }

    public static boolean checkexist(Connection conn, String col, String input) throws SQLException {
        try{
            PreparedStatement a = conn.prepareStatement("SELECT "+col+ " FROM REGISTRATION WHERE "+col+" = ?");
            if(col.equals("BIN"))a.setInt(1, (int)Integer.parseInt(input));
            else a.setString(1, input);
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

    public static boolean updateUni(Connection conn, String email, String bin,String col, String input)throws SQLException{
        try {
            PreparedStatement a = conn.prepareStatement("UPDATE REGISTRATION SET "+col+" = ? WHERE BIN = "+bin+" AND EMAIL = ?");
            a.setString(2, email);
            a.setString(1, input);
            a.executeUpdate();
            
            System.err.println("update successful");
            return true;
        } catch (SQLException e) {
            System.out.println("The input is wrong. Please enter a correct one\n");
            return false;
        }
    }

    public static int getRegCnt(Connection conn, int reqbin) throws SQLException {
        Statement a = conn.createStatement();
        ResultSet rs = a.executeQuery("SELECT COUNT(*) as REG_CNT FROM REGISTRATION WHERE BIN ="+reqbin);

        int cnt = rs.getInt("REG_CNT");
        return cnt;
    }

    public static List<String> getAttBehav(Connection conn) throws SQLException {
        Statement a = conn.createStatement();
        ResultSet rs = a.executeQuery("SELECT BIN,COUNT(EMAIL) as EMAILCNT FROM REGISTRATION GROUP BY BIN");

        List<String> attB = new ArrayList<>();

        if (!rs.isBeforeFirst()) { //check if rs is empty
            System.out.println("No available banquets found.");
            return null;
        }
        while (rs.next()) {
            int bin = rs.getInt("BIN");
            int attCnt  = rs.getInt("EMAILCNT");
            attB.add(bin + "\t" + attCnt);
        }
        return attB;
    }
    
    public static void listAll(Connection conn) throws SQLException {

        PreparedStatement stmt = conn.prepareStatement("SELECT BIN,  EMAIL, DRINKCHOICE, MEALCHOICE, REMARKS, REGISTRATION_TIME, SEATNUM FROM REGISTRATION");
        ResultSet rset = stmt.executeQuery();

        while (rset.next()) {
            int bin = rset.getInt(1);
            String email = rset.getString(2);
            String drinkChoice = rset.getString(3);
            String mealChoice = rset.getString(4);
            String remarks = rset.getString(5);
            Timestamp registrationTime = rset.getTimestamp(6);
            int seatNum = rset.getInt(7);

            System.out.printf(
                    "BIN: %d | Email: %s | Drink: %s | Meal: %s | Remarks: %s | registrationTime: %s | SeatNum: %d %n",
                    bin, email, drinkChoice, mealChoice, ((remarks == null || remarks.isEmpty()) ? "None" : remarks), registrationTime.toLocalDateTime(), seatNum
            );
        }
    }
}
