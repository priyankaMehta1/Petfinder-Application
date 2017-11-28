package com.example.a2019pmehta.apiapp;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
 
public class APIApp extends AppCompatActivity
{
    private TextView result;
    private EditText input;
    private Button search;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apiapp);
        input = (EditText) findViewById(R.id.input);
        result = (TextView) findViewById(R.id.text2);
        search = (Button) findViewById(R.id.button);
        search.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                try {
                    String pets = "barnyard, bird, cat, dog, horse, reptile, smallfurry, potato";
                    if (pets.indexOf(input.getText().toString()) != -1)
                    {
                        if(input.getText().toString().equals("dog"))
                        {sendNotification();}
                        NetworkAsyncTask aync = new NetworkAsyncTask();
                        aync.execute();
                    }
                    else
                    {result.setText(R.string.error);}
                }
                catch (Exception e)
                {result.setText(e.toString());}
            }
        });
    }
        public void sendNotification()
        {
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.pfpfpf).setContentTitle("Petfinder update!").setContentText("Searched for dog!");
            Intent resultIntent = new Intent(this, APIApp.class);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(001, mBuilder.build());
        }
    class NetworkAsyncTask extends AsyncTask<Void, Void, Void>
    {
        private String stringy;
        private String inputt = input.getText().toString();
        protected Void doInBackground(Void... params)
        {
            try
            {
                URL url = new URL("http://api.petfinder.com/breed.list?key=2b192262a1b17a16d40e3e117f30d85d&animal=" + inputt + "&format=json");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(conn.getInputStream());
                StringBuilder stringBuilder = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in,"utf-8"),8);
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                stringy = stringBuilder.toString();
                Log.d("s",stringy);
            }
            catch(Exception e)
            {Log.e("bob","",e);}
            return null;
        }
        protected void onPostExecute(Void resul)
        {
            try
            {
                JSONObject jsonObj = new JSONObject(stringy);
                stringy = jsonObj.toString();
                stringy = stringy.substring(stringy.indexOf("breed")+30);
                stringy = stringy.substring(stringy.indexOf("A"));
                stringy = stringy.replace("{\"$t\":\"", "");
                stringy = stringy.replace("\"},", ", ");
                int count = 0;
                for(int i = 0; i < stringy.length(); i++)
                {
                    if(stringy.charAt(i) == ',')
                    {count ++;}
                    if(count == 10)
                    {
                        stringy = stringy.substring(0,i+1);
                        break;
                    }
                }
                stringy = stringy.substring(0,stringy.length()-1);
                stringy = stringy.replace(", ", "\n");
                result.setText(stringy);
            }
            catch(Exception e)
            {result.setText(stringy);}
        }
    }
}