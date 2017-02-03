package com.github.arocketman.whatmovie.connectors;


import com.uwetrottmann.tmdb2.entities.Movie;

import java.util.ArrayList;

/**
 * Created by Andreuccio on 01/02/2017.
 */

public interface MovieConnector {
    public ArrayList<Movie> getRandomMovies();
    public ArrayList<Movie> getMovies(String genre);
}
