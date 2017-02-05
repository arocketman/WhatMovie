package com.github.arocketman.whatmovie.constants;

import android.widget.FrameLayout;

/**
 * Created by Andreuccio on 03/02/2017.
 */

public class Constants {
    public final static String IMAGES_BASEURL = "http://image.tmdb.org/t/p/w500";
    public static final int TOGGLE_ANIMATION_DURATION = 500;
    public static final float MIN_VOTE = 6.5f;
    public static final int MOVIES_LEFT_FOR_REFRESH = 10;

    //Database Constants
    public static final int DATABASE_VERSION = 10;
    public static final String DATABASE_NAME = "WhatMovie.db";

    public static final String VIEW_KIND_ARG = "viewKind";
    public static final int UNLIKED = 1;
    public static final int LIKED = 0;
    public static final int WATCHED = 2;

}
