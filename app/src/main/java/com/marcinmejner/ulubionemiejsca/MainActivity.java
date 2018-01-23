package com.marcinmejner.ulubionemiejsca;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    static ArrayList<String> miejsca = new ArrayList<>();
    static ArrayList<LatLng> lokacje = new ArrayList<>();
    static ArrayAdapter adapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listView);
        SharedPreferences sharedPreferences =  this.getSharedPreferences("com.marcinmejner.ulubionemiejsca", Context.MODE_PRIVATE);

        ArrayList<String> latitudes = new ArrayList<>();
        ArrayList<String> longitudes = new ArrayList<>();
        miejsca.clear();
        latitudes.clear();
        longitudes.clear();
        lokacje.clear();

        try {
            miejsca = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("miejsca", ObjectSerializer.serialize(new ArrayList<String >())));

            latitudes = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("latitudes", ObjectSerializer.serialize(new ArrayList<String >())));

            longitudes = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("longitudes", ObjectSerializer.serialize(new ArrayList<String >())));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(miejsca.size()>0 && latitudes.size()>0 && longitudes.size()>0){
            if(miejsca.size() == latitudes.size() && longitudes.size() == latitudes.size()){

                for (int i = 0; i < latitudes.size() ; i++) {

                    lokacje.add(new LatLng(Double.parseDouble(latitudes.get(i)), Double.parseDouble(longitudes.get(i))));

                }

            }

        }


        lokacje.add(new LatLng(0,0));
        miejsca.add("Dodaj nowe miejsce...");

        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, miejsca);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                intent.putExtra("numerMiejsca", i);
                startActivity(intent);



            }
        });

    }
}
