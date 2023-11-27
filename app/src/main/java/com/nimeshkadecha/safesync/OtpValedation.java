package com.nimeshkadecha.safesync;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
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

public class OtpValedation extends AppCompatActivity {

    private EditText otp;

    private Button verifyy;


    private View PlodingView;
    private LinearLayout LoadingBlur;
    private CountDownTimer countDownTimer;
    private static final long TIMER_DURATION = 60000; // 1 minute in milliseconds
    private static final long TIMER_INTERVAL = 1000; // 1 second in milliseconds

    //    Verifying internet is ON -----------------------------------------------------------------
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

    //        finding textviews;
    TextView timerTV;
    TextView resendTV;
    String userType,BranchEmail,NgoEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_valedation);

//      Getting Verification Email's from INTENT --------------------------------------------------------
        Bundle useDetailsB = getIntent().getExtras();
        NgoEmail = useDetailsB.getString("ngoEmail");
        BranchEmail = useDetailsB.getString("BranchEmail");
        userType = useDetailsB.getString("UserType");

        //          Removing Supo6rt bar / top line containing name
        Objects.requireNonNull(getSupportActionBar()).hide();
        //        finding textviews;
        timerTV = findViewById(R.id.timerOTP);
        resendTV = findViewById(R.id.resendButton);


        otp = findViewById(R.id.OTP);

//        STARTING timer for resend otp
        startTimer();

//        progressbar
        PlodingView = findViewById(R.id.Ploding);
        LoadingBlur = findViewById(R.id.LoadingBlur);

        PlodingView.setVisibility(View.GONE);
        LoadingBlur.setVisibility(View.GONE);

        resendTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startTimer();

                PlodingView.setVisibility(View.VISIBLE);
                LoadingBlur.setVisibility(View.VISIBLE);

                JSONObject postData = new JSONObject();
                try {
                    postData.put("userType", userType);
                    postData.put("nEmail", NgoEmail);
                    postData.put("bEmail", BranchEmail);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("ENimesh", "DATA to  = " + String.valueOf(postData));

                new SendOtpRequest(String.valueOf(postData),userType,NgoEmail,BranchEmail).execute();

            }
        });

//      Verifying BUTTON OTP ------------------------------------------------------------------------------
        verifyy = findViewById(R.id.Verify);
        verifyy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String otpInput = otp.getText().toString().trim();
                boolean OTP_V = OTPValidate(otpInput);
                if (OTP_V) {

                    if (userType.equals("ngo")) {
                        PlodingView.setVisibility(View.VISIBLE);
                        LoadingBlur.setVisibility(View.VISIBLE);
                        verifyy.setEnabled(false);

                        Log.d("ENimesh", "Start to hit");

                        JSONObject postData = new JSONObject();
                        try {
                            postData.put("nEmail",NgoEmail);
                            postData.put("userType",userType);
                            postData.put("otp",otp.getText().toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("ENimesh", "DATA = " + String.valueOf(postData));
                        new NGO_OTP_Verification(String.valueOf(postData)).execute();

                    } else if (userType.equals("branch")) {
                        PlodingView.setVisibility(View.VISIBLE);
                        LoadingBlur.setVisibility(View.VISIBLE);
                        verifyy.setEnabled(false);

                        Log.d("ENimesh", "Start to hit");

                        JSONObject postData = new JSONObject();
                        try {

                            postData.put("userType",userType);
                            postData.put("nEmail",NgoEmail);
                            postData.put("bEmail",BranchEmail);
                            postData.put("otp",otp.getText().toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("ENimesh", "DATA = " + String.valueOf(postData));
                        new NGO_OTP_Verification(String.valueOf(postData)).execute();
                    }
                } else {
                    otp.setError("O.T.P. must be 6 digits Long");
                    Toast.makeText(OtpValedation.this, "Enter Valid O.T.P.", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    //    OTP validation -------------------------------------------------------------------------------
    private boolean OTPValidate(String otpInput) {
        if (otpInput.length() < 6) {
            return false;
        } else {
            return true;
        }
    }
//--------------------------------------------------------------------------------------------------

    public class NGO_OTP_Verification extends AsyncTask<Void, Void, Void> {


        String login_status = "null";
        String data;

        public NGO_OTP_Verification(String Data) {
            this.data = Data;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            OkHttpClient client = new OkHttpClient();
            Log.d("ENimesh", "Trying");

            Request request = new Request.Builder()
                    .url("https://safesync.onrender.com/CheckCode")
                    .post(RequestBody.create(JSON, data))
                    .build();
            try {
                Response response = client.newCall(request).execute();
                String jsonData = response.body().string();
                JSONObject jsonObject = new JSONObject(jsonData);

                login_status = jsonObject.getString("status");

                Log.d("ENimesh", "status message from API = " + jsonObject.getString("status"));

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            OtpValedation.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    PlodingView.setVisibility(View.GONE);
                    LoadingBlur.setVisibility(View.GONE);
                    verifyy.setEnabled(true);
                    switch (login_status) {
                        case "200":
                            Toast.makeText(OtpValedation.this, "Validate Successfully", Toast.LENGTH_SHORT).show();
                            Intent gotoReset = new Intent(OtpValedation.this,ResetPassword.class);

                            gotoReset.putExtra("ngoEmail",NgoEmail);
                            gotoReset.putExtra("BranchEmail",BranchEmail);
                            gotoReset.putExtra("UserType",userType);

                            startActivity(gotoReset);
                            break;
                        case "406":
                            Toast.makeText(OtpValedation.this, "Message:406. -Wrong OTP", Toast.LENGTH_SHORT).show();
                            break;
                        case "500":
                            Toast.makeText(OtpValedation.this, "Message:500. -Internal server error", Toast.LENGTH_SHORT).show();
                            break;

                    }
                }
            });

        }


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

            if (OTP_Status.equals("200")){

                timerTV.setVisibility(View.VISIBLE);
                startTimer();
                resendTV.setVisibility(View.GONE);

                Toast.makeText(OtpValedation.this, "O.T.P. Successfully Sent Again", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(OtpValedation.this, "Message : "+OTP_Status + " Try Again", Toast.LENGTH_SHORT).show();
            }


        }
    }


    private void startTimer() {
        countDownTimer = new CountDownTimer(TIMER_DURATION, TIMER_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Calculate the remaining minutes and seconds
                long minutes = millisUntilFinished / 60000;
                long seconds = (millisUntilFinished % 60000) / 1000;

                // Update the timer TextView with the remaining time
                timerTV.setText(String.format("%d:%02d", minutes, seconds));
            }

            @Override
            public void onFinish() {
                // Enable the resend button and reset the timer TextView
                resendTV.setVisibility(View.VISIBLE);
                timerTV.setVisibility(View.GONE);
            }
        };

        // Start the timer
        countDownTimer.start();
    }

}