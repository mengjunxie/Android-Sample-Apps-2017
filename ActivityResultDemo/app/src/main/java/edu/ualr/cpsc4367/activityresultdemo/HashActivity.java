package edu.ualr.cpsc4367.activityresultdemo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashActivity extends AppCompatActivity {

    private static final String EXTRA_INPUT_STRING = "edu.ualr.cpsc4367.activityresultdemo.input_string";
    private static final String EXTRA_HASH_RESULT = "edu.ualr.cpsc4367.activityresultdemo.hash_result";

    private Button mDoHashButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hash);

        mDoHashButton = (Button) findViewById(R.id.btn_dohash);
        mDoHashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = getIntent().getCharSequenceExtra(EXTRA_INPUT_STRING).toString();
                String output;
                try {
                    output = doSHA1(input);
                } catch (Exception e) {
                    System.err.println("Exception in Hashing: " + e.getMessage());
                    output = "Error in SHA-1 hashing";
                }
                setHashResult(output);
                HashActivity.this.finish();
            }
        });
    }

    public static Intent newIntent(Context packageContext, String str) {
        Intent i = new Intent(packageContext, HashActivity.class);
        i.putExtra(EXTRA_INPUT_STRING, str);
        return i;
    }

    public static CharSequence getHashResult(Intent result) {
        return result.getCharSequenceExtra(EXTRA_HASH_RESULT);
    }

    private void setHashResult(String checksum) {
        Intent data = new Intent();
        data.putExtra(EXTRA_HASH_RESULT, checksum);
        setResult(RESULT_OK, data);
    }

    private String doSHA1(String input) throws NoSuchAlgorithmException {
        MessageDigest mDigest = MessageDigest.getInstance("SHA1");
        byte[] result = mDigest.digest(input.getBytes());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }
}
