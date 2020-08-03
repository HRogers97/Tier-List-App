package main.tierhaven.adapter.edit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import main.tierhaven.R;
import main.tierhaven.adding.DialogFragments.NewRowDialogFragment;
import main.tierhaven.model.TierRow;

public class TierRowAdapter extends RecyclerView.Adapter<TierRowViewHolder> {

    private List<TierRow> tierRows;
    private boolean isEdit;

    //listeners
    private TierRowViewHolder.OnDirectionChosenListener onDirectionChosenListener;
    private TierRowViewHolder.OnItemRemovedListener onItemRemovedListener;
    private NewRowDialogFragment.ChangeDesiredListener changeDesiredListener;
    private TierRowViewHolder.OnColorClickedListener onColorClickedListener;

    public TierRowAdapter(List<TierRow> tierRows, boolean isEdit) {
        //get data
        this.tierRows = tierRows;
        this.isEdit = isEdit;
    }

    @NonNull
    @Override
    public TierRowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        parent.setClipChildren(false);
        parent.setClipToPadding(false);
        View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_show, parent, false);

        TierRowViewHolder holder = new TierRowViewHolder(root, isEdit);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull TierRowViewHolder holder, int position) {

        //set listeners
        holder.setOnDirectionChosenListener(this.onDirectionChosenListener);
        holder.setOnItemRemovedListener(onItemRemovedListener);
        holder.setOnColorClickedListener(onColorClickedListener);

        //set data
        holder.setRows(tierRows);
        holder.setChangeDesiredListener(changeDesiredListener);
        holder.set(tierRows.get(position));

    }

    @Override
    public int getItemCount() {
        return tierRows.size();
    }

    public void setOnDirectionChosenListener(TierRowViewHolder.OnDirectionChosenListener onDirectionChosenListener) {
        this.onDirectionChosenListener = onDirectionChosenListener;
    }

    public void setOnItemRemovedListener(TierRowViewHolder.OnItemRemovedListener onItemRemovedListener) {
        this.onItemRemovedListener = onItemRemovedListener;
    }

    public void setChangeDesiredListener(NewRowDialogFragment.ChangeDesiredListener changeDesiredListener) {
        this.changeDesiredListener = changeDesiredListener;
    }

    public void setOnColorClickedListener(TierRowViewHolder.OnColorClickedListener onColorClickedListener) {
        this.onColorClickedListener = onColorClickedListener;
    }
}
