package fh_kiel.bleaccessory.Beacon;

// Imports
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import fh_kiel.bleaccessory.app.R;

// Class BeaconAdapter
//
// Description:
// Creates a Room Beacon Cell and fills it with information
public class BeaconAdapter extends ArrayAdapter<RoomBeacon> {

    public BeaconAdapter(Context context) {
        super(context, 0);
    }

    // Function: getView
    //
    // IN:  position	    The position of the item within the adapter's data set of the item whose view we want
    //      convertView	    The old view to reuse, if possible
    //      parent	        The parent that this view will eventually be attached to
    // OUT: View            corresponding to the data at the specified position
    //
    // Description:
    // Get a View that displays the data at the specified position in the data set
    @Override
    public View getView(int index, View convertView, ViewGroup parent)
    {
        if(convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.scan_activity, parent, false);
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
