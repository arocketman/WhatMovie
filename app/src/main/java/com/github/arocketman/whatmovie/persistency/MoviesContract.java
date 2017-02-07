package com.github.arocketman.whatmovie.persistency;

import android.provider.BaseColumns;

/**
 * Defines a contract for the LIKED_MOVIES table.
 */
public final class MoviesContract {

    private MoviesContract(){}

    static class MovieEntry implements BaseColumns{
        static final String TABLE_NAME = "LIKED_MOVIES";
        static final String COLUMN_ID = "ID";
        static final String COLUMN_TITLE = "TITLE";
        static final String POSTER_PATH = "POSTER_PATH";
        static final String COLUMN_DESCRIPTION = "DESCRIPTION";
        static final String COLUMN_VOTE = "VOTE";
        static final String COLUMN_LIKED = "LIKED";

    }

    static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                    MovieEntry.COLUMN_ID + " INTEGER PRIMARY KEY," +
                    MovieEntry.POSTER_PATH + " TEXT," +
                    MovieEntry.COLUMN_TITLE + " TEXT," +
                    MovieEntry.COLUMN_DESCRIPTION + " TEXT," +
                    MovieEntry.COLUMN_VOTE + " DOUBLE," +
                    MovieEntry.COLUMN_LIKED + " INTEGER)";

    static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME;


}
