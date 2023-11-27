package com.nimeshkadecha.safesync;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginScreen extends AppCompatActivity {

    public static final String SHARED_PREFS = "sharedPrefs";
    Button Login_Btn;
    private View PlodingView;
    private LinearLayout LoadingBlur;

    private Spinner spinner;

    public ArrayList<String> NGO_Name_arr;
    public ArrayList<String> NGO_Email_arr;

    //    Code for validating email starts--------------------------------------------------------------
    public boolean EmailValidation(String email) {
        String emailinput = email;
        if (!emailinput.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(emailinput).matches()) {
            return true;
        } else {

            return false;
        }
    }

    //--------------------------------------------------------------------------------------------------
//    Code for validation Password starts-----------------------------------------------------------
    public boolean passwordValidation(String password) {
        String passwordInput = password;
//    if (!passwordInput.isEmpty() && passwordInput.length() > 6) {
//        return true;
//    } else {
//        return false;
//    }
        if (!passwordInput.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }
//--------------------------------------------------------------------------------------------------


    //    Verifying Wifi / internet is ON --------------------------------------------------------------
    boolean checkConnection() {
        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo net = manager.getActiveNetworkInfo();

        if (net == null) {
            return false;
        } else {
            return true;
        }
    }
//--------------------------------------------------------------------------------------------------


    TextInputEditText Branch_email_EDT;
    TextInputEditText NGO_email_EDT;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);

        spinner = findViewById(R.id.spinner);

        NGO_Name_arr = new ArrayList<>();
        NGO_Email_arr = new ArrayList<>();

        TextView forgotPassword = findViewById(R.id.textView2);

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoForgot = new Intent(LoginScreen.this,ForgotPassword.class);
                startActivity(gotoForgot);
            }
        });

        if(checkConnection()){
            new Start_Server().execute();
        }

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        String token_local = sharedPreferences.getString("token","");
        String user_type_local = sharedPreferences.getString("user_type","");

        if(checkConnection()){
            if(!token_local.equals("") && !user_type_local.equals("")){
                Intent gotoHome = new Intent(LoginScreen.this,MainActivity2.class);
                startActivity(gotoHome);
            }
        }else{
            AlertDialog.Builder alert = new AlertDialog.Builder(LoginScreen.this);
            alert.setCancelable(false);
            alert.setTitle("No Internet");
            alert.setMessage("We kindly request you to verify your internet connectivity and attempt to reopen the application.");
            alert.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finishAffinity();
                }
            });

            alert.setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(checkConnection()){
                        if(!token_local.equals("") && !user_type_local.equals("")){

                            new Start_Server().execute();

                            Intent gotoHome = new Intent(LoginScreen.this,MainActivity2.class);
                            startActivity(gotoHome);
                        }
                    }else{
                        Toast.makeText(LoginScreen.this, "No Internet", Toast.LENGTH_SHORT).show();
                        alert.show();
                    }
                }
            });

            alert.show();
        }


//          Removing Suport bar / top line containing name
        Objects.requireNonNull(getSupportActionBar()).hide();


        Login_Btn = findViewById(R.id.login);
        NGO_email_EDT = findViewById(R.id.NOG_email);
        Branch_email_EDT = findViewById(R.id.Branch_email);
        TextInputEditText password_EDT = findViewById(R.id.password);


        TextInputLayout Branch_layout = findViewById(R.id.textInputLayout_BranchID);

        ImageButton NGO_B = findViewById(R.id.NGO_Button);

        ImageButton Branch_B = findViewById(R.id.Branch_Button);

        TextView SignUP = findViewById(R.id.SignUP_Text);
        //        Finding progressbar
        PlodingView = findViewById(R.id.Ploding);
        LoadingBlur = findViewById(R.id.LoadingBlur);
        PlodingView.setVisibility(View.GONE);
        LoadingBlur.setVisibility(View.GONE);

        final String[] UserType = {"ngo"};

        SignUP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://safesync.onrender.com/register"));
                startActivity(intent);
            }
        });

        NGO_B.setAlpha(1f);

        NGO_B.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                spinner.setVisibility(View.GONE);
                NGO_email_EDT.setVisibility(View.VISIBLE);
                UserType[0] = "ngo";
                NGO_B.setAlpha(1f);
                Branch_B.setAlpha(0.6f);
                Branch_layout.setVisibility(View.GONE);
            }
        });

        Branch_B.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserType[0] = "branch";
                Branch_B.setAlpha(1f);
                NGO_B.setAlpha(0.6f);

                NGO_email_EDT.setEnabled(false);
                Branch_layout.setVisibility(View.VISIBLE);

                PlodingView.setVisibility(View.VISIBLE);
                LoadingBlur.setVisibility(View.VISIBLE);


                NGO_Email_arr.clear();
                NGO_Name_arr.clear();

                new Get_Ngo_List().execute();
            }

        });


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                NGO_email_EDT.setText(NGO_Email_arr.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(LoginScreen.this, "Select A NGO", Toast.LENGTH_SHORT).show();
            }
        });

        Login_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkConnection()) {
                    if (UserType[0].equals("ngo")) {
                        if (!EmailValidation(NGO_email_EDT.getText().toString())) {
                            NGO_email_EDT.setError("Please Enter NGO's Email address");
                            Toast.makeText(LoginScreen.this, "Enter Valid Email Address", Toast.LENGTH_SHORT).show();
                        } else if (!passwordValidation(password_EDT.getText().toString())) {
                            password_EDT.setError("Please Enter Password");
                            Toast.makeText(LoginScreen.this, "Enter Valid Password", Toast.LENGTH_SHORT).show();

                        } else {
                            PlodingView.setVisibility(View.VISIBLE);
                            LoadingBlur.setVisibility(View.VISIBLE);

                            Login_Btn.setEnabled(false);

                            Log.d("ENimesh", "Start to hit");

                            JSONObject postData = new JSONObject();
                            try {
                                postData.put("email", NGO_email_EDT.getText().toString());
                                postData.put("password", password_EDT.getText().toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Log.d("ENimesh", "DATA = " + String.valueOf(postData));
                            new NGO_Login(String.valueOf(postData),NGO_email_EDT.getText().toString()).execute();
                        }
                    } else if (UserType[0].equals("branch")) {
                        if (!EmailValidation(NGO_email_EDT.getText().toString())) {
                            NGO_email_EDT.setError("Please Enter NGO's Email address");
                            Toast.makeText(LoginScreen.this, "Enter Valid Email Address in NGO", Toast.LENGTH_SHORT).show();
                        } else if (!EmailValidation(Branch_email_EDT.getText().toString())) {
                            Branch_email_EDT.setError("Please Enter Branch's Email address");
                            Toast.makeText(LoginScreen.this, "Enter Valid Email Address in Branch", Toast.LENGTH_SHORT).show();
                        } else if (!passwordValidation(password_EDT.getText().toString())) {
                            password_EDT.setError("Please enter Password");
                            Toast.makeText(LoginScreen.this, "Enter Valid Password", Toast.LENGTH_SHORT).show();

                        } else {
                            PlodingView.setVisibility(View.VISIBLE);
                            LoadingBlur.setVisibility(View.VISIBLE);
                            Login_Btn.setEnabled(false);

                            Log.d("ENimesh", "Start to hit");

                            JSONObject postData = new JSONObject();
                            try {
                                postData.put("nEmail", NGO_email_EDT.getText().toString());
                                postData.put("bEmail", Branch_email_EDT.getText().toString());
                                postData.put("password", password_EDT.getText().toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Log.d("ENimesh", "DATA = " + String.valueOf(postData));
                            new Branch_Login(String.valueOf(postData)).execute();
                        }
                    }

                } else {
                    Toast.makeText(LoginScreen.this, "Please check your Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    public static class Start_Server extends AsyncTask<Void,Void,Void>{


        String login_status = "null";

        @Override
        protected Void doInBackground(Void... voids) {
                final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                OkHttpClient client = new OkHttpClient();
                Log.d("ENimesh", "Trying");

                Request request = new Request.Builder()
                        .url("https://safesync.onrender.com/isloggedin")
                        .post(RequestBody.create(JSON, "{}"))
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    String jsonData = response.body().string();
                    JSONObject jsonObject = new JSONObject(jsonData);

                    login_status = jsonObject.getString("status");

                    Log.d("ENimesh", "Starting Server = " + jsonObject.getString("status"));

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            return null;
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class NGO_Login extends AsyncTask<Void, Void, Void> {

        String login_status = "null";

        String token = "null";
        String data;
        String N_Email;

        public NGO_Login(String Data,String N_Email) {
            this.data = Data;
            this.N_Email = N_Email;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            OkHttpClient client = new OkHttpClient();
            Log.d("ENimesh", "Trying");

            Request request = new Request.Builder()
                    .url("https://safesync.onrender.com/ngoLogin")
                    .post(RequestBody.create(JSON, data))
                    .build();
            try {
                Response response = client.newCall(request).execute();
                String jsonData = response.body().string();
                JSONObject jsonObject = new JSONObject(jsonData);

                login_status = jsonObject.getString("status");
                if(login_status.equals("202")){
                    token = jsonObject.getString("token");
                }

                Log.d("ENimesh", "status message from API = " + jsonObject.getString("status"));

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            LoginScreen.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    PlodingView.setVisibility(View.GONE);
                    LoadingBlur.setVisibility(View.GONE);
                    Login_Btn.setEnabled(true);
                    switch (login_status) {
                        case "202":
                            PlodingView.setVisibility(View.GONE);
                            LoadingBlur.setVisibility(View.GONE);

                            SharedPreferences sp = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("token", token);
                            editor.putString("user_type", "ngo");
                            editor.putString("NGO_Email", N_Email);
                            editor.apply();

                            Toast.makeText(LoginScreen.this, "Login successfully", Toast.LENGTH_SHORT).show();
                            Intent gotoHome = new Intent(LoginScreen.this,MainActivity2.class);
                            startActivity(gotoHome);
                            break;
                        case "406":
                            Toast.makeText(LoginScreen.this, "ERROR 406:email or password is missing or not reached server", Toast.LENGTH_SHORT).show();
                            break;
                        case "417":
                            Toast.makeText(LoginScreen.this, "Incorrect email or password", Toast.LENGTH_SHORT).show();
                            break;
                        case "401":
                            Toast.makeText(LoginScreen.this, "Otp verification pending", Toast.LENGTH_SHORT).show();
                            break;
                        case "402":
                            Toast.makeText(LoginScreen.this, "Authorization Pending", Toast.LENGTH_SHORT).show();
                            break;
                        case "500":
                            Toast.makeText(LoginScreen.this, "Internal server error", Toast.LENGTH_SHORT).show();
                            break;

                    }
                }
            });

        }

    }

    @SuppressLint("StaticFieldLeak")
    public class Branch_Login extends AsyncTask<Void, Void, Void> {

        String login_status = "null";

        String token = "null";
        String data;

        public Branch_Login(String Data) {
            this.data = Data;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            OkHttpClient client = new OkHttpClient();
            Log.d("ENimesh", "Trying");

            Request request = new Request.Builder()
                    .url("https://safesync.onrender.com/bLogin")
                    .post(RequestBody.create(JSON, data))
                    .build();
            try {
                Response response = client.newCall(request).execute();
                String jsonData = response.body().string();
                JSONObject jsonObject = new JSONObject(jsonData);

                login_status = jsonObject.getString("status");
                if(login_status.equals("202")){
                    token = jsonObject.getString("token");
                }

                Log.d("ENimesh", "status message from API = " + jsonObject.getString("status"));

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            LoginScreen.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    PlodingView.setVisibility(View.GONE);
                    LoadingBlur.setVisibility(View.GONE);
                    Login_Btn.setEnabled(true);
                    switch (login_status) {
                        case "202":
                            SharedPreferences sp = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("token", token);
                            editor.putString("user_type", "branch");
                            editor.putString("NGO_Email", NGO_email_EDT.getText().toString());
                            editor.putString("Branch_Email", Branch_email_EDT.getText().toString());
                            editor.apply();

                            Toast.makeText(LoginScreen.this, "Login successfully", Toast.LENGTH_SHORT).show();
                            Intent gotoHome = new Intent(LoginScreen.this,MainActivity2.class);
                            startActivity(gotoHome);
                            break;
                        case "406":
                            Toast.makeText(LoginScreen.this, "ERROR 406:email or password is missing or not reached server", Toast.LENGTH_SHORT).show();
                            break;
                        case "417":
                            Toast.makeText(LoginScreen.this, "Incorrect email or password", Toast.LENGTH_SHORT).show();
                            break;
                        case "401":
                            Toast.makeText(LoginScreen.this, "Otp verification pending", Toast.LENGTH_SHORT).show();
                            break;
                        case "402":
                            Toast.makeText(LoginScreen.this, "Authorization Pending", Toast.LENGTH_SHORT).show();
                            break;
                        case "500":
                            Toast.makeText(LoginScreen.this, "Internal server error", Toast.LENGTH_SHORT).show();
                            break;

                    }
                }
            });

        }

    }


    public class Get_Ngo_List extends AsyncTask<Void,Void,Void> {

        String status;

        @Override
        protected Void doInBackground(Void... voids) {
            final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://safesync.onrender.com/GetNgoList")
                    .post(RequestBody.create(JSON, "{}"))
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
                    NGO_Name_arr.add(dataObject.getString("nName"));
                    NGO_Email_arr.add(dataObject.getString("nEmail"));
                }
                status = jsonObject.getString("status");

                Log.d("ENimesh", "Starting Server = " + jsonObject.getString("status"));

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);

            LoginScreen.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(status.equals("200")){

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(LoginScreen.this, android.R.layout.simple_spinner_item, NGO_Name_arr);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(adapter);

                        PlodingView.setVisibility(View.GONE);
                        LoadingBlur.setVisibility(View.GONE);

                        spinner.setVisibility(View.VISIBLE);
                    }else{
                        Toast.makeText(LoginScreen.this, "Message: -"+status, Toast.LENGTH_SHORT).show();
                    }



                }
            });

        }
    }


    @Override
    protected void onStart() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        String token_local = sharedPreferences.getString("token","");
        String user_type_local = sharedPreferences.getString("user_type","");
        if(!token_local.equals("") && !user_type_local.equals("")){
            Intent gotoHome = new Intent(LoginScreen.this,MainActivity2.class);
            startActivity(gotoHome);
        }
        super.onStart();
    }
}