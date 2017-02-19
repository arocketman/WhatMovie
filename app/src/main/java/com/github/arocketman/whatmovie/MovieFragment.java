package com.github.arocketman.whatmovie;

import android.app.ProgressDialog;
import android.content.Context;
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflated = inflater.inflate(R.layout.fragment_movies, container, false);
        mSwipeView = (SwipePlaceHolderView) inflated.findViewById(R.id.swipeView);
        mContext = getActivity().getApplicationContext();
        //Retrieving the genre argument
        mGenre = getArguments().getString(Constants.GENRE_ARGUMENT);

        //Build the swipeview and get the movies for the first time.
        Utils.buildSwipeView(mSwipeView);
        new CallMovies(true).execute();

        //Reject button behaviour
        inflated.findViewById(R.id.rejectBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwipeView.doSwipe(false);
                new MoviesDbHelper(getContext()).insertIntoDb(movies.get(lastRemovedCardIndex),Constants.UNLIKED_ARG_ID);
            }
        });

        //Watch button behaviour
        inflated.findViewById(R.id.watchBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwipeView.doSwipe(true);
                new MoviesDbHelper(getContext()).insertIntoDb(movies.get(lastRemovedCardIndex),Constants.WATCHED_ARG_ID);
            }
        });

        //Accept (liked) button behaviour
        inflated.findViewById(R.id.acceptBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwipeView.doSwipe(true);
                new MoviesDbHelper(getContext()).insertIntoDb(movies.get(lastRemovedCardIndex),Constants.LIKED_ARG_ID);
            }
        });

        //Setting up an item remove listener when we swipe in or remove an element via button.
        mSwipeView.addItemRemoveListener(new ItemRemovedListener() {
            @Override
            public void onItemRemoved(int count) {
                updateItemsCount(count);
            }
        });

        return inflated;
    }

    /**
     * Keeps requesting for more movies until we have enough to satisfy the threshold.
     */
    private void getMoviesThreshold() {
        boolean connectionOk = true;
        while(movies.size() < Constants.MOVIES_LEFT_FOR_REFRESH && connectionOk) {
            connectionOk=getMoreMovies();
            if(!connectionOk)
                ((MainActivity)getActivity()).connectionProblemMsg();
        }
    }

    /**
     * Updates the last item and calls for more movies if the item count is less than the threshold.
     * @param count amount of items left in the SwipeView.
     */
    private void updateItemsCount(int count) {
        lastRemovedCardIndex = Utils.updateLastItem(movies.size(),count);
        if(count < Constants.MOVIES_LEFT_FOR_REFRESH) {
            boolean connectionOk = false;
            try {
                connectionOk = new CallMovies(false).execute().get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            if(!connectionOk)
                ((MainActivity)getActivity()).connectionProblemMsg();
            mSwipeView.getBuilder().setDisplayViewCount(3);
        }
    }

    /**
     * Calls the AsyncTask getMovieTask to fetch more movies.
     * @return false if the fetched object is null (connection failing). False otherwise.
     */
    private boolean getMoreMovies() {
        ArrayList<Movie> fetched = new MovieDBConnector(getActivity().getApplicationContext()).getMovies(mGenre, mLastPage++);
        if(fetched==null)
            return false;
        //Iterating through the fetched results.
        Iterator<Movie> movieIterator = fetched.iterator();
        while (movieIterator.hasNext()) {
            final Movie fetchedMovie = movieIterator.next();
            //Removing the element if the fetched movie is already in the database.
            if(isKnown(fetchedMovie))
                movieIterator.remove();
            else {
                if (isValidMovie(fetchedMovie)) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mSwipeView.addView(new MovieCard(mContext, fetchedMovie, mSwipeView));
                        }
                    });
                    addKnownMovie(fetchedMovie);
                }
            }
        }
        Utils.concatenate(movies,fetched);
        return true;
    }

    /**
     * Checks whether the movie is valid or not.
     * Validity is described as non emptiness of title, overview and poster path.
     * @param fetchedMovie the fetched movie.
     * @return true if the fetched movie is valid. False otherwise.
     */
    private boolean isValidMovie(Movie fetchedMovie) {
        return !fetchedMovie.overview.isEmpty() && !fetchedMovie.title.isEmpty() && !fetchedMovie.poster_path.isEmpty();
    }

    /**
     * Checks whether or not the movie already existed in the database.
     * @param fetchedMovie the fetched movie.
     * @return true if the movie existed in DB. False otherwise.
     */
    private boolean isKnown(Movie fetchedMovie) {
        return ((MainActivity)getActivity()).mKnownMoviesIds.contains(fetchedMovie.id);
    }

    /**
     * Adds the movie to the known movies.
     * @param movie the movie to be added.
     */
    private void addKnownMovie(Movie movie){
        ((MainActivity)getActivity()).mKnownMoviesIds.add(movie.id);
    }

    class CallMovies extends AsyncTask<Void,Void,Boolean> {
        ProgressDialog progDailog;
        Boolean isFirstRun;

        /**
         * Creates a new CallMovies AsyncTask
         * @param isFirstRun true if the asyncs task is running for the first time. False otherwise.
         */
        CallMovies(Boolean isFirstRun) {
            super();
            this.isFirstRun = isFirstRun;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if(isFirstRun)
                getMoviesThreshold();
            else
                return getMoreMovies();
            return true;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Creating a "loading" spinner if it's the first run.
            if(isFirstRun) {
                progDailog = new ProgressDialog(getActivity());
                progDailog.setMessage("Loading...");
                progDailog.setIndeterminate(false);
                progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progDailog.setCancelable(true);
                progDailog.show();
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            mSwipeView.refreshDrawableState();
            if(isFirstRun)
                progDailog.dismiss();
        }
    }
}
