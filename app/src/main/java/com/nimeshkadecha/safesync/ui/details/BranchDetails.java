package com.nimeshkadecha.safesync.ui.details;

import static android.content.Context.MODE_PRIVATE;

import static com.nimeshkadecha.safesync.ui.details.NgoDetail.getBranchAdapter;
import static com.nimeshkadecha.safesync.ui.details.NgoDetail.getRecVIew;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.audiofx.DynamicsProcessing;
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
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nimeshkadecha.safesync.Equpment2Adapter;
import com.nimeshkadecha.safesync.EqupmentAdapter;
import com.nimeshkadecha.safesync.MainActivity2;
import com.nimeshkadecha.safesync.R;
import com.nimeshkadecha.safesync.branchAdapter;
import com.nimeshkadecha.safesync.databinding.FragmentBranchDetailsBinding;
import com.nimeshkadecha.safesync.databinding.FragmentNgoDetailBinding;
import com.nimeshkadecha.safesync.ui.gallery.GalleryFragment;

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

public class BranchDetails extends Fragment {

    private FragmentBranchDetailsBinding binding;

    private ArrayList<String> Branch_Coordinates = new ArrayList<>();
    public ArrayList<String> Branch_Name = new ArrayList<>();
    private ArrayList<LatLng> markerPositions = new ArrayList<>();
    public ArrayList<String> Branch_Email = new ArrayList<>();
    private GoogleMap googleMap;
    public static MapView mapView;

    public static MapView mapViewBranch(){
        return mapView;
    }

    private View PlodingView;
    private LinearLayout LoadingBlur;

    static RecyclerView recyclerView;

    public static RecyclerView branchDetailRecView(){
        return recyclerView;
    }


    static Equpment2Adapter adapterDetails;

    public static Equpment2Adapter getEqAdapter(){
        return adapterDetails;
    }

    //    Verifying Wifi / internet is ON --------------------------------------------------------------
    boolean checkConnection() {
        ConnectivityManager manager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo net = manager.getActiveNetworkInfo();

        if (net == null) {
            return false;
        } else {
            return true;
        }
    }
//--------------------------------------------------------------------------------------------------

    static TextView BranchNameTV, ExpertiesTV, ServicesTV, NgoNameTV, NGOEmailTV, BranchContactTV, BranchEmailTV, AddressTV;

    public static TextView get_BranchNameTV(){
        return BranchNameTV;
    }

    public static TextView get_ExpertiesTV(){
        return ExpertiesTV;
    }

    public static TextView get_ServicesTV(){
        return ServicesTV;
    }

    public static TextView get_NgoNameTV(){
        return NgoNameTV;
    }

    public static TextView get_NGOEmailTV(){
        return NGOEmailTV;
    }

    public static TextView get_BranchContactTV(){
        return BranchContactTV;
    }

    public static TextView get_BranchEmailTV(){
        return BranchEmailTV;
    }

    public static TextView get_AddressTV(){
        return AddressTV;
    }




    static String NgoEmail;
    static String BranchEmail;

    public static String getCurrentNgoEmail(){
        return NgoEmail;
    }

    public static String getCurrentBranchEmail(){
        return BranchEmail;
    }

    public static final String SHARED_PREFS = "sharedPrefs";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentBranchDetailsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        PlodingView = root.findViewById(R.id.Ploding);
        LoadingBlur = root.findViewById(R.id.LoadingBlur);
        PlodingView.setVisibility(View.GONE);
        LoadingBlur.setVisibility(View.GONE);

        recyclerView = root.findViewById(R.id.recDetails);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        String BranchName = getArguments().getString("BranchName", "");


        BranchEmail = getArguments().getString("BranchEmail", "");

        NgoEmail = getArguments().getString("NgoEmail", "");

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        String token_local = sharedPreferences.getString("token", "");
        String user_type_local = sharedPreferences.getString("user_type", "");

        BranchNameTV = root.findViewById(R.id.Branch_Name_TV);
        ExpertiesTV = root.findViewById(R.id.Expertise_TV);
        ServicesTV = root.findViewById(R.id.Service_TV);
        BranchContactTV = root.findViewById(R.id.Branch_Contact_TV);
        NgoNameTV = root.findViewById(R.id.Ngo_Title_TV);
        AddressTV = root.findViewById(R.id.Address_TV);

        BranchEmailTV = root.findViewById(R.id.Branch_Email_TV);
        BranchEmailTV.setText("Email = " + BranchEmail);
        NGOEmailTV = root.findViewById(R.id.NGO_Email_TV);
        NGOEmailTV.setText(NgoEmail);
        BranchNameTV.setText(BranchName + "Contact");

        if (checkConnection()) {
            JSONObject postData = new JSONObject();
            try {
                postData.put("need", 4);
                postData.put("token", token_local);
                postData.put("userType", user_type_local);
                postData.put("nEmail", NgoEmail);
                postData.put("bEmail", BranchEmail);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            PlodingView.setVisibility(View.VISIBLE);
            LoadingBlur.setVisibility(View.VISIBLE);
            new get_Branch_Details(String.valueOf(postData)).execute();
            Log.d("SNimesh", "Getting branch details...");

            mapView = root.findViewById(R.id.mapView2);
            mapView.onCreate(savedInstanceState);
            mapView.getMapAsync(this::onMapReady);
        }

        return root;

    }

    public class get_Branch_Details extends AsyncTask<Void, Void, Void> {

        String status;
        String data;
        String name;
        String contact;
        String Services;
        String experties;
        String Address;
        String BranchEmail;

        ArrayList <String> nameArr = new ArrayList<>();
        ArrayList <String> quentityArr = new ArrayList<>();

        public get_Branch_Details(String Data) {
            this.data = Data;
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

                if (jsonObject.has("status")) {
                    status = jsonObject.getString("status");

                    // Access the 'data' object directly
                    JSONObject dataObject = jsonObject.getJSONObject("data");

                    // Retrieve values from the 'data' object
                    name = dataObject.getString("bName");
                    contact = dataObject.getString("bContact");
                    experties = dataObject.getString("bExpertise");
                    BranchEmail = dataObject.getString("bEmail");
//                    Equpments = dataObject.getString("bEquipements");

                    addMarkerFromJsonObject(dataObject);
                    JSONArray dataArray = dataObject.getJSONArray("bEquipements");

                    // Iterate through the array to fetch nName values
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject dataObject1 = dataArray.getJSONObject(i);

                        // Get the current object in the array
                        nameArr.add(dataObject1.getString("eqName"));
                        quentityArr.add(dataObject1.getString("eqQuantity"));
                    }


                    Services = dataObject.getString("bServices");
                    Branch_Coordinates.add(dataObject.getString("bCoordinates"));
                    Address = dataObject.getString("bAddress");

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
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    PlodingView.setVisibility(View.GONE);
                    LoadingBlur.setVisibility(View.GONE);

                    if (status.equals("200")) {
                        ServicesTV.setText(Services);
                        ExpertiesTV.setText(experties);
                        AddressTV.setText("Address :"+Address);
                        BranchContactTV.setText("Contact Number :"+contact);
                        BranchEmailTV.setText("Email :"+BranchEmail);

                        adapterDetails = new Equpment2Adapter(getContext(),nameArr,quentityArr);
                        recyclerView.setAdapter(adapterDetails);
                        adapterDetails.notifyDataSetChanged();

                    } else {
                        Toast.makeText(getActivity(), "Server Message :" + status, Toast.LENGTH_SHORT).show();
                    }

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
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (googleMap != null) {
                    LatLng location = new LatLng(latitude, longitude);
                    googleMap.addMarker(new MarkerOptions().position(location).title(title));
                }
            }
        });

    }

}