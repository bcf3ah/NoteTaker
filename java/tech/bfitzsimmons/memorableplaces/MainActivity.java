package tech.bfitzsimmons.memorableplaces;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    Intent intent;
    public static ArrayAdapter<String> adapter;
    public static SharedPreferences sharedPreferences;
    public static ArrayList<String> placeNames = new ArrayList<>();
    public static ArrayList<Double> placeLats = new ArrayList<>();
    public static ArrayList<Double> placeLongs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize listView
        listView = (ListView) findViewById(R.id.listMain);
        //initialize sharedPreferences
        sharedPreferences = this.getSharedPreferences("tech.bfitzsimmons.memorableplaces", Context.MODE_PRIVATE);
//        sharedPreferences.edit().clear().apply();

        //check sharedPref storage for list of placeNames
        Type typeOfStringList = new TypeToken<ArrayList<String>>() { }.getType();
        String serializedNames = sharedPreferences.getString("placeNames", new Gson().toJson(new ArrayList<String>()));
        ArrayList<String> deserializedNames = new Gson().fromJson(serializedNames,typeOfStringList);


        //check sharedPref storage for lats and longs
        Type typeOfDoubleList = new TypeToken<ArrayList<Double>>() { }.getType();
        String serializedLats = sharedPreferences.getString("placeLats", new Gson().toJson(new ArrayList<Double>()));
        ArrayList<Double> deserializedLats = new Gson().fromJson(serializedLats,typeOfDoubleList);
        String serializedLongs = sharedPreferences.getString("placeLongs", new Gson().toJson(new ArrayList<Double>()));
        ArrayList<Double> deserializedLongs = new Gson().fromJson(serializedLongs,typeOfDoubleList);


        //now repopulate if they exist
        if(deserializedNames.size() > 0 && deserializedLats.size() > 0 && deserializedLongs.size() > 0){
            placeNames = new ArrayList<>(deserializedNames);
            placeLats = new ArrayList<>(deserializedLats);
            placeLongs = new ArrayList<>(deserializedLongs);
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, placeNames);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.putExtra("place", placeNames.get(i));
                intent.putExtra("lat", placeLats.get(i));
                intent.putExtra("lng", placeLongs.get(i));
                startActivity(intent);
            }
        });
    }

    public void goToMap(View view){
        intent = new Intent(getApplicationContext(), MapsActivity.class);
        startActivity(intent);
    }
}
