package wiznet.remote_controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    EditText SERVER_IP;
    EditText SERVER_PORT;
    Button BTN_NEXT;

    public static String server_ip;
    public static String server_port;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_setting);


        SERVER_IP = (EditText) findViewById(R.id.res_txt_ip);
        SERVER_PORT = (EditText) findViewById(R.id.res_txt_port);
        BTN_NEXT = (Button) findViewById(R.id.res_btn_next);


        BTN_NEXT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, ControlActivity.class);

                server_ip = SERVER_IP.getText().toString();
                server_port = SERVER_PORT.getText().toString();

                intent.putExtra(server_ip, server_ip);
                intent.putExtra(server_port, server_port);

                startActivity(intent);
            }
        });

    }


}
