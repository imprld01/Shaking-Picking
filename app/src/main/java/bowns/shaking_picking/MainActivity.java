package bowns.shaking_picking;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import bowns.listener.AccelerometerListener;

public class MainActivity extends AppCompatActivity {

    private TextView tv_x;
    private TextView tv_y;
    private TextView tv_z;

    private SensorManager sm;
    private AccelerometerListener al;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.findViews();

        this.sm = (SensorManager)getSystemService(SENSOR_SERVICE);
        this.al = new AccelerometerListener(MainActivity.this, this.tv_x, this.tv_y, this.tv_z);
    }

    @Override
    protected void onResume() {

        super.onResume();

        /* get total sensor list that android supports */
        //this.sm.getSensorList(Sensor.TYPE_ALL).size();

        Sensor acc = this.sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.sm.registerListener(this.al, acc, this.sm.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {

        super.onPause();

        this.sm.unregisterListener(this.al);
    }

    private void findViews() {

        TextView x_label = (TextView)findViewById(R.id.x_axis_label);
        TextView y_label = (TextView)findViewById(R.id.y_axis_label);
        TextView z_label = (TextView)findViewById(R.id.z_axis_label);

        this.tv_x = (TextView)findViewById(R.id.x_axis);
        this.tv_y = (TextView)findViewById(R.id.y_axis);
        this.tv_z = (TextView)findViewById(R.id.z_axis);

        x_label.setTextSize(25);
        y_label.setTextSize(25);
        z_label.setTextSize(25);
        this.tv_x.setTextSize(25);
        this.tv_y.setTextSize(25);
        this.tv_z.setTextSize(25);
    }
}
