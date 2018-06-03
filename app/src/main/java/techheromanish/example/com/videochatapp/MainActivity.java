package techheromanish.example.com.videochatapp;

import android.app.ListActivity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.ValueEventListener;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

public class MainActivity extends ListActivity {

    // TODO: change this to your own Firebase URL
    private static final String FIREBASE_URL = "https://chatapp-b17f7.firebaseio.com/";
    public static String userid;
    public static String userkulAd;
    public static String mUsername;
    private Firebase mFirebaseRef,nFirebaseRef;
    private ValueEventListener mConnectedListener;
    private ChatListAdapter mChatListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        Bundle extras = getIntent().getExtras();
        userid = extras.getString("userid", IncomingCallScreenActivity.callerId);
        //   userkulAd = extras.getString("userkulad");
        mUsername=extras.getString("musername");

        // Make sure we have a mUsername
     //   setupUsername();



        setTitle("Chatting as " + mUsername);

        // Setup our Firebase mFirebaseRef
        Firebase.setAndroidContext(this);

        mFirebaseRef = new Firebase(FIREBASE_URL).child("chat").child(mUsername).child(userid);
        nFirebaseRef = new Firebase(FIREBASE_URL).child("chat").child(userid).child(mUsername);

        // Setup our input methods. Enter key on the keyboard or pushing the send button
        EditText inputText = (EditText) findViewById(R.id.messageInput);
        inputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_NULL && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    sendMessage();
                }
                return true;
            }
        });

        findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
        findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });findViewById(R.id.callButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                Intent callintent=new Intent(MainActivity.this,PlaceCallActivity.class);
                callintent.putExtra("musername", mUsername);
                callintent.putExtra("userid",userid);
                startActivity(callintent);
                finish();

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        // Setup our view and list adapter. Ensure it scrolls to the bottom as data changes
        final ListView listView = getListView();
        // Tell our list adapter that we only want 50 messages at a time
        mChatListAdapter = new ChatListAdapter(mFirebaseRef, this, R.layout.chat_message, mUsername);
        listView.setAdapter(mChatListAdapter);
        mChatListAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(mChatListAdapter.getCount() - 1);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Chat mychat = (Chat) parent.getItemAtPosition(position);
                //  Toast.makeText(view.getContext(),mychat.getMessage(),Toast.LENGTH_SHORT).show();
                if (!LoginActivity.mUsername.matches(mychat.getAuthor())) {
                    if (LoginActivity.kullaniciMil.matches("English")) {


                        //TranslatemessageText.setText(translate(chat.getMessage(), "en"));
                        Toast.makeText(view.getContext(), translate(mychat.getMessage(), "en"), Toast.LENGTH_SHORT).show();


                    } else if (LoginActivity.kullaniciMil.matches("Français")) {

                        // TranslatemessageText.setText(translate(chat.getMessage(), "fr"));
                        Toast.makeText(view.getContext(), translate(mychat.getMessage(), "fr"), Toast.LENGTH_SHORT).show();

                    } else if (LoginActivity.kullaniciMil.matches("Deutsch")) {

                        //  TranslatemessageText.setText(translate(chat.getMessage(), "de"));
                        Toast.makeText(view.getContext(), translate(mychat.getMessage(), "de"), Toast.LENGTH_SHORT).show();

                    } else if (LoginActivity.kullaniciMil.matches("Türk")) {

                        //   TranslatemessageText.setText(translate(chat.getMessage(), "tr"));
                        Toast.makeText(view.getContext(), translate(mychat.getMessage(), "tr"), Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });

        // Finally, a little indication of connection status
//        mConnectedListener = mFirebaseRef.getRoot().child(".info/connected").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                boolean connected = (Boolean) dataSnapshot.getValue();
////                if (connected) {
////                    Toast.makeText(MainActivity.this, "Connected to Firebase", Toast.LENGTH_SHORT).show();
////                } else {
////                    Toast.makeText(MainActivity.this, "Disconnected from Firebase", Toast.LENGTH_SHORT).show();
////                }
//            }
//
//            @Override
//            public void onCancelled(FirebaseError firebaseError) {
//                // No-op
//            }
//        });
    }

    @Override
    public void onStop() {
        super.onStop();
        mFirebaseRef.getRoot().child(".info/connected").removeEventListener(mConnectedListener);
        mChatListAdapter.cleanup();
    }


    private void sendMessage() {
        EditText inputText = (EditText) findViewById(R.id.messageInput);
        String input = inputText.getText().toString();
        if (!input.equals("")) {
            // Create our 'model', a Chat object
            Chat chat = new Chat(input, mUsername);
            // Create a new, auto-generated child of that chat location, and save our chat data there
            mFirebaseRef.push().setValue(chat);
            nFirebaseRef.push().setValue(chat);
            inputText.setText("");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(this, AnaSayfa.class);
        startActivity(i);
        finish();
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
