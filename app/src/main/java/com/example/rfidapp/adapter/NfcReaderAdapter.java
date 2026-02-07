package com.example.rfidapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.example.rfidapp.R;
import com.example.rfidapp.model.NfcReadModel;

import java.util.ArrayList;

public class NfcReaderAdapter extends RecyclerView.Adapter<NfcReaderAdapter.viewHolder> {
    Context context;
    ArrayList<NfcReadModel> list;

    public NfcReaderAdapter(Context context2, ArrayList<NfcReadModel> arrayList) {
        new ArrayList();
        this.context = context2;
        this.list = arrayList;
    }

    public viewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new viewHolder(LayoutInflater.from(this.context).inflate(R.layout.nfc_reader_list, viewGroup, false));
    }

    public void onBindViewHolder(viewHolder viewholder, int i) {
        NfcReadModel nfcReadModel = this.list.get(i);
        viewholder.nfcType.setText(nfcReadModel.getNfcType());
        viewholder.nfcLoc.setText(nfcReadModel.getNfcLoc());
        if (nfcReadModel.getNfcType() == "Text Record") {
            viewholder.img.setImageResource(R.drawable.text_24);
        } else if (nfcReadModel.getNfcType() == "Uri Record") {
            viewholder.img.setImageResource(R.drawable.url_24);
        }
    }

    public int getItemCount() {
        return this.list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView nfcLoc;
        TextView nfcType;

        public viewHolder(View view) {
            super(view);
            this.nfcType = (TextView) view.findViewById(R.id.nfc_tag_type);
            this.nfcLoc = (TextView) view.findViewById(R.id.nfc_location);
            this.img = (ImageView) view.findViewById(R.id.img_nfc_list);
        }
    }
}
