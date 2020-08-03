package main.tierhaven.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import main.tierhaven.R;
import main.tierhaven.model.TierList;

public class TierListAdapter extends RecyclerView.Adapter<TierListViewHolder> {

    private List<TierList> data;

    public TierListAdapter(List<TierList> data){
        this.data=data;
    }

    @NonNull
    @Override
    public TierListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_tier_list,parent,false);
        TierListViewHolder holder = new TierListViewHolder(root);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull TierListViewHolder holder, int position) {
        holder.set(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
