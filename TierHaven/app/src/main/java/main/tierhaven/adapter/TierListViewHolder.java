package main.tierhaven.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.media.Image;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;
import java.util.List;

import main.tierhaven.List.ListActivity;
import main.tierhaven.List.ListActivityFragment;
import main.tierhaven.TierListApplication;
import main.tierhaven.adding.Edit;
import main.tierhaven.model.SampleData;
import main.tierhaven.model.TierItem;
import main.tierhaven.model.TierList;
import main.tierhaven.R;
import main.tierhaven.model.Review;
import main.tierhaven.model.TierRow;
import main.tierhaven.review.ReviewPickerDialogFragment;

public class TierListViewHolder extends RecyclerView.ViewHolder{

    // Classes
    private final ListActivity activity;
    private static TierListApplication application;

    // LAYOUT ELEMENTS
    private View root;
    private CardView cardViewLayout;
    private TextView title;
    private TextView first;
    private TextView second;
    private TextView third;
    private ImageView firstImage;
    private ImageView secondImage;
    private ImageView thirdImage;

    public TierListViewHolder(@NonNull View root) {
        super(root);

        // Get all the UI elements from the layout
        activity = (ListActivity) root.getContext();
        title = root.findViewById(R.id.Title_TextView);
        first = root.findViewById(R.id.firstItem_TextView);
        second =root.findViewById(R.id.secondItem_TextView);
        third = root.findViewById(R.id.thirdItem_TextView);
        firstImage = root.findViewById(R.id.firstItem_ImageView);
        secondImage = root.findViewById(R.id.secondItem_ImageView);
        thirdImage = root.findViewById(R.id.thirdItem_ImageView);
        cardViewLayout=root.findViewById(R.id.Layout_CardView);
        application = (TierListApplication) activity.getApplication();
        this.root = root;
    }


    /**
     * Shows all of the Tier Lists
     * @param tierList
     */
    @SuppressLint("ClickableViewAccessibility")
    public void set(final TierList tierList){
        SampleData.CreateSampleTiers(activity.getResources());

        List<TierItem> items = new LinkedList<>();
        List<TierRow> rows = new LinkedList<>();

        // Don't show the Tier List if its empty
        if(tierList.getTierRows()==null){
            ConstraintLayout layout = root.findViewById(R.id.List_ConstraintLayout);
            layout.setVisibility(View.GONE);
            return;
        }

        // Sort the Tier Items from highest to lowest in the placement
        int count=0;
        for(int i=0; i<tierList.getTierRows().size();i++){
            if(tierList.getTierRows().get(i).getPlacement()==count) {
                rows.add(tierList.getTierRows().get(i));
                count++;
                i=0;
            }
        }

        // Retrieve all the Tier Items in order
            for(TierRow row: rows){
                if(row.getImages()!=null){
                    for(TierItem item: row.getImages()){
                        items.add(item);
                    }
                }
            }

        // Don't show the Tier List if it has no Tier Item
        if(items.size()==0)
        {
            ConstraintLayout layout = root.findViewById(R.id.List_ConstraintLayout);
            layout.setVisibility(View.GONE);
            return;
        }

        // Adapts the View Holder depending on the number of Tier items in the Tier List
        if(items.size()>=3) {
            firstImage.setImageBitmap(items.get(0).getImage());
            secondImage.setImageBitmap(items.get(1).getImage());
            thirdImage.setImageBitmap(items.get(2).getImage());

            title.setText(tierList.getTierName());
            first.setText("1 - " + items.get(0).getName());
            second.setText("2 - " + items.get(1).getName());
            third.setText("3 - " + items.get(2).getName());
        }
        else if(items.size()==2){
            firstImage.setImageBitmap(items.get(0).getImage());
            secondImage.setImageBitmap(items.get(1).getImage());
            thirdImage.setVisibility(View.GONE);

            title.setText(tierList.getTierName());
            first.setText("1 - " + items.get(0).getName());
            second.setText("2 - " + items.get(1).getName());
            third.setVisibility(View.GONE);
        }
        else{
            firstImage.setImageBitmap(items.get(0).getImage());
            secondImage.setVisibility(View.GONE);
            thirdImage.setVisibility(View.GONE);

            title.setText(tierList.getTierName());
            first.setText("1 - " + items.get(0).getName());
            second.setVisibility(View.GONE);
            third.setVisibility(View.GONE);
        }


        cardViewLayout.setOnTouchListener((view,motionEvent)->{
            long CLICK_THRESHOLD =200;

            activity.startActionMode(new ActionMode.Callback2() {
                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    // Shows the menu when it is clicked
                    if(motionEvent.getAction()== MotionEvent.ACTION_UP && (motionEvent.getEventTime() - motionEvent.getDownTime())<CLICK_THRESHOLD){
                        mode.getMenuInflater().inflate(R.menu.menu_floating_see_or_review,menu);
                    }
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    switch(item.getItemId()) {
                        case R.id.seeReview_MenuItem:
                            // get the reviews and show them
                            List<Review> reviews = ListActivityFragment.getReviewsForTier(tierList.getUuid());
                            ReviewPickerDialogFragment reviewPicker = new ReviewPickerDialogFragment(reviews);
                            reviewPicker.show(activity.getSupportFragmentManager(), "see-reviews");
                            break;

                        case R.id.discussion_MenuItem:
                            // Goes to the discussion activity
                            activity.viewTierList(tierList);
                            break;

                        case R.id.editTierList_MenuItem:
                            // Starts the edit fragment and passes the Uuid of the Tier List
                            Intent intent = new Intent(root.getContext(), Edit.class);
                            intent.putExtra(application.GET_TIER_LIST_UUID,tierList.getUuid());
                            activity.startActivity(intent);
                        case R.id.close_MenuItem:
                            // Closes the floating menu
                            mode.finish();
                    }
                    mode.finish();
                    return true;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {

                }

                @Override
                public void onGetContentRect(ActionMode mode, View view, Rect outRect) {
                    // Show the floating menu next to where the screen was touched
                    int x = (int) Math.floor(motionEvent.getRawX());
                    int y = (int) Math.floor(motionEvent.getRawY());
                    outRect.set(x, y, x, y);
                }

            },ActionMode.TYPE_FLOATING);
          return true;
        });
    }
}
