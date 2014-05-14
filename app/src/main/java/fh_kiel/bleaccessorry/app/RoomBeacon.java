package fh_kiel.bleaccessorry.app;

import java.util.List;

/**
 * Created by Lukas on 09.05.2014.
 */
public class RoomBeacon {
    private String mName;
    private int mSignal;
    private String mAddress;

    public RoomBeacon(String deviceName, String deviceAddress, int rssi) {
        mSignal = rssi;
        mAddress = deviceAddress;
        mName = deviceName;
    }

    public String getName() {
        return mName;
    }

    public int getSignal() {
        return mSignal;
    }

    public String getAddress() {
        return mAddress;
    }
}




