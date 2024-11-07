package com.nimeshkadecha.safesync.ui.details;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nimeshkadecha.safesync.R;
import com.nimeshkadecha.safesync.branchAdapter;
import com.nimeshkadecha.safesync.databinding.FragmentNgoDetailBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NgoDetail extends Fragment {


    private FragmentNgoDetailBinding binding;
    private final ArrayList<String> Branch_Coordinates = new ArrayList<>();
    public ArrayList<String> Branch_Name;
    private final ArrayList<LatLng> markerPositions = new ArrayList<>();
    public static ArrayList<String> Branch_Email;
    private GoogleMap googleMap;
    static MapView mapView;

    public static MapView getMap_NGO(){
        return mapView;
    }

    static RecyclerView recyclerView;

    public static RecyclerView getRecVIew(){
        return recyclerView;
    }
    static String NgoEmail;
    public static String getNgoEmail(){
        return NgoEmail;
    }
    static branchAdapter adapter;

    private View PlodingView;
    private LinearLayout LoadingBlur;

    public static branchAdapter getBranchAdapter(){
        return adapter;
    }

    //    Verifying Wifi / internet is ON --------------------------------------------------------------
    boolean checkConnection() {
        ConnectivityManager manager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo net = manager.getActiveNetworkInfo();

        return net != null;
    }
//--------------------------------------------------------------------------------------------------
    public static final String SHARED_PREFS = "sharedPrefs";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        binding = FragmentNgoDetailBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        PlodingView = root.findViewById(R.id.Ploding);
        LoadingBlur = root.findViewById(R.id.LoadingBlur);
        PlodingView.setVisibility(View.GONE);
        LoadingBlur.setVisibility(View.GONE);

        Branch_Name = new ArrayList<>();
        Branch_Email = new ArrayList<>();
        // Inside YourDestinationFragment
        String NgoName = getArguments().getString("NgoName", "");

        NgoEmail = getArguments().getString("NgoEmail", "");

        TextView heading = root.findViewById(R.id.Ngo_nameHead);

        heading.setText(NgoName);

        mapView = root.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this::onMapReady);

        recyclerView = root.findViewById(R.id.branchList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        String token_local = sharedPreferences.getString("token","");
        String user_type_local = sharedPreferences.getString("user_type","");

        if(checkConnection()){
            JSONObject postData = new JSONObject();
            try {
                postData.put("need", 2);
                postData.put("userType", user_type_local);
                postData.put("token", token_local);
                postData.put("nEmail", NgoEmail);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            PlodingView.setVisibility(View.VISIBLE);
            LoadingBlur.setVisibility(View.VISIBLE);

            new get_Branch_List(String.valueOf(postData)).execute();

            Log.d("SNimesh","Request for Branch List in NgoDetail page...");
        }

        return root;
    }

    public class get_Branch_List extends AsyncTask<Void,Void,Void> {

        String status;


        String data;

        public get_Branch_List(String Data) {
            this.data = Data;

            recyclerView.setAdapter(adapter);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://safesync.onrender.com/isLoggedIn")
                    .post(RequestBody.create(JSON, data))
                    .build();
            try {
                Response response = client.newCall(request).execute();
                String jsonData = response.body().string();
                JSONObject jsonObject = new JSONObject(jsonData);

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
            adapter = new branchAdapter(getContext(),Branch_Email,Branch_Name, getActivity(),NgoEmail);
            recyclerView.setAdapter(adapter);

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    PlodingView.setVisibility(View.GONE);
                    LoadingBlur.setVisibility(View.GONE);

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
        requireActivity().runOnUiThread(() -> {
            if (googleMap != null) {
                LatLng location = new LatLng(latitude, longitude);
                googleMap.addMarker(new MarkerOptions().position(location).title(title));
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void onMapReady(GoogleMap map) {
        googleMap = map;
        // Enable the zoom controls
        googleMap.getUiSettings().setZoomControlsEnabled(true);

    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }}