package com.github.arocketman.whatmovie.connectors;

import android.content.Context;

import com.github.arocketman.whatmovie.constants.Secret;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.uwetrottmann.tmdb2.Tmdb;
import com.uwetrottmann.tmdb2.entities.DiscoverFilter;
import com.uwetrottmann.tmdb2.entities.Movie;
import com.uwetrottmann.tmdb2.entities.PersonResultsPage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Andreuccio on 01/02/2017.
 */

public class BasicConnector {

    Tmdb instance;
    private HashMap<String, Integer> genresMap;

    public BasicConnector(Context context){
        instance = new Tmdb(Secret.API_KEY);
        try {
            genresMap = new HashMap<>();
            if(context != null)
                parseGenres(context.getAssets().open("genres_db.json"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns an arraylist of movies based on genre and a minimum vote.
     * @param genre
     * @param minVote
     * @param page
     * @return
     */
    public ArrayList<Movie> getMoviesByGenre(String genre , float minVote, Integer page){
        try {
            return (ArrayList<Movie>) instance.discoverMovie().
                    vote_average_gte(minVote)
                    .with_genres(new DiscoverFilter(genresMap.get(genre)))
                    .page(page)
                    .build().execute().
                    body().results;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Tmdb getInstance() {
        return instance;
    }

    private void parseGenres(InputStream genresInputStream) throws FileNotFoundException {
        JsonReader reader = new JsonReader(new InputStreamReader(genresInputStream));
        JsonObject parsed = (JsonObject)new JsonParser().parse(reader);
        JsonArray elements = parsed.get("genres").getAsJsonArray();
        for(JsonElement element : elements)
            genresMap.put(element.getAsJsonObject().get("name").toString().replace("\"",""),Integer.valueOf(element.getAsJsonObject().get("id").toString()));
    }

    public ArrayList<PersonResultsPage.ResultsPage> getPopularPersons(Integer page){
        try {
            return (ArrayList<PersonResultsPage.ResultsPage>) instance.personService().popular(page).execute().body().results;
        } catch (IOException e) {
            System.out.println("Page was: " + page);
            e.printStackTrace();
        }
        return null;
    }

    public HashMap<String, Integer> getGenresMap() {
        return genresMap;
    }

}
