package cn.yaoht.robotarm;

import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;


public class MainActivity extends ActionBarActivity {
    JoystickView joystick1, joystick2;
    DatagramSocket socket;
    InetAddress serverAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.layout);
        joystick1 = new JoystickView(this);
        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params1.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params1.addRule(RelativeLayout.ABOVE, R.id.placeholder);
        layout.addView(joystick1, params1);
        joystick1.setUpdateListener(new JoystickView.UpdateListener() {
            @Override
            public void onUpdate(float x, float y) {
                int maxSpeed = 300;
                float xSpeed = x * maxSpeed;
                float ySpeed = y * maxSpeed;
                SendCommand("SetRobotSpeed Vx " + Integer.toString((int)xSpeed));
                SendCommand("SetRobotSpeed Vy " + Integer.toString((int)ySpeed));
            }
        });

        joystick2 = new JoystickView(this);
        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params2.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params2.addRule(RelativeLayout.BELOW, R.id.placeholder);
        layout.addView(joystick2, params2);
        joystick2.setUpdateListener(new JoystickView.UpdateListener() {
            @Override
            public void onUpdate(float x, float y) {
                int maxSpeed = 300;
                float xSpeed = x * maxSpeed;
                float ySpeed = y * maxSpeed;
                SendCommand("SetRobotSpeed Omega " + Integer.toString((int)ySpeed));
            }
        });


        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        try {
            serverAddress = InetAddress.getByName("192.168.2.10");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        SendCommand("EnableSystem");
        SendCommand("SetMaxAccel 1000");
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        SendCommand("ShutdownSystem");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void SendCommand(String command) {
        DatagramPacket pack = new DatagramPacket(command.getBytes(), command.length(), serverAddress, 7);
        try {
            socket.send(pack);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
