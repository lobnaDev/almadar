package com.android.il.almadar;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.il.almadar.admin.EditTechActivity;
import com.android.il.almadar.technician.MessageService;
import com.android.il.almadar.utils.PrefManager;
import com.parse.ParseUser;


public class CarSelectorActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    NavigationView navigationView = null;
    Toolbar toolbar = null;
    TextView usernameTV;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, MessageService.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_selector);


        usernameTV = (TextView) findViewById(R.id.textViewUsername);
        usernameTV.setText(ParseUser.getCurrentUser().getUsername()+"");
        //Set the fragment initially
        MyCarsListFragment fragment = new MyCarsListFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_content, fragment);
        fragmentTransaction.commit();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CarSelectorActivity.this, AddCarFragment.class);
                startActivity(intent);
            }
        });

     /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab);
            fab2.show();
             /*   Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_addCar) {
            // Handle the camera action
            //Set the fragment initially
         /*   AddCarFragment fragment = new AddCarFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.main_content, fragment);
            fragmentTransaction.commit(); */

        }
        else if (id == R.id.nav_Mycar) {
        MyCarsListFragment fragment = new MyCarsListFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.main_content,fragment );
            fragmentTransaction.commit();
        }
        else if (id == R.id.nav_myappintment) {
            MyappintmentFragment fragment = new MyappintmentFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.main_content,fragment );
            fragmentTransaction.commit();
            }

        else if (id == R.id.nav_manage) {
            Intent intent = new Intent(CarSelectorActivity.this, ServicesActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_share) {
                Intent intent1 = new Intent(CarSelectorActivity.this, EditTechActivity.class);
                intent1.putExtra("user", ParseUser.getCurrentUser().getObjectId());
                startActivity(intent1);

        } else if (id == R.id.nav_logout) {
            ParseUser user = ParseUser.getCurrentUser();
            ParseUser.logOut();
            PrefManager pref = new PrefManager(getApplicationContext());
            pref.logout();

            finish();
            Intent intent = new Intent(CarSelectorActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
