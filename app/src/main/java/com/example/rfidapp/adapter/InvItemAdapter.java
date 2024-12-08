package com.example.rfidapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.rfidapp.R;
import com.example.rfidapp.model.EpcModel;

import java.util.List;

public class InvItemAdapter extends RecyclerView.Adapter<InvItemAdapter.listViewHolder> {
    Context con;
    List<EpcModel> inv_epc;

    public interface OnListClick {
        void onInvListClick(String str);
    }

    public InvItemAdapter(List<EpcModel> list, Context context) {
        this.inv_epc = list;
        this.con = context;
    }

    public listViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new listViewHolder(LayoutInflater.from(this.con).inflate(R.layout.inv_item, (ViewGroup) null, false));
    }

    public void onBindViewHolder(listViewHolder listviewholder, int i) {
        listviewholder.tv_epc.setText(this.inv_epc.get(i).getEpc());
    }

    public int getItemCount() {
        return this.inv_epc.size();
    }

    public void addAll(List<EpcModel> list) {
        for (EpcModel add : list) {
            add(add);
        }
    }

    public void add(EpcModel epcModel) {
        this.inv_epc.add(epcModel);
        notifyItemInserted(this.inv_epc.size() - 1);
    }

    public void clear() {
        if (!this.inv_epc.isEmpty()) {
            this.inv_epc.clear();
            notifyDataSetChanged();
        }
    }

    public class listViewHolder extends RecyclerView.ViewHolder {
        LinearLayout ll_list;
        OnListClick onListClick;
        TextView tv_epc;

        public listViewHolder(View view) {
            super(view);
            this.ll_list = (LinearLayout) view.findViewById(R.id.ll_list_tile);
            this.tv_epc = (TextView) view.findViewById(R.id.tv_epc);
        }
    }
}
