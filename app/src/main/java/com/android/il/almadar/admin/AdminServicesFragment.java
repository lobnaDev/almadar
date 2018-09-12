package com.android.il.almadar.admin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.il.almadar.NearestGasStation;
import com.android.il.almadar.R;
import com.android.il.almadar.RepairShopsMapActivity;


public class AdminServicesFragment extends Fragment {

    View view;
    private void init(View view){
        Button carMainten =(Button) view.findViewById(R.id.maintebutton);
        carMainten.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AdminProblemsActivity.class);
                intent.putExtra("service", "maintenance");
                startActivity(intent);
            }
        });
        Button carWash =(Button) view.findViewById(R.id.washbutton);
        carWash.setEnabled(false);
        carWash.setVisibility(View.GONE);
        carWash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RepairShopsMapActivity.class);
                intent.putExtra("service", "wash");
                startActivity(intent);
            }
        });
        Button sendProblem =(Button) view.findViewById(R.id.problembutton);
        sendProblem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AdminProblemsActivity.class);
                intent.putExtra("service", "problem");

                startActivity(intent);
            }
        });

        Button petrolB = (Button) view.findViewById(R.id.bpetrolStation);
        petrolB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NearestGasStation.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            // Inflate the layout for this fragment
            view = inflater
                    .inflate(R.layout.admin_services_fragment, container, false);


        } catch (InflateException e) {
			/* map is already there, just return view as it is */
            e.printStackTrace();
        }
        if(view!=null) {
            init(view);

        }
        return view;
    }
}
