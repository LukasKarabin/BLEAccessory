package fh_kiel.bleaccessory.app;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by casi on 10.06.14.
 */
public class ScheduleEntry {
    private int roomID;
    private int startTime;
    private int endTime;
    private String title;

    ScheduleEntry(int roomID, int startTime, int endTime, String title) {

        this.roomID = roomID;
        this.startTime = startTime;
        this.endTime = endTime;
        this.title = title;
    }


    public void setRoomID(int roomID) {
        this.roomID = roomID;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public int getEndTime() {
        return endTime;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getRoomID() {
        return roomID;
    }

    public int getDay(){
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date((long)startTime*1000));
        int day = cal.get(Calendar.DAY_OF_WEEK);
        return day;
    }

    public int getDuration(){
        int duration = (endTime - startTime)/60;
        return duration;
    }

    public int getColumn(){
        SimpleDateFormat hourFormat = new SimpleDateFormat("HH");
        SimpleDateFormat minFormat = new SimpleDateFormat("mm");

        Date start = new Date((long)startTime*1000);
        int hour = Integer.parseInt(hourFormat.format(start));
        int min = Integer.parseInt(minFormat.format(start));

        double column = (hour-8)*4 + (min/15) + 1;

        return ((int)Math.round(column));

    }

}
