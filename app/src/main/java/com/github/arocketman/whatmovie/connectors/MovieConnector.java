package com.github.arocketman.whatmovie.connectors;


import com.uwetrottmann.tmdb2.entities.Movie;

import java.util.ArrayList;

/**
 * Basic interface used from non-abstract connectors.
 */

public interface MovieConnector {
    public ArrayList<Movie> getRandomMovies();
    public ArrayList<Movie> getMovies(String genre, Integer page);
}
