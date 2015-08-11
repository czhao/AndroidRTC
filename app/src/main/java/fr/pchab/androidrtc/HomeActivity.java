package fr.pchab.androidrtc;

import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeActivity extends AppCompatActivity {

    @Bind(R.id.server_host_input)
    EditText mHostInput;

    SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        settings = getSharedPreferences(AppConst.SHARED_PREF_NAME, 0);
        mHostInput.setText(settings.getString(AppConst.PREF_HOST,""));
    }


    public void onStartAudioSession(View button) {
        //start the activity for audio only activity
        Intent answerCall = new Intent(this, AudioActivity.class);
        answerCall.putExtra(AudioActivity.INTENT_PARAM_TASK, AudioActivity.TASK_INIT);
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

    public void onSaveConfiguration(View v){
        //save the host info into the shared preference
        String input = mHostInput.getText().toString();
        SharedPreferences.Editor e = settings.edit();
        e.putString(AppConst.PREF_HOST, input);
        e.apply();
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
        Intent answerCall = new Intent(this, AudioActivity.class);
        answerCall.putExtra(AudioActivity.INTENT_PARAM_TASK, AudioActivity.TASK_ANSWER);
        answerCall.putExtra(AudioActivity.INTENT_PARAM_SESSION, mCallSessionId);
        startActivityForResult(answerCall, AudioActivity.REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AudioActivity.REQUEST_CODE){
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
