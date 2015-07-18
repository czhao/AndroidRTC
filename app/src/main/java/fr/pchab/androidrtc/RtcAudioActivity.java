package fr.pchab.androidrtc;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Toast;

import org.json.JSONException;
import org.webrtc.MediaStream;
import org.webrtc.VideoRendererGui;

import fr.pchab.webrtcclient.AppRTCAudioManager;
import fr.pchab.webrtcclient.PeerConnectionParameters;
import fr.pchab.webrtcclient.WebRtcClient;

public class RtcAudioActivity extends Activity implements WebRtcClient.RtcListener,NfcAdapter.CreateNdefMessageCallback{
    private static final String VIDEO_CODEC_VP9 = "VP9";
    private static final String AUDIO_CODEC_OPUS = "opus";
    // Local preview screen position before call is connected.
    private WebRtcClient client;
    private String mSocketAddress;
    private String mSessionId, mTask;

    protected final static String INTENT_PARAM_SESSION = "param_session";
    protected final static String INTENT_PARAM_TASK = "param_task";
    protected final static String TASK_ANSWER = "task.answer";
    protected final static String TASK_INIT = "task.init";
    private NfcAdapter mNfcAdapter;

    private AppRTCAudioManager audioManager;

    public static final int REQUEST_CODE = 100;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(
                LayoutParams.FLAG_KEEP_SCREEN_ON
                        | LayoutParams.FLAG_DISMISS_KEYGUARD
                        | LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | LayoutParams.FLAG_TURN_SCREEN_ON);

        setContentView(R.layout.audio);
        mSocketAddress = "http://" + getResources().getString(R.string.host);
        mSocketAddress += (":" + getResources().getString(R.string.port) + "/");

        mSessionId = getIntent().getStringExtra(INTENT_PARAM_SESSION);
        mTask = getIntent().getStringExtra(INTENT_PARAM_TASK);


        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            Toast.makeText(this, "NFC is unavailable", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        mNfcAdapter.setNdefPushMessageCallback(this, this);


        //set the onclick listener to initialize the call
        findViewById(R.id.end_call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //output the caller ID
                Toast.makeText(RtcAudioActivity.this,"Session End "+ mSessionId, Toast.LENGTH_SHORT).show();
                disconnect();
                finish();
            }
        });
        init();

        if (TASK_ANSWER.equals(mTask)) {
            try {
                answer(mSessionId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }

    private void init() {
        PeerConnectionParameters parameters = new PeerConnectionParameters(false, false, 0, 0, 30, 1, VIDEO_CODEC_VP9, false, 1, AUDIO_CODEC_OPUS, true);
        client = new WebRtcClient(this, mSocketAddress, parameters, VideoRendererGui.getEGLContext());
        client.start("android_test");
        //configure the audio manager
        audioManager = AppRTCAudioManager.create(this, new Runnable() {
                    @Override
                    public void run() {
                        onAudioManageStatusChanged();

                    }
                }
        );
        audioManager.init();
    }

    @TargetApi(16)
    @Override
    public NdefMessage createNdefMessage(NfcEvent nfcEvent) {
        if (!TASK_INIT.equals(mTask))
            return null;

        //create the message
        return new NdefMessage(
                new NdefRecord[] { NdefRecord.createMime(
                        "text/plain", mSessionId.getBytes())});
    }

    @Override
    public void onPause() {
        super.onPause();
        if(client != null) {
            client.onPause();
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        // onResume gets called after this to handle the intent
        setIntent(intent);
    }

    @Override
    public void onResume() {
        super.onResume();

        if(client != null) {
            client.onResume();
        }
    }

    @Override
    public void onDestroy() {
        disconnect();
        super.onDestroy();
    }

    @Override
    public void onCallReady(String callId) {
        if (TASK_INIT.equals(mTask)){
            mSessionId = callId;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Session Initialized " + mSessionId, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void disconnect(){
        if (audioManager != null){
            audioManager.close();
            audioManager = null;
        }

        if (client != null) {
            client.onDestroy();
            client = null;
        }
    }

    public void answer(String callerId) throws JSONException {
        client.sendMessage(callerId, "init", null);
    }


    @Override
    public void onStatusChanged(final int newStatus) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int resId = R.string.unknown;
                switch (newStatus) {
                    case WebRtcClient.STATUS.CONNECTING:
                        resId = R.string.connecting;
                        break;
                    case WebRtcClient.STATUS.CONNECTED:
                        resId = R.string.connected;
                        break;
                    case WebRtcClient.STATUS.DISCONNECTED:
                        resId = R.string.disconnected;
                        break;
                }
                Toast.makeText(getApplicationContext(), resId, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onLocalStream(MediaStream localStream) {

    }

    @Override
    public void onAddRemoteStream(MediaStream remoteStream, int endPoint) {

    }

    @Override
    public void onRemoveRemoteStream(int endPoint) {

    }

    private void onAudioManageStatusChanged(){

    }
}