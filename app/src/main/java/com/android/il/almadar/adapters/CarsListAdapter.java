package com.android.il.almadar.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.il.almadar.R;
import com.android.il.almadar.core.Car;

import java.util.List;


public class CarsListAdapter extends ArrayAdapter<Car> {


    List<Car> list;
    Context context;

    public CarsListAdapter(Context context, int resource, List<Car> objects) {
        super(context, resource, objects);
        this.list = objects;
        this.context = context;
    }

    @Override
    public int getCount() {
        return this.list.size();
    }

    @Override
    public Car getItem(int position) {
        return list.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE );
            convertView = inflater.inflate(R.layout.cars_list_item, parent, false);
        }

        Car object = list.get(position);
        TextView tvCarType, tvCarModel, tvCarYear;

        tvCarModel = (TextView) convertView.findViewById(R.id.listTechFullName);
        tvCarType = (TextView) convertView.findViewById(R.id.listTechUsername);
        tvCarYear = (TextView) convertView.findViewById(R.id.listCarTvYear);

        tvCarModel.setText(object.getCarModel());
        tvCarType.setText(object.getCarType());
        tvCarYear.setText(object.getCarYear());
        return convertView;
    }


}
