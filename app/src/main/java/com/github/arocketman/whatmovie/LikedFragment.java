package com.github.arocketman.whatmovie;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.arocketman.whatmovie.constants.Constants;
import com.github.arocketman.whatmovie.persistency.MoviesDbHelper;
import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.uwetrottmann.tmdb2.entities.Movie;

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
        int viewKind = getArguments().getInt(Constants.VIEW_KIND_ARG);
        for(Movie m : new MoviesDbHelper(getContext()).readFromDb(viewKind, false))
            mSwipeView.addView(new MovieCard(getContext(),m,mSwipeView));
        return inflated;
    }

}
