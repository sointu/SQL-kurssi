package com.company;

import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Data data = new Data();
        if(!data.openDB()){
            System.out.println("Ei voida avata tietokantayhteyttä.");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        System.out.println("1. Luo tietokanta.");
        System.out.println("2. Lisää uusi paikka antamalla paikan nimi.");
        System.out.println("3. Lisää uusi asiakas tietokantaan antamalla asiakkaan nimi.");
        System.out.println("4. Lisää uusi paketti tietokantaan. Anna seurantakoodi ja asiakkaan nimi.");
        System.out.println("5. Lisää uusi tapahtuma tietokantaan. Anna seurantakoodi, paikka ja kuvaus.");
        System.out.println("6. Hae kaikki paketin tapahtumat. Anna seurantakoodi.");
        System.out.println("7. Hae kaikki asiakkaan paketit ja niihin liittyvien tapahtumien määrä. Anna asiakkaan nimi.");
        System.out.println("8. Hae annetusta paikasta tapahtumien määrä tiettynä päivänä. Anna paikka ja päivä.");
        System.out.println("9. Suorita tietokannan tehokkuustesti.");
        System.out.println("0. Lopeta ohjelma.");
        System.out.println("Valitse toiminto (1-9 tai 0):");

        boolean quit = false;
        int switchValue;
        do {
            switchValue = scanner.nextInt();
            switch (switchValue) {
                case 1:
                    data.createDatabase();
                    System.out.println("Tietokanta luotu.");
                    break;
                case 2:
                    Scanner scanner2 = new Scanner(System.in);
                    System.out.println("Anna paikan nimi: ");
                    String place = scanner2.nextLine();
                    data.insertPlace(place);
                    break;
                case 3:
                    Scanner scanner3 = new Scanner(System.in);
                    System.out.println("Anna asiakkaan nimi: ");
                    String customer = scanner3.nextLine();
                    data.insertCustomer(customer);
                    break;
                case 4:
                    Scanner scanner4 = new Scanner(System.in);
                    System.out.println("Lisää uusi paketti. Anna paketin seurantakoodi.");
                    int trackingCode = scanner4.nextInt();
                    System.out.println("Anna asiakkaan nimi.");
                    scanner4.nextLine();
                    String customerName = scanner4.nextLine();
                    data.insertParcel(trackingCode, customerName);
                    break;
                case 5:
                    Scanner scanner5 = new Scanner(System.in);
                    System.out.println("Lisää uusi tapahtuma. Anna paketin seurantakoodi.");
                    int trackingCode2 = scanner5.nextInt();
                    System.out.println("Anna paikka.");
                    scanner5.nextLine();
                    String placeName = scanner5.nextLine();
                    System.out.println("Anna aika muodossa yyyy-mm-dd hh:mm:ss.");
                    String dateTime= scanner5.nextLine();
                    System.out.println("Anna kuvaus.");
                    String description = scanner5.nextLine();
                    data.insertEvent(trackingCode2, placeName, dateTime, description);
                    break;
                case 6:
                    Scanner scanner6 = new Scanner(System.in);
                    System.out.println("Etsi tapahtumat. Anna paketin seurantakoodi.");
                    int trackingCode3 = scanner6.nextInt();
                    List<Event> events = data.queryEvents(trackingCode3);
                    //System.out.println(events);
                    System.out.println("Tapahtumat seurantakoodilla "+trackingCode3+":");
                    for(Event event: events) {
                        System.out.println(event);
                    }
                    break;
                case 7:
                    Scanner scanner7 = new Scanner(System.in);
                    System.out.println("Hae kaikki asiakkaan paketit ja niihin liittyvien tapahtumien määrä. Anna asiakkaan nimi.");
                    String customerName2 = scanner7.nextLine();
                    List<CustomerParcel> customerParcels = data.queryCustomerParcels(customerName2);
                    for(CustomerParcel customerParcel: customerParcels) {
                        System.out.println(customerParcel);
                    }
                    break;
                case 8:
                    Scanner scanner8 = new Scanner((System.in));
                    System.out.println("Haetaan annetusta paikasta tapahtumien määrä tiettynä päivänä. Anna paikka.");
                    String paikka = scanner8.nextLine();
                    System.out.println("Anna päivä muodossa YYYY-mm-dd");
                    String paiva = scanner8.nextLine();
                    int count = data.getEvents(paikka, paiva);
                    System.out.println("Paikasta "+paikka+" löytyi "+count+" tapahtumaa päivämäärällä "+paiva+".");
                    break;
                case 9:
                    data.testDB();
                    break;
                case 0:
                    data.closeDB();
                    quit = true;
                    break;
                default:
                    System.out.println("default");
                    break;
            }
        }while(!quit);
        System.out.println("Ohjelma päättyi.");

    }
}
