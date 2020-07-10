package mtech.com.visitortesting;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;

public class MyListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final ArrayList<String> maintitle;
    private final ArrayList<String> subtitle;
    private final ArrayList<String> vehicleNo;
    private final ArrayList<String> EDate;
    private final ArrayList<String> purpose;
    private final ArrayList<String> Act;

    public MyListAdapter(Activity context, ArrayList<String> maintitle, ArrayList<String> subtitle, ArrayList<String> vehicle, ArrayList<String> EDate, ArrayList<String> purpose, ArrayList<String> active) {
        super(context, R.layout.mylist, maintitle);
        // TODO Auto-generated constructor stub

        this.context = context;
        this.maintitle = maintitle;
        this.subtitle = subtitle;
        this.vehicleNo = vehicle;
        this.EDate = EDate;
        this.purpose = purpose;
        this.Act = active;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.mylist, null, true);
        TextView titleText = (TextView) rowView.findViewById(R.id.title);
        TextView subtitleText = (TextView) rowView.findViewById(R.id.subtitle);
        TextView vehNo = (TextView) rowView.findViewById(R.id.vNo);
        TextView EnDate = (TextView) rowView.findViewById(R.id.EDate);
        TextView prp = (TextView) rowView.findViewById(R.id.purpose);
        titleText.setText(maintitle.get(position));
//        Toast.makeText(context, ""+maintitle.size(), Toast.LENGTH_SHORT).show();
        subtitleText.setText(subtitle.get(position));
        vehNo.setText(vehicleNo.get(position));
        EnDate.setText(EDate.get(position));
        prp.setText(purpose.get(position));
        String a = Act.get(position);
//        Toast.makeText(context, ""+a+titleText.getText().toString(), Toast.LENGTH_SHORT).show();
        if (a.equalsIgnoreCase("2")){
            LinearLayout ll = (LinearLayout) rowView.findViewById(R.id.ll);
            ll.setBackgroundColor(Color.RED);
            titleText.setTextColor(Color.WHITE);
            subtitleText.setTextColor(Color.WHITE);
            vehNo.setTextColor(Color.WHITE);
            EnDate.setTextColor(Color.WHITE);
            prp.setTextColor(Color.WHITE);
        }
        else if(a.equalsIgnoreCase("4")){
            LinearLayout ll = (LinearLayout) rowView.findViewById(R.id.ll);
            ll.setBackgroundColor(Color.GREEN);
            titleText.setTextColor(Color.BLACK);
            subtitleText.setTextColor(Color.BLACK);
            vehNo.setTextColor(Color.BLACK);
            EnDate.setTextColor(Color.BLACK);
            prp.setTextColor(Color.BLACK);
        }
        return rowView;

    }

    ;
}  