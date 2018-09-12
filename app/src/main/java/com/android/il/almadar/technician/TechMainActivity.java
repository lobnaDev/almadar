package com.android.il.almadar.technician;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
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
import com.android.il.almadar.admin.EditTechActivity;
import com.android.il.almadar.utils.PrefManager;
import com.parse.ParseUser;

public class TechMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    String issue;


    TextView tvUsername;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, MessageService.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tech_main);
        ProblemsFragment fragment = new ProblemsFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_content, fragment);
        fragmentTransaction.commit();
        issue = "problem";
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        tvUsername = (TextView) findViewById(R.id.textViewUser);
        ParseUser user = ParseUser.getCurrentUser();
        tvUsername.setText(user.getUsername());
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_problem) {
            // Handle the problem action
            issue = "problem";
            ProblemsFragment fragment = new ProblemsFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.main_content,fragment );
            fragmentTransaction.commit();
        } else if (id == R.id.nav_maintain) {
            issue = "maintenance";
            ProblemsFragment fragment = new ProblemsFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.main_content,fragment );
            fragmentTransaction.commit();
        } else if (id == R.id.nav_appointment) {

        } else if (id == R.id.nav_logout) {
            ParseUser.getCurrentUser().logOut();
            finish();
            PrefManager pref = new PrefManager(getApplicationContext());
            pref.logout();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        } else if (id == R.id.nav_share) {
                Intent intent1 = new Intent(TechMainActivity.this, EditTechActivity.class);
                intent1.putExtra("user", ParseUser.getCurrentUser().getObjectId());
                startActivity(intent1);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }


    // getting the issue
    public String getIssue(){
        return this.issue;
    }
}
