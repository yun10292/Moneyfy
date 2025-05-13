package com.example.moneyfy.ui.home.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneyfy.R;
import com.example.moneyfy.data.model.ExpenseItem;

import java.util.List;

public class HomeListAdapter extends RecyclerView.Adapter<HomeListAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(ExpenseItem item);
    }

    private final List<ExpenseItem> data;
    private OnItemClickListener listener;

    public HomeListAdapter(List<ExpenseItem> data) {
        this.data = data;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView methodView, dateView, costView;

        public ViewHolder(View view) {
            super(view);
            methodView = view.findViewById(R.id.list_method);
            dateView = view.findViewById(R.id.list_date);
            costView = view.findViewById(R.id.list_cost);
        }

        public void bind(ExpenseItem item, OnItemClickListener listener) {
            methodView.setText(item.getMethod());
            dateView.setText(item.getDate());
            costView.setText(item.getCost());

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onItemClick(item);
            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(data.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
