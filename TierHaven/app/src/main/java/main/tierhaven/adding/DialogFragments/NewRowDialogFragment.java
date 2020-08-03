package main.tierhaven.adding.DialogFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import main.tierhaven.R;
import main.tierhaven.adapter.edit.TierItemViewHolder;
import main.tierhaven.model.TierItem;
import main.tierhaven.model.TierRow;


public class NewRowDialogFragment extends DialogFragment {

    public interface ChangeDesiredListener{
        void changeDesired(TierItem tierItem, int rowId, int original);
    }

    private List<TierRow> rows;

    private RowAdapter adapter;
    private RecyclerView rowRecycler;
    private ChangeDesiredListener changeDesired;

    public NewRowDialogFragment(List<TierRow> listRows) {
        this.rows = listRows;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_row_picker, container, false);

        rowRecycler = root.findViewById(R.id.rows_RecyclerView);

        //make adapter
        adapter = new RowAdapter();
        //set adapter listener
        adapter.setChangeDesired(changeDesired);

        //set adapter
        rowRecycler.setAdapter(adapter);
        rowRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        rowRecycler.setHasFixedSize(false);

        return root;
    }

    public void setChangeDesired(ChangeDesiredListener changeDesired) {
        this.changeDesired = changeDesired;
    }
    /**
     * Custom adapter to display rows
     */
    private class RowAdapter extends RecyclerView.Adapter<NewRowDialogFragment.RowViewHolder> {

        private  ChangeDesiredListener changeDesired;

        @NonNull
        @Override
        public NewRowDialogFragment.RowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new NewRowDialogFragment.RowViewHolder(
                    LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.show_tier_rows, parent, false)
            );
        }

        @Override
        public void onBindViewHolder(@NonNull NewRowDialogFragment.RowViewHolder holder, int position) {
            //set holder
            holder.setChangeDesired(changeDesired);
            holder.set(rows.get(position));
        }

        @Override
        public int getItemCount() {
            return rows.size();
        }

        public void setChangeDesired(ChangeDesiredListener changeDesired) {
            this.changeDesired = changeDesired;
        }
    }

    /**
     * Custom view holder to display a clickable user
     */
    private class RowViewHolder extends RecyclerView.ViewHolder {

        private  ChangeDesiredListener changeDesired;

        private final TextView rowName;
        private TierRow tierRow;

        public RowViewHolder(@NonNull View itemView) {
            super(itemView);

            //set row name
            rowName = itemView.findViewById(R.id.rowTitle_TextView);
            //make click listener
            rowName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isListenerUsed = false;

                    //search for image in rows
                    for(int i = 0; i < rows.size(); i++) {
                        //if image is found
                        if(rows.get(i).getImages().contains(TierItemViewHolder.getTierItem())) {
                            //if listener is not null
                            if (changeDesired != null) {
                                //call listener
                                changeDesired.changeDesired(TierItemViewHolder.getTierItem(), tierRow.getPlacement(), i);
                                isListenerUsed = true;
                            }
                            break;
                        }
                    }

                    //if listener was not used and listener is not null
                    if (!isListenerUsed && changeDesired != null){
                        //call listener anyways
                        changeDesired.changeDesired(TierItemViewHolder.getTierItem(), tierRow.getPlacement(), -1);
                    }
                    dismiss();

                }
            });
        }

        /**
         * Set the user of the view holder.
         * @param tierRow
         */
        public void set(TierRow tierRow) {
            this.tierRow = tierRow;
            rowName.setText(tierRow.getTitle());
        }

        public void setChangeDesired(ChangeDesiredListener changeDesired) {
            this.changeDesired = changeDesired;
        }
    }
}
