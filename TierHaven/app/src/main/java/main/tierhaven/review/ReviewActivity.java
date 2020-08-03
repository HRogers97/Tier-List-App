package main.tierhaven.review;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import main.tierhaven.R;
import main.tierhaven.adding.Edit;
import main.tierhaven.model.Review;
import main.tierhaven.viewing.DiscussionActivity;

/*
 Created by: Guillaume Valliere
 */

public class ReviewActivity extends AppCompatActivity {

    private ReviewActivityFragment fragment;

    public static class params {
        public static final String full_review = "full_review";
        public static final String basic_review = "basic_review";
        public static final int create_note = 2;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            // When the toolbar back arrow, the review is saved and is sent back
            public void onClick(View view) {
                // Was the review up to see or to make
                if(ReviewActivityFragment.isPost()) {

                    // Get the review and look if it is completed
                    Review writtenReview = ReviewActivityFragment.getReview();
                    ReviewActivityFragment reviewFragment = new ReviewActivityFragment();

                    // If it is not, ask if he wants to leave anyway
                    if (writtenReview.getName() == null || writtenReview.getReason() == null) {
                        AlertDialog dialog = new AlertDialog.Builder(reviewFragment.getContext())
                                .setMessage("There are some reviews missing. Do you want to exit without publishing it?")
                                .setPositiveButton("Yes", (dialog1, which) -> {
                                    finish();
                                })
                                .setNegativeButton("No", (dialog12, which) -> {

                                })
                                .create();
                        dialog.show();
                    } else {
                        // If it is, ask if he wants to publish it
                        AlertDialog dialog = new AlertDialog.Builder(reviewFragment.getContext())
                                .setMessage("Are you ready to publish your review?")
                                .setPositiveButton("Yes", (dialog1, which) -> {
                                    Intent intent = new Intent(getApplicationContext(), DiscussionActivity.class);
                                    // Send the review
                                    intent.putExtra(params.full_review, writtenReview);
                                    setResult(Activity.RESULT_OK, intent);
                                    finish();
                                })
                                .setNeutralButton("Leave", (dialog3, which) -> {
                                    finish();
                                })
                                .setNegativeButton("No", (dialog12, which) -> {

                                })
                                .create();
                        dialog.show();
                    }
                }
                else {
                    finish();
                }
            }
        });

        fragment = (ReviewActivityFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);

        Intent intent = getIntent();
        Review review = intent.getParcelableExtra(params.full_review);
        if(review == null) {
            review = intent.getParcelableExtra(params.basic_review);
            fragment.createReview(review);
        }
        else {
            fragment.showReview(review);
        }
    }

}
