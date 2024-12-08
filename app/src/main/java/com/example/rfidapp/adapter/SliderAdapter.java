package com.example.rfidapp.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.rfidapp.R;
import com.example.rfidapp.model.SliderItems;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SliderViewHolder> {
    /* access modifiers changed from: private */
    public OnItemClickListener onItemClickListener;
    private Runnable runnable = new Runnable() {
        public void run() {
            SliderAdapter.this.sliderItems.addAll(SliderAdapter.this.sliderItems);
            SliderAdapter.this.notifyDataSetChanged();
        }
    };
    /* access modifiers changed from: private */
    public List<SliderItems> sliderItems;
    private ViewPager2 viewPager2;

    public interface OnItemClickListener {
        void onItemClick(String str);
    }

    public SliderAdapter(List<SliderItems> list, ViewPager2 viewPager22, OnItemClickListener onItemClickListener2) {
        this.sliderItems = list;
        this.viewPager2 = viewPager22;
        this.onItemClickListener = onItemClickListener2;
    }

    public SliderViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new SliderViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.slide_item_container, viewGroup, false));
    }

    public void onBindViewHolder(SliderViewHolder sliderViewHolder, @SuppressLint("RecyclerView") final int i) {
        sliderViewHolder.imageView.setImageResource(this.sliderItems.get(i).getImage());
        if (i == this.sliderItems.size() - 2) {
            this.viewPager2.post(this.runnable);
        }
        sliderViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (SliderAdapter.this.onItemClickListener != null) {
                    SliderAdapter.this.onItemClickListener.onItemClick(((SliderItems) SliderAdapter.this.sliderItems.get(i)).getIdentifier());
                }
            }
        });
    }

    public int getItemCount() {
        return this.sliderItems.size();
    }

    public static class SliderViewHolder extends RecyclerView.ViewHolder {
        /* access modifiers changed from: private */
        public RoundedImageView imageView;

        public SliderViewHolder(View view) {
            super(view);
            this.imageView = (RoundedImageView) view.findViewById(R.id.imageSlide);
        }
    }
}
