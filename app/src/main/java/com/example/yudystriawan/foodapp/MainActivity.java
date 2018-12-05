package com.example.yudystriawan.foodapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private TextView tLat, tLon, tTemp, tName, tDescription, tMyLat, tMyLon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        tLat = findViewById(R.id.lat);
        tLon = findViewById(R.id.lon);
        tTemp = findViewById(R.id.temp);
        tName = findViewById(R.id.name);
        tDescription = findViewById(R.id.description);
        tMyLat = findViewById(R.id.myLat);
        tMyLon = findViewById(R.id.myLon);

        getLastKnownLocation();
    }

    private void getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},REQUEST_LOCATION_PERMISSION );
        }
        mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    getWeather(location.getLatitude(), location.getLongitude());
                }
            }

        });
    }

    private void getWeather(double latitude, double longitude) {
        String WEATHER_LINK = "https://api.openweathermap.org/data/2.5/weather?lat="+latitude+"&lon="+longitude+"&appid=28c444227fbea12e1d303822b43f327f";
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
