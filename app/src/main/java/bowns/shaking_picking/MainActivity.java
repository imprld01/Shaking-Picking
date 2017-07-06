package bowns.shaking_picking;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import bowns.listener.AccelerometerListener;

public class MainActivity extends AppCompatActivity {

    private TextView tv_x;
    private TextView tv_y;
    private TextView tv_z;
    private TextView tv_n;

    private SensorManager sm;
    private AccelerometerListener al;

    private Handler handler;

    public static final int UPDATE_ACCURACY_VALUES_UI = 1;
    public static final int UPDATE_PICK_NUMBER_UI = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.findViews();

        this.handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {

                switch(msg.what){
                    case UPDATE_ACCURACY_VALUES_UI:
                        /* show the acc values */
                        Bundle paraPack = msg.getData();
                        tv_x.setText(paraPack.getString("X-VALUE"));
                        tv_y.setText(paraPack.getString("Y-VALUE"));
                        tv_z.setText(paraPack.getString("Z-VALUE"));
                        break;
                    case UPDATE_PICK_NUMBER_UI:
                        tv_n.setText(String.valueOf(msg.arg1));
                        break;
                }
            }
        };

        this.sm = (SensorManager)getSystemService(SENSOR_SERVICE);
        this.al = new AccelerometerListener(MainActivity.this, this.handler, this.tv_x, this.tv_y, this.tv_z, this.tv_n);
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

        this.tv_x = (TextView)findViewById(R.id.x_axis);
        this.tv_y = (TextView)findViewById(R.id.y_axis);
        this.tv_z = (TextView)findViewById(R.id.z_axis);
        this.tv_n = (TextView)findViewById(R.id.number);
    }
}
