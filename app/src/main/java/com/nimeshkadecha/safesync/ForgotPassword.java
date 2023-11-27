package com.nimeshkadecha.safesync;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ForgotPassword extends AppCompatActivity {

    private EditText NGO_Email;

    private View PlodingView;


    Button getOTP;


    private LinearLayout LoadingBlur;
    //    Verifying internet is ON =====================================================================
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);


        final String[] UserType = {"ngo"};

        //          Removing Suport bar / top line containing name
        Objects.requireNonNull(getSupportActionBar()).hide();

        //        Finding  ---------------------------------------------------------------------------------
        NGO_Email = findViewById(R.id.EmailNgo_f);
        //        Finding progressbar
        PlodingView = findViewById(R.id.Ploding);
        LoadingBlur = findViewById(R.id.LoadingBlur);

        PlodingView.setVisibility(View.GONE);
        LoadingBlur.setVisibility(View.GONE);


        TextInputLayout Branch_layout = findViewById(R.id.textInputLayout_BranchID_f);


        ImageButton NGO_B = findViewById(R.id.NGO_Button);

        ImageButton Branch_B = findViewById(R.id.Branch_Button);

        NGO_B.setAlpha(1f);

        NGO_B.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                Branch_layout.setVisibility(View.VISIBLE);

            }
        });

        getOTP = findViewById(R.id.getOTP_btn);

        TextInputEditText Branch_email_EDT = findViewById(R.id.Branch_email_f);

        getOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkConnection()) {
                    if (UserType[0].equals("ngo")) {
                        boolean NV = EmailValidation(NGO_Email.getText().toString().trim());
                        if (NV) {
                            getOTP.setEnabled(false);
                            PlodingView.setVisibility(View.VISIBLE);
                            LoadingBlur.setVisibility(View.VISIBLE);

                            JSONObject postData = new JSONObject();
                            try {
                                postData.put("userType", UserType[0]);
                                postData.put("nEmail", NGO_Email.getText().toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Log.d("ENimesh", "DATA NGO to  = " + String.valueOf(postData));

                            new SendOtpRequest(String.valueOf(postData),UserType[0],NGO_Email.getText().toString(),"").execute();

                        } else {
                            NGO_Email.setError("Enter valid Email");
                            Toast.makeText(ForgotPassword.this, "Invalid Email", Toast.LENGTH_SHORT).show();
                        }
                    } else if (UserType[0].equals("branch")) {
                        boolean NV = EmailValidation(NGO_Email.getText().toString());
                        boolean NV2 = EmailValidation(Branch_email_EDT.getText().toString());

                        if (NV && NV2) {
                            getOTP.setEnabled(false);
                            PlodingView.setVisibility(View.VISIBLE);
                            LoadingBlur.setVisibility(View.VISIBLE);

                            JSONObject postData = new JSONObject();
                            try {
                                postData.put("userType", UserType[0]);
                                postData.put("nEmail", NGO_Email.getText().toString());
                                postData.put("bEmail", Branch_email_EDT.getText().toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Log.d("ENimesh", "DATA to  = " + String.valueOf(postData));

                            new SendOtpRequest(String.valueOf(postData),UserType[0],NGO_Email.getText().toString(),Branch_email_EDT.getText().toString()).execute();

                        } else {
                            if (!NV) {
                                NGO_Email.setError("Enter valid Email");
                            }
                            if (!NV2) {
                                Branch_email_EDT.setError("Enter Valid Branch Email");
                            }
                        }
                    }


                } else {
                    Toast.makeText(ForgotPassword.this, "No Internet", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public class SendOtpRequest extends AsyncTask<Void, Void, Void> {

        String OTP_Status;
        String data;
        String userType;
        String NGO_Emai;
        String BranchEmail;

        public SendOtpRequest (String Data,String userType,String NGO_Emai,String BranchEmail) {
            this.BranchEmail = BranchEmail;
            this.userType = userType;
            this.NGO_Emai = NGO_Emai;
            this.data = Data;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            OkHttpClient client = new OkHttpClient();
            Log.d("ENimesh", "Trying");

            Request request = new Request.Builder()
                    .url("https://safesync.onrender.com/SendOtp")
                    .post(RequestBody.create(JSON, data))
                    .build();
            try {
                Response response = client.newCall(request).execute();
                String jsonData = response.body().string();
                JSONObject jsonObject = new JSONObject(jsonData);

                OTP_Status = jsonObject.getString("status");

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

            getOTP.setEnabled(true);

            if (OTP_Status.equals("200")){
                Intent gotoOTP = new Intent(ForgotPassword.this,OtpValedation.class);
                gotoOTP.putExtra("ngoEmail",NGO_Emai);
                gotoOTP.putExtra("BranchEmail",BranchEmail);
                gotoOTP.putExtra("UserType",userType);
                startActivity(gotoOTP);
            }else{
                Toast.makeText(ForgotPassword.this, "Message : "+OTP_Status + " Try Again", Toast.LENGTH_SHORT).show();
            }


        }
    }
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

}