package bowns.listener;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AccelerometerListener implements SensorEventListener {

    private Context context;

    private TextView tv_x;
    private TextView tv_y;
    private TextView tv_z;

    private final static String fName = "accValuesRecord.txt";

    public AccelerometerListener(Context context, TextView tv_x, TextView tv_y, TextView tv_z) {

        this.context = context;

        this.tv_x = tv_x;
        this.tv_y = tv_y;
        this.tv_z = tv_z;
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
            FileOutputStream fos = this.context.openFileOutput(AccelerometerListener.fName, this.context.MODE_APPEND);
            fos.write(sb.toString().getBytes());
            fos.close();
            
            Toast.makeText(this.context,
                    "saving at " + this.context.getFileStreamPath(AccelerometerListener.fName),
                    Toast.LENGTH_LONG).show();
        }
        catch(IOException e) {
            Toast.makeText(this.context,
                    "saving file error:(", Toast.LENGTH_LONG).show();
        }
    }
}