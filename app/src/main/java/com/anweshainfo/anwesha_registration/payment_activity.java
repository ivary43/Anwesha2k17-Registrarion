package com.anweshainfo.anwesha_registration;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by manish on 2/11/17.
 */

public class payment_activity extends AppCompatActivity {

    RequestQueue mQueue;
    @BindView(R.id.enter_fee_paid_textview)
    TextView enterFeePaidTextView;
    @BindView(R.id.payment_value)
    EditText amount_payable;
    @BindView(R.id.submit_button)
    Button submit;
    @BindView(R.id.anwesha_id_textview)
    TextView anweshaIDTextView;
    @BindView(R.id.name_textview)
    TextView nameTextView;
    @BindView(R.id.college_textview)
    TextView collegeTextView;
    @BindView(R.id.city_textview)
    TextView cityTextView;
    @BindView(R.id.phone_textview)
    TextView phoneTextView;
    @BindView(R.id.email_textview)
    TextView emailTextView;
    @BindView(R.id.fee_paid_textview)
    TextView feePaidTextView;
    private String jsonresponse;
    private String mPaymentUrl;
    /**
     * @personId is the id of the person(the participant who is making the payment )
     * */
    private String personId;
    private String mName;
    private String mCollege;
    private String mPhoneNo;
    private String mEmail;
    private String mCity;
    private String mFeePaid;
    private String uId;
    private String key;
    private SharedPreferences mSharedPreferences;
    private String amount = null;
    private boolean viewOnly = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_reg);
        //bind butterknife with this
        ButterKnife.bind(this);

        mQueue = Volley.newRequestQueue(this);
        jsonresponse = getIntent().getStringExtra("jsonresponse");
//        jsonresponse = "{\"status\":1,\"http\":200,\"message\":{\"name\":\"Manish Kumar\",\"pId\":\"4224\",\"fbID\":\"-1509447087\",\"college\":\"IIT Patna\",\"sex\":\"M\",\"mobile\":\"8935067180\",\"email\":\"warriorjordan16@gmail.com\",\"dob\":\"1997-11-27\",\"city\":\"Patna\",\"refcode\":\"\",\"feePaid\":\"0\",\"rcv\":\"0\",\"isRegTeam\":\"8924569\",\"confirm\":\"1\",\"time\":\"2017-10-31 16:21:27\",\"iitp\":\"0\",\"qrurl\":\"http:\\/\\/anwesha.info\\/qr\\/anw4224.png\"}}";
        viewOnly = getIntent().getBooleanExtra("viewOnly", true);
        Log.e("muks", "Response: " + jsonresponse);
        //function call to make the pID
        setupUser(jsonresponse);
        setupUI();

        if (!viewOnly) {
            //getting the key
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            key = mSharedPreferences.getString("keyPay", null);

            //building the base URL
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
            enterFeePaidTextView.setVisibility(View.VISIBLE);
            amount_payable.setVisibility(View.VISIBLE);
            submit.setVisibility(View.VISIBLE);
        } else {
            enterFeePaidTextView.setVisibility(View.GONE);
            amount_payable.setVisibility(View.GONE);
            submit.setVisibility(View.GONE);
        }
    }


    private void setupUser(String json) {

        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONObject message = jsonObject.getJSONObject("message");
            //Extracting user info
            String pID = message.getString("pId");
            if (pID != null) {
                personId =  pID;
                mName = message.getString("name");
                mCollege = message.getString("college");
                mPhoneNo = message.getString("mobile");
                mEmail = message.getString("email");
                mCity = message.getString("city");
                mFeePaid = message.getString("feePaid");
            }

        } catch (JSONException e) {
            Log.e("LOG_TAG", e.getMessage());
        }
    }

    private void setupUI() {
        if (personId != null) anweshaIDTextView.setText(personId);
        if (mName != null) nameTextView.setText(mName);
        if (mCollege != null) collegeTextView.setText(mCollege);
        if (mCity != null) cityTextView.setText(mCity);
        if (mPhoneNo != null) phoneTextView.setText(mPhoneNo);
        if (mEmail != null) emailTextView.setText(mEmail);
        if (mFeePaid != null) feePaidTextView.setText(mFeePaid);
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
               // params.put(getResources().getString(R.string.payment_val), key);
                params.put(getResources().getString(R.string.payment_amt), "" + amount);
                params.put("authKey", mSharedPreferences.getString("key", ""));
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