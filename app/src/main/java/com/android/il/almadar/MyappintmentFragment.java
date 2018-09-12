package com.android.il.almadar;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyappintmentFragment extends Fragment {


    private Button bProblem, bMaintenance, bCarWash;

    View view;
    private void init(){
        bProblem = (Button)view.findViewById(R.id.buttonProblem);
        bCarWash = (Button) view.findViewById(R.id.buttonAppintment);
        bMaintenance = (Button) view.findViewById(R.id.buttonMaintenance);

        bProblem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MaintenanceHistoryActivity.class);
                intent.putExtra("service", "problem");
                startActivity(intent);
            }
        });

        bCarWash.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AllCarWashAppointments.class);
                startActivity(intent);
            }
        });

        bMaintenance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MaintenanceHistoryActivity.class);
                intent.putExtra("service", "maintenance");
                startActivity(intent);

            }
        });
    }
    public MyappintmentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            // Inflate the layout for this fragment
            view = inflater
                    .inflate(R.layout.fragment_myappintment, container, false);


        } catch (InflateException e) {
			/* map is already there, just return view as it is */
            e.printStackTrace();
        }
        if(view!=null) {
            init();
            //  getCars();
        }
        return view;    }


}
