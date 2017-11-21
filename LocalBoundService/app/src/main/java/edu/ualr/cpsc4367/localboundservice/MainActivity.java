package edu.ualr.cpsc4367.localboundservice;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private LocalBoundService mBoundService;
    private boolean mIsBound = false;

    private Button mButtonConnectService;
    private Button mButtonGenerateNumber;
    private TextView mTextShowNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextShowNumber = (TextView)findViewById(R.id.tv_show);

        mButtonConnectService = (Button)findViewById(R.id.button_connect);
        mButtonConnectService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doBindService();
            }
        });

        mButtonGenerateNumber = (Button)findViewById(R.id.button_generate);
        mButtonGenerateNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int num = mBoundService.getRandomNumber();
                mTextShowNumber.setText(Integer.toString(num));
            }
        });
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            mBoundService = ((LocalBoundService.LocalBinder) service).getService();

            // Tell the user about this for our demo.
            Toast.makeText(MainActivity.this, R.string.local_service_connected,
                    Toast.LENGTH_SHORT).show();
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.
            mBoundService = null;
            Toast.makeText(MainActivity.this, R.string.local_service_disconnected,
                    Toast.LENGTH_SHORT).show();
        }
    };

    // Establish a connection with the service.  We use an explicit
    // class name because we want a specific service implementation that
    // we know will be running in our own process (and thus won't be
    // supporting component replacement by other applications).
    void doBindService() {
        bindService(new Intent(MainActivity.this,
                LocalBoundService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService() {
        if (mIsBound) {
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
    }
}
