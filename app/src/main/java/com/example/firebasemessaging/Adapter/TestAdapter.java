package com.example.firebasemessaging.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firebasemessaging.R;

import java.util.ArrayList;

public class TestAdapter extends RecyclerView.Adapter<TestAdapter.ViewHolder> {

    ArrayList<String> testList;
    ItemClick itemClick;

    public TestAdapter(ArrayList<String> testList, ItemClick itemClick) {
        this.testList = testList;
        this.itemClick = itemClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_test, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String test = testList.get(position);
        holder.textView.setText(test);

        holder.itemView.setOnClickListener(v -> {
            itemClick.click(position);
        });
    }

    @Override
    public int getItemCount() {
        if(testList != null)
            return  testList.size();
        return 0;
    }

    public interface ItemClick {
        void click(int position);
    };

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.txt_test);
        }
    }
}
