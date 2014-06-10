package fh_kiel.bleaccessory.app;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
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

public class ScanActivity extends Activity implements BluetoothAdapter.LeScanCallback {
    private static final String TAG = "ScanActivity";

    private BluetoothAdapter mBluetoothAdapter;
    /* Collect unique devices discovered, keyed by address */
    private HashMap<String, RoomBeacon> mBeacons;
    private BeaconAdapter mAdapter;
    private Menu menuRef;

    private void fillDummy()
    {
        RoomBeacon beacon = new RoomBeacon("Dummy", "11:11:11:11:11:11", -33);
        mHandler.sendMessage(Message.obtain(null, 0, beacon));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setProgressBarIndeterminate(true);

        /*
         * We are going to display all the device beacons that we discover
         * in a list, using a custom adapter implementation
         */
        ListView list = new ListView(this);
        mAdapter = new BeaconAdapter(this);
        list.setAdapter(mAdapter);
        setContentView(list);

        fillDummy();

        list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                String str= "Selected test";
                Toast.makeText(getApplicationContext(),str,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String str2= "Click : "+i;
                 str2=  ((RoomBeacon)adapterView.getItemAtPosition(i)).getName() + " " + ((RoomBeacon)adapterView.getItemAtPosition(i)).getAddress();
                Toast.makeText(getApplicationContext(),str2,Toast.LENGTH_SHORT).show();

                Intent intenttest = new Intent(ScanActivity.this, RoomPlanActivity.class);
                intenttest.putExtra("EXTRA_BEACON", ((RoomBeacon)adapterView.getItemAtPosition(i)).getAddress());
                startActivity(intenttest);
            }
        });

        /*
         * Bluetooth in Android 4.3 is accessed via the BluetoothManager, rather than
         * the old static BluetoothAdapter.getInstance()
         */
        BluetoothManager manager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        mBluetoothAdapter = manager.getAdapter();

        mBeacons = new HashMap<String, RoomBeacon>();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.scan_menu, menu);
        this.menuRef = menu;
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*
         * We need to enforce that Bluetooth is first enabled, and take the
         * user to settings to enable it if they have not done so.
         */
        //TODO Das muss wieder rein
       /* if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            //Bluetooth is disabled
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBtIntent);
            finish();
            return;
        }*/

        /*
         * Check for Bluetooth LE Support.  In production, our manifest entry will keep this
         * from installing on these devices, but this will allow test devices or other
         * sideloads to report whether or not the feature exists.
         */
        //TODO Das muss wieder rein
        /*if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "No LE Support.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }*/

        //Begin scanning for LE devices
        startScan();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Cancel any scans in progress
        mHandler.removeCallbacks(mStopRunnable);
        mHandler.removeCallbacks(mStartRunnable);
        mBluetoothAdapter.stopLeScan(this);
    }

    private Runnable mStopRunnable = new Runnable() {
        @Override
        public void run() {
            stopScan();
        }
    };
    private Runnable mStartRunnable = new Runnable() {
        @Override
        public void run() {
            startScan();
        }
    };

    private void startScan() {
        //Scan for devices advertising the thermometer service
        if(menuRef != null)
            menuRef.findItem(R.id.action_scan).setTitle("Stop");
        mBluetoothAdapter.startLeScan(this);
        setProgressBarIndeterminateVisibility(true);
        mHandler.postDelayed(mStopRunnable, 5000);
    }

    private void stopScan() {
        if(menuRef != null)
            menuRef.findItem(R.id.action_scan).setTitle("Scan");
        mBluetoothAdapter.stopLeScan(this);
        setProgressBarIndeterminateVisibility(false);
    }

    /* BluetoothAdapter.LeScanCallback */

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        Log.i(TAG, "New LE Device: " + device.getName() + " @ " + rssi);
        Log.i(TAG, "rawData: " + scanRecord.toString());
        /*
         * We need to parse out of the AD structures from the scan record
         */
        /*
        /**

        List<AdRecord> records = AdRecord.parseScanRecord(scanRecord);
        if (records.size() == 0) {
            Log.i(TAG, "Scan Record Empty");
        } else {
            Log.i(TAG, "Scan Record: "
                    + TextUtils.join(",", records));
        }*/

        /*
         * Create a new beacon from the list of obtains AD structures
         * and pass it up to the main thread
         */
        RoomBeacon beacon = new RoomBeacon(device.getName(), device.getAddress(), rssi);
        mHandler.sendMessage(Message.obtain(null, 0, beacon));
    }

    /*
     * We have a Handler to process scan results on the main thread,
     * add them to our list adapter, and update the view
     */
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_scan:
                if (item.getTitle().equals("Scan"))
                {
                    startScan();
                }
                else
                {
                    stopScan();
                }
                return(true);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

