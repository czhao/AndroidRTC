package fr.pchab.androidrtc;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.audio_only_option)
    public void audioOnly(Button button) {
        //start the activity for audio only activity
        Intent answerCall = new Intent(this, RtcAudioActivity.class);
        answerCall.putExtra(RtcAudioActivity.INTENT_PARAM_TASK, RtcAudioActivity.TASK_INIT);
        startActivity(answerCall);
    }

    @Override
    public void onNewIntent(Intent intent) {
        // onResume gets called after this to handle the intent
        setIntent(intent);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            processIntent(getIntent());
        }
    }


    /**
     * Parses the NDEF Message from the intent and prints to the TextView
     */
    void processIntent(Intent intent) {
        Parcelable[] rawMessages = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);
        // only one message sent during the beam
        NdefMessage msg = (NdefMessage) rawMessages[0];
        // record 0 contains the MIME type, record 1 is the AAR, if present
        String mCallSessionId = new String(msg.getRecords()[0].getPayload());
        Toast.makeText(this, "New Session Acquired: " +mCallSessionId, Toast.LENGTH_SHORT).show();

        //start the new activity
        Intent answerCall = new Intent(this, RtcAudioActivity.class);
        answerCall.putExtra(RtcAudioActivity.INTENT_PARAM_TASK, RtcAudioActivity.TASK_ANSWER);
        answerCall.putExtra(RtcAudioActivity.INTENT_PARAM_SESSION, mCallSessionId);
        startActivityForResult(answerCall, RtcAudioActivity.REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RtcAudioActivity.REQUEST_CODE){
            //remove the intent set via NFC beam
            setIntent(new Intent());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
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
}
