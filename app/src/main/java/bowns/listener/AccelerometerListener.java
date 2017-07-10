package bowns.listener;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import java.util.Random;

import bowns.shaking_picking.MainActivity;
import bowns.thread.SaveAccuracyValues;

public class AccelerometerListener implements SensorEventListener {

    private Context context;
    private Handler mainHandler;

    private double past_y;
    private double past_z;

    private long pastTime;

    private boolean noGap;

    private final static int SHAKE_THRESHOLD = 50;

    public AccelerometerListener(Context context, Handler mainHandler) {

        this.context = context;
        this.mainHandler = mainHandler;

        this.past_y = 0;
        this.past_z = 0;

        this.pastTime = 0;

        this.noGap = true;
    }

    @Override
    public void finalize() {

        try {
            super.finalize();
        }
        catch(Throwable e) {
            Toast.makeText(this.context, "finalize exception :(", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    public void onSensorChanged(SensorEvent event) {

        /* get sensor by event, and get the sensor info by this obj */
        // Sensor acc = event.sensor;

        float [] gValues = event.values;

        double x = Math.round(gValues[0] * 100) / 100.0;
        double y = Math.round(gValues[1] * 100) / 100.0;
        double z = Math.round(gValues[2] * 100) / 100.0;

        this.shakeDetect(y, z);

        Bundle paraPack = new Bundle();
        paraPack.putString("X-VALUE", ((x >= 0) ? "+" : "") + String.valueOf(x));
        paraPack.putString("Y-VALUE", ((y >= 0) ? "+" : "") + String.valueOf(y));
        paraPack.putString("Z-VALUE", ((z >= 0) ? "+" : "") + String.valueOf(z));
        Message m = new Message();
        m.what = MainActivity.UPDATE_ACCURACY_VALUES_UI;
        m.setData(paraPack);
        this.mainHandler.sendMessage(m);

        new SaveAccuracyValues(this.context, paraPack).start();
    }

    private void shakeDetect(double y, double z) {

        /* determine it's shaking or not */
        long current = System.currentTimeMillis();
        if (this.pastTime != 0 && (current - this.pastTime) > 36) { // sampling every 36ms
            double diff = Math.abs(y - this.past_y) + Math.abs(z - this.past_z);

            if (diff > AccelerometerListener.SHAKE_THRESHOLD){
                if(this.noGap) {
                    this.toPick();
                    this.noGap = false;
                }
                else this.noGap = true;
            }

            this.past_y = y;
            this.past_z = z;
        }
        this.pastTime = current;
    }

    private void toPick() {

        Message m = new Message();
        m.arg1 = new Random().nextInt(100) + 1;
        m.what = MainActivity.UPDATE_PICK_NUMBER_UI;
        this.mainHandler.sendMessage(m);
    }
}