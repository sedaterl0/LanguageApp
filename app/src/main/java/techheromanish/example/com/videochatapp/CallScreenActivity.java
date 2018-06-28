package techheromanish.example.com.videochatapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.sinch.android.rtc.AudioController;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallEndCause;
import com.sinch.android.rtc.calling.CallState;
import com.sinch.android.rtc.video.VideoCallListener;
import com.sinch.android.rtc.video.VideoController;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class CallScreenActivity extends BaseActivity {

    static final String TAG = CallScreenActivity.class.getSimpleName();
    static final String CALL_START_TIME = "callStartTime";
    static final String ADDED_LISTENER = "addedListener";
    private MusicIntentReceiver myReceiver;
    private AudioPlayer mAudioPlayer;
    private Timer mTimer;
    private UpdateCallDurationTask mDurationTask;

    private String mCallId, callerId;
    private long mCallStart = 0;
    private boolean mAddedListener = false;
    private boolean mVideoViewsAdded = false;

    private TextView mCallDuration;
    private TextView mCallState;
    private TextView mCallerName;
    SpeechRecognizer speechRecognizer;
    ImageButton speechToTextButton;
    TextView txt;

    private static final String FIREBASE_URL = "https://chatapp-b17f7.firebaseio.com/";
    private Firebase mFirebaseRef, nFirebaseRef, lFirebaseRef;

    String dil, mesaj;

   // Chat ch=new Chat();

    private class UpdateCallDurationTask extends TimerTask {

        @Override
        public void run() {
            CallScreenActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateCallDuration();
                }
            });
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putLong(CALL_START_TIME, mCallStart);
        savedInstanceState.putBoolean(ADDED_LISTENER, mAddedListener);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mCallStart = savedInstanceState.getLong(CALL_START_TIME);
        mAddedListener = savedInstanceState.getBoolean(ADDED_LISTENER);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.callscreen);
        Bundle bundle = getIntent().getExtras();
        callerId = bundle.getString("callerId", MainActivity.userid);
        mAudioPlayer = new AudioPlayer(this);
        mCallDuration = (TextView) findViewById(R.id.callDuration);
        mCallerName = (TextView) findViewById(R.id.remoteUser);
        mCallState = (TextView) findViewById(R.id.callState);
        ImageButton endCallButton = (ImageButton) findViewById(R.id.hangupButton);
        speechToTextButton = (ImageButton) findViewById(R.id.btndinle);
        txt = (TextView) findViewById(R.id.txtkonus);

        myReceiver = new MusicIntentReceiver();

        Firebase.setAndroidContext(this);

        mFirebaseRef = new Firebase(FIREBASE_URL).child("goruntulusohbet").child(LoginActivity.mUsername).child(callerId);
        lFirebaseRef = new Firebase(FIREBASE_URL).child("goruntulusohbet").child(callerId).child(LoginActivity.mUsername);



        nFirebaseRef = new Firebase(FIREBASE_URL);
        // TODO Auto-generated method stub
             /*   loadingDialog = new ProgressDialog(context);
                loadingDialog.setCancelable(false);
                loadingDialog.setMessage("Lütfen Konuşun");
                loadingDialog.show();*/

        endCallButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                endCall();
            }
        });

        mCallId = getIntent().getStringExtra(SinchService.CALL_ID);

        if (savedInstanceState == null) {
            mCallStart = System.currentTimeMillis();
        }
    }

    @Override
    public void onServiceConnected() {
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            if (!mAddedListener) {
                call.addCallListener(new SinchCallListener());
                mAddedListener = true;
            }
        } else {
            Log.e(TAG, "Started with invalid callId, aborting.");
            finish();
        }

        updateUI();
    }

    @Override
    protected void onResume() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(myReceiver, filter);
        super.onResume();
    }

    //start the timer for the call duration here
    @Override
    public void onStart() {

        super.onStart();
        mTimer = new Timer();
        mDurationTask = new UpdateCallDurationTask();
        mTimer.schedule(mDurationTask, 0, 500);
        updateUI();


        nFirebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //   Chat ch = new Chat();
                // dataSnapshot.child("1").getValue(Chat.class);
                //      if (!ch.getAuthor().matches(MainActivity.mUsername)){

                //
                // dataSnapshot.child("1").getValue(Chat.class);
                dil = dataSnapshot.child("Users").child(callerId).getValue(UsersClass.class).getKullaniciMilliyet();


                if (dataSnapshot.child("goruntulusohbet").child(callerId).child(LoginActivity.mUsername).getValue() != null) {


                    mesaj = dataSnapshot.child("goruntulusohbet").child(callerId).child(LoginActivity.mUsername).child("1").getValue(Chat.class).getMessage();

                    if (dil.matches("English")) {

                        Translate.LanguageListOption.targetLanguage("en");

                    } else if (dil.matches("Français")) {

                        Translate.LanguageListOption.targetLanguage("fr");

                    } else if (dil.matches("Deutsch")) {

                        Translate.LanguageListOption.targetLanguage("de");

                    } else if (dil.matches("Türk")) {

                        Translate.LanguageListOption.targetLanguage("tr");

                    }

                    if (LoginActivity.kullaniciMil.matches("English")) {

                        txt.setText(translate(mesaj, "en"));


                    } else if (LoginActivity.kullaniciMil.matches("Français")) {

                        txt.setText(translate(mesaj, "fr"));

                    } else if (LoginActivity.kullaniciMil.matches("Deutsch")) {

                        txt.setText(translate(mesaj, "de"));

                    } else if (LoginActivity.kullaniciMil.matches("Türk")) {

                        txt.setText(translate(mesaj, "tr"));

                    }

                    //txt.setText(dataSnapshot.child("goruntulusohbet").child(MainActivity.userid).child(MainActivity.mUsername).child("1").getValue(Chat.class).getMessage());
                }

                //   }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }


    private String translate(String textToTranslate, String targetLanguage) {

        TranslateOptions options = TranslateOptions.newBuilder()
                .setApiKey(getString(R.string.translate_api)).build();
        Translate trService = options.getService();
        Translation translation = trService.translate(textToTranslate, Translate.TranslateOption.targetLanguage(targetLanguage));
        return translation.getTranslatedText();


    }

    //method to update video feeds in the UI
    private void updateUI() {
        if (getSinchServiceInterface() == null) {
            return; // early
        }

        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            mCallerName.setText(call.getRemoteUserId());
            mCallState.setText(call.getState().toString());
            //initiating
            if (call.getState() == CallState.ESTABLISHED) {
                //when the call is established, addVideoViews configures the video to  be shown

                addVideoViews();
            }

        }
        try {
            speechToTextButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    speechRecognizer = SpeechRecognizer
                            .createSpeechRecognizer(v.getContext());

                    speechRecognizer
                            .setRecognitionListener(new RecognitionListener() {

                                @Override
                                public void onRmsChanged(float arg0) {
                                    // TODO Auto-generated method stub

                                }

                                @Override
                                public void onResults(Bundle results) {
                                    // TODO Auto-generated method stub
                                    // loadingDialog.dismiss();
                                    ArrayList<String> speechResults = results
                                            .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                                    for (String speechResult : speechResults) {
                                        Log.i(TAG, speechResult);
                                        //  txt.setText(speechResult);
//                                    Toast.makeText(
//                                            getApplicationContext(),
//                                            speechResult,
//                                            Toast.LENGTH_SHORT).show();
                                        Chat chat = new Chat(speechResult, MainActivity.mUsername);
                                        mFirebaseRef.child("1").setValue(chat);
                                    }
                                }

                                @Override
                                public void onReadyForSpeech(Bundle arg0) {
                                    // TODO Auto-generated method stub

                                }

                                @Override
                                public void onPartialResults(Bundle arg0) {
                                    // TODO Auto-generated method stub

                                }

                                @Override
                                public void onEvent(int arg0, Bundle arg1) {
                                    // TODO Auto-generated method stub

                                }


                                @Override
                                public void onError(int errorCode) {
                                    // TODO Auto-generated method stub
                                    //      loadingDialog.dismiss();
//                                Toast.makeText(
//                                        getApplicationContext(),
//                                    //    "Bir Hata Oluştu Lütfen Tekrar Deneyin..."+errorCode,
//                                        Toast.LENGTH_SHORT).show();

                                }


                                @Override
                                public void onEndOfSpeech() {
                                    // TODO Auto-generated method stub
                                    //     loadingDialog
                                    //              .setMessage("Kayıt Bitti.Sonuçlar Getiriliyor");

                                }

                                @Override
                                public void onBufferReceived(byte[] arg0) {
                                    // TODO Auto-generated method stub

                                }

                                @Override
                                public void onBeginningOfSpeech() {
                                    // TODO Auto-generated method stub
                                    //  loadingDialog.setMessage("Kayıt Başladı");
                                }
                            });

                    Intent recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());
                    recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
                    speechRecognizer.startListening(recognizerIntent);

                }
            });
        } catch (Exception e) {

        }


    }

    @Override
    protected void onDestroy() {
        if (speechRecognizer != null) {
            speechRecognizer.stopListening();
            speechRecognizer.cancel();
            speechRecognizer.destroy();
        }
        super.onDestroy();
    }

    private class MusicIntentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
                int state = intent.getIntExtra("state", -1);
                switch (state) {
                    case 0:
                        Log.d(TAG, "Headset is unplugged");
                        break;
                    case 1:
                        Log.d(TAG, "Headset is plugged");
                        break;
                    default:
                        Log.d(TAG, "I have no idea what the headset state is");
                }
            }
        }
    }

    //stop the timer when call is ended
    @Override
    public void onStop() {
        super.onStop();
        mDurationTask.cancel();
        mTimer.cancel();
        removeVideoViews();
    }

    interface TranslateCallback {
        void onSuccess(String translatedText);

        void onFailure();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        endCall();
    }

    //method to end the call
    private void endCall() {

        try {
            mFirebaseRef.removeValue();
            lFirebaseRef.removeValue();
        } catch (Exception e) {
        }

        mAudioPlayer.stopProgressTone();
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.hangup();
        }
        finish();
        Intent maindon=new Intent(CallScreenActivity.this,MainActivity.class);
        maindon.putExtra("userid", MainActivity.userid);
        maindon.putExtra("musername", LoginActivity.mUsername);

        startActivity(maindon);
    }

    private String formatTimespan(long timespan) {
        long totalSeconds = timespan / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format(Locale.US, "%02d:%02d", minutes, seconds);
    }

    //method to update live duration of the call
    private void updateCallDuration() {
        if (mCallStart > 0) {
            mCallDuration.setText(formatTimespan(System.currentTimeMillis() - mCallStart));
        }
    }

    //method which sets up the video feeds from the server to the UI of the activity
    private void addVideoViews() {
        if (mVideoViewsAdded || getSinchServiceInterface() == null) {
            return; //early
        }

        final VideoController vc = getSinchServiceInterface().getVideoController();
        if (vc != null) {
            RelativeLayout localView = (RelativeLayout) findViewById(R.id.localVideo);
            localView.addView(vc.getLocalView());

            localView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //this toggles the front camera to rear camera and vice versa
                    vc.toggleCaptureDevicePosition();
                }
            });

            RelativeLayout view = (RelativeLayout) findViewById(R.id.remoteVideo);
            view.addView(vc.getRemoteView());
            mVideoViewsAdded = true;
        }
    }

    //removes video feeds from the app once the call is terminated
    private void removeVideoViews() {
        if (getSinchServiceInterface() == null) {
            return; // early
        }

        VideoController vc = getSinchServiceInterface().getVideoController();
        if (vc != null) {
            RelativeLayout view = (RelativeLayout) findViewById(R.id.remoteVideo);
            view.removeView(vc.getRemoteView());

            RelativeLayout localView = (RelativeLayout) findViewById(R.id.localVideo);
            localView.removeView(vc.getLocalView());
            mVideoViewsAdded = false;
        }
    }

    private class SinchCallListener implements VideoCallListener {

        @Override
        public void onCallEnded(Call call) {
            CallEndCause cause = call.getDetails().getEndCause();
            Log.d(TAG, "Call ended. Reason: " + cause.toString());
            mAudioPlayer.stopProgressTone();
            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
            String endMsg = "Call ended: " + call.getDetails().toString();
            // Toast.makeText(CallScreenActivity.this, endMsg, Toast.LENGTH_LONG).show();

            endCall();
        }

        @Override
        public void onCallEstablished(Call call) {
            Log.d(TAG, "Call established");
            mAudioPlayer.stopProgressTone();
            mCallState.setText(call.getState().toString());
            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
            AudioController audioController = getSinchServiceInterface().getAudioController();
            audioController.enableSpeaker();
            AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            boolean headsetEnabled = am.isWiredHeadsetOn();
            if (headsetEnabled) {
                audioController.disableSpeaker();

            }
            mCallStart = System.currentTimeMillis();
            Log.d(TAG, "Call offered video: " + call.getDetails().isVideoOffered());
        }

        @Override
        public void onCallProgressing(Call call) {
            Log.d(TAG, "Call progressing");
            mAudioPlayer.playProgressTone();
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {
            // Send a push through your push provider here, e.g. GCM
        }

        @Override
        public void onVideoTrackAdded(Call call) {
            Log.d(TAG, "Video track added");
            addVideoViews();
        }
    }
}
