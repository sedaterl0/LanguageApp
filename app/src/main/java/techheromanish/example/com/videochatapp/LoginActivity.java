package techheromanish.example.com.videochatapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.sinch.android.rtc.SinchError;

import java.util.Random;

public class LoginActivity extends BaseActivity implements SinchService.StartFailedListener {


    private ProgressDialog mSpinner;
    private Button btn;
    public static String mUsername,userid;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

     /*   Bundle extras = getIntent().getExtras();
        userid = extras.getString("userid");
        mUsername=extras.getString("musername");*/
       // btn = (Button) findViewById(R.id.loginButton);
        //asking for permissions here



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.CAMERA, android.Manifest.permission.ACCESS_NETWORK_STATE, android.Manifest.permission.READ_PHONE_STATE},100);
        }

     /*   btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                loginClicked(mUsername);
            }
        });
*/

        //initializing UI elements


    }

    private void setupUsername() {
        SharedPreferences prefs = getApplication().getSharedPreferences("ChatPrefs", 0);
        mUsername = prefs.getString("username", null);

        if (mUsername == null) {
            Random r = new Random();
            // Assign a random user name if we don't have one saved.
            mUsername = "JavaUser" + r.nextInt(100000);
            prefs.edit().putString("username", mUsername).commit();

        }
        Intent i = new Intent(this, LanguageAppService.class);
        i.putExtra("username", mUsername);
        this.startService(i);
    }
    @Override
    protected void onStart() {
        setupUsername();
        super.onStart();

    }

    //this method is invoked when the connection is established with the SinchService
    @Override
    protected void onServiceConnected() {

        getSinchServiceInterface().setStartListener(this);
        Bundle ext = getIntent().getExtras();

        loginClicked(mUsername);
    }

    @Override
    protected void onPause() {
        if (mSpinner != null) {
            mSpinner.dismiss();
        }
        super.onPause();
    }

    @Override
    public void onStartFailed(SinchError error) {
        Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show();
        if (mSpinner != null) {
            mSpinner.dismiss();
        }
    }

    //Invoked when just after the service is connected with Sinch
    @Override
    public void onStarted() {
        openPlaceCallActivity();
    }

    //Login is Clicked to manually to connect to the Sinch Service
    private void loginClicked(String name) {
        //String userName = mLoginName.getText().toString();


        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_LONG).show();
            return;
        }

        if (!getSinchServiceInterface().isStarted()) {
            getSinchServiceInterface().startClient(name);
            showSpinner();
        } else {
            openPlaceCallActivity();
        }

    }


    //Once the connection is made to the Sinch Service, It takes you to the next activity where you enter the name of the user to whom the call is to be placed
    private void openPlaceCallActivity() {
        Intent mainActivity = new Intent(this, AnaSayfa.class);
        mainActivity.putExtra("musername", mUsername);
      //  mainActivity.putExtra("userid", call);
        startActivity(mainActivity);
    }

    private void showSpinner() {
        mSpinner = new ProgressDialog(this);
        mSpinner.setTitle("Logging in");
        mSpinner.setMessage("Please wait...");
        mSpinner.show();
    }
}