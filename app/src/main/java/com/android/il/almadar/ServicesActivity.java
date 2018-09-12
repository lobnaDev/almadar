package com.android.il.almadar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class ServicesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services);

        Button carMainten =(Button) findViewById(R.id.maintebutton);
        carMainten.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ServicesActivity.this, MaintenanceActivity.class);
                startActivity(intent);
            }
        });
        Button carWash =(Button) findViewById(R.id.washbutton);
        carWash.setVisibility(View.GONE);
        carWash.setEnabled(false);
        carWash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ServicesActivity.this, RepairShopsMapActivity.class);
                intent.putExtra("service", "wash");
                startActivity(intent);
            }
        });
        Button sendProblem =(Button) findViewById(R.id.problembutton);
        sendProblem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ServicesActivity.this, RepairShopsMapActivity.class);
                intent.putExtra("service", "problem");
                startActivity(intent);
            }
        });

        Button petrolB = (Button) findViewById(R.id.bpetrolStation);
        petrolB.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ServicesActivity.this, NearestGasStation.class);
                startActivity(intent);
            }
        });

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }


}
