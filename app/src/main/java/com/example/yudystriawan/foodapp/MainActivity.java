package com.example.yudystriawan.foodapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private static final String WEATHER_LINK = "https://api.openweathermap.org/data/2.5/weather?q=Malang,id&appid=28c444227fbea12e1d303822b43f327f";
    private TextView tLat, tLon, tTemp, tName, tDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tLat = findViewById(R.id.lat);
        tLon = findViewById(R.id.lon);
        tTemp = findViewById(R.id.temp);
        tName = findViewById(R.id.name);
        tDescription = findViewById(R.id.description);

        getWeather();
    }

    private void getWeather() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, WEATHER_LINK, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject coord = response.getJSONObject("coord");
                    String lat = String.valueOf(coord.getDouble("lat"));
                    String lon = String.valueOf(coord.getDouble("lon"));

                    JSONObject main = response.getJSONObject("main");
                    double temp = main.getDouble("temp")-273.15;

                    String name = response.getString("name");

                    JSONArray weather = response.getJSONArray("weather");
                    JSONObject index = weather.getJSONObject(0);
                    String description = index.getString("description");

                    tLat.setText("Lat : "+lat);
                    tLon.setText("Lon : "+lon);
                    tTemp.setText("Temp : "+temp);
                    tName.setText("Name : "+name);
                    tDescription.setText("Description : "+description);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "No Connection", Toast.LENGTH_SHORT).show();
            }
        });

        Singleton.getInstance(this.getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }
}
