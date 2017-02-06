package com.github.arocketman.whatmovie.connectors;

import android.content.Context;

import com.github.arocketman.whatmovie.constants.Constants;
import com.github.arocketman.whatmovie.constants.Secret;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.uwetrottmann.tmdb2.Tmdb;
import com.uwetrottmann.tmdb2.entities.DiscoverFilter;
import com.uwetrottmann.tmdb2.entities.Movie;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/**
 * Provides a basic implementation of a themoviedb connector.
 * This class shouldn't be instantiated hence it's declared as abstract.
 * It's just used as a basic connector from its children and does not implement the MovieConnector
 * interface.
 */

abstract class BasicConnector {

    private Tmdb instance;
    private HashMap<String, Integer> genresMap;

    BasicConnector(Context context){
        instance = new Tmdb(Secret.API_KEY);
        try {
            genresMap = new HashMap<>();
            if(context != null)
                parseGenres(context.getAssets().open(Constants.GENRES_DB_FILENAME));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns an arraylist of movies based on genre and a minimum vote.
     * @param genre the particular genre to fetch movies from
     * @param minVote minimum vote for the movies to be included
     * @param page results page to read from
     * @return arraylist of movies
     */
    protected ArrayList<Movie> getMoviesByGenre(String genre , float minVote, Integer page){
        String language = Locale.getDefault().getLanguage();
        try {
            return (ArrayList<Movie>) instance.discoverMovie().
                    vote_average_gte(minVote)
                    .with_genres(new DiscoverFilter(genresMap.get(genre)))
                    .page(page)
                    .language(language)
                    .build().execute().
                    body().results;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected Tmdb getInstance() {
        return instance;
    }

    /**
     * Fills up the genres map given the input stream of genres.
     * @param genresInputStream
     * @throws FileNotFoundException
     */
    private void parseGenres(InputStream genresInputStream) throws FileNotFoundException {
        JsonReader reader = new JsonReader(new InputStreamReader(genresInputStream));
        JsonObject parsed = (JsonObject)new JsonParser().parse(reader);
        JsonArray elements = parsed.get("genres").getAsJsonArray();
        for(JsonElement element : elements)
            genresMap.put(element.getAsJsonObject().get("name").toString().replace("\"",""),Integer.valueOf(element.getAsJsonObject().get("id").toString()));
    }

}
