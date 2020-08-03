package main.tierhaven.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import main.tierhaven.R;
import main.tierhaven.adapter.edit.TierItemAdapter;
import main.tierhaven.model.TierRow;

public class TierRowDisplayViewHolder extends RecyclerView.ViewHolder {

    private final View root;
    private final TextView titleTextView;
    private final RecyclerView tierItemsRecyclerView;


    public TierRowDisplayViewHolder(@NonNull View root) {
        super(root);

        titleTextView = root.findViewById(R.id.tierTitle_textView);
        tierItemsRecyclerView = root.findViewById(R.id.tierItems_recyclerView);

        this.root = root;
    }

    public void set(TierRow tierRow){
        titleTextView.setText(tierRow.getTitle());
        titleTextView.setBackgroundColor(root.getResources().getColor(tierRow.getColor(), null));

        TierItemAdapter adapter = new TierItemAdapter(tierRow.getImages());
        tierItemsRecyclerView.setAdapter(adapter);
        tierItemsRecyclerView.setLayoutManager(new LinearLayoutManager(root.getContext(), LinearLayoutManager.HORIZONTAL, false));
    }


}
