package fh_kiel.bleaccessory.Beacon;

// Class RoomBeacon
//
// Description:
// Used for Room Beacon Objects.
public class RoomBeacon {
    private String mName;       // Device Name
    private int mSignal;        // RSSI
    private String mAddress;    // MAC-Address

    // Constructor
    public RoomBeacon(String deviceName, String deviceAddress, int rssi) {
        mSignal = rssi;
        mAddress = deviceAddress;
        mName = deviceName;
    }

    // Getter
    public String getName() {
        return mName;
    }

    public int getSignal() {
        return mSignal;
    }

    public String getAddress() {
        return mAddress;
    }

    // Function toString
    //
    // Description:
    // Allows to print out a Beacon
    @Override
    public String toString() {
        return "Beacon [Address=" + mAddress + ", Name=" + mName + "]";
    }
}





