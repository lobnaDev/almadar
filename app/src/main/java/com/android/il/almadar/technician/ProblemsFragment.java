package com.android.il.almadar.technician;

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
import com.android.il.almadar.core.UserService;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class ProblemsFragment extends Fragment {

    private ListView lvMaintenanceHistory;
    private ArrayList<UserService> list = new ArrayList<UserService>();
    private MaintenanceAdapter adapter;
    View view;

    String issue;
    private  void init(){
        TechMainActivity activity = (TechMainActivity) getActivity();
        issue = activity.getIssue();
        lvMaintenanceHistory = (ListView) view.findViewById(R.id.listViewCars);
        TextView tvTitle = (TextView) view.findViewById(R.id.textViewPro);
        tvTitle.setText("Submitted " + issue.toUpperCase());

        lvMaintenanceHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserService service = list.get(position);
                Intent intent = new Intent(getActivity(), EditProblemActivity.class);
                intent.putExtra("id", service.getObject().getObjectId());
                startActivity(intent);
            }
        });
    }

    private void getListItems(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserService");
        query.whereEqualTo("Tech", ParseUser.getCurrentUser());
        query.whereEqualTo("Service",issue );
        query.include("Car");
        query.include("repair_shop");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> scoreList, ParseException e) {
                if (e == null) {
                    Log.e("Cars", "Retrieved " + scoreList.size() + " cars");
                    for(ParseObject object: scoreList){
                        UserService service = new UserService();
                        try {
                            service.setObject(object);
                            service.setCar((ParseObject) object.get("Car"));
                            service.setRepairShop((ParseObject)object.get("repair_shop"));
                            list.add(service);
                        } catch (Exception e1){
                            e1.printStackTrace();
                        }
                    }
                    adapter = new MaintenanceAdapter(getActivity(), R.layout.car_wash_list_item, list);
                    lvMaintenanceHistory.setAdapter(adapter);
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
            init();
            getListItems();
        }
        return view;
    }

}
