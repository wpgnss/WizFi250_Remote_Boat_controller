package wiznet.remote_controller;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Administrator on 2016-03-15.
 */
public class ControlActivity extends Activity {


    private Thread connectionThread;
    private Socket socket;
    private String ip;
    private int port;

    private ImageButton btn_up;
    private ImageButton btn_down;
    private ImageButton btn_left;
    private ImageButton btn_right;
    private ImageButton btn_stop;
    private TextView txt_status;

    private String Direction = "Boat_direction:none";
    private Handler sendHadler = null;

    private Runnable sendTask = new Runnable(){
        @Override
        public void run(){
            Log.d("sendTask", Direction);
            sendMsg(Direction);
            sendHadler.postDelayed(this, 500);
        }
    };


    private View.OnTouchListener mTouchEvent = new View.OnTouchListener() {

        String TAG = "OnTouchListener";

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            ImageButton btn = (ImageButton) v;
            int action = event.getAction();
            int id = v.getId();

            if (action == MotionEvent.ACTION_DOWN) {
                switch (id) {
                    case R.id.res_btn_up:
                        btn.setBackgroundResource(R.drawable.up_pressed);
                        Direction = "WIZnet_boat_direction:forward";
                        // sendMsg("gogogo");
                        Log.d(TAG, "up");
//                        return false;
                        break;
                    case R.id.res_btn_down:
                        btn.setBackgroundResource(R.drawable.down_pressed);
//                        sendMsg("backbackback");
                        Direction = "WIZnet_boat_direction:back";
                        Log.d(TAG, "down");
//                        return false;
                        break;
                    case R.id.res_btn_left:
                        btn.setBackgroundResource(R.drawable.left_pressed);
//                        sendMsg("leftleftleft");
                        Direction = "WIZnet_boat_direction:left";
                        Log.d(TAG, "left");
//                        return false;
                        break;
                    case R.id.res_btn_right:
                        btn.setBackgroundResource(R.drawable.right_pressed);
//                        sendMsg("rightrightright");
                        Direction = "WIZnet_boat_direction:right";
                        Log.d(TAG, "right");
//                        return false;
                        break;
                    case R.id.res_btn_stop:
                        btn.setBackgroundResource(R.drawable.stop_pressed);
//                        sendMsg("breakbreakbreak");
                        Direction = "WIZnet_boat_direction:stop";
                        Log.d(TAG, "stop");
//                        return false;
                        break;
                }

                if(sendHadler != null)
                    return true;
                sendHadler = new Handler();
                sendHadler.postDelayed(sendTask, 500);
            }

            if (action == MotionEvent.ACTION_UP) {
//                if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP) {
                if(sendHadler == null)
                    return true;
                sendHadler.removeCallbacks(sendTask);
                sendHadler = null;
                Direction = "WIZnet_boat_direction:none";

                switch (id) {
                    case R.id.res_btn_up:
                        btn.setBackgroundResource(R.drawable.up);
                        break;
                    case R.id.res_btn_down:
                        btn.setBackgroundResource(R.drawable.down);
                        break;
                    case R.id.res_btn_left:
                        btn.setBackgroundResource(R.drawable.left);
                        break;
                    case R.id.res_btn_right:
                        btn.setBackgroundResource(R.drawable.right);
                        break;
                    case R.id.res_btn_stop:
                        btn.setBackgroundResource(R.drawable.stop);
                        break;
                }
            }


            return true;
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_control);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        String TAG = "   >>>  MAIN ";

        btn_up = (ImageButton) findViewById(R.id.res_btn_up);
        btn_down = (ImageButton) findViewById(R.id.res_btn_down);
        btn_left = (ImageButton) findViewById(R.id.res_btn_left);
        btn_right = (ImageButton) findViewById(R.id.res_btn_right);
        btn_stop = (ImageButton) findViewById(R.id.res_btn_stop);
        txt_status = (TextView) findViewById(R.id.res_txt_status);

        btn_up.setOnTouchListener(mTouchEvent);
        btn_down.setOnTouchListener(mTouchEvent);
        btn_left.setOnTouchListener(mTouchEvent);
        btn_right.setOnTouchListener(mTouchEvent);
        btn_stop.setOnTouchListener(mTouchEvent);
        ip = getIntent().getExtras().getString(MainActivity.server_ip);
        port = Integer.parseInt(getIntent().getExtras().getString(MainActivity.server_port));
        txt_status.setText("disconnected..");
        setConnection();
    }


    public void setConnection() {

        Runnable runnable = new Runnable() {
            String TAG = "setConnection";

            @Override
            public void run() {
                try {

                    socket = new Socket(ip, port);

                } catch (UnknownHostException e) {
                    Log.e(TAG, "UnknownHostException " + e.getMessage());
                    e.printStackTrace();
                } catch (IOException e) {
                    Log.e(TAG, "IOException " + e.getMessage());
                    e.printStackTrace();
                }
                // txt_status.setText("Connected..");
            }
        };
        connectionThread = new Thread(runnable);
        connectionThread.start();
    }


    private void sendMsg(String msg) {

        String TAG = "sendData";

        try {
            if (socket.isConnected() || !socket.isClosed()) {
                txt_status.setText("Connected..");
            }

        } catch (NullPointerException e) {
            e.printStackTrace();
            txt_status.setText("disconnected..");
            return;
        }

            try {

                BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
                OutputStreamWriter sendstream = new OutputStreamWriter(bos, "US-ASCII");
                PrintWriter writer = new PrintWriter(sendstream);

                writer.print(msg);
                writer.flush();
                //wait(1000);

//                connectionThread.sleep(100);
            } catch (IOException e) {
                Log.e(TAG, "IOException " + e.getMessage());
                e.printStackTrace();
            } catch (NullPointerException e) {
                Log.e(TAG, "NullPointerException " + e.getMessage());
            }
    }


    @Override
    protected void onStop() {
        super.onStop();
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            socket.close();
            connectionThread.join(1000);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
