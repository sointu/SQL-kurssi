package com.company;

public class CustomerParcel {
    private int trackingCode;
    private int eventCount;

    public int getTrackingCode() {
        return trackingCode;
    }

    public void setTrackingCode(int trackingCode) {
        this.trackingCode = trackingCode;
    }

    public int getEventCount() {
        return eventCount;
    }

    public void setEventCount(int eventCount) {
        this.eventCount = eventCount;
    }

    @Override
    public String toString() {
        return "Seurantakoodi: "+this.getTrackingCode()+
                ", tapahtumien määrä: "+this.getEventCount();
    }
}
