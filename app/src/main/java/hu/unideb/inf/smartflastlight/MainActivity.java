package hu.unideb.inf.smartflastlight;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class MainActivity extends AppCompatActivity {
    SensorManager sensorManager;
    Sensor flashlightSensor;
    SensorEventListener flashlightEventListener;
    TextView lightText;
    long lastTime = 0;
    boolean isFlashOn = false;    
    CameraManager cameraManager;
    String cameraId;
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
                            toggleFlashlight();
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
        
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            cameraId = cameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            throw new RuntimeException(e);
        }
        

    }
    private void toggleFlashlight(){
        try {
            if (cameraManager != null && cameraId != null){
                isFlashOn = !isFlashOn;
                cameraManager.setTorchMode(cameraId,isFlashOn);
                lightText.setText("FlashLight " + (isFlashOn ? "ON" : "OFF"));

            }
        } catch (CameraAccessException e){
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sensorManager != null) {
            sensorManager.registerListener(flashlightEventListener, flashlightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(flashlightEventListener);
        }
    }


}