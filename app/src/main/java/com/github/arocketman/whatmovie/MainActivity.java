package com.github.arocketman.whatmovie;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.transition.TransitionManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.github.arocketman.whatmovie.persistency.MoviesDbHelper;
import com.uwetrottmann.tmdb2.entities.Movie;

public class MainActivity extends AppCompatActivity  {

    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerList = (ListView) findViewById(R.id.navList);
        ((DrawerLayout)mDrawerList.getParent()).openDrawer(Gravity.LEFT);

        mAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.drawer_menu_items));
        mDrawerList.setAdapter(mAdapter);
        mDrawerList.setSelection(3);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, int position, long id) {
                if(position >= 2) {
                    changeGenre(mDrawerList.getItemAtPosition(position).toString());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((DrawerLayout) parent.getParent()).closeDrawers();
                        }
                    });
                }
                else{
                    for(Movie m : new MoviesDbHelper(getApplicationContext()).readFromDb(true)){
                        boolean liked = mDrawerList.getItemAtPosition(position).equals("Liked");
                        openLikedFragment(liked);
                    }
                }
            }
        });

        MovieFragment fragment = new MovieFragment();
        Bundle arguments = new Bundle();
        fragment.setArguments(arguments);
        arguments.putString("genre",mDrawerList.getSelectedItem().toString());
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.flContent, fragment).commit();

    }

    private void openLikedFragment(boolean liked) {
        LikedFragment fragment = new LikedFragment();
        Bundle arguments = new Bundle();
        fragment.setArguments(arguments);
        arguments.putBoolean("liked",liked);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent,fragment).commit();
    }

    private void changeGenre(String genre){
        MovieFragment fragment = new MovieFragment();
        Bundle arguments = new Bundle();
        fragment.setArguments(arguments);
        arguments.putString("genre",genre);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent,fragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
