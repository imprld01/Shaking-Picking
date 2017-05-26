package bowns.listener;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Environment;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class AccelerometerListener implements SensorEventListener {

    private Context context;

    private TextView tv_x;
    private TextView tv_y;
    private TextView tv_z;
    private TextView tv_n;

    private double past_y;
    private double past_z;

    private long pastTime;

    private boolean noGap;

    private final static int SHAKE_THRESHOLD = 50;
    private final static String fName = "accValuesRecord.txt";

    public AccelerometerListener(Context context, TextView tv_x, TextView tv_y, TextView tv_z, TextView tv_n) {

        this.context = context;

        this.tv_x = tv_x;
        this.tv_y = tv_y;
        this.tv_z = tv_z;
        this.tv_n = tv_n;

        this.past_y = 0;
        this.past_z = 0;

        this.pastTime = 0;

        noGap = true;
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

        String xs = ((x >= 0) ? "+" : "") + String.valueOf(x);
        String ys = ((y >= 0) ? "+" : "") + String.valueOf(y);
        String zs = ((z >= 0) ? "+" : "") + String.valueOf(z);

        /* show the acc values */
        this.tv_x.setText(xs);
        this.tv_y.setText(ys);
        this.tv_z.setText(zs);

        /* save the acc values */
        StringBuilder sb = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss.SSS");
        sb.append(sdf.format(new Date())); sb.append("\t");
        sb.append(xs); sb.append("\t");
        sb.append(ys); sb.append("\t");
        sb.append(zs); sb.append("\n");

        try {
            /* save under internal storage */
            FileOutputStream fos = this.context.openFileOutput(AccelerometerListener.fName, this.context.MODE_APPEND);
            fos.write(sb.toString().getBytes());
            fos.close();

            /*
            Toast.makeText(this.context,
                    "saving at " + this.context.getFileStreamPath(AccelerometerListener.fName),
                    Toast.LENGTH_LONG).show();
            */

            /* save under external storage */
            File ef = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            File file = new File(ef, AccelerometerListener.fName);

            if (!this.isSdcardWritable())
                Toast.makeText(this.context, "sdcard isn't writable!", Toast.LENGTH_LONG).show();
            else {
                if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
                if (file.exists()) file.delete();

                OutputStream os = new FileOutputStream(file);
                os.write(sb.toString().getBytes());
                os.close();

                /*
                Toast.makeText(this.context,
                        "saving at " + file.toString(), Toast.LENGTH_LONG).show();
                */
            }
        }
        catch(IOException e) {
            Toast.makeText(this.context,
                    "saving file error :(", Toast.LENGTH_LONG).show();
        }
    }

    private void toPick() {

        Random rand = new Random();
        int result = rand.nextInt(100) + 1;
        this.tv_n.setText(String.valueOf(result));
    }

    private boolean isSdcardWritable() {

        String state = Environment.getExternalStorageState();
        if(state.equals(Environment.MEDIA_MOUNTED)) return true;
        else return false;
    }
}