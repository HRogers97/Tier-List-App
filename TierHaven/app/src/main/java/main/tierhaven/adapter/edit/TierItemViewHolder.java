package main.tierhaven.adapter.edit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.UUID;

import main.tierhaven.List.ListActivity;
import main.tierhaven.R;
import main.tierhaven.adding.Edit;
import main.tierhaven.adding.DialogFragments.NewRowDialogFragment;
import main.tierhaven.model.Review;
import main.tierhaven.model.TierItem;
import main.tierhaven.model.TierRow;
import main.tierhaven.review.ReviewActivity;
import main.tierhaven.viewing.DiscussionActivity;

public class TierItemViewHolder extends RecyclerView.ViewHolder {

    //interfaces for events
    public interface OnImageClickedListener{
        public void onImagedClicked(TierItem tierItem);
    }

    public interface OnItemLongClickListener {
        public void onItemLongClick(int placement);
    }

    private final View root;
    private final ImageView image;
    private static TierItem currentItem;

    private final LinearLayout tieritemholder;

    private DiscussionActivity discussionActivity;
    private Edit editActivity;

    //listeners
    private OnImageClickedListener onImageClickedListener;
    private OnItemLongClickListener onItemLongClickListener;
    private NewRowDialogFragment.ChangeDesiredListener changeDesiredListener;

    private TierItem tierItem;
    private List<TierRow> rows;

    private String message;
    private final String DEFAULTMESSAGE = "Are you sure you want to remove item from row?";

    public TierItemViewHolder(@NonNull View itemView) {

        super(itemView);

        root = itemView;

        image = root.findViewById(R.id.tierItem_imageView);

        tieritemholder = root.findViewById(R.id.tieritemholder);

        try {
            discussionActivity = (DiscussionActivity) root.getContext();
        }
        catch(Exception e) {
            discussionActivity = null;
        }

        try {
            editActivity = (Edit) root.getContext();
        }
        catch(Exception e) {
            editActivity = null;
        }

        message = DEFAULTMESSAGE;
    }

    public void set(TierItem tierItem){

        //set current tier item
        this.tierItem = tierItem;
        image.setImageBitmap(tierItem.getImage());

        tieritemholder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentItem = tierItem;
                //if listener not null
                if (onImageClickedListener != null){
                    //image is clicked
                    onImageClickedListener.onImagedClicked(tierItem);
                }

                // If the context is the discussion fragment, then create new review
                if(discussionActivity != null) {
                    Review newReview = new Review(UUID.randomUUID().toString(), currentItem.getUuid(), ListActivity.getCurrentList().getUuid(),String.valueOf(tierItem.getRowUuid()));
                    Intent intent = new Intent(root.getContext(), ReviewActivity.class);
                    intent.putExtra(ReviewActivity.params.basic_review, newReview);
                    discussionActivity.startActivityForResult(intent, 2);
                }
                else if(editActivity != null) {
                    //make dialog fragment if rows are not null
                    if (rows != null){
                        NewRowDialogFragment reviewPicker = new NewRowDialogFragment(rows);
                        reviewPicker.setChangeDesired(changeDesiredListener);
                        reviewPicker.show(editActivity.getSupportFragmentManager(), "see-rows");
                    }
                }
            }
        });

        tieritemholder.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                message = DEFAULTMESSAGE;
                //trigger event
                onItemLongClickListener.onItemLongClick(TierItemViewHolder.this.tierItem.getPlacement());
                return true;
            }
        });
    }


    public void setOnImageClickedListener(OnImageClickedListener onImageClickedListener) {
        this.onImageClickedListener = onImageClickedListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public void setRows(List<TierRow> rows){
        this.rows = rows;
    }

    public static Bitmap getTierImg() {
        return currentItem.getImage();
    }

    public static TierItem getTierItem() {
        return currentItem;
    }

    public void setChangeDesiredListener(NewRowDialogFragment.ChangeDesiredListener changeDesiredListener) {
        this.changeDesiredListener = changeDesiredListener;
    }

    public void setMessage(String message){
        this.message = message;
    }

}
