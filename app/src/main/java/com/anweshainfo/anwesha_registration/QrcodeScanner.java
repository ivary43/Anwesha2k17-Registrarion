package com.anweshainfo.anwesha_registration;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by manish on 25/10/17.
 */

public class QrcodeScanner extends Fragment implements ZXingScannerView.ResultHandler {


    private ZXingScannerView mScannerView;

    public static QrcodeScanner newInstance() {
        QrcodeScanner qrcodeScanner = new QrcodeScanner();
        return qrcodeScanner;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mScannerView = new ZXingScannerView(getActivity());
        mScannerView.setAutoFocus(true);
        return mScannerView;

    }

    /**
     * Sets the {@link me.dm7.barcodescanner.zxing.ZXingScannerView.ResultHandler} callback and
     * opens Camera
     */
    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    /**
     * Closes the Camera
     */
    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    /**
     * Callback for {@link ZXingScannerView} which retrieves data from QRCode
     *
     * @param result Contains data scanned from QRCode
     */

    @Override
    public void handleResult(Result result) {

    }

}
