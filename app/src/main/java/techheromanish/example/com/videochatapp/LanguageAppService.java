package techheromanish.example.com.videochatapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.IBinder;
import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class LanguageAppService extends Service implements ValueEventListener {
    private static final String FIREBASE_URL = "https://chatapp-b17f7.firebaseio.com/";
    Context context;
    Notification notification;
    Chat chat = new Chat();
    private String mUsername;
    private Firebase mRef;

    public LanguageAppService() {


    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        SharedPreferences prefs = getApplication().getSharedPreferences("ChatPrefs", 0);
        mUsername = prefs.getString("username", null);
        // mUsername=intent.getStringExtra("username");

        if (mUsername == null) {
            onDestroy();
        } else {
            Firebase.setAndroidContext(this);
            mRef = new Firebase("https://chatapp-b17f7.firebaseio.com/chat/" + mUsername);
            mRef.addValueEventListener(this);
            //todo  burasımesaj bildirimleri için işimizi görebilir denenecek
            /*ref.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                    Post newPost = dataSnapshot.getValue(Post.class);
                    System.out.println("Author: " + newPost.author);
                    System.out.println("Title: " + newPost.title);
                    System.out.println("Previous Post ID: " + prevChildKey);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {}

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {}

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
            */
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        Log.e("service", "service nesnesi yaratıldı");
    }

    //sa kardeşim
    //aleyküm selam kardeşim
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {


     /*   for (DataSnapshot kullanicilar : dataSnapshot.getChildren()) {


            for (DataSnapshot author : kullanicilar.getChildren()) {

                if (!author.child("author").getValue().toString().matches(mUsername)) {*/
//skype yapalım

                    NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    Notification.Builder nb = new Notification.Builder(this);
                    nb.setContentTitle("Language App");
                    nb.setContentText("Mesaj Geldi");
                    nb.setSmallIcon(R.drawable.icon);
                    nb.setTicker("Bildirim Geldi");
                    nb.setAutoCancel(true);
                    nb.setLights(Color.BLUE, 1, 1);

                    Intent intent = new Intent(context, AnaSayfa.class);
                    PendingIntent pending = PendingIntent.getActivity(context, 0, intent, 0);//Notificationa tıklanınca açılacak activityi belirliyoruz
                    nb.setContentIntent(pending);

                    Notification notification = nb.getNotification();
                    notification.vibrate = new long[]{500, 500, 500, 500};
                    nm.notify(0, notification);


           /*     }

            }

        }*/

    }


    @Override
    public void onCancelled(FirebaseError firebaseError) {

    }
}
