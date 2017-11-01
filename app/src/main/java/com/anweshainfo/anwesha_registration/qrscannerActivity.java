package com.anweshainfo.anwesha_registration;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.Result;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.dm7.barcodescanner.zxing.ZXingScannerView;


/**
 * Created by manish on 27/10/17.
 */

public class qrscannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;
    private final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    private ArrayAdapter<String> spinnerArrayAdapter;
    private ArrayList<String> string = new ArrayList<>();
    private ArrayList<String> id = new ArrayList<>();
    private String mBaseUrl;
    private SharedPreferences mSharedPreferences;
    RequestQueue mQueue;
    @BindView(R.id.scanner)
    LinearLayout scannerView;

    @BindView(R.id.spinner_events_name)
    Spinner eventsspinner;

    private String eventName;
    private String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_selector);
        ButterKnife.bind(this);
        mBaseUrl = getResources().getString(R.string.url_register);
        mQueue = Volley.newRequestQueue(this);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        mScannerView = new ZXingScannerView(this);
        mScannerView.setAutoFocus(true);
        checkPermission();

        //Extracting the event data
        string = getIntent().getStringArrayListExtra("mEventsName");
        id = getIntent().getStringArrayListExtra("mEventId");


        //set the array adapter
        spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, string);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        eventsspinner.setAdapter(spinnerArrayAdapter);

        eventsspinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                show(i);
                //setting the value
                eventName = string.get(i);
                eventId = id.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Start the scan
                Scan();
            }
        });


    }

    public void show(int i) {
        Toast.makeText(this, string.get(i), Toast.LENGTH_LONG).show();
    }

    public void Scan() {
        setContentView(mScannerView);
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    protected void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();

    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        Log.v("TAG", rawResult.getText()); // Prints scan results
        Log.v("TAG", rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)

        //appending the base url
        String postUrl = mBaseUrl + rawResult.getText();
        //make a network call
        makePost(postUrl);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Scan Result");
        builder.setMessage(rawResult.getText());
        AlertDialog alert1 = builder.create();
        alert1.show();
        mScannerView.resumeCameraPreview(this);
    }

    //check whether there is permission
    public void checkPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            //We don't need an explanation because this will definitely require camera access to scan
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this, "This is done", Toast.LENGTH_LONG).show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Error");
                    builder.setMessage("Unable to Start camera.\n Go to Settings\n->App->Anwesha2k17-Registration->Permission\n" +
                            "Turn on Camera there");
                    AlertDialog alert1 = builder.create();
                    alert1.show();
                    this.finish();
                }
            }
        }
    }

    private void makePost(String postUrl) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, postUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v("Response:", response);

                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            int status = jsonObject.getInt("http");

                            switch (status) {
                                case 200:

                                    Toast.makeText(getApplicationContext(), "Log In Successful", Toast.LENGTH_LONG).show();

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
                params.put(getString(R.string.event_id), eventId);
                params.put(getString(R.string.qr_orgid), getuID());
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

    private String getuID() {
        String uID = mSharedPreferences.getString("uID", null);
        return uID;
    }


}