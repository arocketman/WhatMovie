package com.github.arocketman.whatmovie.constants;

import android.widget.FrameLayout;

/**
 * This class holds most of the constants used throughout the project.
 */
public class Constants {
    //themoviedb constants
    public final static String IMAGES_BASEURL = "http://image.tmdb.org/t/p/w500";
    public static final float MIN_VOTE = 6.5f;
    public static final int MOVIES_LEFT_FOR_REFRESH = 10;

    //Arguments
    public static final String VIEW_KIND_ARG = "viewKind";
    public static final int UNLIKED_ARG_ID = 1;
    public static final int LIKED_ARG_ID = 0;
    public static final int WATCHED_ARG_ID = 2;
    public static final int REMOVE_ARG_ID = -1;

    //Database Constants
    public static final int DATABASE_VERSION = 10;
    public static final String DATABASE_NAME = "WhatMovie.db";
    public static final String GENRES_DB_FILENAME = "genres_db.json";

    //Other constants
    public static final int TOGGLE_ANIMATION_DURATION = 500;
    public static final String GENRE_ARGUMENT = "genre";
}
