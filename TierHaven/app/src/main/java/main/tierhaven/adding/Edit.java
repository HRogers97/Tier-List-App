package main.tierhaven.adding;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;

import main.tierhaven.R;
import main.tierhaven.TierListApplication;
import main.tierhaven.model.Category;
import main.tierhaven.model.ListFragmentSampleData;
import main.tierhaven.model.SampleData;
import main.tierhaven.model.ServerObject;
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

public class Edit extends AppCompatActivity implements EditFragment.OnEditChosenListener {

    private List<String> categoryNames;

    //parameters for intent from tier item edit
    public static class params{
        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
    }

    private static TierListApplication application;

    //request code
    public static final int GETIMAGEREQUESTCODE = 1;
    public static final int CHANGETIERITEMREQUESTCODE = 2;

    private EditText tierListTitle;
    private Spinner tierListCategorySpinner;
    private TierItem currentTierItem;

    private boolean isUpdate;

    private String uuid;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            //if request is for image
            if (requestCode == GETIMAGEREQUESTCODE){
                try {
                    //get image
                    final Uri imageUri = data.getData();
                    final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                    //set tier item
                    TierItem tierItem = new TierItem();
                    tierItem.setImage(selectedImage);
                    tierItem.setPlacement(0);
                    tierItem.setDescription("");
                    tierItem.setName("");
                    tierItem.setUuid(UUID.randomUUID().toString());

                    // URL to the repo where the Tier Item is going to be saved on the server
                    String url = ALL_TIER_ITEMS_URL;

                    // Save the New Tier Item on the server via a POST request
                    HttpRequest request = new HttpRequest(url, HttpRequest.Method.POST);
                    request.setRequestBody("application/json", tierItem.toJson());
                    HttpRequestTask httpRequestTask = new HttpRequestTask();
                    httpRequestTask.setOnResponseListener(new OnResponseListener<HttpResponse>() {
                        @Override
                        public void onResponse(HttpResponse data) {

                        }
                    });
                    httpRequestTask.setOnErrorListener(new OnErrorListener() {
                        @Override
                        public void onError(Exception error) {
                            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                    httpRequestTask.execute(request);

                    //add tier item to fragment
                    ((EditFragment) getSupportFragmentManager().findFragmentById(R.id.fragment)).addToUnassignedImages(tierItem);
                    ((EditFragment) getSupportFragmentManager().findFragmentById(R.id.fragment)).makeList();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                        Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
                }
            }
            //if user wanted to change tier item
            else if (requestCode == CHANGETIERITEMREQUESTCODE){
                //change name and description of tier item
                currentTierItem.setName(data.getStringExtra(params.NAME));
                currentTierItem.setDescription(data.getStringExtra(params.DESCRIPTION));
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        application = (TierListApplication) getApplication();

        isUpdate = false;

        tierListTitle = findViewById(R.id.tierlist_title_editText);

        FloatingActionButton fab = findViewById(R.id.fab);

        //set fab listener
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //show alert dialog to see if user wants to add another tier item
                new AlertDialog.Builder(Edit.this).setTitle("Are you sure you want to add another tier item?")
                                                          .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                              @Override
                                                              public void onClick(DialogInterface dialogInterface, int i) {
                                                                  //make intent
                                                                  Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                                                                  photoPickerIntent.setType("image/*");
                                                                  startActivityForResult(photoPickerIntent, GETIMAGEREQUESTCODE);
                                                              }
                                                          })
                                                          .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                              @Override
                                                              public void onClick(DialogInterface dialogInterface, int i) {

                                                              }
                                                          })
                                                          .show();
            }
        });


        //get category spinner
        tierListCategorySpinner = findViewById(R.id.tierlist_category_spinner);
        //make category list of spinners
        List<Category> categories = new ListFragmentSampleData().getCategories();

        // Get all the category names
        categoryNames = new LinkedList<>();
        for (Category category:categories)
            categoryNames.add(category.getName());

        //make adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>( getApplicationContext(), R.layout.category_list_options,R.id.sort_TextView);
        //put data in adapter
        adapter.addAll(categoryNames);
        //put adapter in spinner
        tierListCategorySpinner.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //make menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.save_menuitem){
            //Alert dialog to see if user wants to save
            new AlertDialog.Builder(Edit.this).setTitle("Are you sure you want to save?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            TierList tierList = makeTierList();

                            application.serverObjects=new LinkedList<>();

                            //if it's an existing tier list
                            /*if (isUpdate){
                                //update
                                if(tierList.getTierRows()!=null){
                                    for(TierRow row: tierList.getTierRows()){
                                        row.setTierUuid(tierList.getUuid());
                                        if(row.getImages()!=null){
                                            for(TierItem item:row.getImages()){
                                                item.setRowUuid(row.getUuid());
                                                application.serverObjects.add(item);
                                            }
                                        }
                                        application.serverObjects.add(row);
                                    }
                                }
                                application.serverObjects.add(tierList);

                                application.putDataToServer(0);

                            }
                            //otherwise it is create
                            else{*/

                            // URL to the repo where the Tier List is going to be saved on the server
                            String url = ALL_TIER_LISTS_URL;
                            tierList.setUuid("");
                            // Save the New Tier List on the server via a POST request
                            HttpRequest request = new HttpRequest(url, HttpRequest.Method.POST);
                            request.setRequestBody("application/json", tierList.toJson());
                            HttpRequestTask httpRequestTask = new HttpRequestTask();
                            httpRequestTask.setOnResponseListener(new OnResponseListener<HttpResponse>() {
                                @Override
                                public void onResponse(HttpResponse data) {
                                    String location = data.getHeaders().toString();
                                    String[] values = location.split("/");
                                    String val = values[5];
                                    String[] values1 = val.split("]");
                                    String listUuid = values1[0];

                                    tierList.setUuid(listUuid);
                                    if(tierList.getTierRows()!=null){
                                        for(TierRow row: tierList.getTierRows()){
                                            row.setTierUuid(tierList.getUuid());
                                            if(row.getImages()!=null){
                                                for(TierItem item:row.getImages()){
                                                    item.setRowUuid(row.getUuid());
                                                    application.serverObjects.add(item);
                                                }
                                            }
                                            application.serverObjects.add(row);
                                        }
                                    }

                                    application.putDataToServer(0);
                                }
                            });
                            httpRequestTask.setOnErrorListener(new OnErrorListener() {
                                @Override
                                public void onError(Exception error) {
                                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                            httpRequestTask.execute(request);

                            //}
                            Edit.this.finish();
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            }).create().show();
            //finish();
        }
        else if (item.getItemId() == R.id.exit_menuitem){
            new AlertDialog.Builder(Edit.this).setTitle("Are you sure you want to exit without saving?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Edit.this.finish();
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            }).create().show();
        }

        return super.onOptionsItemSelected(item);
    }

    private TierList makeTierList(){
        EditFragment fragment = (EditFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);

        TierList tierList = new TierList().setTierName(tierListTitle.getText().toString())
                                          .setTierRows(fragment.getRows())
                                          .setCategory(tierListCategorySpinner.getSelectedItem().toString());
        tierList.setTierName(tierListTitle.getText().toString());
        tierList.setCategory( tierListCategorySpinner.getSelectedItem().toString() );
        tierList.setUsername("Hunter Rogers");
        tierList.setUuid(uuid);

        for (int i = 0; i < tierList.getTierRows().size(); i++){
            tierList.getTierRows().get(i).setTierUuid(tierList.getUuid());
        }

        return tierList;
    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        EditFragment fragment = (EditFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
        fragment.setOnEditChosenListener(this);

        //get tier tier if it exists
        String tier = getIntent().getStringExtra(application.GET_TIER_LIST_UUID);

        //if tier exists
        if (tier != null){
            //get tier item
            TierList tierList = application.getTierListByUuid(tier);
            //set to update mode
            isUpdate=true;

            //makes sure the data is in proper position in case of not good data
            for (int i = 0; i < tierList.getTierRows().size(); i++){
                if (tierList.getTierRows().get(i).getImages() == null){
                    tierList.getTierRows().get(i).setImages(new ArrayList<>());
                }
                else{
                    for (int c =0; c < tierList.getTierRows().get(i).getImages().size(); c++){
                        tierList.getTierRows().get(i).getImages().get(c).setPlacement(c);
                    }
                }
            }

            //set tier item for fragment
            fragment.setTier(tierList.getTierRows());
            //set title
            tierListTitle.setText(tierList.getTierName());
            //set category
            tierListCategorySpinner.setSelection(categoryNames.indexOf(tierList.getCategory()));
            //get uuid
            uuid = tierList.getUuid();
        }
        //otherwise create
        else{
            //make new uuid
            uuid = UUID.randomUUID().toString();
        }

        fragment.setUuid(uuid);
    }

    @Override
    public void onEditChosen(TierItem tierItem) {
        //save tier item
        currentTierItem = tierItem;

        //make intent
        Intent intent = new Intent(this, EditTieritem.class);
        //send name and description to intent
        intent.putExtra(EditTieritem.params.NAME, tierItem.getName());
        intent.putExtra(EditTieritem.params.DESCRIPTION, tierItem.getDescription());
        //start intent
        startActivityForResult(intent, CHANGETIERITEMREQUESTCODE);

    }
}
