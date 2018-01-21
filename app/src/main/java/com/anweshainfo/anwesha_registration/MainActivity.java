package com.anweshainfo.anwesha_registration;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"(),:;<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$";
    TextView forgotPassword;
    String mEmail;
    String mPassword;
    RequestQueue mQueue;
    SharedPreferences.Editor sharedPreferences;
    SharedPreferences isLoggedin ;
    private String mUrl;
    private Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    private Matcher matcher;
    private ArrayList<String> mEventsName = new ArrayList<>();
    private ArrayList<String> mEventId = new ArrayList<>();

    @BindView(R.id.button_signin)
    Button buttonSignIn;

    @BindView(R.id.eamil_id_wrapper_signin)
    TextInputLayout emailIDWrapper;

    @BindView(R.id.password_wrapper_signin)
    TextInputLayout passwordWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mQueue = Volley.newRequestQueue(this);
        isLoggedin=PreferenceManager.getDefaultSharedPreferences(this) ;
        mUrl = getString(R.string.url_login);

        if( isLoggedin.getBoolean("isloggedIn",false))
        {
            Intent intent = new Intent(MainActivity.this, qrscannerActivity.class);
            startActivity(intent);
        }
        setContentView(R.layout.login_page);
        ButterKnife.bind(this);

        setHints();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this).edit();
        forgotPassword = (TextView) findViewById(R.id.forgot_password);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri webpage = Uri.parse(getString(R.string.url_forgot_password));
                Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });


//        //TODO: set the Api call
        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearErrors();
                boolean b = validateInputs();
                if (b) {
                    //saving username for sharing the uId for post request
                    sharedPreferences.putString("uID", mEmail);
                    //Code for sending the details
                    Toast.makeText(getApplicationContext(), "Logging in..", Toast.LENGTH_SHORT).show();
                    buttonSignIn.setVisibility(View.GONE);
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, mUrl,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Log.v("Response:", response);
                                    sharedPreferences.putString("jsonResponse", response);
                                    sharedPreferences.apply();

                                    try {

                                        JSONObject jsonObject = new JSONObject(response);
                                        int status = Integer.parseInt(jsonObject.getString(getString(R.string.JSON_status)));
                                        //extracting the key from the user call
                                        String key = getKey(jsonObject);
                                        sharedPreferences.putString("keyPay", key);
                                        sharedPreferences.apply();

                                        switch (status) {
                                            case 200:
                                                sharedPreferences.putBoolean("isloggedIn",true);
                                                sharedPreferences.apply();
                                                Toast.makeText(getApplicationContext(), "Log In Successful" + mEventsName.size(), Toast.LENGTH_LONG).show();
                                                //filter the eventid
                                                Intent intent = new Intent(MainActivity.this, qrscannerActivity.class);
                                                startActivity(intent);


                                                int userID = Integer.parseInt(jsonObject.getString("userID"));
                                                //TODO:process json response
                                                finish();
                                                break;
                                            case 400:
                                                Toast.makeText(getApplicationContext(), "Invalid Email Id", Toast.LENGTH_SHORT).show();
                                                break;
                                            case 409:
                                                Toast.makeText(getApplicationContext(), R.string.message_registration_duplicate, Toast.LENGTH_LONG).show();
                                                finish();
                                                break;
                                            case 403:
                                                Toast.makeText(getApplicationContext(), "Invalid Login", Toast.LENGTH_LONG).show();
                                                finish();
                                                break;
                                            default:
                                                Toast.makeText(getApplicationContext(), "Error logging in. Please try again later", Toast.LENGTH_SHORT).show();
                                        }

                                        buttonSignIn.setVisibility(View.VISIBLE);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.v("Error : ", error.toString());
                                    error.printStackTrace();
                                    Toast.makeText(getApplicationContext(), "Error logging in. Please try again later", Toast.LENGTH_SHORT).show();
                                    buttonSignIn.setVisibility(View.VISIBLE);
                                }
                            }
                    ) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put(getString(R.string.signin_param_username), mEmail);
                            params.put(getString(R.string.signin_password), mPassword);
                            return params;
                        }

                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> headers = new HashMap<>();
                            headers.put("Accept", "application/json");
                            return headers;
                        }
                    };
                    mQueue.add(stringRequest);
                }
            }
        });
    }

    private void clearErrors() {
        emailIDWrapper.setErrorEnabled(false);
        passwordWrapper.setErrorEnabled(false);
    }

    private boolean validateInputs() {
        if (isAnyFieldEmpty())
            return false;

        mEmail = emailIDWrapper.getEditText().getText().toString();
        mPassword = passwordWrapper.getEditText().getText().toString();

        return true;
    }

    private boolean isAnyFieldEmpty() {
        boolean flag = false;
        if (TextUtils.isEmpty(emailIDWrapper.getEditText().getText().toString())) {
            flag = true;
            emailIDWrapper.setError(getString(R.string.error_empty_field));
        }
        if (TextUtils.isEmpty(passwordWrapper.getEditText().getText().toString())) {
            flag = true;
            passwordWrapper.setError(getString(R.string.error_empty_field));
        }
        return flag;
    }

    private void setHints() {
        emailIDWrapper.setHint(getString(R.string.email_id_hint));
        passwordWrapper.setHint(getString(R.string.password_hint));
    }

    private String getKey(JSONObject jsonObject) {

        try {
            JSONObject special = jsonObject.getJSONObject("special");
            String key = special.getString("isRegTeam");
            return key;

        } catch (JSONException e) {
            Log.e("TAgggggggg", e.getMessage());
        }

        return null;

    }

}
