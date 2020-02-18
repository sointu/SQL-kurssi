package com.company;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Data {

    public static final String CONN_STRING = "jdbc:sqlite:D:\\KATJA\\KOODAUS_2019\\SQL\\HARJOITUSTYO\\test.db";

    // kaikki transaktion sisään, yhtäaikainen käyttö?
    // tietokannan avaus vain kerran?
    // ainakin sulkemiset lisättävä?

    public void createDatabase() {
        try {
            Connection conn = DriverManager.getConnection(CONN_STRING);
            Statement statement = conn.createStatement();
            statement.execute("PRAGMA foreign_keys = ON;");
            statement.execute("CREATE TABLE IF NOT EXISTS Paikat(\n" +
                    "  id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "  nimi TEXT UNIQUE NOT NULL\n" +
                    ")");
            statement.execute("CREATE TABLE IF NOT EXISTS Asiakkaat (\n" +
                    "  id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "  nimi TEXT UNIQUE NOT NULL\n" +
                    ")");
            statement.execute("CREATE TABLE IF NOT EXISTS Paketit (\n" +
                    "  koodi INTEGER PRIMARY KEY,\n" +
                    "  asiakas_nimi TEXT REFERENCES Asiakkaat(nimi) \n" +
                    ")");
            statement.execute("CREATE TABLE IF NOT EXISTS Tapahtumat (\n" +
                    "  id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "  kuvaus TEXT,\n" +
                    "  aika DATETIME,\n" +
                    "  paikka_nimi INTEGER REFERENCES Paikat(nimi), \n" +
                    "  paketti_koodi INTEGER REFERENCES Paketit(koodi) \n" +
                    "  )");
            statement.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println("Tietokantaa ei voitu luoda." + e.getMessage());
        }
    }

    public void insertPlace(String place) {
        try {
            Connection conn = DriverManager.getConnection(CONN_STRING);
            PreparedStatement p = conn.prepareStatement("INSERT INTO Paikat(nimi) VALUES(?)");
            p.setString(1, place);
            p.executeUpdate();
            System.out.println("Paikka \"" + place + "\" lisätty.");
        } catch (SQLException e) {
            System.out.println("Paikkaa ei voitu lisätä. " + e.getMessage());
        }
    }

    public void insertCustomer(String customer) {
        try {
            Connection conn = DriverManager.getConnection(CONN_STRING);
            PreparedStatement p = conn.prepareStatement("INSERT INTO Asiakkaat(nimi) VALUES(?)");
            p.setString(1, customer);
            p.executeUpdate();
            System.out.println("Asiakas \"" + customer + "\" lisätty.");
        } catch (SQLException e) {
            System.out.println("Asiakasta ei voitu lisätä. " + e.getMessage());
        }
    }

    public void insertParcel(int trackingCode, String customerName) {
        try {
            Connection conn = DriverManager.getConnection(CONN_STRING);
            // asiakkaan olemassaolon tarkistus edellyttää vierasavaimien valvontaa
            Statement s = conn.createStatement();
            s.execute("PRAGMA foreign_keys = ON");
            PreparedStatement p = conn.prepareStatement("INSERT INTO Paketit(koodi, asiakas_nimi) VALUES(?, ?)");
            p.setInt(1, trackingCode);
            p.setString(2, customerName);
            p.executeUpdate();
            System.out.println("Asiakkaan " + customerName + " paketti lisätty seurantakoodilla " + trackingCode+".");
        } catch (SQLException e) {
            System.out.println("Pakettia ei voitu lisätä. " + e.getMessage());
        }
    }

    public void insertEvent(int trackingCode, String placeName, String dateTime, String description) {
        try{
            Connection conn = DriverManager.getConnection((CONN_STRING));
            Statement s = conn.createStatement();
            s.execute("PRAGMA foreign_keys = ON");
            PreparedStatement p = conn.prepareStatement("INSERT INTO Tapahtumat(paketti_koodi, paikka_nimi, aika, kuvaus) VALUES (?, ?, ?, ?)");
            p.setInt(1, trackingCode);
            p.setString(2, placeName);
            p.setString(3, dateTime);
            p.setString(4, description);
            p.executeUpdate();
            System.out.println("Tapahtuma lisätty tietokantaan. Seurantakoodi: "+trackingCode+", paikka: "+placeName+", aika: "+dateTime+", kuvaus: "+description+".");
        }catch(SQLException e){
            System.out.println("Tapahtumaa ei voitu lisätä. "+e.getMessage());
        }
    }

    public List<Event> queryEvents(int trackingCode){
        try {
            Connection conn = DriverManager.getConnection((CONN_STRING));
            Statement s = conn.createStatement();  // ei tarvita?
            PreparedStatement p = conn.prepareStatement("SELECT * FROM Tapahtumat WHERE paketti_koodi == ?");
            p.setInt(1, trackingCode);

            ResultSet results = p.executeQuery();

            List<Event> events = new ArrayList<>();

            while(results.next()){
                Event event = new Event();
                event.setId(results.getInt("id"));
                event.setAika(results.getString("aika"));
                event.setKuvaus(results.getString("kuvaus"));
                event.setPaikka_nimi(results.getString("paikka_nimi"));
                event.setPaketti_koodi(results.getInt("paketti_koodi"));
                events.add(event);
            }
            return events;
        } catch (SQLException e) {
            System.out.println("jotain"+e.getMessage());
            return null;
        }
        }

         public List<CustomerParcel> queryCustomerParcels(String customerName){
             try{
                 Connection conn = DriverManager.getConnection((CONN_STRING));
                 Statement s = conn.createStatement(); // ei tarvita?
                 PreparedStatement p = conn.prepareStatement("SELECT P.koodi, COUNT(T.id)\n" +
                         "                    FROM Paketit P, Tapahtumat T\n" +
                         "                    WHERE P.asiakas_nimi = ? AND P.koodi = T.paketti_koodi\n" +
                         "                    GROUP BY P.koodi");
                 p.setString(1, customerName);

                 ResultSet results = p.executeQuery();
                 List<CustomerParcel> customerParcels = new ArrayList<>();

                 while(results.next()){
                     CustomerParcel customerParcel = new CustomerParcel();
                     customerParcel.setTrackingCode(results.getInt("koodi"));
                     customerParcel.setEventCount(results.getInt("COUNT(T.id)"));
                     customerParcels.add(customerParcel);
                 }
                 return customerParcels;
             }catch(SQLException e){
                 System.out.println("Ei voitu löytää paketteja/tapahtumia. " + e.getMessage());
                 return null;
             }
         }
         public int getEvents(String place, String day){
            try{
                Connection conn = DriverManager.getConnection(CONN_STRING);
                PreparedStatement p = conn.prepareStatement("SELECT COUNT(T.id)\n" +
                        "FROM Tapahtumat T\n" +
                        "WHERE paikka_nimi = ? AND strftime('%Y-%m-%d',aika)= ?");
                p.setString(1, place);
                p.setString(2, day);
                ResultSet result = p.executeQuery();
                int count = result.getInt("COUNT(T.id)");

                return count;
            }catch(SQLException e){
                System.out.println("Tapahtumia ei voitu hakea. "+e.getMessage());
                return -1;
            }
         }
    }




