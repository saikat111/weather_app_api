package com.codingburg.weatherappapi;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private TextView tempa,locations,timezone,lat,lon,datetime,sunrise,sunset,uv,wind;
    LocationManager locationManager;
    String latitude, longitude;
    private RequestQueue mRequestQueue;
    private static final int REQUEST_LOCATION = 1;
    ProgressBar l;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tempa = findViewById(R.id.tempa);
        locations = findViewById(R.id.location);
        timezone = findViewById(R.id.timezone);
        lat = findViewById(R.id.lat);
        lon = findViewById(R.id.lon);
        datetime = findViewById(R.id.datetime);
        sunrise = findViewById(R.id.sunrise);
        sunset = findViewById(R.id.sunset);
        uv = findViewById(R.id.uv);
        l = findViewById(R.id.l);
        wind = findViewById(R.id.wind);
        mRequestQueue = Volley.newRequestQueue(this);
        ActivityCompat.requestPermissions( this,
                new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        locations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                l.setVisibility(View.VISIBLE);
                /*location nManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);*/
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    OnGPS();
                } else {
                    getLocation();
                }
            }
        });
    }
    private void OnGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("Yes", new  DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(
                MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {

            LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            float longitude = (float) location.getLongitude();
            float latitude = (float) location.getLatitude();
            lat.setText(String.valueOf(latitude));
            lon.setText(String.valueOf(longitude));
            getWeatherdata(lat.getText().toString(), lon.getText().toString());
                /*Toast.makeText(this, "Unable to find location.", Toast.LENGTH_SHORT).show();*/

        }
    }

    private void getWeatherdata(String lat, String lon) {
        String BASE_URL = "https://api.weatherbit.io/v2.0/current?lat=" + lat + "&lon=" + lon + "&key=af35c70e3c4d48b899ee9be2d07c53f1&include=minutely";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, BASE_URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    JSONArray jsonArray = response.getJSONArray("data");
                    for(int i = 0; i<jsonArray.length(); i++){
                        JSONObject dataget = jsonArray.getJSONObject(i);
                        locations.setText(dataget.getString("city_name"));
                        timezone.setText(dataget.getString("timezone"));
                       // sunrise.setText(dataget.getString("sunrise"));
                       // sunset.setText(dataget.getString("sunset"));
                         sunrise.setText("6:15");
                         sunset.setText("5:18");
                        datetime.setText(dataget.getString("datetime"));
                        uv.setText(String.valueOf(dataget.getInt("uv")));
                        tempa.setText( String.valueOf(dataget.getInt("app_temp")));
                        wind.setText( String.valueOf(dataget.getInt("wind_spd")));
                        l.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        mRequestQueue.add(jsonObjectRequest);
    }
}