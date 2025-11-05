package model;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
public class Meal {
    private int bin;
    private String type;
    private String dishName;
    private double price;
    private String specialCuisine;

    public Meal(int mealID, int bin, String type, String dishName, double price, String specialCuisine) {
        this.bin = bin;
        this.type = type;
        this.dishName = dishName;
        this.price = price;
        this.specialCuisine = specialCuisine;
    }

    public int getBin() {
        return bin;
    }
    public void setBin(int bin) {
        this.bin = bin;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getSpecialCuisine() {
        return specialCuisine;
    }

    public void setSpecialCuisine(String specialCuisine) {
        this.specialCuisine = specialCuisine;
    }

    public static List<String> getPopMeal(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT M.DISHNAME, COUNT(R.MEALCHOICE) AS AMT FROM MEAL M, REGISTRATION R WHERE M.DISHNAME = R.MEALCHOICE GROUP BY M.DISHNAME ORDER BY AMT DESC");
        List<String> popMeals = new ArrayList<>();

        while (rs.next()) {
            String dishName = rs.getString("DISHNAME");
            int count = rs.getInt("AMT");
            popMeals.add(count+"\t" + dishName);
        }
        if (popMeals.isEmpty()) {
            System.out.println("No meals found in registrations.");
        } else {
            for (String dish : popMeals) {
                System.out.println(dish);
            }
        }
        return popMeals;
    }

    //SELECT M.BIN, SUM(PRICE), BANQUETNAME FROM MEAL M, BANQUET B GROUP BY M.BIN ORDER BY PRICE DESC

    public static List<String> getExpensive(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT M.BIN, SUM(M.PRICE) as SUMP, B.BANQUETNAME FROM MEAL M, BANQUET B WHERE B.BIN = M.BIN GROUP BY M.BIN, B.BANQUETNAME ORDER BY SUMP DESC");
        List<String> rich = new ArrayList<>();

        while (rs.next()) {
            String bin = rs.getString("BIN");
            int sump = rs.getInt("SUMP");
            String banquetName = rs.getString("BANQUETNAME");
            rich.add(bin+"\t" + sump + "\t\t" + banquetName);
        }
        if (rich.isEmpty()) {
            System.out.println("No banquets found in registrations.");
        } else {
            for (String i : rich) {
                System.out.println(i);
            }
        }
        return rich;
    }

    public static boolean listwithbin(Connection conn, int bin) throws SQLException{
        try{
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM MEAL WHERE BIN = ?");
            stmt.setInt(1, bin);
            ResultSet rset = stmt.executeQuery();

            while (rset.next()) {
                String dishname = rset.getString(2);
                int price = rset.getInt(3);
                String type = rset.getString(4);
                String SPECIALCUISINE = rset.getString(5);

                System.out.printf(
                        "DishName: %s | Price: %d | Type: %s | SpecialCuisine: %s%n",
                        dishname, price, type, SPECIALCUISINE
                    );
            }
            rset.wasNull();
            return true;
        }
        catch(SQLException e){
            System.err.println("Input is wrong. Please enter a correct one.");
            return false;
        }
    }
    public static boolean checkexistUni(Connection conn, int bin, String col, String input) throws SQLException {
        try{
            PreparedStatement a = conn.prepareStatement("SELECT "+col+ " FROM MEAL WHERE "+col+" = ? AND BIN = "+bin);
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
}