package com.github.arocketman.whatmovie;

import android.app.ProgressDialog;
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
        mGenre = getArguments().getString(Constants.GENRE_ARGUMENT);
        Utils.buildSwipeView(mSwipeView);

        new callMovies(true).execute();

        inflated.findViewById(R.id.rejectBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwipeView.doSwipe(false);
                new MoviesDbHelper(getContext()).insertIntoDb(movies.get(lastRemovedCardIndex),Constants.UNLIKED_ARG_ID);
            }
        });

        inflated.findViewById(R.id.watchBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwipeView.doSwipe(true);
                new MoviesDbHelper(getContext()).insertIntoDb(movies.get(lastRemovedCardIndex),Constants.WATCHED_ARG_ID);
            }
        });

        inflated.findViewById(R.id.acceptBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwipeView.doSwipe(true);
                new MoviesDbHelper(getContext()).insertIntoDb(movies.get(lastRemovedCardIndex),Constants.LIKED_ARG_ID);
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

    private void getMoviesThreshold() {
        boolean connectionOk = true;
        while(movies.size() < Constants.MOVIES_LEFT_FOR_REFRESH && connectionOk) {
            connectionOk=getMoreMovies();
            if(!connectionOk)
                ((MainActivity)getActivity()).connectionProblemMsg();
        }
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
        lastRemovedCardIndex = Utils.updateLastItem(movies.size(),count);
        if(count < Constants.MOVIES_LEFT_FOR_REFRESH) {
            boolean connectionOk = false;
            try {
                connectionOk = new callMovies(false).execute().get();
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
        Iterator<Movie> movieIterator = fetched.iterator();
        while (movieIterator.hasNext()) {
            final Movie fetchedMovie = movieIterator.next();
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

    private boolean isValidMovie(Movie fetchedMovie) {
        return !fetchedMovie.overview.isEmpty() && !fetchedMovie.title.isEmpty() && !fetchedMovie.poster_path.isEmpty();
    }

    private boolean isKnown(Movie fetchedMovie) {
        return ((MainActivity)getActivity()).mKnownMoviesIds.contains(fetchedMovie.id);
    }

    private void addKnownMovie(Movie movie){
        ((MainActivity)getActivity()).mKnownMoviesIds.add(movie.id);
    }

    class callMovies extends AsyncTask<Void,Void,Boolean> {
        ProgressDialog progDailog;
        Boolean isFirstRun;

        callMovies(Boolean isFirstRun) {
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
