package fh_kiel.bleaccessorry.app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.util.HashMap;

public class BeaconView extends View {

    private static final float RADIUS = 25f;

    private static final float MAX_SIGNAL = -50;
    private static final float MIN_SIGNAL = -100;

    private HashMap<String, Integer> mPositions;
    private HashMap<String, RoomBeacon> mBeacons;

    private int mDrawRadius;
    private Paint mCirclePaint;
    private TextPaint mTextPaint;

    public BeaconView(Context context) {
        this(context, null);
    }

    public BeaconView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BeaconView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mPositions = new HashMap<String, Integer>();
        mBeacons = new HashMap<String, RoomBeacon>();

        mDrawRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, RADIUS,
                getResources().getDisplayMetrics());

        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setStyle(Paint.Style.FILL);

        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextSize(mDrawRadius);
    }

    public void updateBeacon(RoomBeacon beacon) {
        if (!mPositions.containsKey(beacon.getAddress())) {
            //Randomize x-position
            int random = (int)(Math.random() * getWidth());
            int clamped = Math.max(mDrawRadius, Math.min(getWidth() - mDrawRadius, random));
            mPositions.put(beacon.getAddress(), clamped);
        }

        mBeacons.put(beacon.getAddress(), beacon);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //Draw any backgrounds
        super.onDraw(canvas);

        //Draw user
        mCirclePaint.setColor(Color.BLACK);
        canvas.drawCircle(getWidth() / 2, getHeight(), mDrawRadius, mCirclePaint);
    }
}