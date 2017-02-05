package com.github.arocketman.whatmovie;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.arocketman.whatmovie.connectors.MovieDBConnector;
import com.github.arocketman.whatmovie.constants.Constants;
import com.github.arocketman.whatmovie.persistency.MoviesDbHelper;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.listeners.ItemRemovedListener;
import com.uwetrottmann.tmdb2.entities.Movie;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class LikedFragment extends Fragment {

    private SwipePlaceHolderView mSwipeView;
    private Context mContext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View inflated = inflater.inflate(R.layout.fragment_liked, container, false);
        mSwipeView = (SwipePlaceHolderView) inflated.findViewById(R.id.swipeView);
        mContext = getActivity().getApplicationContext();

        mSwipeView.getBuilder()
                .setDisplayViewCount(3)
                .setSwipeDecor(new SwipeDecor()
                        .setPaddingTop(0)
                        .setRelativeScale(0.01f));
        boolean liked = getArguments().getBoolean("liked");
        for(Movie m : new MoviesDbHelper(getContext()).readFromDb(liked))
            mSwipeView.addView(new MovieCard(getContext(),m,mSwipeView));
        return inflated;
    }

}
