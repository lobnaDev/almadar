package com.android.il.almadar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MaintenanceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance);

        Button engine =(Button) findViewById(R.id.Enginebutton);
        Button ac =(Button) findViewById(R.id.ACbutton);
        Button wheels =(Button) findViewById(R.id.wheelsbutton);
        Button breakers =(Button) findViewById(R.id.breakerbutton);




        engine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startIntent("engine");
            }
        });

        ac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startIntent("ac");
            }
        });

        wheels.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startIntent("wheels");
            }
        });

        breakers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startIntent("breakers");
            }
        });


        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }


    /**
     *  Starting the intent
     * @param type
     */
    private void startIntent(String type){
        Intent intent = new Intent(MaintenanceActivity.this, RepairShopsMapActivity.class);
        intent.putExtra("service", "maintenance");
        intent.putExtra("tool", type );

        startActivity(intent);

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
