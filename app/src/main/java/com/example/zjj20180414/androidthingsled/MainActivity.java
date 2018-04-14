package com.example.zjj20180414.androidthingsled;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import com.google.android.things.contrib.driver.button.Button;
import com.google.android.things.contrib.driver.button.ButtonInputDriver;
import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManager;

import java.io.IOException;

/**
 * Skeleton of an Android Things activity.
 * <p>
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 * <p>
 * <pre>{@code
 * PeripheralManagerService service = new PeripheralManagerService();
 * mLedGpio = service.openGpio("BCM6");
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
 * mLedGpio.setValue(true);
 * }</pre>
 * <p>
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 * @see <a href="https://github.com/androidthings/contrib-drivers#readme">https://github.com/androidthings/contrib-drivers#readme</a>
 */
public class MainActivity extends Activity {
    
    private Gpio mLedGpio;
    private ButtonInputDriver mButtonInputerDriver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        //使用PeripheralManager列出可用的GPIO端口
        PeripheralManager pio = PeripheralManager.getInstance();

        try {
            mLedGpio = pio.openGpio(BoardDefaults.getGPIOForLED());//获取设备型号的LED的GPIO端口
            mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);//设
        } catch (IOException e) {
            Log.e(String.valueOf(MainActivity.this), "led GPIO口错误", e);
        }

        try {
            mButtonInputerDriver = new ButtonInputDriver(
                    BoardDefaults.getGPIOForButton(),//获取设备型号的LED的GPIO端口
                    Button.LogicState.PRESSED_WHEN_LOW,//
                    KeyEvent.KEYCODE_SPACE);//按键事件，判断按键状态
            mButtonInputerDriver.register();//注册button
        } catch (IOException e) {
            Log.e(String.valueOf(MainActivity.this), "button GPIO口错误", e);
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_SPACE){
            //关闭LED灯
            setLedValue(false);
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_SPACE){
            //打开LED灯
            setLedValue(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void setLedValue(boolean ledValue) {
        try{
            mLedGpio.setValue(ledValue);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    protected void onStop() {
        if (mButtonInputerDriver != null){
            mButtonInputerDriver.unregister();
        }
        try{
            mButtonInputerDriver.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mButtonInputerDriver = null;
        }
        
        if (mLedGpio != null){
            try {
                mLedGpio.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                mLedGpio = null;
            }
        }
        super.onStop();
    }
}
