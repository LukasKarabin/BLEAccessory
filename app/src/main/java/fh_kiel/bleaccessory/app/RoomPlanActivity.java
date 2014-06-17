package fh_kiel.bleaccessory.app;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;



public class RoomPlanActivity extends Activity {

    int roomId = 123;
    List<ScheduleEntry> entries = new ArrayList<ScheduleEntry>();

    Intent intent;// = getIntent();

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        intent = getIntent();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.roomplan_activity);

        new GetRoomData().execute();
    }

    /**
     * Async task class to get json by making HTTP call
     */
    private class GetRoomData extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            // Making a request to url and getting response


            String jsonStr = sh.makeServiceCall("http://149.222.134.13:3000/"+intent.getStringExtra("EXTRA_BEACON")+"/schedule" , ServiceHandler.GET);
            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                try {

                    // Getting JSON Array node
                    JSONObject jsonEntry = new JSONObject(jsonStr);

                    JSONArray jsonEntries = jsonEntry.getJSONArray("schedules");

                    DateFormat df = new SimpleDateFormat("HH:mm:ss");
                    Date start = new Date();
                    Date end = new Date();
                    String titleStr;
                    String timeStrStart;
                    String timeStrEnd;
                    int day = 1;
                    for (int i = 0; i < jsonEntries.length(); i++) {

                        titleStr = jsonEntries.getJSONObject(i).getString("title");
                        timeStrStart = jsonEntries.getJSONObject(i).getString("start");
                        timeStrEnd = jsonEntries.getJSONObject(i).getString("end");
                        try {
                            //day = 1;
                            start = df.parse(timeStrStart);
                            end = df.parse(timeStrEnd);
                            day = jsonEntries.getJSONObject(i).getInt("day");


                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        //Date date = new Date(jsonEntries.getJSONObject(i).getInt("date"));

                        ScheduleEntry entry = new ScheduleEntry(roomId, start, end, new Date(), day, titleStr);
                        entries.add(entry);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            /**
             * Updating parsed JSON data into ListView
             * */


            for(int i = 0; i < entries.size(); i++){
                TableRow row = new TableRow(getApplicationContext());

                ScheduleEntry entry = entries.get(i);

                switch(entry.getDay()){

                    case 1:
                        row = (TableRow) findViewById(R.id.rowmon);
                        break;
                    case 2:
                        row = (TableRow) findViewById(R.id.rowtu);
                        break;
                    case 3:
                        row = (TableRow) findViewById(R.id.rowwe);
                        break;
                    case 4:
                        row = (TableRow) findViewById(R.id.rowthu);
                        break;
                    case 5:
                        row = (TableRow) findViewById(R.id.rowfri);
                        break;
                    case 6:
                        row = (TableRow) findViewById(R.id.rowsat);
                        break;
                    case 7:
                        row = (TableRow) findViewById(R.id.rowsun);
                        break;
                }

                TextView entryView = new TextView(getApplicationContext());
                TableRow.LayoutParams params = new TableRow.LayoutParams();
                int span = (int)entry.getDuration()/15;
                params.span = span;
                params.column = entry.getColumn();
                entryView.setBackgroundColor(0xff669900);
                entryView.setLayoutParams(params);
                entryView.setText(entry.getTitle());
                row.addView(entryView);
            }
        }
    }
}


