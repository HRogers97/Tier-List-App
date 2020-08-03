package main.tierhaven.adapter.edit;

import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import main.tierhaven.R;
import main.tierhaven.adding.DialogFragments.NewRowDialogFragment;
import main.tierhaven.model.TierItem;
import main.tierhaven.model.TierRow;


public class TierRowViewHolder extends RecyclerView.ViewHolder {

    public interface OnDirectionChosenListener {
        void onDirectionChosen(Direction direction, TierRow tierRow);
    }

    public interface OnColorClickedListener{
        void onColorClicked(int row);
    }

    public interface OnItemRemovedListener{
        void onItemRemoved(int row ,int placement);
    }


    private final LinearLayout rowshow_layout;
    private final LinearLayout optionsHolder;

    public enum Direction {UP, DOWN};


    private final View root;
    private final EditText title_editText;

    private static RecyclerView images;

    private TierRow tierRow;

    private ImageButton colorButton;
    private ImageButton upButton;
    private ImageButton downButton;

    private boolean isEdit;

    //listeners
    private OnDirectionChosenListener onDirectionChosenListener;
    private OnItemRemovedListener onItemRemovedListener;
    private NewRowDialogFragment.ChangeDesiredListener changeDesiredListener;
    private OnColorClickedListener onColorClickedListener;

    private List<TierItem> tierItems;
    private List<TierRow> rows;

    public TierRowViewHolder(@NonNull View itemView, boolean isEdit) {
        super(itemView);

        root = itemView;
        this.isEdit = isEdit;

        //get all ui components
        images = root.findViewById(R.id.images_recyclerview);
        colorButton = root.findViewById(R.id.color_button);
        upButton = root.findViewById(R.id.up_button);
        downButton = root.findViewById(R.id.down_button);
        title_editText = root.findViewById(R.id.tiertitle_editview);

        optionsHolder = root.findViewById(R.id.optionHolders);
        rowshow_layout = root.findViewById(R.id.rowshow_layout);

        //trigger listener if up is chosen
        upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TierRowViewHolder.this.onDirectionChosenListener != null){
                    onDirectionChosenListener.onDirectionChosen(Direction.UP, tierRow);
                }
            }
        });

        //trigger listener if down is chosen
        downButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TierRowViewHolder.this.onDirectionChosenListener != null){
                    onDirectionChosenListener.onDirectionChosen(Direction.DOWN, tierRow);
                }
            }
        });

        //set background color
        rowshow_layout.setBackgroundColor(Color.rgb(64, 61, 55));

    }


    public void set(final TierRow tierRow){
        this.tierRow = tierRow;
        title_editText.setText(tierRow.getTitle());

        //make text watcher for title
        title_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tierRow.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //get images
        tierItems = tierRow.getImages();

        //set adapters if items are not null
        if (tierItems != null) {
            //make adapter
            TierItemAdapter itemAdapter = new TierItemAdapter(tierItems);
            itemAdapter.setRows(rows);
            //pass in listener
            itemAdapter.setChangeDesiredListener(changeDesiredListener);

            //if item removed listener is set
            if(onItemRemovedListener != null){
                itemAdapter.setOnItemLongClickListener(new TierItemViewHolder.OnItemLongClickListener() {
                    @Override
                    public void onItemLongClick(int placement) {
                        //trigger listener
                        TierRowViewHolder.this.onItemRemovedListener.onItemRemoved(tierRow.getPlacement(), placement);
                    }
                });
            }

            //set adapter
            images.setAdapter(itemAdapter);
            images.setLayoutManager(new GridLayoutManager(root.getContext(), 5));
        }

        //set background color
        title_editText.setBackgroundColor(root.getResources().getColor(tierRow.getColor(), null));
        //title_editText.setBackground();
        title_editText.setEnabled(isEdit);
        if (!isEdit){
            optionsHolder.setVisibility(View.GONE);
        }

        //listener for color button
        colorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if color listener not null
                if (onColorClickedListener != null){
                    //trigger color listener
                    onColorClickedListener.onColorClicked(tierRow.getPlacement());
                }
            }
        });

    }

    public void setOnItemRemovedListener(OnItemRemovedListener onItemRemovedListener) {
        this.onItemRemovedListener = onItemRemovedListener;
    }

    public void setOnDirectionChosenListener(OnDirectionChosenListener onDirectionChosenListener) {
        this.onDirectionChosenListener = onDirectionChosenListener;
    }

    public void setOnColorClickedListener(OnColorClickedListener onColorClickedListener) {
        this.onColorClickedListener = onColorClickedListener;
    }

    public void setChangeDesiredListener(NewRowDialogFragment.ChangeDesiredListener changeDesiredListener) {
        this.changeDesiredListener = changeDesiredListener;
    }
    public void setRows(List<TierRow> rows) {
        this.rows = rows;
    }


}
