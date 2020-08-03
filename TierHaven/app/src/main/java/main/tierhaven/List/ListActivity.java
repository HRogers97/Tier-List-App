package main.tierhaven.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import android.widget.Spinner;

import java.lang.reflect.Method;

import main.tierhaven.R;
import main.tierhaven.model.TierList;
import main.tierhaven.viewing.DiscussionActivity;
import main.tierhaven.viewing.DiscussionActivityFragment;

public class ListActivity extends AppCompatActivity {

    // Fragment that retrieves the Tier Lists
    private ListActivityFragment fragment = (ListActivityFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);

    // Search Criteria's
    private String searchTerm;
    private String category;

    // Tier List to be shown in the Discussion Activity
    private static TierList currentList;

    /**
     *  Handles the search Event when the user clicks on the search floating button
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enables the images to be saved
        if(Build.VERSION.SDK_INT>=24){
            try{
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        // Set the toolbar
        setContentView(R.layout.activity_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fragment = (ListActivityFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);

        // Get the search button
        FloatingActionButton searchButton = findViewById(R.id.Search_FloatingActionButton);

        // Event handler for when the search button is clicked
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            // Retrieve the search criteria's from the fragment
            searchTerm = fragment.getSearchTerm();
            category = fragment.getCategory();

            if(searchTerm.equals(""))
                searchTerm="All";

            // Filter the Tier Lists so that only those who meet the search criteria's are displayed
            fragment.performSearch();

            // Show the user what they searched for
            Snackbar.make(view, "You are searching for "+searchTerm+" in "+category, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    /**
     *  Starts the Discussion Activity and sends it the Tier List that the user chose
     * @param tierList
     */
    public void viewTierList(TierList tierList){
        // Send the selected tier list to discussion activity
        currentList = tierList;
        // Creates an intent and starts the activity
        Intent intent = new Intent(getApplicationContext(), DiscussionActivity.class);
        intent.putExtra(DiscussionActivityFragment.Params.TIER_LIST, currentList.getUuid());

        startActivityForResult(intent, DiscussionActivityFragment.Params.GET_CATEGORY);
    }

    /**
     *  Enables other Fragments to retrieve the Tier List that the user selected
     * @return TierList
     */
    public static TierList getCurrentList() {
        return currentList;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if(requestCode == DiscussionActivityFragment.Params.GET_CATEGORY){
                // Get category that was selected in discussion fragment and update category in List Activity
                String cat = data.getStringExtra(DiscussionActivityFragment.Params.CATEGORY);
                fragment.ChangeCategory(cat);
                fragment.performSearch();
            }
        }
    }
}
