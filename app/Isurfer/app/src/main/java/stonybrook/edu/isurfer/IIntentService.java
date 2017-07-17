package stonybrook.edu.isurfer;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


public class IIntentService extends IntentService {
    Iservice service2;

    public IIntentService() {
        super("IIntentService");
            }

    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        Log.d("Service","Initialising");
        Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
        boolean isNetworkConnected = extras.getBoolean("isNetworkConnected");

        if(isNetworkConnected){
            Toast.makeText(this, "Connection Available", Toast.LENGTH_SHORT).show();
            Log.d("TAG","You are Connected to Internet");
            startService(new Intent(getBaseContext(), Iservice.class));
        }
        else {
            Toast.makeText(this, "Connected to Internet", Toast.LENGTH_SHORT).show();
            Log.d("TAG","You are not Connected to Internet");
        }
    }

}
