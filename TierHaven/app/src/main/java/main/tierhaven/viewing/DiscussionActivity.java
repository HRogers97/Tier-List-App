package main.tierhaven.viewing;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import main.tierhaven.List.ListActivityFragment;
import main.tierhaven.R;
import main.tierhaven.TierListApplication;
import main.tierhaven.model.Comment;
import main.tierhaven.model.Review;
import main.tierhaven.networking.HttpRequest;
import main.tierhaven.networking.HttpRequestTask;
import main.tierhaven.networking.HttpResponse;
import main.tierhaven.networking.OnResponseListener;
import main.tierhaven.review.ReviewActivity;
import main.tierhaven.util.EditCommentDialog;

public class DiscussionActivity extends AppCompatActivity implements EditCommentDialog.CommentDialogListener {
    public Comment comment;
    public int pos;
    public RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discussion);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Add back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Return to list activity
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });
    }

    // Apply the edit to the comment and update adapter
    @Override
    public void ApplyEdit(String commentBody) {
        // Update the body of the comment
        comment.setBody(commentBody);

        // Get application and buld the URL
        TierListApplication application = (TierListApplication) getApplication();
        String url = application.PROTOCOL+application.LOCALHOST+application.PORT+"/comment/"+comment.getUuid();

        // Make the request to the server
        HttpRequest request = new HttpRequest(url, HttpRequest.Method.PATCH);
        request.setRequestBody("application/json", comment.toJson());
        HttpRequestTask httpRequestTask = new HttpRequestTask();
        httpRequestTask.setOnResponseListener(data -> {
            // Nothing to do here
        });
        httpRequestTask.setOnErrorListener(error -> {
            // Simply show the error message
            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
        });
        httpRequestTask.execute(request);

        // Update the adapter
        adapter.notifyItemChanged(pos);
    }

    // New Review is returned
    // TO DO change to params
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if(requestCode == ReviewActivity.params.create_note) {
                ListActivityFragment activity = new ListActivityFragment();

                // get the review
                Review newReview = data.getParcelableExtra(ReviewActivity.params.full_review);
                TierListApplication application = new TierListApplication();

                // Put request to add the note to the server
                String url = application.PROTOCOL+application.LOCALHOST+application.PORT+"/review/" + newReview.getUuid();
                HttpRequest request = new HttpRequest(url, HttpRequest.Method.PUT);
                request.setRequestBody("application/json", newReview.toJson());

                HttpRequestTask httpRequestTask = new HttpRequestTask();
                httpRequestTask.setOnResponseListener(new OnResponseListener<HttpResponse>() {
                    @Override
                    public void onResponse(HttpResponse data) {
                        activity.updateLists();
                        Toast.makeText(getApplicationContext(), "Thank you for adding a review!", Toast.LENGTH_LONG).show();
                    }
                });
                httpRequestTask.execute(request);

            }
        }
    }
}
