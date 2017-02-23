package com.github.arocketman.whatmovie.persistency;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.github.arocketman.whatmovie.constants.Constants;
import com.uwetrottmann.tmdb2.entities.Movie;

import java.util.ArrayList;

/**
 * Main class to access the movies database and interact with it.
 */
public class MoviesDbHelper extends SQLiteOpenHelper {

    public MoviesDbHelper(Context context){
        super(context, Constants.DATABASE_NAME,null,Constants.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(MoviesContract.SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(MoviesContract.SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db,oldVersion,newVersion);
    }

    /**
     * Inserts the movie into the database.
     * @param movie the movie to insert
     * @param movieKind Liked(0),Unliked(1),Watchlist(2)
     * @return true if the insertion is successful.
     */
    public boolean insertIntoDb(Movie movie , int movieKind){
        ContentValues values = new ContentValues();
        values.put(MoviesContract.MovieEntry.COLUMN_ID,movie.id);
        values.put(MoviesContract.MovieEntry.COLUMN_TITLE,movie.title);
        values.put(MoviesContract.MovieEntry.COLUMN_DESCRIPTION,movie.overview);
        values.put(MoviesContract.MovieEntry.COLUMN_LIKED,movieKind);
        values.put(MoviesContract.MovieEntry.COLUMN_VOTE,movie.vote_average);
        values.put(MoviesContract.MovieEntry.POSTER_PATH,movie.poster_path);
        //Returns true if the insert method is different from -1 (insert failed)
        System.out.println("ID: " + movie.id);
        boolean isOk = this.getWritableDatabase().insert(MoviesContract.MovieEntry.TABLE_NAME,null,values) != -1;
        this.getWritableDatabase().close();
        return isOk;
    }

    /**
     * Reads movies from the database table specified in the MoviesContract class
     * @param viewKind Liked,Unliked or watchlist. See Constants for more info.
     * @param getAll If true the viewKind parameter is discarded and gets all the entries.
     * @return An arraylist being the result of the given query.
     */
    public ArrayList<Movie> readFromDb(int viewKind, boolean getAll){
        String selection = null;
        String[] selectionArgs = null;
        if(!getAll) {
            selection = MoviesContract.MovieEntry.COLUMN_LIKED + " = ?";
            selectionArgs = (new String[1]);
            selectionArgs[0]=String.valueOf(viewKind);
        }
        Cursor cursor = getReadableDatabase().query(
            MoviesContract.MovieEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        ArrayList<Movie> queryResults = new ArrayList<>();
        while(cursor.moveToNext()) {
            Movie m = new Movie();
            m.title = cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_TITLE));
            m.id = cursor.getInt(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_ID));
            m.overview = cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_DESCRIPTION));
            m.vote_average = cursor.getDouble(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_VOTE));
            m.poster_path = cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.POSTER_PATH));
            queryResults.add(m);
        }
        cursor.close();
        return queryResults;
    }

    /**
     * Changes the status of the movie to the selected one (liked, unliked, watchlist).
     * @param movieID the id of the movie to change the status.
     * @param LikedStatus the status to which to change (defined in Constants).
     */
    public void changeLikedStatus(Integer movieID, Integer LikedStatus){
        ContentValues values = new ContentValues();
        values.put(MoviesContract.MovieEntry.COLUMN_LIKED,LikedStatus);
        this.getWritableDatabase().update(MoviesContract.MovieEntry.TABLE_NAME,values,MoviesContract.MovieEntry.COLUMN_ID+"="+movieID,null);
        this.getWritableDatabase().close();
    }

    /**
     * Deletes movie from database.
     * @param movieID Id of the movie to remove.
     */
    public void deleteMovie(Integer movieID){
        this.getWritableDatabase().delete(MoviesContract.MovieEntry.TABLE_NAME,MoviesContract.MovieEntry.COLUMN_ID + "=" + movieID,null);
        this.getWritableDatabase().close();
    }
}
