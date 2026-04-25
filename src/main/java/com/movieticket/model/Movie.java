package com.movieticket.model;

public class Movie {
    private int     movieId;
    private String  title;
    private String  genre;
    private String  language;
    private int     durationMin;
    private String  rating;
    private String  description;
    private String  posterPath;
    private boolean nowShowing;

    public Movie() { }

    public Movie(String title, String genre, String language, int durationMin,
                 String rating, String description, boolean nowShowing) {
        this.title       = title;
        this.genre       = genre;
        this.language    = language;
        this.durationMin = durationMin;
        this.rating      = rating;
        this.description = description;
        this.nowShowing  = nowShowing;
    }

    public int     getMovieId()      { return movieId; }
    public void    setMovieId(int v) { this.movieId = v; }
    public String  getTitle()        { return title; }
    public void    setTitle(String v){ this.title = v; }
    public String  getGenre()        { return genre; }
    public void    setGenre(String v){ this.genre = v; }
    public String  getLanguage()     { return language; }
    public void    setLanguage(String v){ this.language = v; }
    public int     getDurationMin()  { return durationMin; }
    public void    setDurationMin(int v){ this.durationMin = v; }
    public String  getRating()       { return rating; }
    public void    setRating(String v){ this.rating = v; }
    public String  getDescription()  { return description; }
    public void    setDescription(String v){ this.description = v; }
    public String  getPosterPath()   { return posterPath; }
    public void    setPosterPath(String v){ this.posterPath = v; }
    public boolean isNowShowing()    { return nowShowing; }
    public void    setNowShowing(boolean v){ this.nowShowing = v; }

    @Override public String toString() { return title + " (" + language + ")"; }
}
