package stonybrook.edu.isurfer;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import static stonybrook.edu.isurfer.Iservice.sendHistoryFile;

public class MainActivity extends AppCompatActivity {
    TextView editText;
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webView = (WebView)findViewById(R.id.web_view);

        Toast.makeText(this, "App Started", Toast.LENGTH_LONG).show();


        if(!serviceRunning()) {
            startService(new Intent(getBaseContext(), Iservice.class));
            editText = (TextView) findViewById(R.id.textBox);
            editText.setText("Registration Successful");
            Log.d("tag3", "Service Started");
        }
        else Log.d("tag3", "Service Already Started");
    }

    public boolean serviceRunning(){
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
        {
            if ("mcc.project.intermittentNetworkService".equals(service.service.getClassName()))
            {
                Log.d("tag4", "Not starting");
                return true;
            }
        }
        return false;
    }

    public void update(View v){
        HistoryFetcher fH = new HistoryFetcher();

        sendHistoryFile(fH.fetchHistory(getBaseContext()));
    }

    public void url1(View v){
        File f = new File(getFilesDir() + "/userFolder/url_1.html");
        Log.d("path",f.getAbsolutePath());
        String url = "file:///" + f.getAbsolutePath() ;
        Intent myIntent = new Intent(MainActivity.this, Webview.class);
        myIntent.putExtra("url", url); //Optional parameters
        MainActivity.this.startActivity(myIntent);
    }

    public void url2(View v){
        File f = new File(getFilesDir() + "/userFolder/url_2.html");
        Log.d("path",f.getAbsolutePath());
        String url = "file:///" + f.getAbsolutePath() ;
        Intent myIntent = new Intent(MainActivity.this, Webview.class);
        myIntent.putExtra("url", url); //Optional parameters
        MainActivity.this.startActivity(myIntent);
    }

    public void url3(View v){
        File f = new File(getFilesDir() + "/userFolder/url_3.html");
        Log.d("path",f.getAbsolutePath());
        String url = "file:///" + f.getAbsolutePath() ;
        Intent myIntent = new Intent(MainActivity.this, Webview.class);
        myIntent.putExtra("url", url); //Optional parameters
        MainActivity.this.startActivity(myIntent);
    }
}
