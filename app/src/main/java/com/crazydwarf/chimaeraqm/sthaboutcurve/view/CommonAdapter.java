package com.crazydwarf.chimaeraqm.sthaboutcurve.view;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.crazydwarf.chimaeraqm.sthaboutcurve.R;


public class CommonAdapter extends RecyclerView.Adapter<CommonAdapter.CommonViewHolder>
{
    public String[] texts = null;
    public Integer[] imageIds = null;

    private OnCommonRVItemClickListener onCommonRVItemClickListener;

    public void setTexts(String[] texts) {
        this.texts = texts;
    }

    public void setImageIds(Integer[] imageIds) {
        this.imageIds = imageIds;
    }

    public void setOnCommonRVItemClickListener(OnCommonRVItemClickListener onCommonRVItemClickListener) {
        this.onCommonRVItemClickListener = onCommonRVItemClickListener;
    }

    public CommonAdapter(String[] texts, Integer[] imageIds) {
        this.texts = texts;
        this.imageIds = imageIds;
    }

    @NonNull
    @Override
    public CommonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_common,parent,false);
        CommonViewHolder commonViewHolder = new CommonViewHolder(view);
        return commonViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CommonViewHolder holder, final int position) {
        holder.common_tv.setText(texts[position]);
        holder.common_im.setImageResource(imageIds[position]);
        holder.common_holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onCommonRVItemClickListener != null)
                {
                    onCommonRVItemClickListener.onItemClick(v,position);
                }
            }
        });

        holder.common_holder.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(onCommonRVItemClickListener != null)
                {
                    onCommonRVItemClickListener.onItemLongClick(v,position);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return texts.length;
    }

    public static class CommonViewHolder extends RecyclerView.ViewHolder
    {
        public ConstraintLayout common_holder;
        public ImageView common_im;
        public TextView common_tv;
        public CommonViewHolder(View itemView) {
            super(itemView);
            common_holder = itemView.findViewById(R.id.holder_common);
            common_im = itemView.findViewById(R.id.im_common);
            common_tv = itemView.findViewById(R.id.tv_common);
        }
    }

    public static interface OnCommonRVItemClickListener{
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }
}
