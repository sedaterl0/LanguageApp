package techheromanish.example.com.videochatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.sinch.android.rtc.SinchError;

public class LoginActivity extends BaseActivity implements SinchService.StartFailedListener,View.OnClickListener {


    private ProgressDialog mSpinner;
    private Button giris_btn;
    public static String mUsername,userid;
    public static String kullaniciMil, kullaniciAd;
    Spinner spin;
    String[] milliyet = new String[]{"English", "Français", "Deutsch", "Türk"};
    EditText edtKullanici;

    String kullaniciMilSpin = null;
    LinearLayout lineergiris;





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
        lineergiris = (LinearLayout) findViewById(R.id.layoutgiris);
        giris_btn = (Button) findViewById(R.id.btn_giris);
        edtKullanici = (EditText) findViewById(R.id.edt_kullaniciadi);
        spin = (Spinner) findViewById(R.id.spn_kullanicidil);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, milliyet);
        spin.setAdapter(arrayAdapter);
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                kullaniciMilSpin = parent.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        giris_btn.setOnClickListener(this);
    }

    private void setupUsername() {
        SharedPreferences prefs = getApplication().getSharedPreferences("ChatPrefs", 0);
        mUsername = prefs.getString("username", null);
        // kullaniciAd = prefs.getString("KullaniciAdi", null);
        kullaniciMil = prefs.getString("KullaniciMilliyet", null);

        if (mUsername == null) {
            //  Random r = new Random();
            // Assign a random user name if we don't have one saved.
            // mUsername = "JavaUser" + r.nextInt(100000);
            lineergiris.setBackgroundColor(Color.BLUE);
            mUsername = edtKullanici.getText().toString();
            prefs.edit().putString("username", mUsername).commit();

            //  prefs.edit().putString("KullaniciAdi", kullaniciAd).commit();
            prefs.edit().putString("KullaniciMilliyet", kullaniciMil).commit();


        }
        if (kullaniciMil == null && kullaniciMilSpin != null) {
            kullaniciMil = kullaniciMilSpin;
        }

        Intent i = new Intent(this, LanguageAppService.class);
        i.putExtra("username", mUsername);
        //  i.putExtra("KullaniciAdi", kullaniciAd);
        i.putExtra("KullaniciMilliyet", kullaniciMil);
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
        if (mUsername != null && kullaniciMil != null) {
        loginClicked(mUsername);
        giris_btn.setVisibility(View.INVISIBLE);
        edtKullanici.setVisibility(View.INVISIBLE);
        spin.setVisibility(View.INVISIBLE);}
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
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainActivity = new Intent(LoginActivity.this, AnaSayfa.class);
                mainActivity.putExtra("musername", mUsername);
                //  mainActivity.putExtra("userid", call);
                startActivity(mainActivity);
                finish();
            }
        }, 2000);

    }

    private void showSpinner() {
        mSpinner = new ProgressDialog(this);
        mSpinner.setTitle("Logging in");
        mSpinner.setMessage("Please wait...");

        mSpinner.show();
    }

    @Override
    public void onClick(View v) {
        SharedPreferences prefs = getApplication().getSharedPreferences("ChatPrefs", 0);
        mUsername = edtKullanici.getText().toString();
        prefs.edit().putString("username", mUsername).commit();
        if (kullaniciMil == null) {
            setupUsername();
        }
        prefs.edit().putString("KullaniciMilliyet", kullaniciMil).commit();
        loginClicked(mUsername);
    }
}