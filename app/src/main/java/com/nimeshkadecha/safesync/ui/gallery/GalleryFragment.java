package com.nimeshkadecha.safesync.ui.gallery;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.nimeshkadecha.safesync.EqupmentAdapter;
import com.nimeshkadecha.safesync.LoginScreen;
import com.nimeshkadecha.safesync.MyAdapter;
import com.nimeshkadecha.safesync.R;
import com.nimeshkadecha.safesync.databinding.FragmentGalleryBinding;
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

public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;


    public static final String SHARED_PREFS = "sharedPrefs";

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

    static TextInputEditText areaOfExpertise_edt;

    public static TextInputEditText getAreaOfExpertise_edt(){
        return areaOfExpertise_edt;
    }

    private View PlodingView;
    private LinearLayout LoadingBlur;
    static TextInputEditText NGO_name_edt;

    public static TextInputEditText getNGO_name_edt(){
        return NGO_name_edt;
    }
    static TextInputEditText contactNumber_edt;

    public static TextInputEditText getContactNumber_EDT(){
        return contactNumber_edt;
    }

    TextView NGO_Name,BranchEmail;
    static TextInputEditText Branch_name_edt,contactNumber_branch_edt,service_branch_edt,areaOfExpertise_branch_edt,equipements_edt,Quentity;

    public static TextInputEditText Branch_name_edt(){
        return Branch_name_edt;
    }
    public static TextInputEditText contactNumber_branch_edt(){
        return contactNumber_branch_edt;
    }
    public static TextInputEditText areaOfExpertise_branch_edt(){
        return areaOfExpertise_branch_edt;
    }
    public static TextInputEditText equipements_edt(){
        return equipements_edt;
    }
    public static TextInputEditText getQuentityEDT(){
        return Quentity;
    }
    public static TextInputEditText service_branch_edt(){
        return service_branch_edt;
    }

    String conradates;
    String Address;
    int pageLoadCounter = 0;

    static RecyclerView recyclerView;
    static EqupmentAdapter adapter;

    public static RecyclerView getRec(){
        return recyclerView;
    }

    public static EqupmentAdapter getEqupmentAdapter(){
        return adapter;
    }


    static ArrayList quentity, name;

    public static ArrayList getQuentityArray (){
        return quentity;
    }

    public static ArrayList getNameArray(){
        return name;
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        String token_local = sharedPreferences.getString("token", "");
        String user_type_local = sharedPreferences.getString("user_type", "");
        String NGO_Email_local = sharedPreferences.getString("NGO_Email", "");
        String Branch_Email_local = sharedPreferences.getString("Branch_Email", "");

        PlodingView = root.findViewById(R.id.Ploding);
        LoadingBlur = root.findViewById(R.id.LoadingBlur);
        PlodingView.setVisibility(View.GONE);
        LoadingBlur.setVisibility(View.GONE);

        // branch -------------------------------
        contactNumber_branch_edt = root.findViewById(R.id.contactNumber_branch);

        service_branch_edt = root.findViewById(R.id.service_branch);

        areaOfExpertise_branch_edt = root.findViewById(R.id.areaOfExpertise_branch);

        Button update_branch = root.findViewById(R.id.Update_Branch_profile);

        NGO_Name= root.findViewById(R.id.NGO_Name_heading);
        NGO_Name.setText("NGO -"+NGO_Email_local);
        BranchEmail= root.findViewById(R.id.Branch_Email_title);
        Quentity= root.findViewById(R.id.Quentity);
        equipements_edt= root.findViewById(R.id.EqName);

        Branch_name_edt = root.findViewById(R.id.Branch_name);
        BranchEmail.setText("Branch -"+Branch_Email_local);

        recyclerView = root.findViewById(R.id.recyclerView_Eq);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // ngo-----------------------------
        TextView NGO_Email_Heading;

        NGO_Email_Heading = root.findViewById(R.id.NGO_Email_Heading);
        NGO_Email_Heading.setText(NGO_Email_local);

        NGO_name_edt = root.findViewById(R.id.NGO_name);
        contactNumber_edt = root.findViewById(R.id.contactNumber);
        areaOfExpertise_edt = root.findViewById(R.id.areaOfExpertise);

        Button Update_Ngo_profile = root.findViewById(R.id.Update_Ngo_profile);

        LinearLayout NGO_ProfileDetails = root.findViewById(R.id.NGO_Profile_Layout);
        LinearLayout Branch_ProfileDetails = root.findViewById(R.id.Branch_Profile_Layout);

        // ---------------------------------------
        switch (user_type_local) {
            case "ngo":
                if(pageLoadCounter == 0){
                    pageLoadCounter++;
                    if (checkConnection()) {
                        JSONObject postData = new JSONObject();
                        try {
                            postData.put("need", 5);
                            postData.put("token", token_local);
                            postData.put("userType", user_type_local);
                            postData.put("nEmail", NGO_Email_local);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        PlodingView.setVisibility(View.VISIBLE);
                        LoadingBlur.setVisibility(View.VISIBLE);
                        new GetNgoProfileDetails(String.valueOf(postData)).execute();
                    }
                }

                NGO_ProfileDetails.setVisibility(View.VISIBLE);
                Branch_ProfileDetails.setVisibility(View.GONE);
                Update_Ngo_profile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (checkConnection()) {
                            if (valideEdittext(NGO_name_edt)) {
                                if (valideEdittext(contactNumber_edt) && contactNumber_edt.getText().toString().length() == 10) {
                                    if (valideEdittext(areaOfExpertise_edt)) {
                                        PlodingView.setVisibility(View.VISIBLE);
                                        LoadingBlur.setVisibility(View.VISIBLE);

                                        JSONObject postData1 = new JSONObject();

                                        try{
                                            try {
                                                postData1.put("email", NGO_Email_local);
                                                postData1.put("name", NGO_name_edt.getText().toString());
                                                postData1.put("contact", contactNumber_edt.getText().toString());
                                                postData1.put("expertise", areaOfExpertise_edt.getText().toString());
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }

                                        PlodingView.setVisibility(View.VISIBLE);
                                        LoadingBlur.setVisibility(View.VISIBLE);
                                        new UpdateNgoDetails(String.valueOf(postData1)).execute();
                                        
                                    } else {
                                        areaOfExpertise_edt.setError("List all Area of Expertise separated by (,) ");
                                    }
                                } else {
                                    contactNumber_edt.setError("Enter valid contact number");
                                }
                            } else {
                                NGO_name_edt.setError("Enter NGO name");
                            }
                        } else {
                            Toast.makeText(getActivity(), "No Internet", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                break;

            case "branch":
                if(pageLoadCounter == 0){
                    pageLoadCounter++;
                    if (checkConnection()) {
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
                        PlodingView.setVisibility(View.VISIBLE);
                        LoadingBlur.setVisibility(View.VISIBLE);
                        new GetBranchProfileDetails(String.valueOf(postData)).execute();
                    }
                }

                NGO_ProfileDetails.setVisibility(View.GONE);
                Branch_ProfileDetails.setVisibility(View.VISIBLE);

                name = new ArrayList<>();

                quentity = new ArrayList<>();


                update_branch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (checkConnection()) {
                            JSONObject postData = new JSONObject();
                            try {
                                postData.put("bEmail", Branch_Email_local);
                                postData.put("nEmail", NGO_Email_local);
                                postData.put("name", Branch_name_edt.getText().toString());
                                postData.put("address", Address);
                                postData.put("contact", contactNumber_branch_edt.getText().toString());
                                postData.put("coordinates", conradates);
                                postData.put("services", service_branch_edt.getText().toString());
                                postData.put("expertise", areaOfExpertise_branch_edt.getText().toString());
                                JSONArray equipmentArray = new JSONArray();

                                // Assuming 'name' and 'quantity' have the same size
                                for (int i = 0; i < name.size(); i++) {
                                    JSONObject equipmentObject = new JSONObject();
                                    equipmentObject.put("eqName", name.get(i));
                                    equipmentObject.put("eqQuantity", quentity.get(i));
                                    equipmentArray.put(equipmentObject);
                                }

                                postData.put("equipements", equipmentArray);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            Log.d("ENimesh","Posting data : = "+postData);
                            PlodingView.setVisibility(View.VISIBLE);
                            LoadingBlur.setVisibility(View.VISIBLE);
                            new UpdateBranchDetails(String.valueOf(postData)).execute();

//                            if (valideEdittext(Branch_name_edt)) {
//                                if (valideEdittext(contactNumber_branch_edt) && contactNumber_branch_edt.getText().toString().length() == 10) {
//                                    if (valideEdittext(service_branch_edt)) {
//                                        if (valideEdittext(areaOfExpertise_branch_edt)) {
//                                            if (valideEdittext(equipements_edt)) {
//
//                                            } else {
//                                                equipements_edt.setError("List all equipments separated by (,) ");
//                                            }
//                                        } else {
//                                            areaOfExpertise_branch_edt.setError("List all Area of Expertise separated by (,) ");
//                                        }
//                                    } else {
//                                        service_branch_edt.setError("List all Services separated by (,)");
//                                    }
//                                } else {
//                                    contactNumber_branch_edt.setError("Enter valid contact number");
//                                }
//                            } else {
//                                Branch_name_edt.setError("Enter Name of Branch");
//                            }
                        } else {
                            Toast.makeText(getActivity(), "No Internet", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                Button addEq = root.findViewById(R.id.AddEqupment);

                addEq.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!equipements_edt.getText().toString().equals("") && !Quentity.getText().toString().equals("")){
                            name.add(equipements_edt.getText().toString());
                            quentity.add(Quentity.getText().toString());
                            equipements_edt.setText("");
                            Quentity.setText("");

                            adapter = new EqupmentAdapter(getContext(),GalleryFragment.this.name,quentity,equipements_edt,Quentity);
                            recyclerView.setAdapter(adapter);

                        }else{
                            Toast.makeText(getActivity(), "Fill Detail to add them", Toast.LENGTH_SHORT).show();
                        }


                    }
                });

                break;

            default:
                Toast.makeText(getActivity(), "Invalid User Login Again", Toast.LENGTH_SHORT).show();
                getActivity().finish();
        }

        return root;
    }


    @Override
    public void onResume() {
        super.onResume();

    }

    public class UpdateNgoDetails extends AsyncTask<Void, Void, Void> {

        String status;
        String data;

        public UpdateNgoDetails(String Data) {
            this.data = Data;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            OkHttpClient client = new OkHttpClient();
            Log.d("ENimesh", "Trying");

            Log.d("ENimesh", "MY data = " + data);
            Request request = new Request.Builder()
                    .url("https://safesync.onrender.com/UpdateNgo")
                    .post(RequestBody.create(JSON, data))
                    .build();
            try {
                Response response = client.newCall(request).execute();
                String jsonData = response.body().string();
                Log.d("ENimesh", "Response: " + jsonData);
                JSONObject jsonObject = new JSONObject(jsonData);

                Log.d("ENimesh", "data = " + jsonData);

                if (jsonObject.has("status")) {
                    status = jsonObject.getString("status");

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
                    if (status != null && status.equals("200")) {
                        Toast.makeText(getActivity(), "Updated SuccessFully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Status = " + status, Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }
    public class UpdateBranchDetails extends AsyncTask<Void, Void, Void> {

        String status;
        String data;

        public UpdateBranchDetails(String Data) {
            this.data = Data;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            OkHttpClient client = new OkHttpClient();
            Log.d("ENimesh", "Trying");

            Log.d("ENimesh", "MY data = " + data);
            Request request = new Request.Builder()
                    .url("https://safesync.onrender.com/UpdateBranch")
                    .post(RequestBody.create(JSON, data))
                    .build();
            try {
                Response response = client.newCall(request).execute();
                String jsonData = response.body().string();
                Log.d("ENimesh", "Response: " + jsonData);
                JSONObject jsonObject = new JSONObject(jsonData);

                Log.d("ENimesh", "data = " + jsonData);

                if (jsonObject.has("status")) {
                    status = jsonObject.getString("status");

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
                    if (status != null && status.equals("200")) {
                        Toast.makeText(getActivity(), "Updated SuccessFully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Status = " + status, Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }


    public class GetNgoProfileDetails extends AsyncTask<Void, Void, Void> {

        String status;

        String data;

        String name;
        String contact;
        String experties;

        public GetNgoProfileDetails(String Data) {
            this.data = Data;
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

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    PlodingView.setVisibility(View.GONE);
                    LoadingBlur.setVisibility(View.GONE);
                    if (status != null && status.equals("200")) {
                        NGO_name_edt.setText(name);
                        contactNumber_edt.setText(contact);
                        areaOfExpertise_edt.setText(experties);

                        Toast.makeText(getActivity(), "Data field", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Status = " + status, Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }
    public class GetBranchProfileDetails extends AsyncTask<Void, Void, Void> {

        String status;
        String data;
        String name;
        String contact;
        String Services;
        String experties;

        public GetBranchProfileDetails(String Data) {
            this.data = Data;
            if(GalleryFragment.name != null){
                GalleryFragment.name.clear();
            }
            if(quentity != null){
                quentity.clear();
            }
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
//                    Equpments = dataObject.getString("bEquipements");

                    JSONArray dataArray = dataObject.getJSONArray("bEquipements");

                    // Iterate through the array to fetch nName values
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject dataObject1 = dataArray.getJSONObject(i);

                        Log.d("ENimesh","In array = "+dataObject1.getString("eqName"));

                        // Get the current object in the array
                        GalleryFragment.this.name.add(dataObject1.getString("eqName"));
                        quentity.add(dataObject1.getString("eqQuantity"));
                    }


                    Services = dataObject.getString("bServices");
                    conradates = dataObject.getString("bCoordinates");
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
                    if (status != null && status.equals("200")) {
                        Branch_name_edt.setText(name);
                        contactNumber_branch_edt.setText(contact);
                        areaOfExpertise_branch_edt.setText(experties);
                        service_branch_edt.setText(Services);

                        adapter = new EqupmentAdapter(getContext(),GalleryFragment.this.name,quentity,equipements_edt,Quentity);
                        recyclerView.setAdapter(adapter);


                        Toast.makeText(getActivity(), "Data field", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Status = " + status, Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

    boolean valideEdittext(TextInputEditText t_edt) {
        if (t_edt.getText().toString().trim().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}