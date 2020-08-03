package main.tierhaven.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import main.tierhaven.TierListApplication;
import main.tierhaven.model.Comment;
import main.tierhaven.networking.HttpRequest;
import main.tierhaven.networking.HttpRequestTask;
import main.tierhaven.util.EditCommentDialog;
import main.tierhaven.viewing.DiscussionActivity;
import main.tierhaven.viewing.DiscussionActivityFragment;
import main.tierhaven.R;

public class CommentViewHolder extends RecyclerView.ViewHolder {

    private final TextView commentBodyTextView;
    private final View root;
    private final ImageButton deleteImageButton;
    private final ImageButton editImageButton;
    private String newComement;
    public CommentAdapter adapter;

    public CommentViewHolder(@NonNull View root) {
        super(root);

        commentBodyTextView = root.findViewById(R.id.commentBody_textView);
        deleteImageButton = root.findViewById(R.id.delete_imageButton);
        editImageButton = root.findViewById(R.id.edit_imageButton);

        this.root = root;
    }

    public void set(final Comment comment){
        DiscussionActivity discussionActivity = (DiscussionActivity) root.getContext();
        DiscussionActivityFragment discussionActivityFragment = (DiscussionActivityFragment) discussionActivity.getSupportFragmentManager().findFragmentById(R.id.fragment);

        commentBodyTextView.setText(comment.getBody());

        // If comment was left in the current session, add delete and edit buttons
        // Else they are not displayed
        int visibility = View.GONE;
        if(comment.isCurrSession()){
            visibility = View.VISIBLE;
        }
        deleteImageButton.setVisibility(visibility);
        editImageButton.setVisibility(visibility);

        deleteImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteComment(getAdapterPosition());
            }
        });

        editImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditComment(comment, getAdapterPosition());
            }
        });
    }

    public void DeleteComment(int pos){
        // Get fragment, adapter, and tier list application
        // Used to update adapter and make request to server
        DiscussionActivity discussionActivity = (DiscussionActivity) root.getContext();
        DiscussionActivityFragment discussionActivityFragment = (DiscussionActivityFragment) discussionActivity.getSupportFragmentManager().findFragmentById(R.id.fragment);
        RecyclerView commentRecyclerView = discussionActivityFragment.commentRecyclerView;
        RecyclerView.Adapter adapter = commentRecyclerView.getAdapter();
        TierListApplication application = (TierListApplication) discussionActivity.getApplication();

        // Get comment and build the url
        Comment comment = (Comment) discussionActivityFragment.comments.get(pos);
        String url = application.PROTOCOL+application.LOCALHOST+application.PORT+"/comment/"+comment.getUuid();

        // Make the request to server
        HttpRequest request = new HttpRequest(url, HttpRequest.Method.DELETE);
        HttpRequestTask httpRequestTask = new HttpRequestTask();
        httpRequestTask.setOnResponseListener(data -> {
            // Nothing to do here
        });
        httpRequestTask.setOnErrorListener(error -> {
            // Simply show the error message
            Toast.makeText(discussionActivityFragment.getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
        });
        httpRequestTask.execute(request);

        // Update adapter to remove comment
        discussionActivityFragment.comments.remove(pos);
        adapter.notifyItemRemoved(pos);
    }

    public void EditComment(Comment comment, int pos){
        // Get fragment and adapter
        DiscussionActivity discussionActivity = (DiscussionActivity) root.getContext();
        DiscussionActivityFragment discussionActivityFragment = (DiscussionActivityFragment) discussionActivity.getSupportFragmentManager().findFragmentById(R.id.fragment);
        RecyclerView commentRecyclerView = discussionActivityFragment.commentRecyclerView;
        RecyclerView.Adapter adapter = commentRecyclerView.getAdapter();

        // Set variables in activity to apply the edit from the dialog fragment
        discussionActivity.comment = comment;
        discussionActivity.pos = pos;
        discussionActivity.adapter = adapter;

        // Display the edit comment dialog fragment
        // The actual edit of the comment is done through the dialog fragment and the discussion activity
        EditCommentDialog dialog = new EditCommentDialog(comment.getBody());
        dialog.show(discussionActivity.getSupportFragmentManager(), "Edit Comment");
    }
}
