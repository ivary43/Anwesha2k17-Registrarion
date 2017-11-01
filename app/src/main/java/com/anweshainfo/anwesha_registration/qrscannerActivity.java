package com.anweshainfo.anwesha_registration;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
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

import com.google.zxing.Result;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

/**
 * Created by manish on 27/10/17.
 */

public class qrscannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;
    private final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    private ArrayAdapter<String> spinnerArrayAdapter;
    private ArrayList<String> string = new ArrayList<>();
    private ArrayList<String> id = new ArrayList<>();
    @BindView(R.id.scanner)
    LinearLayout scannerView;

    @BindView(R.id.spinner_events_name)
    Spinner eventsspinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_selector);
        ButterKnife.bind(this);

        mScannerView = new ZXingScannerView(this);
        mScannerView.setAutoFocus(true);
        checkPermission();

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
}