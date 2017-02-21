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
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.listeners.ItemRemovedListener;
import com.uwetrottmann.tmdb2.entities.Movie;

import java.util.ArrayList;

/**
 * The LikedFragment class is responsible for inflating andmanaging the "Liked,unliked,watchlist"
 * categories.
 */
public class LikedFragment extends Fragment {

    ArrayList<Movie> movies = new ArrayList<>();
    int lastRemovedCardIndex;
    SwipePlaceHolderView mSwipeView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View inflated = inflater.inflate(R.layout.fragment_liked, container, false);
        mSwipeView = (SwipePlaceHolderView) inflated.findViewById(R.id.swipeView);
        Utils.buildSwipeView(mSwipeView);
        //We will populate the swipeview based on what the user clicked.
        int viewKind = getArguments().getInt(Constants.VIEW_KIND_ARG);
        movies = new MoviesDbHelper(getContext()).readFromDb(viewKind, false);
        for(Movie m : movies)
            mSwipeView.addView(new MovieCard(getContext(),m));

        //Adding button listeners.
        inflated.findViewById(R.id.LikedAcceptBtn).setOnClickListener(new ButtonListener(Constants.LIKED_ARG_ID,false));
        inflated.findViewById(R.id.LikeddelBtn).setOnClickListener(new ButtonListener(Constants.REMOVE_ARG_ID,true));
        inflated.findViewById(R.id.LikedRejectBtn).setOnClickListener(new ButtonListener(Constants.UNLIKED_ARG_ID,true));

        ImageButton button;
        if(viewKind == Constants.LIKED_ARG_ID) {
            button = ((ImageButton) inflated.findViewById(R.id.LikedAcceptBtn));
            ((ViewGroup)button.getParent()).removeView(button);
        }
        else if(viewKind == Constants.UNLIKED_ARG_ID) {
            button = ((ImageButton) inflated.findViewById(R.id.LikedRejectBtn));
            ((ViewGroup)button.getParent()).removeView(button);
        }

        mSwipeView.addItemRemoveListener(new ItemRemovedListener() {
            @Override
            public void onItemRemoved(int count) {
                lastRemovedCardIndex = Utils.updateLastItem(movies.size(),count);
            }
        });

        return inflated;
    }

    class ButtonListener implements View.OnClickListener {

        int operation;
        boolean isSwipeIn;

        ButtonListener(int operation, boolean isSwipeIn) {
            this.operation = operation;
            this.isSwipeIn = isSwipeIn;
        }

        @Override
        public void onClick(View v) {
            mSwipeView.doSwipe(isSwipeIn);
            if(!movies.isEmpty()) {
                //Changing the status if we are liking/unliking a movie
                if(operation == Constants.LIKED_ARG_ID || operation == Constants.UNLIKED_ARG_ID)
                    new MoviesDbHelper(getContext()).changeLikedStatus(movies.get(lastRemovedCardIndex).id,operation);
                //Otherwise the operation must be of deletion and we proceed to call deleteMovie.
                else
                    new MoviesDbHelper(getContext()).deleteMovie(movies.get(lastRemovedCardIndex).id);
                movies.remove(movies.get(lastRemovedCardIndex));
            }
        }
    }

}
