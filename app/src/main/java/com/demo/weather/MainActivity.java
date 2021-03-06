package com.demo.weather;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather?q=%s&appid=3a7fd8f6c4b9eba310051c3fa2376ed0&lang=ru&units=metric";
    private TextView textView;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textViewWeather);
        editText = findViewById(R.id.editText);
    }

    public void onClickWeather(View view) throws ExecutionException, InterruptedException {
        String city = editText.getText().toString().trim();


        if (!city.isEmpty()) {
            DownloadJSONWeather task = new DownloadJSONWeather();
            String url = String.format(WEATHER_URL, city);
            String info = task.execute(url).get().toString();
        }
    }


    private class DownloadJSONWeather extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            URL url = null;
            HttpURLConnection httpURLConnection = null;
            StringBuilder stringBuilder = new StringBuilder();

            try {
                url = new URL(strings[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                String line = reader.readLine();
                while (line != null) {
                    stringBuilder.append(line);
                    line = reader.readLine();
                }

                return stringBuilder.toString();
            } catch (IOException e) {
                e.printStackTrace();

            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }

            return stringBuilder.toString();
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                String name = jsonObject.getString("name");
                String temp = jsonObject.getJSONObject("main").getString("temp");
                int tempRound = (int) Math.round(Double.parseDouble(temp));
                String description = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");
                String weather = String.format(getString(R.string.description_weather), name, tempRound, description);
                textView.setText(weather);
            } catch (JSONException ex) {
                ex.printStackTrace();
                String sdf = s.toString();
                Toast.makeText(getApplicationContext(), R.string.false_search, Toast.LENGTH_SHORT).show();
            }

        }
    }

}
