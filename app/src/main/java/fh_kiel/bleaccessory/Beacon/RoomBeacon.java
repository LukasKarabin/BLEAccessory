package fh_kiel.bleaccessory.Beacon;

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

    @Override
    public String toString() {
        return "Beacon [Address=" + mAddress + ", Name=" + mName + "]";
    }
}





