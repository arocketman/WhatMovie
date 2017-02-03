package com.github.arocketman.whatmovie.test;

import com.github.arocketman.whatmovie.connectors.BasicConnector;
import com.uwetrottmann.tmdb2.entities.Movie;
import com.uwetrottmann.tmdb2.entities.PersonResultsPage;

import org.junit.Ignore;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Created by Andreuccio on 02/02/2017.
 */
public class BasicConnectorTest {

    @Ignore
    @Test
    public void getPopularPeople() throws IOException {
        BasicConnector connector = new BasicConnector(null);
        ArrayList<PersonResultsPage.ResultsPage> results = connector.getPopularPersons(1);
        assertFalse(results.isEmpty());
    }

    @Test
    public void parseGenresDB() throws FileNotFoundException {
        BasicConnector connector = new BasicConnector(null);
        assertEquals(Integer.valueOf(35),connector.getGenresMap().get("Comedy"));
    }

    @Test
    public void getMoviesByGenre(){
        BasicConnector connector = new BasicConnector(null);
        ArrayList<Movie> movies = connector.getMoviesByGenre("Comedy",7.0f,1);
        assertFalse(movies.isEmpty());
    }
}