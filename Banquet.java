package model;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Banquet {
    private int bin;
    private String name;
    private LocalDateTime dateTime;
    private String address;
    private String location;
    private String contactFN;
    private String contactLN;
    private char available;
    private int quota;


    public Banquet(int bin, String name, LocalDateTime dateTime, String address, String location,
                   String contactFN, String contactLN, char available, int quota) {
        this.bin = bin;
        this.name = name;
        this.dateTime = dateTime;
        this.address = address;
        this.location = location;
        this.contactFN = contactFN;
        this.contactLN = contactLN;
        this.available = available;
        this.quota = quota;
    }

    public int getBin() {
        return bin;
    }

    public void setBin(int bin) {
        this.bin = bin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getContactFN() {
        return contactFN;
    }

    public void setContactFN(String contactFN) {
        this.contactFN = contactFN;
    }

    public String getContactLN() {
        return contactLN;
    }

    public void setContactLN(String contactLN) {
        this.contactLN = contactLN;
    }

    public char getAvailable() {
        return available;
    }

    public void setAvailable(char available) {
        this.available = available;
    }

    public int getQuota() {
        return quota;
    }

    public void setQuota(int quota) {
        this.quota = quota;
    }

    public void create(Connection conn) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO BANQUET (BIN, BANQUETNAME, DATETIME, ADDRESS, LOCATION, CONTACTFN, CONTACTLN, AVAILABLE, QUOTA, MAXQUOTA) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

        stmt.setInt(1, bin);
        stmt.setString(2, this.name);
        stmt.setTimestamp(3, Timestamp.valueOf(this.dateTime));
        stmt.setString(4, this.address);
        stmt.setString(5, this.location);
        stmt.setString(6, this.contactFN);
        stmt.setString(7, this.contactLN);
        stmt.setString(8, String.valueOf(this.available));
        stmt.setInt(9, this.quota);
        stmt.setInt(10, this.quota);

        stmt.executeUpdate();
        System.out.println("New banquet created");
    }

    public static void listAll(Connection conn) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT BIN, BANQUETNAME, DATETIME, ADDRESS, LOCATION, CONTACTFN, CONTACTLN, AVAILABLE, QUOTA, MAXQUOTA FROM BANQUET ORDER BY BIN ");
        ResultSet rset = stmt.executeQuery();

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
            int mquota = rset.getInt(10);


            System.out.printf(
                    "BIN: %d | Name: %s | DateTime: %s | Address: %s | Location: %s | Contact: %s %s | Available: %s | Quota: %d | MaxQuota: %d%n",
                    bin, name, dateTime.toLocalDateTime(), address, location, contactFN, contactLN, available, quota, mquota
            );
        }

    }
    
    public static List<String> getAvailable(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT B.BIN, B.BANQUETNAME, (B.MAXQUOTA-B.QUOTA) AS NQ, B.MAXQUOTA as MQ " +
                   "FROM BANQUET B, REGISTRATION R " +
                   "WHERE B.BIN = R.BIN AND B.AVAILABLE = 'Y' " +
                   "GROUP BY B.BIN, B.BANQUETNAME, B.MAXQUOTA, B.QUOTA " +
                   "ORDER BY B.BIN");
        List<String> ybq = new ArrayList<>();

        if (!rs.isBeforeFirst()) { //check if rs is empty
            System.out.println("No available banquets found.");
            return null;
        }
        while (rs.next()) {
            int bin = rs.getInt("BIN");
            String banquetName = rs.getString("BANQUETNAME");
            //int bincnt = rs.getInt("BINCNT");
            int nq = rs.getInt("NQ");
            int mquota = rs.getInt("MQ");
            //ybq.add("BIN: " + bin + ", Banquet Name: " + banquetName + "\t"+bincnt + "/" + bquota);
            ybq.add(bin +"\t" + nq + "/" + mquota + "\t\t" +banquetName);
        }
        return ybq;
    }

    public static int getnum(Connection conn, String col, int reqbin) throws SQLException{
        PreparedStatement stmt = conn.prepareStatement("SELECT "+col+" FROM BANQUET WHERE BIN = "+reqbin);
        ResultSet rset = stmt.executeQuery();
        int quota=0;
        while (rset.next()) {
            quota = rset.getInt(1);
        }
        return quota;
    }
}