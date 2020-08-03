package main.tierhaven.adapter.edit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import main.tierhaven.R;
import main.tierhaven.adding.DialogFragments.NewRowDialogFragment;
import main.tierhaven.model.TierItem;
import main.tierhaven.model.TierRow;


public class TierItemAdapter extends RecyclerView.Adapter<TierItemViewHolder> {

    private List<TierItem> data;

    //all click listeners
    private TierItemViewHolder.OnImageClickedListener onImageClickedListener;
    private TierItemViewHolder.OnItemLongClickListener onItemLongClickListener;
    private NewRowDialogFragment.ChangeDesiredListener changeDesiredListener;
    private String message;

    private List<TierRow> rows;

    public TierItemAdapter(List<TierItem> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public TierItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        parent.setClipChildren(false);
        parent.setClipToPadding(false);
        View root = LayoutInflater.from(parent.getContext()) .inflate(R.layout.tieritem_show,parent,false);

        TierItemViewHolder holder = new TierItemViewHolder(root);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull TierItemViewHolder holder, int position) {

        //set listeners and rows from the ones given
        holder.setRows(rows);
        holder.setOnImageClickedListener(onImageClickedListener);
        holder.setOnItemLongClickListener(onItemLongClickListener);
        holder.setChangeDesiredListener(changeDesiredListener);

        if (message != null){
            holder.setMessage(message);
        }

        //set holder
        holder.set(data.get(position));
    }

    @Override
    public int getItemCount() {
        if(data != null)
            return data.size();
        else
            return 0;
    }

    public void setOnImageClickedListener(TierItemViewHolder.OnImageClickedListener onImageClickedListener) {
        this.onImageClickedListener = onImageClickedListener;
    }

    public void setOnItemLongClickListener(TierItemViewHolder.OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }
    public void setChangeDesiredListener(NewRowDialogFragment.ChangeDesiredListener changeDesiredListener) {
        this.changeDesiredListener = changeDesiredListener;
    }

    public void setRows(List<TierRow> rows){
        this.rows = rows;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
