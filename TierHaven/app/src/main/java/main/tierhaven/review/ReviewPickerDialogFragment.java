package main.tierhaven.review;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import main.tierhaven.List.ListActivity;
import main.tierhaven.List.ListActivityFragment;
import main.tierhaven.R;
import main.tierhaven.model.Review;

/*
 Created by: Guillaume Valliere
 */

public class ReviewPickerDialogFragment extends DialogFragment {

    // fields
    private List<Review> reviews;
    private ReviewAdapter adapter;
    private RecyclerView reviewRecycler;
    private Button doneButton;

    // constructors
    public ReviewPickerDialogFragment() {
    }

    public ReviewPickerDialogFragment(List<Review> reviews) {
        this.reviews = reviews;
    }

    // functions
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_review_picker, container, false);

        // set fields
        reviewRecycler = root.findViewById(R.id.rows_RecyclerView);
        adapter = new ReviewAdapter();

        // get reviews in the recycler view
        reviewRecycler.setAdapter(adapter);
        reviewRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        reviewRecycler.setHasFixedSize(false);

        doneButton = root.findViewById(R.id.done_Button);
        doneButton.setOnClickListener(v -> dismiss());
        return root;
    }

    /**
     * Custom adapter to display reviews
     */
    private class ReviewAdapter extends RecyclerView.Adapter<ReviewPickerDialogFragment.ReviewViewHolder> {
        @NonNull
        @Override
        public ReviewPickerDialogFragment.ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ReviewPickerDialogFragment.ReviewViewHolder(
                    LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.show_review_reviews, parent, false)
            );
        }

        @Override
        public void onBindViewHolder(@NonNull ReviewPickerDialogFragment.ReviewViewHolder holder, int position) {
            holder.set(reviews.get(position));
        }

        @Override
        public int getItemCount() {
            return reviews.size();
        }
    }

    /**
     * Custom view holder to display a clickable user
     */
    private class ReviewViewHolder extends RecyclerView.ViewHolder {

        private final TextView reviewName;
        private final ImageView avatar;
        private final ListActivity activity;
        private Review review;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);

            activity = (ListActivity) getContext();
            reviewName = itemView.findViewById(R.id.reviewName_TextView);
            avatar = itemView.findViewById(R.id.avatar_ImageView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        // send to ReviewActivityFragment to be displayed
                        Intent intent = new Intent(activity, ReviewActivity.class);
                        intent.putExtra(ReviewActivity.params.full_review, review);
                        activity.startActivityForResult(intent, 1);
                    }
                    catch (Exception e) {

                    }
                }
            });
        }

        /**
         * Set the user of the view holder.
         * @param review
         */
        public void set(Review review) {
            this.review = review;
            reviewName.setText(review.getName());
            // get the image
            avatar.setImageBitmap(ListActivityFragment.getReviewImg(review.getItemUuid()));
        }
    }
}
