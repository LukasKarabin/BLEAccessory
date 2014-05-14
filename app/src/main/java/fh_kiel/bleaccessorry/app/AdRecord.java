package fh_kiel.bleaccessorry.app;

/**
 * Created by Lukas on 09.05.2014.
 */
        import java.util.ArrayList;
        import java.util.Arrays;
        import java.util.List;

/**
 * Created by Hans on 08.05.2014.
 */
public class AdRecord {
    private int mLength;
    private int mType;
    private byte[] mData;

    public AdRecord(int length, int type, byte[] data)
    {
        mLength = length;
        mType = type;
        mData = data;
    }

    public static List<AdRecord> parseScanRecord(byte[] scanRecord) {
        List<AdRecord> records = new ArrayList<AdRecord>();
        int index = 0;
        while(index < scanRecord.length) {
            int length = scanRecord[index++];
            if(length == 0) break;
            int type = scanRecord[index];
            if(type == 0) break;

            byte[] data = Arrays.copyOfRange(scanRecord, index+1, index+length);

            records.add(new AdRecord(length, type, data));
        }

        return records;
    }

    public int getType() {
        return mType;
    }

    public static String getName(AdRecord nameRecord) {
        return new String(nameRecord.mData);
    }
}
