package com.nimeshkadecha.safesync;

import static com.nimeshkadecha.safesync.ui.details.NgoDetail.getBranchAdapter;
import static com.nimeshkadecha.safesync.ui.details.NgoDetail.getMap;
import static com.nimeshkadecha.safesync.ui.details.NgoDetail.getNgoEmail;
import static com.nimeshkadecha.safesync.ui.details.NgoDetail.getRecVIew;
import static com.nimeshkadecha.safesync.ui.gallery.GalleryFragment.Branch_name_edt;
import static com.nimeshkadecha.safesync.ui.gallery.GalleryFragment.areaOfExpertise_branch_edt;
import static com.nimeshkadecha.safesync.ui.gallery.GalleryFragment.contactNumber_branch_edt;
import static com.nimeshkadecha.safesync.ui.gallery.GalleryFragment.equipements_edt;
import static com.nimeshkadecha.safesync.ui.gallery.GalleryFragment.getAreaOfExpertise_edt;
import static com.nimeshkadecha.safesync.ui.gallery.GalleryFragment.getContactNumber_EDT;
import static com.nimeshkadecha.safesync.ui.gallery.GalleryFragment.getNGO_name_edt;
import static com.nimeshkadecha.safesync.ui.gallery.GalleryFragment.service_branch_edt;
import static com.nimeshkadecha.safesync.ui.home.HomeFragment.getAdapter;
import static com.nimeshkadecha.safesync.ui.home.HomeFragment.getRecyclerView;

import static java.security.AccessController.getContext;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.nimeshkadecha.safesync.databinding.ActivityMain2Binding;
import com.nimeshkadecha.safesync.ui.details.NgoDetail;
import com.nimeshkadecha.safesync.ui.gallery.GalleryFragment;
import com.nimeshkadecha.safesync.ui.home.HomeFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity2 extends AppCompatActivity {


    public static final String SHARED_PREFS = "sharedPrefs";
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMain2Binding binding;

//    NGO Detail---------------------

    private ArrayList<String> Branch_Coordinates = new ArrayList<>();
    public ArrayList<String> Branch_Name = new ArrayList<>();
    private ArrayList<LatLng> markerPositions = new ArrayList<>();
    public ArrayList<String> Branch_Email = new ArrayList<>();
    private GoogleMap googleMap;
    private MapView mapView ;
//    ------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMain2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        SharedPreferences sharedPreferences = MainActivity2.this.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        String token_local = sharedPreferences.getString("token", "");
        String user_type_local = sharedPreferences.getString("user_type", "");
        String NGO_Email_local = sharedPreferences.getString("NGO_Email", "");
        String Branch_Email_local = sharedPreferences.getString("Branch_Email", "");

        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(MainActivity2.this, R.id.nav_host_fragment_content_main);
                int currentDestinationId = navController.getCurrentDestination().getId();
                // Check the current fragment and call the corresponding method
                if (currentDestinationId == R.id.nav_home) {
                    binding.appBarMain.fab.setEnabled(false);
                    Toast.makeText(MainActivity2.this, "Fetching Data ...", Toast.LENGTH_SHORT).show();

                    JSONObject postData = new JSONObject();
                    try {
                        postData.put("need", 1);
                        postData.put("token", token_local);
                        postData.put("userType", user_type_local);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    new getAllData_Again(String.valueOf(postData)).execute();

                } else if (currentDestinationId == R.id.nav_gallery) {
                    if(user_type_local.equals("ngo")){
                        binding.appBarMain.fab.setEnabled(false);
                        Toast.makeText(MainActivity2.this, "Fetching Data ...", Toast.LENGTH_SHORT).show();

                        JSONObject postData = new JSONObject();
                        try {
                            postData.put("need", 5);
                            postData.put("token", token_local);
                            postData.put("userType", user_type_local);
                            postData.put("nEmail", NGO_Email_local);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        new GetNgoProfileDetails_Again(String.valueOf(postData)).execute();
                    }else if(user_type_local.equals("branch")){
                        JSONObject postData = new JSONObject();
                        try {
                            postData.put("need", 4);
                            postData.put("token", token_local);
                            postData.put("userType", user_type_local);
                            postData.put("nEmail", NGO_Email_local);
                            postData.put("bEmail", Branch_Email_local);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        new GetBranchProfileDetails_Again(String.valueOf(postData)).execute();
                    }else{
                        Toast.makeText(MainActivity2.this, "User Typr ERROR", Toast.LENGTH_SHORT).show();
                    }


                } else if (currentDestinationId == R.id.NgoDetail) {
                    JSONObject postData = new JSONObject();
                    try {
                        postData.put("need", 2);
                        postData.put("userType", user_type_local);
                        postData.put("token", token_local);
                        postData.put("nEmail", getNgoEmail());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    new get_Branch_List_Again(String.valueOf(postData)).execute();
                    mapView =getMap();
                    mapView.onCreate(savedInstanceState);
                    mapView.getMapAsync(MainActivity2.this::onMapReady);

                } else if (currentDestinationId == R.id.nav_slideshow) {
                    Toast.makeText(MainActivity2.this, "nav_slideshow", Toast.LENGTH_SHORT).show();
                }
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,R.id.LogOut,R.id.NgoDetail)
                .setOpenableLayout(drawer)
                .build();
        
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    public class getAllData_Again extends AsyncTask<Void,Void,Void> {

        String status;

        public ArrayList<String> NGO_Name;
        public ArrayList<String> NGO_Email;

        String data;

        RecyclerView recyclerView ;

        RecyclerView.Adapter adapter;


        public getAllData_Again(String Data) {
            this.data = Data;

            NGO_Name = new ArrayList<>();
            NGO_Email = new ArrayList<>();
            recyclerView = getRecyclerView();

            adapter = getAdapter();
        }


        @Override
        protected Void doInBackground(Void... voids) {
            final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            OkHttpClient client = new OkHttpClient();
            Log.d("ENimesh", "Trying");

            Log.d("ENimesh","MY data = "+data);
            Request request = new Request.Builder()
                    .url("https://safesync.onrender.com/isLoggedIn")
                    .post(RequestBody.create(JSON, data))
                    .build();
            try {
                Response response = client.newCall(request).execute();
                String jsonData = response.body().string();
                JSONObject jsonObject = new JSONObject(jsonData);

                Log.d("ENimesh","data = "+jsonData);

                JSONArray dataArray = jsonObject.getJSONArray("data");

                // Iterate through the array to fetch nName values
                for (int i = 0; i < dataArray.length(); i++) {
                    // Get the current object in the array
                    JSONObject dataObject = dataArray.getJSONObject(i);
                    NGO_Name.add(dataObject.getString("nName"));
                    NGO_Email.add(dataObject.getString("nEmail"));
                }
                status = jsonObject.getString("status");

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);

            MainActivity2.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    binding.appBarMain.fab.setEnabled(true);
                    if(status.equals("200")){
                        adapter = new MyAdapter(getApplicationContext(),NGO_Email,NGO_Name, MainActivity2.this);
                        recyclerView.setAdapter(adapter);
                        Toast.makeText(MainActivity2.this, "Data Updated", Toast.LENGTH_SHORT).show();
                    }else{

                        Toast.makeText(MainActivity2.this, "Status = "+status, Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

    public class GetNgoProfileDetails_Again extends AsyncTask<Void, Void, Void> {

        String status;

        String data;

        String name;
        String contact;
        String experties;

        TextInputEditText NGO_name_edt,contactNumber_edt,areaOfExpertise_edt;


        public GetNgoProfileDetails_Again(String Data) {
            this.data = Data;

            NGO_name_edt = getNGO_name_edt();
            contactNumber_edt = getContactNumber_EDT();
            areaOfExpertise_edt = getAreaOfExpertise_edt();

        }


        @Override
        protected Void doInBackground(Void... voids) {
            final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            OkHttpClient client = new OkHttpClient();
            Log.d("ENimesh", "Trying");

            Log.d("ENimesh", "MY data = " + data);
            Request request = new Request.Builder()
                    .url("https://safesync.onrender.com/isLoggedIn")
                    .post(RequestBody.create(JSON, data))
                    .build();
            try {
                Response response = client.newCall(request).execute();
                String jsonData = response.body().string();
                JSONObject jsonObject = new JSONObject(jsonData);

                Log.d("ENimesh", "data = " + jsonData);

                if (jsonObject.has("status")) {
                    status = jsonObject.getString("status");

                    // Access the 'data' object directly
                    JSONObject dataObject = jsonObject.getJSONObject("data");

                    // Retrieve values from the 'data' object
                    name = dataObject.getString("nName");
                    contact = dataObject.getString("nContact");
                    experties = dataObject.getString("nExpertise");
                } else {
                    // Handle the case where 'status' field is not present in the JSON response
                    Log.e("ENimesh", "Status field not found in JSON response");
                }

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);

            MainActivity2.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    binding.appBarMain.fab.setEnabled(true);
                    if (status != null && status.equals("200")) {
                        NGO_name_edt.setText(name);
                        contactNumber_edt.setText(contact);
                        areaOfExpertise_edt.setText(experties);

                        Toast.makeText(MainActivity2.this, "Data field", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity2.this, "Status = " + status, Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

    public class GetBranchProfileDetails_Again extends AsyncTask<Void, Void, Void> {

        String status;

        String data;

        String name;
        String contact;
        String Services;
        String experties;
        String Equpments;

        TextInputEditText Branch_name_edt,contactNumber_branch_edt,service_branch_edt,areaOfExpertise_branch_edt,equipements_edt;

        public GetBranchProfileDetails_Again(String Data) {
            this.data = Data;

            Branch_name_edt = Branch_name_edt();
            contactNumber_branch_edt = contactNumber_branch_edt();
            service_branch_edt = service_branch_edt();
            areaOfExpertise_branch_edt = areaOfExpertise_branch_edt();
            equipements_edt = equipements_edt();

        }


        @Override
        protected Void doInBackground(Void... voids) {
            final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            OkHttpClient client = new OkHttpClient();
            Log.d("ENimesh", "Trying");

            Log.d("ENimesh", "MY data = " + data);
            Request request = new Request.Builder()
                    .url("https://safesync.onrender.com/isLoggedIn")
                    .post(RequestBody.create(JSON, data))
                    .build();
            try {
                Response response = client.newCall(request).execute();
                String jsonData = response.body().string();
                JSONObject jsonObject = new JSONObject(jsonData);

                Log.d("ENimesh", "data = " + jsonData);

                if (jsonObject.has("status")) {
                    status = jsonObject.getString("status");

                    // Access the 'data' object directly
                    JSONObject dataObject = jsonObject.getJSONObject("data");

                    // Retrieve values from the 'data' object
                    name = dataObject.getString("bName");
                    contact = dataObject.getString("bContact");
                    experties = dataObject.getString("bExpertise");
                    Equpments = dataObject.getString("bEquipements");
                    Services = dataObject.getString("bServices");
                    //TODO: add services
                } else {
                    // Handle the case where 'status' field is not present in the JSON response
                    Log.e("ENimesh", "Status field not found in JSON response");
                }

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);

            MainActivity2.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (status != null && status.equals("200")) {
                        Branch_name_edt.setText(name);
                        contactNumber_branch_edt.setText(contact);
                        areaOfExpertise_branch_edt.setText(experties);
                        equipements_edt.setText(Equpments);
                        service_branch_edt.setText(Services);

                        Toast.makeText(MainActivity2.this, "Data field", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity2.this, "Status = " + status, Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }


//    NGO DETAIL ---------------------------------------------------

    public class get_Branch_List_Again extends AsyncTask<Void,Void,Void> {

        String status;
        String data;

        RecyclerView recyclerView;

        branchAdapter adapter;


        public get_Branch_List_Again(String Data) {
            this.data = Data;

            recyclerView = getRecVIew();
            adapter = getBranchAdapter();

            recyclerView.setAdapter(adapter);
            Branch_Name.clear();
            Branch_Email.clear();
            Branch_Coordinates.clear();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            OkHttpClient client = new OkHttpClient();
            Log.d("ENimesh", "Trying");

            Log.d("ENimesh","MY data = "+data);
            Request request = new Request.Builder()
                    .url("https://safesync.onrender.com/isLoggedIn")
                    .post(RequestBody.create(JSON, data))
                    .build();
            try {
                Response response = client.newCall(request).execute();
                String jsonData = response.body().string();
                JSONObject jsonObject = new JSONObject(jsonData);

                Log.d("ENimesh","data = "+jsonData);

                JSONArray dataArray = jsonObject.getJSONArray("data");

                // Iterate through the array to fetch nName values
                for (int i = 0; i < dataArray.length(); i++) {
                    // Get the current object in the array
                    JSONObject dataObject = dataArray.getJSONObject(i);
                    Branch_Name.add(dataObject.getString("bName"));
                    Branch_Email.add(dataObject.getString("bEmail"));
                    Branch_Coordinates.add(dataObject.getString("bCoordinates"));

                    addMarkerFromJsonObject(dataObject);

                }
                status = jsonObject.getString("status");

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            adapter = new branchAdapter(getApplicationContext(),Branch_Email,Branch_Name, MainActivity2.this);
            recyclerView.setAdapter(adapter);

            MainActivity2.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (googleMap != null) {
                        // Iterate through the list of branch coordinates and add markers
                        for (int i = 0; i < Branch_Name.size(); i++) {
                            String branchName = Branch_Name.get(i);
                            double latitude = Double.parseDouble(Branch_Coordinates.get(i).split(",")[0]);
                            double longitude = Double.parseDouble(Branch_Coordinates.get(i).split(",")[1]);

                            addMarkerToMap(branchName, latitude, longitude);
                        }

                        // Zoom to fit all markers on the map
                        zoomToFitMarkers();
                    }
                }
            });

        }
    }
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        // Enable the zoom controls
        googleMap.getUiSettings().setZoomControlsEnabled(true);

    }
    public void zoomToFitMarkers() {
        // Create a LatLngBounds builder
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        // Iterate through all markers and include their positions in the builder
        for (LatLng markerPosition : markerPositions) {
            builder.include(markerPosition);
        }

        // Build the LatLngBounds
        LatLngBounds bounds = builder.build();

        // Calculate padding to make markers visible
        int padding = 50;

        // Animate the camera to fit all markers with padding
        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
    }

    public void addMarkerFromJsonObject(JSONObject dataObject) {

        try {
            String branchName = dataObject.getString("bName");
            String coordinates = dataObject.getString("bCoordinates");

            // Split the coordinates string into latitude and longitude
            String[] latLngArray = coordinates.split(",");
            double latitude = Double.parseDouble(latLngArray[0]);
            double longitude = Double.parseDouble(latLngArray[1]);

            // Add a marker to the map
            addMarkerToMap(branchName, latitude, longitude);

            // Save the marker position for later use
            LatLng markerPosition = new LatLng(latitude, longitude);
            markerPositions.add(markerPosition);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void addMarkerToMap(String title, double latitude, double longitude) {
        MainActivity2.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (googleMap != null) {
                    LatLng location = new LatLng(latitude, longitude);
                    googleMap.addMarker(new MarkerOptions().position(location).title(title));
                }
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main_activity2, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        NavController navController = Navigation.findNavController(MainActivity2.this, R.id.nav_host_fragment_content_main);
        int currentDestinationId = navController.getCurrentDestination().getId();

        // Check if the fragment is the one you want to handle back press for
        if (currentDestinationId == R.id.nav_home) {
            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity2.this);
            alert.setTitle("Exit App");
            alert.setMessage("Confirm Exit");
            alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finishAffinity();
                }
            });
            alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });

            alert.show();
        } else {
            // If not the desired fragment, let the system handle the back press
            super.onBackPressed();
        }
    }
}