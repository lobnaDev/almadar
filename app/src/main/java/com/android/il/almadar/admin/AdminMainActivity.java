package com.android.il.almadar.admin;

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

import com.android.il.almadar.LoginActivity;
import com.android.il.almadar.R;
import com.android.il.almadar.core.RepairShop;
import com.android.il.almadar.utils.PrefManager;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class AdminMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    RepairShop shop;
    ParseUser user;
    private void getCurrentUser(){
        ParseQuery<ParseUser> query = ParseQuery.getQuery("_User");
        query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.include("repair_shop");
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                user = objects.get(0);
                shop = new RepairShop();
                shop.setParseObject( ((ParseObject) objects.get(0).get("repair_shop")));
                TextView tvRepairShopAdmin = (TextView) findViewById(R.id.tvRepairShopAdmin);
                TextView tvUsername = (TextView) findViewById(R.id.textViewAdminUsername);

                tvRepairShopAdmin.setText(shop.getRepairShopName() + " Admin");
                tvUsername.setText(user.getString("full_name"));
            }
        });


    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);
        getCurrentUser();
        NewProblemsFragment fragment = new NewProblemsFragment();
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
                Intent intent1 = new Intent(AdminMainActivity.this, AddNewTechActivity.class);
                startActivity(intent1);
            }
        });

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

    public String getIssue(){
        return "problem";
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch(id){
            case R.id.nav_logout:
                ParseUser.logOut();
                finish();
                Intent intent = new Intent(AdminMainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                PrefManager pref = new PrefManager(getApplicationContext());
                pref.logout();
                break;
            case R.id.nav_services:
                AdminServicesFragment fragment = new AdminServicesFragment();
                android.support.v4.app.FragmentTransaction fragmentTransaction =
                        getSupportFragmentManager().beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putBoolean("new", true);
                fragment.setArguments(bundle);

                fragmentTransaction.replace(R.id.main_content, fragment);
                fragmentTransaction.commit();
                break;
            case R.id.nav_techs:
                TechFragment fragment2 = new TechFragment();
                android.support.v4.app.FragmentTransaction fragmentTransaction2 =
                        getSupportFragmentManager().beginTransaction();

                fragmentTransaction2.replace(R.id.main_content, fragment2);
                fragmentTransaction2.commit();
                break;
            case R.id.nav_profile:
                Intent intent1 = new Intent(AdminMainActivity.this, EditTechActivity.class);
                intent1.putExtra("user", ParseUser.getCurrentUser().getObjectId());
                startActivity(intent1);
                break;

            case R.id.nav_repair_shop:
                Intent intent2 = new Intent(AdminMainActivity.this, EditRepairShopFragment.class);
                startActivity(intent2);

                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
