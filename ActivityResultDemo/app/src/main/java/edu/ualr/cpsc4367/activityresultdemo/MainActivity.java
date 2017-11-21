package edu.ualr.cpsc4367.activityresultdemo;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_HASH = 11;
    private Button mHashButton;
    private TextView mChecksumTextView;
    private EditText mInputEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mChecksumTextView = (TextView)findViewById(R.id.tv_show_checksum);
        mInputEditText = (EditText)findViewById(R.id.ed_input);

        mHashButton = (Button)findViewById(R.id.btn_hash);
        mHashButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = HashActivity.newIntent(MainActivity.this, mInputEditText.getText().toString());
                mInputEditText.setText("");
                startActivityForResult(intent, REQUEST_CODE_HASH);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK || requestCode != REQUEST_CODE_HASH)
            return;

        if (data != null) {
            mChecksumTextView.setText(HashActivity.getHashResult(data));
        }
    }
}
