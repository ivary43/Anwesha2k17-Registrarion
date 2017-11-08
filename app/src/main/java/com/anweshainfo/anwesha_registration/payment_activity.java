package com.anweshainfo.anwesha_registration;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.R.attr.x;

/**
 * Created by manish on 2/11/17.
 */

public class payment_activity extends AppCompatActivity {

    private String jsonresponse;
    private String mPaymentUrl;
    /**
     * @personId is the id of the person(the participant who is making the payment )
     * */
    private String personId;
    private String uId;
    private String key;
    private SharedPreferences mSharedPreferences;
    RequestQueue mQueue;

    private String amount = null;

    @BindView(R.id.payment_value)
    EditText amount_payable;

    @BindView(R.id.submit_button)
    Button submit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_reg);
        //bind butterknife with this
        ButterKnife.bind(this);

        mQueue = Volley.newRequestQueue(this);
        jsonresponse = getIntent().getStringExtra("jsonresponse");

        //function call to make the pID
        personId = getpId(jsonresponse);

        //getiing the key
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        key = mSharedPreferences.getString("keyPay", null);

        //buliding the base URl
        mPaymentUrl = getResources().getString(R.string.makePaymentUrl) + personId;

        //getting the uID
        uId = mSharedPreferences.getString("uID", null);
        uId = uId.substring(3);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //initalising the amount value
                amount = amount_payable.getText().toString();
                //trim the text
                amount.trim();
                if (amount == null) {
                    Toast.makeText(getApplicationContext(), " Enter the amount first!!", Toast.LENGTH_LONG).show();

                } else {
                    Log.e("TAG", amount + "  " + uId + "  " + key + "    " + personId);
                    makePayment();
                }

            }
        });


    }


    private String getpId(String json) {

        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONObject message = jsonObject.getJSONObject("message");
            //Extracting the pID
            String pID = message.getString("pId");
            return pID;
        } catch (JSONException e) {
            Log.e("LOG_TAG", e.getMessage());
        }
        return null;
    }

    private void makePayment() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, mPaymentUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v("Response payment :", response);

                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            int status = jsonObject.getInt("httpstat");

                            switch (status) {
                                case 200:

                                    Toast.makeText(getApplicationContext(), "Payment Successful", Toast.LENGTH_LONG).show();
                                    break;
                                case 400:
                                    Toast.makeText(getApplicationContext(), "Invalid Email Id", Toast.LENGTH_SHORT).show();
                                    break;
                                case 409:
                                    Toast.makeText(getApplicationContext(), R.string.message_registration_duplicate, Toast.LENGTH_LONG).show();

                                    break;
                                case 403:
                                    Toast.makeText(getApplicationContext(), "Invalid Login", Toast.LENGTH_LONG).show();

                                    break;
                                default:
                                    Toast.makeText(getApplicationContext(), "Error logging in. Please try again later", Toast.LENGTH_SHORT).show();
                            }
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
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(getResources().getString(R.string.payment_uID), uId);
                params.put(getResources().getString(R.string.payment_val), key);
                params.put(getResources().getString(R.string.payment_amt), "" + amount);
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