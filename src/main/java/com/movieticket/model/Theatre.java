package com.movieticket.model;

public class Theatre {
    private int    theatreId;
    private String name;
    private String city;
    private String address;
    private int    totalSeats;

    public Theatre() { }

    public Theatre(String name, String city, String address, int totalSeats) {
        this.name       = name;
        this.city       = city;
        this.address    = address;
        this.totalSeats = totalSeats;
    }

    public int    getTheatreId() { return theatreId; }
    public void   setTheatreId(int v){ this.theatreId = v; }
    public String getName()      { return name; }
    public void   setName(String v){ this.name = v; }
    public String getCity()      { return city; }
    public void   setCity(String v){ this.city = v; }
    public String getAddress()   { return address; }
    public void   setAddress(String v){ this.address = v; }
    public int    getTotalSeats(){ return totalSeats; }
    public void   setTotalSeats(int v){ this.totalSeats = v; }

    @Override public String toString() { return name + ", " + city; }
}
