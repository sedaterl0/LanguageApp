package techheromanish.example.com.videochatapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

import java.util.ArrayList;

public class KonusmaDinleme extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    SpeechRecognizer speechRecognizer;
    ProgressDialog loadingDialog;
    Context context;
    Button speechToTextButton;
    TextView txt;
    private static final String API_KEY = "MY_API_KEY";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_konusma_dinleme);
        context = this;
        //   speechToTextButton = (Button) findViewById(R.id.speechToTextButton);
        txt = (TextView) findViewById(R.id.txtkonusma);
        // TODO Auto-generated method stub
             /*   loadingDialog = new ProgressDialog(context);
                loadingDialog.setCancelable(false);
                loadingDialog.setMessage("Lütfen Konuşun");
                loadingDialog.show();*/
        speechRecognizer = SpeechRecognizer
                .createSpeechRecognizer(context);

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
                            txt.setText(speechResult);
                            Translate.LanguageListOption.targetLanguage("en");

                            txt.setText(translate(speechResult, "en"));


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
                        Toast.makeText(
                                getApplicationContext(),
                                "Bir Hata Oluştu Lütfen Tekrar Deneyin...",
                                Toast.LENGTH_SHORT).show();

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

    @Override
    protected void onDestroy() {
        if (speechRecognizer != null) {
            speechRecognizer.stopListening();
            speechRecognizer.cancel();
            speechRecognizer.destroy();
        }
        super.onDestroy();
    }

    private String translate(String textToTranslate, String targetLanguage) {

        TranslateOptions options = TranslateOptions.newBuilder()
                .setApiKey("AIzaSyDBGQwU3XGHOgI2uiyPP_P4fQDkwjVR7pk").build();
        Translate trService = options.getService();
        Translation translation = trService.translate(textToTranslate, Translate.TranslateOption.targetLanguage(targetLanguage));
        return translation.getTranslatedText();


    }

    interface TranslateCallback {
        void onSuccess(String translatedText);

        void onFailure();
    }
}