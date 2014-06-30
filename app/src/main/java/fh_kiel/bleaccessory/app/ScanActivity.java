package fh_kiel.bleaccessory.app;

// Imports
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import fh_kiel.bleaccessory.Beacon.BeaconAdapter;
import fh_kiel.bleaccessory.Beacon.RoomBeacon;
import java.util.HashMap;

// Class ScanActivity
//
// Description:
// Responsible for the Scan Overview, Communication with BLE Devices and Menu
public class ScanActivity extends Activity implements BluetoothAdapter.LeScanCallback {

    // Cass Variables
    private static final String TAG = "ScanActivity"; //Log Tag
    private BluetoothAdapter mBluetoothAdapter;
    private HashMap<String, RoomBeacon> mBeacons;
    private BeaconAdapter mAdapter;
    private Menu menuRef;

    // Function: fillDummy
    //
    // IN: void
    // OUT: void
    //
    // Description:
    // Generates a Dummy Beacon for testing purposes
    private void fillDummy()
    {
        RoomBeacon beacon = new RoomBeacon("Dummy", "11:11:11:11:11:11", -33);
        mHandler.sendMessage(Message.obtain(null, 0, beacon));
    }

    // Function: onCreate
    //
    // IN: savedInstanceState   If the activity is being re-initialized after previously being shut
    // down then this Bundle contains the data
    // OUT: void
    //
    // Description:
    // Called when the activity is first created. Initialises View.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setProgressBarIndeterminate(true);

        // We are going to display all the device beacons that we discover
        // in a list, using a custom adapter implementation
        ListView list = new ListView(this);
        mAdapter = new BeaconAdapter(this);
        list.setAdapter(mAdapter);
        setContentView(list);
        fillDummy(); //Create Beacon Dummy

        // Configure onItemClickListener
        // Overrides onItemClick
        //
        // Gets Beacon from clicked Position and put the Name without : into Extra.
        // Extra is accessible at next view
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent myIntent = new Intent(ScanActivity.this, RoomPlanActivity.class);
                myIntent.putExtra("EXTRA_BEACON", ((RoomBeacon)adapterView.getItemAtPosition(i)).getAddress().replace(":", ""));
                startActivity(myIntent);
            }
        });

        //Bluetooth in Android 4.3 is accessed via the BluetoothManager, rather than
        //the old static BluetoothAdapter.getInstance()
        BluetoothManager manager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        mBluetoothAdapter = manager.getAdapter();
        mBeacons = new HashMap<String, RoomBeacon>();
    }

    // Function: onCreateOptionsMenu
    //
    // IN: menu         The options menu in which you place your items.
    // OUT: boolean     You must return true for the menu to be displayed; if you return false it
    //                  will not be shown.
    //
    // Description:
    // Initialize the contents of the Activity's standard options menu. Menu
    // items will be placed in to menu.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.scan_menu, menu);
        this.menuRef = menu;
        return true;
    }

    // Function: onResume
    //
    // IN: void
    // OUT: void
    //
    // Description:
    // Called when the activity will start interacting with the user.
    @Override
    protected void onResume() {
        super.onResume();

        //We need to enforce that Bluetooth is first enabled, and take the
        //user to settings to enable it if they have not done so.
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            //Bluetooth is disabled
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBtIntent);
            finish();
            return;
        }

        //Check for Bluetooth LE Support. In production, our manifest entry will keep this
        //from installing on these devices, but this will allow test devices or other
        //sideloads to report whether or not the feature exists.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "No LE Support.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        //Begin scanning for LE devices
        startScan();
    }

    // Function: onPause
    //
    // IN: void
    // OUT: void
    //
    // Description:
    // Called when the system is about to start resuming a previous activity.
    @Override
    protected void onPause() {
        super.onPause();
        //Cancel any scans in progress
        mHandler.removeCallbacks(mStopRunnable);
        mHandler.removeCallbacks(mStartRunnable);
        mBluetoothAdapter.stopLeScan(this);
    }

    //Stop Scan for BLLE Devices
    private Runnable mStopRunnable = new Runnable() {
        @Override
        public void run() {
            stopScan();
        }
    };
    //Start Scan for BLE Devices
    private Runnable mStartRunnable = new Runnable() {
        @Override
        public void run() {
            startScan();
        }
    };

    // Function: startScan
    //
    // IN: void
    // OUT: void
    //
    // Description:
    // Start Scan, change menu title to Stop and show Progress
    private void startScan() {
        //Scan for devices advertising the thermometer service
        if(menuRef != null)
            menuRef.findItem(R.id.action_scan).setTitle("Stop");
        mBluetoothAdapter.startLeScan(this);
        setProgressBarIndeterminateVisibility(true);
        mHandler.postDelayed(mStopRunnable, 5000);
    }

    // Function: StopScan
    //
    // IN: void
    // OUT: void
    //
    // Description:
    // Stop Scan, change menu title to Stop and stop Progress
    private void stopScan() {
        if(menuRef != null)
            menuRef.findItem(R.id.action_scan).setTitle("Scan");
        mBluetoothAdapter.stopLeScan(this);
        setProgressBarIndeterminateVisibility(false);
    }

    //BluetoothAdapter.LeScanCallback

    // Function: onLeScan
    //
    // IN:  device      Identifies the remote device
    //      rssi	    The RSSI value for the remote device as reported by the Bluetooth hardware.
    //                  0 if no RSSI value is available.
    //      scanRecord	The content of the advertisement record offered by the remote device.
    // OUT: void
    //
    // Description:
    // Callback reporting an LE device found during a device scan. Beacon will be saved here
    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        Log.i(TAG, "New LE Device: " + device.getName() + " @ " + rssi);
        Log.i(TAG, "rawData: " + scanRecord.toString());
        //Create a new beacon from the list of obtains AD structures
        //and pass it up to the main thread
        RoomBeacon beacon = new RoomBeacon(device.getName(), device.getAddress(), rssi);
        mHandler.sendMessage(Message.obtain(null, 0, beacon));
    }

    // We have a Handler to process scan results on the main thread,
    // add them to our list adapter, and update the view
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            RoomBeacon beacon = (RoomBeacon) msg.obj;
            mBeacons.put(beacon.getAddress(), beacon);
            mAdapter.setNotifyOnChange(false);
            mAdapter.clear();
            mAdapter.addAll(mBeacons.values());
            mAdapter.notifyDataSetChanged();
        }
    };

    // Function: onOptionsItemSelected
    //
    // IN: item         The menu item that was selected.
    // OUT: boolean     Return false to allow normal menu processing to proceed,
    //                  true to consume it here.
    //
    // Description:
    // This hook is called whenever an item in your options menu is selected.
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_scan:
                if (item.getTitle().equals("Scan")) // Start scan, if Scan allowed
                {
                    startScan();
                }
                else                                // otherwise stop scan
                {
                    stopScan();
                }
                return(true);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

