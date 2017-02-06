package com.github.arocketman.whatmovie.connectors;

import android.content.Context;

import com.github.arocketman.whatmovie.constants.Constants;
import com.uwetrottmann.tmdb2.entities.Movie;
import java.util.ArrayList;

/**
 * This is the connector that is used for themoviedb.org
 */
public class MovieDBConnector extends BasicConnector implements MovieConnector {

    public MovieDBConnector(Context context) {
        super(context);
    }

    @Override
    public ArrayList<Movie> getRandomMovies() {
        return null;
    }

    @Override
    public ArrayList<Movie> getMovies(String genre,Integer page) {
        return this.getMoviesByGenre(genre, Constants.MIN_VOTE,page);
    }
}
