package com.github.arocketman.whatmovie;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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

import com.github.arocketman.whatmovie.constants.Constants;
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

    public HashSet<Integer> mKnownMoviesIds = new HashSet<>();

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
                if(position >= 3)
                    //The minus one here is because of the separator element in the draweritems.
                    changeGenre(itemsArray[drawer.getCurrentSelectedPosition()-1]);
                else
                        openLikedFragment(drawer.getCurrentSelectedPosition());
                drawer.closeDrawer();
                return true;
            }
        });

        for(Movie m : (new MoviesDbHelper(getApplicationContext())).readFromDb(0,true))
            mKnownMoviesIds.add(m.id);
        MovieFragment fragment = new MovieFragment();
        Bundle arguments = new Bundle();
        fragment.setArguments(arguments);
        arguments.putString("genre",itemsArray[drawer.getCurrentSelectedPosition()-1]);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.flContent, fragment).commit();

    }

    private void openLikedFragment(int viewKind) {
        LikedFragment fragment = new LikedFragment();
        Bundle arguments = new Bundle();
        fragment.setArguments(arguments);
        arguments.putInt(Constants.VIEW_KIND_ARG,viewKind);
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

    /**
     * Populated the drawer with all the genres and user catalog elements (like,unlike,watchlist)
     * @param drawer the drawer that has to be populated
     * @param itemsArray string list that it's going to populate the drawer.
     */
    private void populateDrawer(Drawer drawer, String [] itemsArray){
        FontAwesome.Icon [] icons = {FontAwesome.Icon.faw_heart,FontAwesome.Icon.faw_thumbs_down, FontAwesome.Icon.faw_bookmark,FontAwesome.Icon.faw_address_book, FontAwesome.Icon.faw_bolt,
                FontAwesome.Icon.faw_lightbulb_o, FontAwesome.Icon.faw_heartbeat, FontAwesome.Icon.faw_motorcycle, FontAwesome.Icon.faw_smile_o, FontAwesome.Icon.faw_question,
                FontAwesome.Icon.faw_music, FontAwesome.Icon.faw_flask, FontAwesome.Icon.faw_space_shuttle, FontAwesome.Icon.faw_key, FontAwesome.Icon.faw_gratipay, FontAwesome.Icon.faw_television,
                FontAwesome.Icon.faw_object_group, FontAwesome.Icon.faw_leaf, FontAwesome.Icon.faw_dot_circle_o, FontAwesome.Icon.faw_child, FontAwesome.Icon.faw_shower, FontAwesome.Icon.faw_book};
        for(int i = 0; i < icons.length; i++) {
            PrimaryDrawerItem drawerItem = new PrimaryDrawerItem().withIdentifier(i).withName(itemsArray[i]).withIcon(icons[i]).withIconColor(Color.rgb(10 * i, 20 * i, 30 * i));
            if(i==0)
                drawerItem.withIconColor(Color.RED);
            //Adding separator item between user catalogs and genres
            if(i == 3) {
                drawer.addItem(new DividerDrawerItem());
            }
            drawer.addItem(drawerItem);
        }
    }

    /**
     * Creates a snackbar message for no connection
     */
    void connectionProblemMsg(){
        Snackbar snackbar = Snackbar
                .make(getWindow().getDecorView().getRootView(),  getResources().getString(R.string.no_connection_message), Snackbar.LENGTH_INDEFINITE);

        snackbar.show();
        snackbar.setAction("DISMISS", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

}
