package fh_kiel.bleaccessory.Beacon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import fh_kiel.bleaccessorry.app.R;

/**
 * Created by Lukas on 09.05.2014.
 */
public class BeaconAdapter extends ArrayAdapter<RoomBeacon> {

    public BeaconAdapter(Context context) {
        super(context, 0);
    }

    @Override
    public View getView(int index, View convertView, ViewGroup parent)
    {
        if(convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.beacon_item, parent, false);
        }
        RoomBeacon beacon = getItem(index);

        TextView nameView = (TextView) convertView.findViewById(R.id.text_name);
        nameView.setText(beacon.getName());

        TextView addressView = (TextView) convertView.findViewById(R.id.text_address);
        addressView.setText(beacon.getAddress());

        TextView rssiView = (TextView) convertView.findViewById(R.id.text_rssi);
        rssiView.setText(String.format("%ddBm", beacon.getSignal()));


        return convertView;
    }
}
