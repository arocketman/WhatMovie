package com.github.arocketman.whatmovie;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.github.arocketman.whatmovie.persistency.MoviesDbHelper;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.uwetrottmann.tmdb2.entities.Movie;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class MainActivity extends AppCompatActivity  {

    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;
    public HashSet<Integer> mKnownMoviesIds = new HashSet<>();

    private void populateDrawer(Drawer drawer, String [] itemsArray){
        FontAwesome.Icon [] icons = {FontAwesome.Icon.faw_heart,FontAwesome.Icon.faw_thumbs_down, FontAwesome.Icon.faw_address_book, FontAwesome.Icon.faw_bolt,
                FontAwesome.Icon.faw_lightbulb_o, FontAwesome.Icon.faw_heartbeat, FontAwesome.Icon.faw_motorcycle, FontAwesome.Icon.faw_smile_o, FontAwesome.Icon.faw_question,
                FontAwesome.Icon.faw_music, FontAwesome.Icon.faw_flask, FontAwesome.Icon.faw_space_shuttle, FontAwesome.Icon.faw_key, FontAwesome.Icon.faw_gratipay, FontAwesome.Icon.faw_television,
                FontAwesome.Icon.faw_object_group, FontAwesome.Icon.faw_leaf, FontAwesome.Icon.faw_dot_circle_o, FontAwesome.Icon.faw_child, FontAwesome.Icon.faw_shower, FontAwesome.Icon.faw_book};
        for(int i = 0; i < icons.length; i++) {
            PrimaryDrawerItem drawerItem = new PrimaryDrawerItem().withIdentifier(i).withName(itemsArray[i]).withIcon(icons[i]).withIconColor(Color.rgb(10 * i, 20 * i, 30 * i));
            if(i==0)
                drawerItem.withIconColor(Color.RED);
            if(i == 2) {
                drawer.addItem(new DividerDrawerItem());
            }
            drawer.addItem(drawerItem);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Drawer drawer = new DrawerBuilder().withActivity(this).build();
        final String [] itemsArray = getResources().getStringArray(R.array.drawer_menu_items);
        populateDrawer(drawer,itemsArray);

        drawer.setSelectionAtPosition((new Random()).nextInt(drawer.getDrawerItems().size()));

        drawer.setOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                if(position >= 2)
                    changeGenre(itemsArray[drawer.getCurrentSelectedPosition()]);
                else {
                    for (Movie m : new MoviesDbHelper(getApplicationContext()).readFromDb(true, false)) {
                        boolean liked = itemsArray[drawer.getCurrentSelectedPosition()].equals("Liked");
                        openLikedFragment(liked);
                    }
                }
                return true;
            }
        });
/*
        mDrawerList = (ListView) findViewById(R.id.navList);
        ((DrawerLayout)mDrawerList.getParent()).openDrawer(Gravity.LEFT);

        mAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.drawer_menu_items));
        mDrawerList.setAdapter(mAdapter);
        mDrawerList.setSelection((new Random()).nextInt(mAdapter.getCount()));
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, int position, long id) {
                //First two positions are reserved for "Liked and Unliked"
                if(position >= 2) {
                    changeGenre(mDrawerList.getItemAtPosition(position).toString());
                    closeDrawer(parent);
                }
                else{
                    for(Movie m : new MoviesDbHelper(getApplicationContext()).readFromDb(true, false)){
                        boolean liked = mDrawerList.getItemAtPosition(position).equals("Liked");
                        openLikedFragment(liked);
                    }

                    closeDrawer(parent);
                }
            }

        });*/

        for(Movie m : (new MoviesDbHelper(getApplicationContext())).readFromDb(false,true))
            mKnownMoviesIds.add(m.id);
        MovieFragment fragment = new MovieFragment();
        Bundle arguments = new Bundle();
        fragment.setArguments(arguments);
        arguments.putString("genre",itemsArray[drawer.getCurrentSelectedPosition()]);
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

    private void closeDrawer(final AdapterView<?> parent) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((DrawerLayout) parent.getParent()).closeDrawers();
            }
        });
    }

}
