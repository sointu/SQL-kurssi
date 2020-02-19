package com.company;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Data {

    public static final String CONN_STRING = "jdbc:sqlite:D:\\KATJA\\KOODAUS_2019\\SQL\\HARJOITUSTYO\\test.db";

    private Connection conn;

    public boolean openDB() {
        try {
            conn = DriverManager.getConnection(CONN_STRING);
            return true;
        } catch (SQLException e) {
            System.out.println("Tietokantayhteyttä ei voitu avata " + e.getMessage());
            return false;
        }
    }

    public void closeDB() {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            System.out.println("Tietokantaa ei voitu sulkea: " + e.getMessage());
        }
    }

    public void createDatabase() {
        try {
            openDB();
            Statement s = conn.createStatement();
            s.execute("BEGIN TRANSACTION");

            // Paketit- ja Tapahtumat-tauluissa on vierasavaimia
            s.execute("PRAGMA foreign_keys = ON;");
            s.execute("CREATE TABLE IF NOT EXISTS Paikat(\n" +
                    "  id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "  nimi TEXT UNIQUE NOT NULL\n" +
                    ")");
            s.execute("CREATE TABLE IF NOT EXISTS Asiakkaat (\n" +
                    "  id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "  nimi TEXT UNIQUE NOT NULL\n" +
                    ")");
            s.execute("CREATE TABLE IF NOT EXISTS Paketit (\n" +
                    "  koodi INTEGER PRIMARY KEY,\n" +
                    "  asiakas_nimi TEXT REFERENCES Asiakkaat(nimi) \n" +
                    ")");
            s.execute("CREATE TABLE IF NOT EXISTS Tapahtumat (\n" +
                    "  id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "  kuvaus TEXT,\n" +
                    "  aika DATETIME,\n" +
                    "  paikka_nimi INTEGER REFERENCES Paikat(nimi), \n" +
                    "  paketti_koodi INTEGER REFERENCES Paketit(koodi) \n" +
                    "  )");
            s.execute("COMMIT");
            s.close();
        } catch (SQLException e) {
            System.out.println("Tietokantaa ei voitu luoda." + e.getMessage());
        }
    }

    public void insertPlace(String place) {
        try {
            openDB();
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
            openDB();
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
            openDB();
            Statement s = conn.createStatement();
            PreparedStatement p = conn.prepareStatement("INSERT INTO Paketit(koodi, asiakas_nimi) VALUES(?, ?)");
            p.setInt(1, trackingCode);
            p.setString(2, customerName);
            p.executeUpdate();
            System.out.println("Asiakkaan " + customerName + " paketti lisätty seurantakoodilla " + trackingCode + ".");
            s.close();
        } catch (SQLException e) {
            System.out.println("Pakettia ei voitu lisätä. " + e.getMessage());
        }
    }

    public void insertEvent(int trackingCode, String placeName, String dateTime, String description) {
        try {
            openDB();
            Statement s = conn.createStatement();
            PreparedStatement p = conn.prepareStatement("INSERT INTO Tapahtumat(paketti_koodi, paikka_nimi, aika, kuvaus) VALUES (?, ?, ?, ?)");
            p.setInt(1, trackingCode);
            p.setString(2, placeName);
            p.setString(3, dateTime);
            p.setString(4, description);
            p.executeUpdate();
            System.out.println("Tapahtuma lisätty tietokantaan. Seurantakoodi: " + trackingCode + ", paikka: " + placeName + ", aika: " + dateTime + ", kuvaus: " + description + ".");
            s.close();
        } catch (SQLException e) {
            System.out.println("Tapahtumaa ei voitu lisätä. " + e.getMessage());
        }
    }

    public List<Event> queryEvents(int trackingCode) {
        try {
            openDB();
            PreparedStatement p = conn.prepareStatement("SELECT * FROM Tapahtumat WHERE paketti_koodi == ?");
            p.setInt(1, trackingCode);

            ResultSet results = p.executeQuery();

            List<Event> events = new ArrayList<>();

            while (results.next()) {
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
            System.out.println("Tapahtumia ei löytynyt." + e.getMessage());
            return null;
        }
    }

    public List<CustomerParcel> queryCustomerParcels(String customerName) {
        try {
            openDB();
            PreparedStatement p = conn.prepareStatement("SELECT P.koodi, COUNT(T.id)\n" +
                    "                    FROM Paketit P, Tapahtumat T\n" +
                    "                    WHERE P.asiakas_nimi = ? AND P.koodi = T.paketti_koodi\n" +
                    "                    GROUP BY P.koodi");
            p.setString(1, customerName);

            ResultSet results = p.executeQuery();
            List<CustomerParcel> customerParcels = new ArrayList<>();

            while (results.next()) {
                CustomerParcel customerParcel = new CustomerParcel();
                customerParcel.setTrackingCode(results.getInt("koodi"));
                customerParcel.setEventCount(results.getInt("COUNT(T.id)"));
                customerParcels.add(customerParcel);
            }
            return customerParcels;
        } catch (SQLException e) {
            System.out.println("Ei voitu löytää paketteja/tapahtumia. " + e.getMessage());
            return null;
        }
    }

    public int getEvents(String place, String day) {
        try {
            openDB();
            PreparedStatement p = conn.prepareStatement("SELECT COUNT(T.id)\n" +
                    "FROM Tapahtumat T\n" +
                    "WHERE paikka_nimi = ? AND strftime('%Y-%m-%d',aika)= ?");
            p.setString(1, place);
            p.setString(2, day);
            ResultSet result = p.executeQuery();
            int count = result.getInt("COUNT(T.id)");

            return count;
        } catch (SQLException e) {
            System.out.println("Tapahtumia ei voitu hakea. " + e.getMessage());
            return -1;
        }
    }

    public void testDB() {
        try {
            openDB();
            Statement s = conn.createStatement();

            // indeksien luonti
            s.execute("CREATE INDEX idx_asiakasnimi ON Paketit (asiakas_nimi)");
            s.execute("CREATE INDEX idx_pakettikoodi ON Tapahtumat (paketti_koodi)");

            s.execute("BEGIN TRANSACTION");

            // 1. Paikkojen lisäys
            PreparedStatement p = conn.prepareStatement("INSERT INTO Paikat(nimi) VALUES(?)");
            // 2. Asiakkaiden lisäys
            PreparedStatement p2 = conn.prepareStatement("INSERT INTO Asiakkaat(nimi) VALUES(?)");
            // 3. Pakettien lisäys
            s.execute("PRAGMA foreign_keys = ON");
            PreparedStatement p3 = conn.prepareStatement("INSERT INTO Paketit(koodi, asiakas_nimi) VALUES(?, ?)");
            // 4. Tapahtumien lisäys
            PreparedStatement p4 = conn.prepareStatement("INSERT INTO Tapahtumat(paketti_koodi, paikka_nimi, aika, kuvaus) VALUES (?, ?, ?, ?)");
            // 5. Haetaan asiakkaan pakettien määrä
            PreparedStatement p5 = conn.prepareStatement("SELECT COUNT(*) FROM Paketit WHERE asiakas_nimi == ?");
            // 6. Haetaan paketin tapahtumien määrä
            PreparedStatement p6 = conn.prepareStatement("SELECT COUNT(T.id) FROM Tapahtumat T WHERE paketti_koodi = ?");

            // 1. Paikkojen lisäys
            long time_1a = System.nanoTime();
            for (int i = 1; i < 1001; i++) {
                p.setString(1, "P" + i);
                p.executeUpdate();
            }
            long time_1b = System.nanoTime();
            System.out.println("Paikkojen lisäys: aikaa kului " + (time_1b - time_1a) / 1e9 + " sekuntia");

            // 2. Asiakkaiden lisäys
            long time_2a = System.nanoTime();
            for (int i = 1; i < 1001; i++) {
                p2.setString(1, "A" + i);
                p2.executeUpdate();
            }
            long time_2b = System.nanoTime();
            System.out.println("Asiakkaiden lisäys: aikaa kului " + (time_2b - time_2a) / 1e9 + " sekuntia");

            // 3. Pakettien lisäys
            long time_3a = System.nanoTime();
            for (int i = 1; i < 1001; i++) {
                p3.setInt(1, i);
                p3.setString(2, "A" + i);
                p3.executeUpdate();
            }
            long time_3b = System.nanoTime();
            System.out.println("Pakettien lisäys: aikaa kului " + (time_3b - time_3a) / 1e9 + " sekuntia");

            // 4. Tapahtumien lisäys
            long time_4a = System.nanoTime();

            for (int j = 1; j < 1001; j++) {
                for (int i = 1; i < 1001; i++) {
                    p4.setInt(1, i);
                    p4.setString(2, "P" + i);
                    p4.setString(3, "2020-02-18 00:00:00");
                    p4.setString(4, "kuvaus");
                    p4.executeUpdate();
                }
            }
            long time_4b = System.nanoTime();
            System.out.println("Tapahtumisen lisäys: aikaa kului " + (time_4b - time_4a) / 1e9 + " sekuntia");

            // 5. Haetaan asiakkaan pakettien määrä
            long time_5a = System.nanoTime();

            for (int i = 1; i < 1001; i++) {
                p5.setString(1, "A" + i);
                p5.executeQuery();
            }

            long time_5b = System.nanoTime();
            System.out.println("Asiakkaan pakettien määrän haku: aikaa kului " + (time_5b - time_5a) / 1e9 + " sekuntia");

            // 5. Haetaan paketin tapahtumien määrä
            long time_6a = System.nanoTime();

            for (int i = 1; i < 1001; i++) {
                p6.setString(1, "P" + i);
                p6.executeQuery();
            }

            long time_6b = System.nanoTime();
            System.out.println("Paketin tapahtumien määrän haku: aikaa kului " + (time_6b - time_6a) / 1e9 + " sekuntia");

            s.execute("COMMIT");
            s.close();
            System.out.println("Testi valmis.");
        } catch (SQLException e) {
            System.out.println("Testi ei onnistunut. " + e.getMessage());
        }
    }
}




