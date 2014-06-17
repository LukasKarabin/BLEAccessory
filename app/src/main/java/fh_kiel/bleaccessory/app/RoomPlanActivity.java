package fh_kiel.bleaccessory.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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
        intent = intent = getIntent();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.roomplan_activity);

        String id = intent.getStringExtra("EXTRA_BEACON");
        Toast.makeText(getApplicationContext(),id,Toast.LENGTH_SHORT).show();

        new GetRoomData().execute();

        for(int i = 0; i < (7*13); i++){
            TableRow row = new TableRow(getApplicationContext());

            TextView entryView = new TextView(getApplicationContext());
            entryView.setBackgroundColor(0xff669900);

            row.addView(entryView);
        }
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


            String jsonStr = sh.makeServiceCall("http://149.222.134.187:3000/111111111111/schedule" , ServiceHandler.GET);

            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                try {

                    // Getting JSON Array node
                    JSONObject jsonEntry = new JSONObject(jsonStr);

                    JSONArray jsonEntries = jsonEntry.getJSONArray("entries");
                    for (int i = 0; i < jsonEntries.length(); i++) {

                        String title = jsonEntries.getJSONObject(i).getString("title");
                        int start = jsonEntries.getJSONObject(i).getInt("start_timestamp");
                        int end = jsonEntries.getJSONObject(i).getInt("end_timestamp");

                        ScheduleEntry entry = new ScheduleEntry(roomId, start, end, title);


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

                    case 2:
                        row = (TableRow) findViewById(R.id.rowmon);
                        break;
                    case 3:
                        row = (TableRow) findViewById(R.id.rowtu);
                        break;
                    case 4:
                        row = (TableRow) findViewById(R.id.rowwe);
                        break;
                    case 5:
                        row = (TableRow) findViewById(R.id.rowthu);
                        break;

                    case 6:
                        row = (TableRow) findViewById(R.id.rowfri);
                        break;

                }

                TextView entryView = new TextView(getApplicationContext());
                TableRow.LayoutParams params = new TableRow.LayoutParams();
                Integer span = entry.getDuration()/15;
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


