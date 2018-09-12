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
import com.android.il.almadar.adapters.MaintenanceAdapter;
import com.android.il.almadar.core.RepairShop;
import com.android.il.almadar.core.UserService;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;



public class NewProblemsFragment extends Fragment {

    private ListView lvMaintenanceHistory;
    private ArrayList<UserService> list = new ArrayList<UserService>();
    private MaintenanceAdapter adapter;
    View view;

    String issue;



    private  void init(){
        AdminMainActivity activity = (AdminMainActivity) getActivity();
        list = new ArrayList<>();
        issue = activity.getIssue();
        lvMaintenanceHistory = (ListView) view.findViewById(R.id.listViewCars);
        TextView tvTitle = (TextView) view.findViewById(R.id.textViewPro);
        tvTitle.setText("Submitted " + issue.toUpperCase());

        lvMaintenanceHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserService service = list.get(position);
                Intent intent = new Intent(getActivity(), EditNewProblemActivity.class);
                intent.putExtra("id", service.getObject().getObjectId());
                startActivity(intent);
            }
        });
    }

    ParseUser user;
    RepairShop shop;

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
        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserService");
        query.whereEqualTo("Service", issue);
        query.whereEqualTo("repair_shop", shop.getParseObject());
        Log.e("Before Done", "Retrieved " + list.size() + " cars");
        //       shop.setParseObject((ParseObject) ParseUser.getCurrentUser().get("repair_shop"));
        query.include("Car");
        query.include("repair_shop");
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> scoreList, ParseException e) {
                if (e == null) {
                    Log.e("ProblemFragment", "Retrieved " + scoreList.size() + " cars");
                    for(ParseObject object: scoreList){
                            if (object.get("Tech") == null) {
                                try {
                                    UserService service = new UserService();
                                    service.setObject(object);
                                    service.setCar((ParseObject) object.get("Car"));
                                    service.setRepairShop((ParseObject) object.get("repair_shop"));
                                    list.add(service);
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }
                            }

                    }

                    Log.e("Size", list.size()+"");

                    try {
                        adapter = new MaintenanceAdapter(getActivity(), R.layout.car_wash_list_item, list);
                        lvMaintenanceHistory.setAdapter(adapter);

                    }catch (Exception e1){
                        e1.printStackTrace();
                    }
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
        if(view!=null) {
            try {
                init();
                getCurrentUser();
                Log.e("Create NewFrag", list.size() + " list");
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
         //   init();
            list = new ArrayList<>();

            getCurrentUser();
            Log.e("Resume NewFrag", list.size()+" list");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
