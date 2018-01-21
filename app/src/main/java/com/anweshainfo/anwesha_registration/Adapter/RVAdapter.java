package com.anweshainfo.anwesha_registration.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.anweshainfo.anwesha_registration.R;
import com.anweshainfo.anwesha_registration.model.Participant;

import java.util.ArrayList;

/**
 * Created by mayank on 22/1/18.
 */

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ViewHolder> {

    private ArrayList<Participant> participants;

    public RVAdapter(ArrayList<Participant> participants) {
        this.participants = participants;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_participant, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Participant participant = participants.get(position);
        holder.srno.setText(position + 1 + "");
        holder.name.setText(participant.getName());
        holder.anwid.setText(participant.getAnwid());
    }

    @Override
    public int getItemCount() {
        return participants != null ? participants.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView srno;
        private TextView name;
        private TextView anwid;

        private ViewHolder(View view) {
            super(view);
            srno = view.findViewById(R.id.tv_srno);
            name = view.findViewById(R.id.tv_name);
            anwid = view.findViewById(R.id.tv_anwid);
        }
    }
}
