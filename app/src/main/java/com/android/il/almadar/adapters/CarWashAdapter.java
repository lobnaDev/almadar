package com.android.il.almadar.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.il.almadar.R;
import com.android.il.almadar.core.CarWash;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Labon on 31/12/2015.
 */
public class CarWashAdapter extends ArrayAdapter<CarWash> {

    Context context;
    List<CarWash> list;

    public CarWashAdapter(Context context, int resource, List<CarWash> objects) {
        super(context, resource, objects);
        this.context = context;
        this.list = objects;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public CarWash getItem(int position) {
        return list.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE );
            convertView = inflater.inflate(R.layout.car_wash_list_item, parent, false);
        }

        CarWash object = list.get(position);
        TextView tvCarType, tvCarModel, tvCarDate, tvStatus, tvStatusTextView;

        tvCarModel = (TextView) convertView.findViewById(R.id.listTechFullName);
        tvCarType = (TextView) convertView.findViewById(R.id.listTechUsername);
        tvCarDate = (TextView) convertView.findViewById(R.id.listCarTvDate);

        tvStatus = (TextView) convertView.findViewById(R.id.listItemStatus);
        tvStatusTextView = (TextView) convertView.findViewById(R.id.textViewStatusTitle);

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        if(object.getCar()!=null) {
            tvCarModel.setText(object.getCar().getCarModel());
            tvCarType.setText(object.getCar().getCarType());
            tvCarDate.setText(dateFormat.format(object.getAppointmentDate()));
            tvStatus.setVisibility(View.GONE);
            tvStatusTextView.setVisibility(View.GONE);
        }


        return convertView;
    }
}
