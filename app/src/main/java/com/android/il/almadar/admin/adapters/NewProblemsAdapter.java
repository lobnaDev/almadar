package com.android.il.almadar.admin.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.il.almadar.R;
import com.android.il.almadar.core.UserService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Labon on 19/3/2016.
 */
public class NewProblemsAdapter extends ArrayAdapter<UserService> {

    private Context context;
    private List<UserService> list;

    public NewProblemsAdapter(Context context, int resource, List<UserService> objects) {
        super(context, resource, objects);
        this.context = context;
        this.list = objects;
    }


    @Override
    public UserService getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE );
            convertView = inflater.inflate(R.layout.car_wash_list_item, parent, false);
        }

        UserService object = list.get(position);
        TextView tvCarType, tvCarModel, tvCarDate, tvStatus, tvStatusTextView;

        TextView tvCarInfo = (TextView) convertView.findViewById(R.id.textViewItemCT);
        TextView tvService = (TextView) convertView.findViewById(R.id.textViewItemCM);
        tvStatus = (TextView) convertView.findViewById(R.id.listItemStatus);
        tvStatusTextView = (TextView) convertView.findViewById(R.id.textViewStatusTitle);
        TextView textView = (TextView) convertView.findViewById(R.id.textViewAppDate);
        textView.setText("Date Requested: ");
        tvCarModel = (TextView) convertView.findViewById(R.id.listTechFullName);
        tvCarType = (TextView) convertView.findViewById(R.id.listTechUsername);
        tvCarDate = (TextView) convertView.findViewById(R.id.listCarTvDate);
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        tvCarInfo.setText("Car: ");

        if(object.getCar()!=null) {
            tvCarType.setText(object.getCar().getCarType() + " " + object.getCar().getCarModel());
            if(object.getServiceSubTool()!=null)
                tvCarModel.setText(object.getServiceName() + ": "+ object.getServiceTool() + ": " +object.getServiceSubTool());
            else
                tvCarModel.setText(object.getServiceName() + ": "+ object.getServiceTool());

            tvCarDate.setText(dateFormat.format(object.getServiceDate()));
            tvService.setText("Service: ");
            if(object.getServiceFinishDate()==null){
                tvStatus.setText("not complete");
            } else{
                tvStatus.setText("complete");
            }
            //tvStatus.setRawInputType();
        }

        return convertView;
    }
}
