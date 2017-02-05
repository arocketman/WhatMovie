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
import android.view.Window;

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
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

public class MovieFragment extends Fragment {

    private SwipePlaceHolderView mSwipeView;
    private Context mContext;
    private String mGenre;
    private Integer mLastPage = 1;
    private int lastRemovedCardIndex;
    private ArrayList<Movie> movies = new ArrayList<>();

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflated = inflater.inflate(R.layout.fragment_movies, container, false);
        mSwipeView = (SwipePlaceHolderView) inflated.findViewById(R.id.swipeView);
        mContext = getActivity().getApplicationContext();
        mGenre = getArguments().getString("genre");
        mSwipeView.getBuilder()
                .setDisplayViewCount(3)
                .setSwipeDecor(new SwipeDecor()
                        .setPaddingTop(0)
                        .setRelativeScale(0.01f));

        while(movies.size() < Constants.MOVIES_LEFT_FOR_REFRESH)
            getMoreMovies();

        inflated.findViewById(R.id.rejectBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwipeView.doSwipe(false);
                new MoviesDbHelper(getContext()).insertIntoDb(movies.get(lastRemovedCardIndex),Constants.UNLIKED);
            }
        });

        inflated.findViewById(R.id.watchBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwipeView.doSwipe(true);
                new MoviesDbHelper(getContext()).insertIntoDb(movies.get(lastRemovedCardIndex),Constants.WATCHED);
            }
        });

        inflated.findViewById(R.id.acceptBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwipeView.doSwipe(true);
                new MoviesDbHelper(getContext()).insertIntoDb(movies.get(lastRemovedCardIndex),Constants.LIKED);
            }
        });

        mSwipeView.addItemRemoveListener(new ItemRemovedListener() {
            @Override
            public void onItemRemoved(int count) {
                updateItemsCount(count);
            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(getActivity()).addApi(AppIndex.API).build();
        return inflated;
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Movie Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    /**
     * Updates the last item popped from the movies ArrayList.
     * @param count number of items left to swipe.
     */
    private void updateLastItem(int count){
        lastRemovedCardIndex = (movies.size() - count);
    }

    @Override
    public void onStart() {
        super.onStart();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    /**
     * Updates the last item and calls for more movies if the item count is less than the threshold.
     * @param count
     */
    private void updateItemsCount(int count) {
        updateLastItem(count);
        if(count < Constants.MOVIES_LEFT_FOR_REFRESH) {
            getMoreMovies();
            mSwipeView.getBuilder().setDisplayViewCount(3);
        }
    }

    /**
     * Calls the AsyncTask getMovieTask to fetch more movies.
     */
    private void getMoreMovies() {
        try {
            ArrayList<Movie> fetched = new getMoviesTask().execute().get();
            Iterator<Movie> movieIterator = fetched.iterator();
            while (movieIterator.hasNext()) {
                Movie fetchedMovie = movieIterator.next();
                if(isKnown(fetchedMovie))
                    movieIterator.remove();
                else {
                    mSwipeView.addView(new MovieCard(mContext, fetchedMovie, mSwipeView));
                    addKnownMovie(fetchedMovie);
                }
            }
            Utils.concatenate(movies,fetched);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private boolean isKnown(Movie fetchedMovie) {
        return ((MainActivity)getActivity()).mKnownMoviesIds.contains(fetchedMovie.id);
    }

    private void addKnownMovie(Movie movie){
        ((MainActivity)getActivity()).mKnownMoviesIds.add(movie.id);
    }

    class getMoviesTask extends AsyncTask<String, Void, ArrayList<Movie>> {
        @Override
        protected ArrayList<Movie> doInBackground(String... params) {
            return new MovieDBConnector(getActivity().getApplicationContext()).getMovies(mGenre, mLastPage++);
        }

    }
}
