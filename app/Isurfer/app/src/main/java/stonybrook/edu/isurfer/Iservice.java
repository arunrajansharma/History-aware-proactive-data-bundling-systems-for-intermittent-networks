package stonybrook.edu.isurfer;

import android.app.ActivityManager;
import android.app.Service;

import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;
import android.util.Log;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.ArrayList;



public class Iservice extends Service {
    static WebSocketClient mWebSocketClient;
    static boolean registrationFlag = false;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getConnected();
        if(registrationFlag == false){
            HistoryFetcher fH = new HistoryFetcher();
            sendHistoryFile(fH.fetchHistory(getBaseContext()));
            registrationFlag = true;
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Destroy", "Destroyed");
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }

    public void connectWebSocket() {
        URI uri;
        try {
            uri = new URI("ws://127.0.0.1:8078");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.d("Websocket", "Opened");
                registrationFlag = true;
            }

            @Override
            public void onMessage(String s) {
                Log.d("Websocket", "Message Received is " + s);
            }

            @Override
            public void onMessage( ByteBuffer data ){
                Log.d("Websocket", "Received");
                Zipper z = new Zipper();
                z.makeZip(data,getBaseContext());
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.d("Websocket", "Closed " + s);
            }

            @Override
            public void onError(Exception e) {
                Log.d("Websocket", "Error " + e.getMessage());
            }
        };
        mWebSocketClient.connect();
    }

    public static void sendHistoryFile(ArrayList<JSONObject> json){
        while("NOT_YET_CONNECTED".equals(mWebSocketClient.getReadyState().toString()));
        Log.d("Log", mWebSocketClient.getReadyState().toString());
        StringBuilder sb = new StringBuilder();
        for(int i =0; i<json.size();i++){
            sb.append(json.get(i) + "\n");
            Log.d("JSON", json.get(i).toString());
        }
        mWebSocketClient.send(sb.toString());
    }

    private void getConnected(){
        connectWebSocket();
    }

    public boolean serviceRunning(){
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
        {
            if ("stonybrook.edu.isurfer.Iservice".equals(service.service.getClassName()))
            {
                Log.d("tag4", "Not starting");
                return true;
            }
        }
        return false;
    }
}
