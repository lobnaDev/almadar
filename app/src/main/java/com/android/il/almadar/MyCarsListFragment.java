package com.android.il.almadar;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.il.almadar.adapters.CarsListAdapter;
import com.android.il.almadar.core.Car;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyCarsListFragment extends Fragment {

    ListView list;
    View view;
    List<Car> carsList;
    CarsListAdapter adapter;
    // initialize
    private void init(){
        list =(ListView) view.findViewById(R.id.listViewCars);
        carsList = new ArrayList<Car>();
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MyCarsListFragment.this.getActivity(), EditCarActivity.class);
                intent.putExtra("car_id", carsList.get(position).getObject().getObjectId());
                startActivity(intent);
            }
        });

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Car");
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> scoreList, ParseException e) {
                if (e == null) {
                    Log.e("Cars", "Retrieved " + scoreList.size() + " cars");
                    for(ParseObject object: scoreList){
                        Car car = new Car("Car");
                        car.setObject(object);
                        carsList.add(car);
                    }
                    adapter = new CarsListAdapter(getActivity(), R.layout.cars_list_item, carsList);
                    list.setAdapter(adapter);
                } else {
                    Log.e("Cars", "Error: " + e.getMessage());
                    e.printStackTrace();
                }
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
                    .inflate(R.layout.fragment_my_car, container, false);


        } catch (InflateException e) {
			/* map is already there, just return view as it is */
            e.printStackTrace();
        }
        if(view!=null)
            init();
        return view;
    }


}
