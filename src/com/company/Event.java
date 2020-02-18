package com.company;

public class Event {
    private int id;
    private String kuvaus;
    private String aika;
    private String paikka_nimi;
    private int paketti_koodi;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKuvaus() {
        return kuvaus;
    }

    public void setKuvaus(String kuvaus) {
        this.kuvaus = kuvaus;
    }

    public String getAika() {
        return aika;
    }

    public void setAika(String aika) {
        this.aika = aika;
    }

    public String getPaikka_nimi() {
        return paikka_nimi;
    }

    public void setPaikka_nimi(String paikka_nimi) {
        this.paikka_nimi = paikka_nimi;
    }

    public int getPaketti_koodi() {
        return paketti_koodi;
    }

    public void setPaketti_koodi(int paketti_koodi) {
        this.paketti_koodi = paketti_koodi;
    }
    // objektin tulostus
    @Override
    public String toString() {
        return "Id: " + this.getId() +
                ", Kuvaus: " + this.getKuvaus()+
                ", Paikka: " + this.getPaikka_nimi()+
                ", Aika: " + this.getPaikka_nimi();
    }
}
