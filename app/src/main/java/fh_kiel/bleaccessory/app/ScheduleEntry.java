package fh_kiel.bleaccessory.app;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by casi on 10.06.14.
 */
public class ScheduleEntry {
    private int roomID;
    private Date start;
    private Date end;
    private int day;
    private Date date;
    private String title;

    ScheduleEntry(int roomID, Date start, Date end, Date date, int day, String title) {

        this.roomID = roomID;
        this.start = start;
        this.end = end;
        this.date = date;
        this.day = day;
        this.title = title;
    }


    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }


    public int getRoomID() {
        return roomID;
    }

    public int getDay(){

        return day;
    }

    public long getDuration(){
        long duration = ((end.getTime() - start.getTime())/60000);
        return duration;
    }

    public int getColumn(){
        SimpleDateFormat hourFormat = new SimpleDateFormat("HH");
        SimpleDateFormat minFormat = new SimpleDateFormat("mm");

        int hour = Integer.parseInt(hourFormat.format(start));
        int min = Integer.parseInt(minFormat.format(start));

        double column = (hour-8)*4 + (min/15) + 1;

        return ((int)Math.round(column));

    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
