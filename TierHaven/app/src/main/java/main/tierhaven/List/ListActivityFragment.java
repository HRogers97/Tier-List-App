package main.tierhaven.List;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.ListFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import main.tierhaven.R;
import main.tierhaven.TierListApplication;
import main.tierhaven.adapter.TierListAdapter;
import main.tierhaven.adding.Edit;
import main.tierhaven.model.Category;
import main.tierhaven.model.ListFragmentSampleData;
import main.tierhaven.model.Review;
import main.tierhaven.model.TierItem;
import main.tierhaven.model.TierList;
import main.tierhaven.model.TierRow;
import main.tierhaven.networking.HttpRequest;
import main.tierhaven.networking.HttpRequestTask;
import main.tierhaven.networking.HttpResponse;
import main.tierhaven.networking.OnErrorListener;
import main.tierhaven.networking.OnResponseListener;

import static main.tierhaven.TierListApplication.ALL_TIER_ITEMS_URL;
import static main.tierhaven.TierListApplication.ALL_TIER_LISTS_URL;
import static main.tierhaven.TierListApplication.ALL_TIER_ROWS_URL;
import static main.tierhaven.TierListApplication.TIER_LISTS_REPO;

/**
 * @author Mathieu Trudeau
 *
 * Display's a list of Tier Lists
 */
public class ListActivityFragment extends Fragment {

    // UI objects
    private EditText searchTerm_EditText;
    private Spinner categorySearch_Spinner;
    private RecyclerView tierlists_RecyclerView;
    private Button createButton;
    private SwipeRefreshLayout refreshSwipe;

    // Search criteria's
    private String searchTerm="";
    private String category="";

    // Lists for the Tier Lists
    private List<TierList> tierLists = new LinkedList<>();
    private List<TierRow> tierRows = new LinkedList<>();
    private static List<TierItem> tierItems = new LinkedList<>();

    // Reviews
    private static Review[] reviews;

    // Application class
    private static TierListApplication application;

    private View root;
    private List<Category> categories;

    /**
     * Empty Constructor
     */
    public ListActivityFragment() {
    }

    /**
     *  Inflates the view and sets all the listeners and adapters for the fragment
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return View root
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Save the View as root so it can be reused
        root = inflater.inflate(R.layout.fragment_list, container, false);

        // Gets the application class
        application = (TierListApplication) getActivity().getApplication();


        // Retrieve the search term input text from view
        searchTerm_EditText = root.findViewById(R.id.SearchTier_EditText);

        // Save the search term that has been provided by the user
        searchTerm_EditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchTerm = searchTerm_EditText.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Get the category search spinner from the view
        categorySearch_Spinner = root.findViewById(R.id.Categories_Spinner);

        // TODO: Make the Categories come from the database and make it so that we can add some to the database
        // TODO: Not gonna do that...
        // Get all the categories from the sample data
        categories = new ListFragmentSampleData().getCategories();

        // Get all the category names from the list of categories
        List<String> categoryNames = new LinkedList<>();
        for (Category category: categories)
            categoryNames.add(category.getName());

        // Show all the category names on the spinner so that the user can select one
        ArrayAdapter<String> adapter = new ArrayAdapter<>(root.getContext(), R.layout.category_list_options,R.id.sort_TextView);
        adapter.addAll(categoryNames);
        categorySearch_Spinner.setAdapter(adapter);

        // Save the category name that the user selected from the spinner
        categorySearch_Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = categorySearch_Spinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Get the Create button from the View
        createButton = root.findViewById(R.id.create_Button);

        // When the user clicks on the Create button, go to the Edit class so that they can Create a new Tier List
        createButton.setOnClickListener(v -> {
            Intent intent = new Intent(root.getContext(), Edit.class);
            startActivity(intent);
        });

        // Get the refresh swipe from the View
        refreshSwipe = root.findViewById(R.id.updateTierLists_SwipeRefresh);

        // When the user swipes downwards on the view, pull the Tier Lists from the server and display them
        refreshSwipe.setOnRefreshListener(this::updateLists);

        // Start the refresh when the fragment is created
        refreshSwipe.setRefreshing(true);
        refreshSwipe.post(this::updateLists);

        // Return the View
        return root;
    }

    /**
     *  Retrieves the Tier Lists from the Server
     */
    public void updateLists(){
        application.tierItems=new LinkedList<>();
        application.tierRows=new LinkedList<>();
        application.tierLists=new LinkedList<>();
        getTierItems();
        GetReviews();
    }

    /**
     *  Displays the retrieved Tier Lists
     */
    public void showTierLists(){

        tierLists = application.tierLists;
        tierRows = application.tierRows;
        tierItems = application.tierItems;

        // Disabled the refreshing
        refreshSwipe.setRefreshing(false);

        // Display the Tier Lists
        tierlists_RecyclerView=root.findViewById(R.id.TierLists_RecyclerView);
        TierListAdapter adapter1 = new TierListAdapter(tierLists);
        tierlists_RecyclerView.setAdapter(adapter1);
        tierlists_RecyclerView.setLayoutManager(new GridLayoutManager(getContext(),1));
    }

    /**
     * Retrieve the Tier Lists from the server using the application class
     */
    private void getTierLists(){
        application.getDataFromServer(ALL_TIER_LISTS_URL, getActivity(), "A error occurred while retrieving the Tier Lists",
                () -> {
                    TierList[] lists = TierList.parseJsonArray(application.dataReceived.getResponseBody());

                    for(TierList list : lists)
                        if(!application.tierLists.contains(list))
                            application.tierLists.add(list);

                        // Construct the Tier Lists by adding their children Tier Rows
                    for(TierItem ti:application.tierItems) {
                        for (TierRow tr : application.tierRows) {
                            if(tr.getUuid().equals(ti.getRowUuid())){
                                tr.addTierItem(ti);
                            }
                        }
                    }

                    // Construct the Tier Rows by adding their children Tier Items
                    for (TierRow tr:application.tierRows){
                        for (TierList tl: application.tierLists){
                            if(tl.getUuid().equals(tr.getTierUuid())){
                                tl.addTierRow(tr);
                            }
                        }
                    }

                    // Display the Tier Lists
                    showTierLists();

                    return null;
                }, () -> {
                    refreshSwipe.setRefreshing(false);
                    return null;
                });
    }

    /**
     * Retrieve the Tier Rows from the Server using the application class
     */
    private void getTierRows(){
        application.getDataFromServer(ALL_TIER_ROWS_URL, getActivity(), "A error occurred while retrieving the Tier Rows",
                () -> {
                    TierRow[] rows = TierRow.parseJsonArray(application.dataReceived.getResponseBody());

                    for(TierRow row : rows)
                        if(!application.tierRows.contains(row)) {
                            application.tierRows.add(row);
                        }
                    getTierLists();
                    return null;
                }, () -> {
                    refreshSwipe.setRefreshing(false);
                    return null;
                });
    }

    /**
     * Retrieve the Tier Items from the server using the application class
     */
    private void getTierItems(){
        application.getDataFromServer(ALL_TIER_ITEMS_URL, getActivity(), "A error occurred while retrieving the Tier Items",
                () -> {
                    TierItem[] items = TierItem.parseJsonArray(application.dataReceived.getResponseBody());

                    for(TierItem item : items)
                        if(!application.tierItems.contains(item))
                            application.tierItems.add(item);
                    getTierRows();
                    return null;
                }, () -> {
                    refreshSwipe.setRefreshing(false);
                    return null;
                });
    }

    /**
     *  Creates a new list of Tier Lists that meets the search criteria's and displays them
     */
    public void performSearch() {
        // New list with the filtered Tier Lists
        List<TierList> newTierLists = new ArrayList<>();

        // Loop through all the Tier Lists
        for (TierList tierList : tierLists) {

            // A category has been chosen
            if (!getCategory().equals("All")) {
                // A search term has been chosen
                if (!getSearchTerm().equals("")) {
                    // Save the Tier List if both the category and the term match
                    if (tierList.getCategory().equals(getCategory()) && tierList.getTierName().equals(getSearchTerm())) {
                        newTierLists.add(tierList);
                    }
                }
                else if (tierList.getCategory().equals(getCategory())) {
                    // Add only if the category match
                    newTierLists.add(tierList);
                }
            }
            else if (!getSearchTerm().equals("")) {
                // Only match the search term
                if (tierList.getTierName().equals(getSearchTerm())) {
                    newTierLists.add(tierList);
                }
            }
            else {
                // No Filter, so add everything
                newTierLists.add(tierList);
            }
        }

        // Display the filtered Tier Lists
        tierlists_RecyclerView = root.findViewById(R.id.TierLists_RecyclerView);
        TierListAdapter adapter1 = new TierListAdapter(newTierLists);
        tierlists_RecyclerView.setAdapter(adapter1);
        tierlists_RecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
    }

    /**
     *
     */
    private void GetReviews() {
        String url = application.PROTOCOL+application.LOCALHOST+application.PORT+"/review";

        HttpRequest request = new HttpRequest(url, HttpRequest.Method.GET);
        HttpRequestTask httpRequestTask = new HttpRequestTask();
        httpRequestTask.setOnResponseListener(new OnResponseListener<HttpResponse>() {
            @Override
            public void onResponse(HttpResponse data) {
                if(data.getResponseCode()!=200)
                    return;
                reviews = Review.parseJsonArray(data.getResponseBody());
            }
        });
        httpRequestTask.setOnErrorListener(new OnErrorListener() {
            @Override
            public void onError(Exception error) {
                AlertDialog dialog = new AlertDialog.Builder(getActivity())
                        .setMessage("A error occurred while the Reviews")
                        .setNegativeButton("Cancel", (dialog1, which) -> {
                            //refreshSwipe.setRefreshing(false);
                        })
                        .setPositiveButton("Try Again", (dialog12, which) -> {
                            //retrieveNotes();
                        })
                        .create();

                dialog.show();
            }
        });
        httpRequestTask.execute(request);
    }

    /**
     * Gets the search term that is currently typed in the search bar
     * @return String searchTerm
     */
    public String getSearchTerm(){
        return searchTerm;
    }

    /**
     * Gets the category that is currently chosen in the spinner
     * @return String category
     */
    public String getCategory(){
        return category;
    }

    /**
     *
     * @param tier
     * @return
     */
    public static List<Review> getReviewsForTier(String tier) {
        List<Review> reviewsInTier = new LinkedList<>();
        for(Review rev: reviews) {
            if(rev.getTierUuid().equals(tier)) {
                reviewsInTier.add(rev);
            }
        }
        return reviewsInTier;
    }

    /**
     *
     * @param item
     * @return
     */
    public static Bitmap getReviewImg(String item) {
        for(TierItem ti: tierItems) {
            if(ti.getUuid().equals(item)) {
                return ti.getImage();
            }
        }
        return null;
    }

    // Set the category that was selected in discussion fragment
    public void ChangeCategory(String category){
        this.category = category;

        // Get index of category and update spinner
        for(Category cat : categories){
            if(cat.getName().equals(category)){
                int pos = categories.indexOf(cat);
                categorySearch_Spinner.setSelection(pos);
            }
        }

        // Reset search term
        searchTerm = "";
        searchTerm_EditText.setText("");
    }
}