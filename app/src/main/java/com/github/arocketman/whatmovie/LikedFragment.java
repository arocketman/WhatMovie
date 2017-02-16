package com.github.arocketman.whatmovie;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.github.arocketman.whatmovie.constants.Constants;
import com.github.arocketman.whatmovie.persistency.MoviesDbHelper;
import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.uwetrottmann.tmdb2.entities.Movie;

/**
 * The LikedFragment class is responsible for inflating the "Liked,unliked,watchlist" categories.
 */
public class LikedFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View inflated = inflater.inflate(R.layout.fragment_liked, container, false);
        SwipePlaceHolderView mSwipeView = (SwipePlaceHolderView) inflated.findViewById(R.id.swipeView);
        Utils.buildSwipeView(mSwipeView);
        //We will populate the swipeview based on what the user clicked.
        int viewKind = getArguments().getInt(Constants.VIEW_KIND_ARG);
        for(Movie m : new MoviesDbHelper(getContext()).readFromDb(viewKind, false))
            mSwipeView.addView(new MovieCard(getContext(),m, mSwipeView));
        ImageButton button = null;
        if(viewKind == Constants.LIKED_ARG_ID) {
            button = ((ImageButton) inflated.findViewById(R.id.LikedAcceptBtn));
            ((ViewGroup)button.getParent()).removeView(button);
        }
        else if(viewKind == Constants.UNLIKED_ARG_ID) {
            button = ((ImageButton) inflated.findViewById(R.id.LikedRejectBtn));
            ((ViewGroup)button.getParent()).removeView(button);
        }


        return inflated;
    }

}
