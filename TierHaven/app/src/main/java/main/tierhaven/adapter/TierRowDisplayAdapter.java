package main.tierhaven.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import main.tierhaven.R;
import main.tierhaven.model.TierRow;

public class TierRowDisplayAdapter extends RecyclerView.Adapter<TierRowDisplayViewHolder> {
    private List<TierRow> data;

    public TierRowDisplayAdapter(List<TierRow> data){
        this.data = data;
    }

    @NonNull
    @Override
    public TierRowDisplayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.tier_row_display, parent, false);

        return new TierRowDisplayViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull TierRowDisplayViewHolder holder, int position) {
        holder.set(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
