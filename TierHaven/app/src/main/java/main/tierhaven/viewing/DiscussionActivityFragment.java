package main.tierhaven.viewing;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import main.tierhaven.R;
import main.tierhaven.TierListApplication;
import main.tierhaven.adapter.CommentAdapter;
import main.tierhaven.adapter.TierRowDisplayAdapter;
import main.tierhaven.model.Comment;
import main.tierhaven.model.TierItem;
import main.tierhaven.model.TierList;
import main.tierhaven.model.TierRow;
import main.tierhaven.networking.HttpRequest;
import main.tierhaven.networking.HttpRequestTask;
import main.tierhaven.networking.HttpResponse;
import main.tierhaven.networking.OnResponseListener;

/**
 * A placeholder fragment containing a simple view.
 */
public class DiscussionActivityFragment extends Fragment {

    private Button commentSubmitBtn;
    private EditText commentEditText;
    private CommentAdapter adapter;
    public ArrayList comments;
    public RecyclerView commentRecyclerView;
    private TextView category;
    private RecyclerView tierListRecyclerView;
    private static List<TierRow> tierRowData;
    private static TierList tierList;
    private TierListApplication application;
    private String url;
    private TextView infoTextView;

    public static class Params{
        public static final String CATEGORY = "category";
        public static final String TIER_LIST = "tierlist";
        public static final int GET_CATEGORY = 0;
    }

    public DiscussionActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_discussion, container, false);

        application = (TierListApplication) getActivity().getApplication();

        // Get tier list
        String tierUuid = getActivity().getIntent().getStringExtra(Params.TIER_LIST);
        tierList = application.getTierListByUuid(tierUuid);
        tierRowData = tierList.getTierRows();

        // Get views
        commentRecyclerView = root.findViewById(R.id.comment_recyclerView);
        category = root.findViewById(R.id.category_textView);
        tierListRecyclerView = root.findViewById(R.id.tier_recyclerView);
        commentEditText = root.findViewById(R.id.comment_editText);
        commentSubmitBtn = root.findViewById(R.id.commentSubmit_button);
        infoTextView = root.findViewById(R.id.tierInfo_textView);

        // Get comments from server and display them in the recycler view
        GetComments();

        // Submit comment
        commentSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SubmitComment();
            }
        });

        // Clicking the category will return to list activity with the category selected
        category.setText("#" + tierList.getCategory());
        category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = new AlertDialog.Builder(getContext())
                        .setMessage("Are you sure you want to leave the discussion?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent();
                                intent.putExtra(Params.CATEGORY, tierList.getCategory());
                                getActivity().setResult(Activity.RESULT_OK, intent);
                                getActivity().finish();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).show();
            }
        });

        // Display information about the tier list
        // (Name and creator)
        String info = tierList.getTierName() + ", created by " + tierList.getUsername();
        infoTextView.setText(info);

        // Sort the tier rows and their items
        Collections.sort(tierRowData, new Comparator<TierRow>() {
            @Override
            public int compare(TierRow o1, TierRow o2) {
                return o1.getPlacement() - o2.getPlacement();
            }
        });
        for(TierRow row : tierRowData){
            if(row.getImages() != null){
                Collections.sort(row.getImages(), new Comparator<TierItem>() {
                    @Override
                    public int compare(TierItem o1, TierItem o2) {
                        return o1.getPlacement() - o2.getPlacement();
                    }
                });
            }
        }

        // Set up Tier list recycler view
        TierRowDisplayAdapter tierAdapter = new TierRowDisplayAdapter(tierRowData);
        tierListRecyclerView.setAdapter(tierAdapter);
        tierListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return root;
    }

    // Submit a comment
    private void SubmitComment () {
        String commentBody = commentEditText.getText().toString();

        // Do not submit the comment if nothing was entered
        if(TextUtils.isEmpty(commentBody.trim())){
            // Alert the user that nothing was entered
            AlertDialog dialog = new AlertDialog.Builder(getContext())
                    .setMessage("Please enter a comment before submitting")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    }).show();

            return;
        }

        // Make a new comment with the current session set to true
        // Setting the current session will make delete and edit buttons appear
        Comment newComment = new Comment(tierList.getUuid(), commentBody);
        newComment.setCurrSession(true);

        // Add new comment to adapter
        comments.add(newComment);
        adapter.notifyItemInserted(comments.size() - 1);

        // Clear the edit text after submitting the comment
        commentEditText.getText().clear();

        // Scroll the adapter to the bottom to show new comment
        commentRecyclerView.scrollToPosition(comments.size()-1);

        // Build the url
        String url = application.PROTOCOL+application.LOCALHOST+application.PORT+"/comment/"+newComment.getUuid();

        // Make the request to the server
        HttpRequest request = new HttpRequest(url, HttpRequest.Method.PUT);
        request.setRequestBody("application/json", newComment.toJson());
        HttpRequestTask httpRequestTask = new HttpRequestTask();
        httpRequestTask.setOnResponseListener(data -> {

            // Update the uuid of the comment with the cne from the server
            String[] newCommentUrl = data.getHeaders().get("Location").get(0).split("/");
            String uuid = newCommentUrl[newCommentUrl.length - 1];
            newComment.setUuid(uuid);
        });
        httpRequestTask.setOnErrorListener(error -> {
            // Simply show the error message
            Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
        });
        httpRequestTask.execute(request);
    }

    // Get the comments from the server
    private void GetComments(){
        // Set the url for the request
        url = application.PROTOCOL+ application.LOCALHOST+ application.PORT + "/comment";

        // Make and execute the request
        HttpRequest request = new HttpRequest(url, HttpRequest.Method.GET);
        HttpRequestTask httpRequestTask = new HttpRequestTask();
        httpRequestTask.setOnResponseListener(new OnResponseListener<HttpResponse>() {
            @Override
            public void onResponse(HttpResponse data) {
                if(data.getResponseCode()!=200)
                    return;

                // Get the comments from the server
                Comment[] arr = Comment.parseJsonArray(data.getResponseBody());
                DisplayComments(arr);
            }
        });
        httpRequestTask.execute(request);
    }

    // Takes array of comments and displays the comments for the tier list being viewed
    private void DisplayComments(Comment[] commentArr){
        // Filter the array of comments to only comments for the tier list being viewed
        Object[] filteredComments = Arrays.stream(commentArr).filter(x -> (x.getTieruuid()).equals(tierList.getUuid())).toArray();

        // Add the comments to the recycler view and display them
        comments = new ArrayList<>(Arrays.asList(filteredComments));
        adapter = new CommentAdapter(comments);
        commentRecyclerView.setAdapter(adapter);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Recycler view is scrolled to the bottom by default
        commentRecyclerView.scrollToPosition(comments.size()-1);
    }

    public static String[] getRows() {
        String[] output = new String[tierRowData.size()];

        for(int i = 0; i < tierRowData.size(); i++) {
            output[i] = tierRowData.get(i).getTitle();
        }

        return output;
    }

    public static String getTitleFromRowNum(String uuid) {
        for(TierRow row: tierRowData) {
            if(row.getUuid().equals(uuid)) {
                return row.getTitle();
            }
        }
        return "";
    }
}
