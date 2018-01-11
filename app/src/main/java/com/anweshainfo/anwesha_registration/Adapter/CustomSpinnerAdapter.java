package com.anweshainfo.anwesha_registration.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.anweshainfo.anwesha_registration.R;

import java.util.ArrayList;

/**
 * Created by manish on 10/1/18.
 */

public class CustomSpinnerAdapter  extends BaseAdapter implements SpinnerAdapter{
        ArrayList<String> mString = new ArrayList<>();
        private  Context mcontext ;
        private LayoutInflater minflater ;
        public  CustomSpinnerAdapter(Context context , ArrayList<String> string )
        {       this.mString=string ;
                this.mcontext=context ;

        }


    @Override
    public int getCount() {
        return  mString.size() ;
    }

    @Override
    public Object getItem(int i) {
        return mString.get(i);
    }

    @Override
    public long getItemId(int i) {
        return (long)i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        minflater=LayoutInflater.from(mcontext) ;
        view=minflater.inflate(R.layout.dropdownview,null);
        TextView textView=(TextView)view.findViewById(R.id.spinnertextview) ;
        String ms = mString.get(i) ;
        textView.setText(ms) ;

        return view ;

    }

}
