package com.android.il.almadar.admin;

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
import android.widget.TextView;

import com.android.il.almadar.R;
import com.android.il.almadar.admin.adapters.TechniciansAdapter;
import com.android.il.almadar.core.RepairShop;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class TechFragment  extends Fragment{

    private ListView listView;
    private ArrayList<ParseUser> list = new ArrayList<ParseUser>();
    private TechniciansAdapter adapter;
    View view;
    ParseUser user;
    RepairShop shop;

    private  void init(){
        AdminMainActivity activity = (AdminMainActivity) getActivity();
        listView = (ListView) view.findViewById(R.id.listViewCars);
        TextView tvTitle = (TextView) view.findViewById(R.id.textViewPro);
        tvTitle.setText("Repair shop Technicians");
     //   tvTitle.setText("Submitted " + issue.toUpperCase());

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ParseUser service = list.get(position);
                Intent intent = new Intent(getActivity(), EditTechActivity.class);
                intent.putExtra("user", service.getObjectId());
                startActivity(intent);
            }
        });
    }


    private void getCurrentUser(){
        ParseQuery<ParseUser> query = ParseQuery.getQuery("_User");
        query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.include("repair_shop");
        shop = new RepairShop();
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                user = objects.get(0);
                shop.setParseObject((ParseObject) objects.get(0).get("repair_shop"));
                getListItems(shop);
            }
        });


    }
    private void getListItems(RepairShop shop){
        list = new ArrayList<>();
//        query2.whereEqualTo("object_id", ((ParseObject)ParseUser.getCurrentUser().get("repair_shop")).getObjectId());
        ParseQuery<ParseUser> query = ParseQuery.getQuery("_User");
        query.whereEqualTo("role", "tech");
        query.whereEqualTo("repair_shop", shop.getParseObject());

        query.include("repair_shop");
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> scoreList, ParseException e) {
                if (e == null) {
                    Log.e("Users", "Retrieved " + scoreList.size() + " users");
                    for (ParseUser object : scoreList) {
                        list.add(object);
                        Log.e("Users", "Retrieved " + object.getUsername() + " users");
                    }

                } else {
                    Log.e("Users", "Error: " + e.getMessage());
                    e.printStackTrace();
                }

                adapter = new TechniciansAdapter(getActivity(), R.layout.tech_list_item, list);
                listView.setAdapter(adapter);

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
        if(view!=null) {
            init();
            getCurrentUser();
        }
        return view;
    }
}
