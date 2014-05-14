package fh_kiel.bleaccessorry.app;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;


public class MainActivity extends Activity implements BluetoothAdapter.LeScanCallback {

    private TextView out;
    private Button ScanButton;

    private final static int REQUEST_ENABLE_BT = 1;

    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;

    private HashMap<String, RoomBeacon> mBeacons;
    private BeaconAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        out = (TextView) findViewById(R.id.textView2);
        //ScanButton = (Button) findViewById(R.id.button);

        // Initializes Bluetooth adapter.
        mBluetoothAdapter = ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        mAdapter = new BeaconAdapter(this);
        //list.setAdapter(mAdapter);
        mBeacons = new HashMap<String, RoomBeacon>();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //myText.append("Resume\n");

        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        /* Check for BLE support */
        if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "No LE Support, sorry.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }

    @Override //deny 1:0 allow 1:-1
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        //Close App if deny BLE Connection
        if (resultCode == 0)
        {
            super.finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Cancel any scans in progress
        mHandler.removeCallbacks(mStopRunnable);
        mHandler.removeCallbacks(mStartRunnable);
        mBluetoothAdapter.stopLeScan(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
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
        //Scan for devices
        mBluetoothAdapter.startLeScan(this);
        /* we can add a list of device UUIDs to filter the results for only those devices with the
           UUIDs we are looking for.
         */

        //setProgressBarIndeterminate(true);
        //mHandler.postDelayed(mStopRunnable, 5000);
    }

    private void stopScan() {
        mBluetoothAdapter.stopLeScan(this);
        setProgressBarIndeterminate(false);
        mHandler.postDelayed(mStartRunnable, 2500);
    }

    /* BluetoothAdapter.LeScanCallback */
    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        Log.i("1", "New LE Device: " + device.getName() + " @ " + rssi);
        /*
        List<AdRecord> records = AdRecord.parseScanRecord(scanRecord);
        if(records.size() == 0) {
            Log.i("1", "Scan Record Empty");
        } else {
            Log.i("1", "Scan Record: " + TextUtils.join(",", records));
        }*/

        RoomBeacon beacon = new RoomBeacon(device.getName(), device.getAddress(), rssi);
        mHandler.sendMessage(Message.obtain(null, 0, beacon));
    }

    /* Handler to process scan results on the main Thread, add them to the list adapter,
       and update the view
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

    public void onClickTestScan(View v){
        startScan();
        out.append("Button Pressed\n");
    }

    @Override
    protected void onStart() {
        super.onStart();
        //myText.append("Start\n");
    }

    @Override
    protected void onStop() {
        super.onStop();
       // myText.append("Stop\n");
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        //myText.append("Restart\n");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //myText.append("Destroy\n");
    }
}
