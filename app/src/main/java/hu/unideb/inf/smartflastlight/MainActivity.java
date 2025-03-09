package hu.unideb.inf.smartflastlight;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    SensorManager sensorManager;
    Sensor flashlightSensor;
    SensorEventListener flashlightEventListener;
    TextView lightText;
    private long lastTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        lightText = findViewById(R.id.lightText);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        flashlightSensor = sensorManager.getDefaultSensor((Sensor.TYPE_ACCELEROMETER));

        if (flashlightSensor != null){
            flashlightEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
                    float x = event.values[0], y = event.values[1], z = event.values[2];
                    float acceleration = (float) Math.sqrt(x * x + y * y + z*z);
                    long timeNow = System.currentTimeMillis();

                    if (acceleration > 12){
                        if((timeNow - lastTime) > 1000){
                            lastTime = timeNow;
                            lightText.setText("Shake detected! Light On");
                        }
                    }
                }

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
            sensorManager.registerListener(flashlightEventListener,flashlightSensor,SensorManager.SENSOR_DELAY_NORMAL);

        } else {
            lightText.setText("Accelerometer not available");
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(flashlightEventListener, flashlightSensor,SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(flashlightEventListener);
    }


}