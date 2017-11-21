package edu.ualr.cpsc4367.remoteboundservice;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button mButtonConnectService;
    private Button mButtonGenerateNumber;
    private TextView mTextShowNumber;

    /**
     * Messenger for communicating with service.
     */
    private Messenger mBoundService = null;
    /**
     * Flag indicating whether we have called bind on the service.
     */
    private boolean mIsBound;
    /**
     * Some text view we are using to show state information.
     */
    private TextView mCallbackText;

    /**
     * Handler of incoming messages from service.
     */
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case RemoteBoundService.MSG_SET_VALUE:
                    mCallbackText.setText("Received from service: " + msg.arg1);
                    break;
                case RemoteBoundService.MSG_GENERATE_RANDOM_NUM:
                    mTextShowNumber.setText(Integer.toString(msg.arg1));
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    private final Messenger mMessenger = new Messenger(new IncomingHandler());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextShowNumber = (TextView) findViewById(R.id.tv_show_num);
        mCallbackText = (TextView) findViewById(R.id.tv_show_state);

        mButtonConnectService = (Button) findViewById(R.id.button_connect);
        mButtonConnectService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doBindService();
            }
        });

        mButtonGenerateNumber = (Button) findViewById(R.id.button_generate);
        mButtonGenerateNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsBound == false) {
                    mTextShowNumber.setText("Service is not ready");
                    return;
                }

                Message msg = Message.obtain(null,
                        RemoteBoundService.MSG_GENERATE_RANDOM_NUM, this.hashCode(), 0);
                msg.replyTo = mMessenger;
                try {
                    Log.i("MainActivity", "before send()");
                    mBoundService.send(msg);
                    Log.i("MainActivity", "after send()");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  We are communicating with our
            // service through an IDL interface, so get a client-side
            // representation of that from the raw service object.
            mBoundService = new Messenger(service);
            mCallbackText.setText("Attached.");

            // We want to monitor the service for as long as we are
            // connected to it.
            try {
                Message msg = Message.obtain(null,
                        RemoteBoundService.MSG_REGISTER_CLIENT);
                msg.replyTo = mMessenger;
                mBoundService.send(msg);

                // Give it some value as an example.
                msg = Message.obtain(null,
                        RemoteBoundService.MSG_SET_VALUE, this.hashCode(), 0);
                mBoundService.send(msg);
            } catch (RemoteException e) {
                // In this case the service has crashed before we could even
                // do anything with it; we can count on soon being
                // disconnected (and then reconnected if it can be restarted)
                // so there is no need to do anything here.
            }

            // As part of the sample, tell the user what happened.
            Toast.makeText(MainActivity.this, R.string.remote_service_connected,
                    Toast.LENGTH_SHORT).show();
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            mBoundService = null;
            mCallbackText.setText("Disconnected.");

            // As part of the sample, tell the user what happened.
            Toast.makeText(MainActivity.this, R.string.remote_service_disconnected,
                    Toast.LENGTH_SHORT).show();
        }
    };

    // Establish a connection with the service.  We use an explicit
    // class name because we want a specific service implementation that
    // we know will be running in our own process (and thus won't be
    // supporting component replacement by other applications).
    void doBindService() {
        bindService(new Intent(MainActivity.this,
                RemoteBoundService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
        mCallbackText.setText("Binding.");
    }

    void doUnbindService() {
        if (mIsBound) {
            // If we have received the service, and hence registered with
            // it, then now is the time to unregister.
            if (mBoundService != null) {
                try {
                    Message msg = Message.obtain(null,
                            RemoteBoundService.MSG_UNREGISTER_CLIENT);
                    msg.replyTo = mMessenger;
                    mBoundService.send(msg);
                } catch (RemoteException e) {
                    // There is nothing special we need to do if the service
                    // has crashed.
                }
            }

            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
            mCallbackText.setText("Unbinding.");
        }
    }
}
