package com.nimeshkadecha.safesync;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ResetPassword extends AppCompatActivity {


    private EditText password, confirmPassword;

    private Button confirm;


    private View PlodingView;
    private LinearLayout LoadingBlur;

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

    String userType,BranchEmail,NgoEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);


        Bundle useDetailsB = getIntent().getExtras();
        NgoEmail = useDetailsB.getString("ngoEmail");
        BranchEmail = useDetailsB.getString("BranchEmail");
        userType = useDetailsB.getString("UserType");


        //        progressbar
        PlodingView = findViewById(R.id.Ploding);
        LoadingBlur = findViewById(R.id.LoadingBlur);

        PlodingView.setVisibility(View.GONE);
        LoadingBlur.setVisibility(View.GONE);

        //          Removing Supo6rt bar / top line containing name
        Objects.requireNonNull(getSupportActionBar()).hide();

//        finding
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.ConfirmPassword);

        //        Confirm Button ---------------------------------------------------------------------------
        confirm = findViewById(R.id.confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Calling Function
                if(checkConnection()){
                    PlodingView.setVisibility(View.VISIBLE);
                    LoadingBlur.setVisibility(View.VISIBLE);
                    Confirm();
                }else{
                    Toast.makeText(ResetPassword.this, "No Internet", Toast.LENGTH_SHORT).show();
                }

            }
        });
//--------------------------------------------------------------------------------------------------
    }
    //    validating Password length >= 6 --------------------------------------------------------------
    private boolean PasswordValidation(EditText password, EditText confirmPassword) {
        String passwordInput = password.getText().toString().trim();
        String confirmPasswordInput = confirmPassword.getText().toString().trim();
        if (passwordInput.length() != confirmPasswordInput.length() || passwordInput.length()<6) {
            return false;
        } else {
            return true;
        }
    }
//--------------------------------------------------------------------------------------------------

    //    RESETTING PASSWORD ---------------------------------------------------------------------------
    public void Confirm() {
        boolean VP = PasswordValidation(password, confirmPassword);
        if (VP) {
            if (password.getText().toString().equals(confirmPassword.getText().toString())) {
                confirm.setEnabled(false);
                JSONObject postData = new JSONObject();
                if(userType.equals("ngo")){
                    try {
                        postData.put("userType", userType);
                        postData.put("nEmail",NgoEmail);
                        postData.put("password", password.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    try {
                        postData.put("userType", userType);
                        postData.put("nEmail",NgoEmail);
                        postData.put("bEmail", BranchEmail);
                        postData.put("password", password.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                Log.d("ENimesh", "DATA to RESET PASSWORD -  = " + String.valueOf(postData));

                new ResetPassword_API(String.valueOf(postData)).execute();


            } else {
                Toast.makeText(this, "Password Don't Match", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Invalid Password", Toast.LENGTH_SHORT).show();
        }
    }
//--------------------------------------------------------------------------------------------------



    public class ResetPassword_API extends AsyncTask<Void, Void, Void> {

        String ResetStatus;
        String data;

        public ResetPassword_API (String Data) {
            this.data = Data;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            OkHttpClient client = new OkHttpClient();
            Log.d("ENimesh", "Trying");

            Request request = new Request.Builder()
                    .url("https://safesync.onrender.com/ChangePassword")
                    .post(RequestBody.create(JSON, data))
                    .build();
            try {
                Response response = client.newCall(request).execute();
                String jsonData = response.body().string();
                JSONObject jsonObject = new JSONObject(jsonData);

                ResetStatus = jsonObject.getString("status");

                Log.d("ENimesh", "Starting Server = " + jsonObject.getString("status"));

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            PlodingView.setVisibility(View.GONE);
            LoadingBlur.setVisibility(View.GONE);

            if (ResetStatus.equals("200")) {
                Intent SucessfullyLogin = new Intent(ResetPassword.this, LoginScreen.class);
                startActivity(SucessfullyLogin);
                finish();
                Toast.makeText(ResetPassword.this, "Password Updated", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(ResetPassword.this, "Message : "+ResetStatus + " Try Again", Toast.LENGTH_SHORT).show();
            }

        }
    }


}