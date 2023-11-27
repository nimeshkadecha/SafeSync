package com.nimeshkadecha.safesync.ui.home;

import android.app.Activity;
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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nimeshkadecha.safesync.LoginScreen;
import com.nimeshkadecha.safesync.MainActivity2;
import com.nimeshkadecha.safesync.MyAdapter;
import com.nimeshkadecha.safesync.R;
import com.nimeshkadecha.safesync.databinding.FragmentHomeBinding;
import com.nimeshkadecha.safesync.ui.details.NgoDetail;

import org.jetbrains.annotations.TestOnly;
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

public class HomeFragment extends Fragment {

    int counter = 0;
    private FragmentHomeBinding binding;

    static RecyclerView recyclerView;
    static MyAdapter adapter;

    private View PlodingView;
    private LinearLayout LoadingBlur;

    public static final String SHARED_PREFS = "sharedPrefs";

    public static RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public static MyAdapter getAdapter() {
        return adapter;
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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        String token_local = sharedPreferences.getString("token","");
        String user_type_local = sharedPreferences.getString("user_type","");

        recyclerView = root.findViewById(R.id.ngo_r_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        PlodingView = root.findViewById(R.id.Ploding);
        LoadingBlur = root.findViewById(R.id.LoadingBlur);
        PlodingView.setVisibility(View.GONE);
        LoadingBlur.setVisibility(View.GONE);

        if(checkConnection()){
            JSONObject postData = new JSONObject();
            try {
                postData.put("need", 1);
                postData.put("token", token_local);
                postData.put("userType", user_type_local);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(counter == 0){

                PlodingView.setVisibility(View.VISIBLE);
                LoadingBlur.setVisibility(View.VISIBLE);
                new getAllData(String.valueOf(postData)).execute();
                counter++;
            }else{
                recyclerView.setAdapter(adapter);
            }
        }

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public class getAllData extends AsyncTask<Void,Void,Void> {

        String status;

        public ArrayList<String> NGO_Name;
        public ArrayList<String> NGO_Email;

        String data;

        RecyclerView recyclerView ;


        public getAllData(String Data) {
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

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(status.equals("200")){
                        adapter = new MyAdapter(getContext(),NGO_Email,NGO_Name, getActivity());
                        recyclerView.setAdapter(adapter);
                        PlodingView.setVisibility(View.GONE);
                        LoadingBlur.setVisibility(View.GONE);
                    }else{

                        Toast.makeText(getActivity(), "Status = "+status, Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

}