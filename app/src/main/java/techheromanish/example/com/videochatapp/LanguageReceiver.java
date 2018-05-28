package techheromanish.example.com.videochatapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class LanguageReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        context.startService(new Intent(context, LanguageAppService.class));

    }
}
