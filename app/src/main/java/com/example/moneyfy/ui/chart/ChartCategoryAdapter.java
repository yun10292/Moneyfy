package com.example.moneyfy.ui.chart;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneyfy.R;
import com.example.moneyfy.data.model.ChartCategoryItem;

import java.util.List;

public class ChartCategoryAdapter extends RecyclerView.Adapter<ChartCategoryAdapter.ViewHolder> {

    private final List<ChartCategoryItem> itemList;

    public ChartCategoryAdapter(List<ChartCategoryItem> itemList) {
        this.itemList = itemList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView method, cost;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.category_icon);
            method = itemView.findViewById(R.id.category_method);
            cost = itemView.findViewById(R.id.category_cost);
        }

        public void bind(ChartCategoryItem item) {
            icon.setImageResource(item.getIconResId());
            method.setText(item.getCategoryMethod());
            cost.setText(item.getCost());
        }
    }

    @NonNull
    @Override
    public ChartCategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChartCategoryAdapter.ViewHolder holder, int position) {
        holder.bind(itemList.get(position));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
