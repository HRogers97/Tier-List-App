package main.tierhaven.review;

import androidx.fragment.app.Fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import main.tierhaven.List.ListActivityFragment;
import main.tierhaven.R;
import main.tierhaven.adapter.edit.TierItemViewHolder;
import main.tierhaven.model.Review;
import main.tierhaven.viewing.DiscussionActivityFragment;

/*
 Created by: Guillaume Valliere
 */

public class ReviewActivityFragment extends Fragment {

    // fields
    private TextView title;
    private EditText name;
    private TextView origin;
    private Spinner newPosition;
    private EditText why;
    private ImageView profile;
    private static Review review;

    private static boolean isPost;
    private static View root;

    // constructor
    public ReviewActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // set fields
        root = inflater.inflate(R.layout.fragment_review, container, false);

        title = root.findViewById(R.id.reviewTitle_TextView);
        name = root.findViewById(R.id.name_EditText);
        profile = root.findViewById(R.id.picture_ImageView);
        origin = root.findViewById(R.id.origin_TextView);
        newPosition = root.findViewById(R.id.correction_Spinner);
        why = root.findViewById(R.id.why_EditText);

        // listeners that change the review item
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                review.setName(editable.toString());
            }
        });

        newPosition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                review.setNewPosition(newPosition.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        why.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                review.setReason(editable.toString());
            }
        });

        return root;
    }

    // function that is called when the user wants to create a new review
    public void createReview(Review baseReview) {
        // set flag
        isPost = true;

        review = baseReview;
        title.setText(R.string.edit_review);

        // get the row the item comes from
        origin.setText(DiscussionActivityFragment.getTitleFromRowNum(review.getOrigin()));

        // get the image that was clicked
        profile.setImageBitmap(TierItemViewHolder.getTierImg());

        // get all the rows from the tier list and display them
        String[] tiers = DiscussionActivityFragment.getRows();
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(), R.layout.new_position_review, R.id.tier_TextView);
        dataAdapter.addAll(tiers);
        newPosition.setAdapter(dataAdapter);
    }

    public void showReview(Review fullReview) {
        // set flag
        isPost = false;

        // Show the data in the review
        review = fullReview;
        title.setText(R.string.see_review);
        origin.setText(review.getOrigin());

        name.setText(review.getName());
        name.setEnabled(false);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(), R.layout.new_position_review, R.id.tier_TextView);
        dataAdapter.addAll(review.getNewPosition());

        newPosition.setAdapter(dataAdapter);
        newPosition.setEnabled(false);
        newPosition.getBackground().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);

        profile.setImageBitmap(ListActivityFragment.getReviewImg(review.getItemUuid()));

        why.setText(review.getReason());
        why.setEnabled(false);
    }

    // Returns the current review
    public static Review getReview() {
        return review;
    }

    // Returns a flag saying if the review was meant to be shown or posted
    public static boolean isPost() {
        return isPost;
    }

    // Context to show alert
    public Context getContext() {
        return root.getContext();
    }
}
