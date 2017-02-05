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
 * Created by Andreuccio on 05/02/2017.
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

    public boolean insertIntoDb(Movie movie , boolean liked){
        ContentValues values = new ContentValues();
        values.put(MoviesContract.MovieEntry.COLUMN_ID,movie.id);
        values.put(MoviesContract.MovieEntry.COLUMN_TITLE,movie.title);
        values.put(MoviesContract.MovieEntry.COLUMN_DESCRIPTION,movie.overview);
        values.put(MoviesContract.MovieEntry.COLUMN_LIKED,liked);
        values.put(MoviesContract.MovieEntry.COLUMN_VOTE,movie.vote_average);
        //Returns true if the insert method is different from -1 (insert failed)
        return this.getWritableDatabase().insert(MoviesContract.MovieEntry.TABLE_NAME,null,values) != -1;
    }

    public ArrayList<Movie> readFromDb(boolean liked){
        Cursor cursor = getReadableDatabase().query(
            MoviesContract.MovieEntry.TABLE_NAME,
                null,
                null,
                null,
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
            queryResults.add(m);
        }
        return queryResults;
    }
}
