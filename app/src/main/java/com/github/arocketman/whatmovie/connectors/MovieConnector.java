package com.github.arocketman.whatmovie.connectors;


import com.uwetrottmann.tmdb2.entities.Movie;

import java.util.ArrayList;

/**
 * Basic interface used from non-abstract connectors.
 * The interface is supposed to allow for future diversity in movie services.
 * As of the first release only themoviedb is supported.
 */
interface MovieConnector {
    ArrayList<Movie> getMovies(String genre, Integer page);
}
