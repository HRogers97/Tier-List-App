package main.tierhaven.adding;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import main.tierhaven.R;
import main.tierhaven.TierListApplication;
import main.tierhaven.adapter.edit.TierItemAdapter;
import main.tierhaven.adapter.edit.TierItemViewHolder;
import main.tierhaven.adapter.edit.TierRowAdapter;
import main.tierhaven.adapter.edit.TierRowViewHolder;
import main.tierhaven.adding.DialogFragments.ColorPickerDialogFragment;
import main.tierhaven.adding.DialogFragments.NewRowDialogFragment;
import main.tierhaven.model.SampleData;
import main.tierhaven.model.TierItem;
import main.tierhaven.model.TierList;
import main.tierhaven.model.TierRow;
import main.tierhaven.networking.HttpRequest;
import main.tierhaven.networking.HttpRequestTask;
import main.tierhaven.networking.HttpResponse;
import main.tierhaven.networking.OnErrorListener;
import main.tierhaven.networking.OnResponseListener;

import static main.tierhaven.TierListApplication.ALL_TIER_ITEMS_URL;
import static main.tierhaven.TierListApplication.ALL_TIER_ROWS_URL;

/**
 * A placeholder fragment containing a simple view.
 */
public class EditFragment extends Fragment {

    //make interface in case user wants to edit
    public interface OnEditChosenListener{
        void onEditChosen(TierItem tierItem);
    }

    private  List<TierRow> tierRowList;
    private  List<TierItem> unassignedTierItems;
    private  RecyclerView tierImages;
    private  RecyclerView tierRows;
    static boolean isEdit = true;
    private ImageButton add;

    private static TierListApplication application;

    private OnEditChosenListener onEditChosenListener;

    private  View root;
    private TierRowAdapter adapter;

    private String uuid;

    public EditFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_edit, container, false);

        application = (TierListApplication) getActivity().getApplication();

        //adds new tier row to the bottom
        add = root.findViewById(R.id.tierrowadd_imageButton);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTierRow();

                makeList();
            }
        });

        //make lists empty
        tierRowList = new ArrayList<>();
        unassignedTierItems = new ArrayList<>();

        //get recycler views
        tierImages = root.findViewById(R.id.tierimage_recyclerview);
        tierRows = root.findViewById(R.id.tierrow_recyclerview);
        tierRows.setClipChildren(false);
        tierRows.setClipToPadding(false);

        //makeList();
        return root;
    }


    //this makes an adapter for the row recycler view
    private  RecyclerView.Adapter<TierRowViewHolder> makeAdapter(){

        orderTierRowList();

        //make adapter
        adapter = new TierRowAdapter(tierRowList, isEdit);

        //make direction chosen listener
        adapter.setOnDirectionChosenListener(new TierRowViewHolder.OnDirectionChosenListener() {
            @Override
            public void onDirectionChosen(TierRowViewHolder.Direction direction, TierRow tierRow) {

                //switches placement of tier row depending on the direction chosen
                if (direction == TierRowViewHolder.Direction.DOWN){
                    changePlacement(tierRow.getPlacement(), false);
                }
                else if (direction == TierRowViewHolder.Direction.UP){
                    changePlacement(tierRow.getPlacement(), true);
                }

                //resets rows
                makeList();
            }
        });

        //make listener in case user wants to remove item
        adapter.setOnItemRemovedListener(new TierRowViewHolder.OnItemRemovedListener() {

            @Override
            public void onItemRemoved(int row, int placement) {

                //make action bar to see if user wants to remove or edit item
                getActivity().startActionMode(new ActionMode.Callback() {
                    @Override
                    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                        actionMode.getMenuInflater().inflate(R.menu.menu_edit_tieritem_options, menu);
                        return true;
                    }

                    @Override
                    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                        return false;
                    }

                    @Override
                    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {

                        //if user pressed remove
                        if (menuItem.getItemId() == R.id.remove_menuitem){
                            //make alert dialog to confirm
                            new AlertDialog.Builder(root.getContext()).setTitle("Are you sure you want to remove this item from this row?")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            addToUnassignedImagesFromRows(row, placement);
                                        }
                                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            }).show();
                        }
                        //if user pressed edit
                        else if (menuItem.getItemId() == R.id.edit_menuitem){
                            if (onEditChosenListener != null)
                                onEditChosenListener.onEditChosen(tierRowList.get(row).getImages().get(placement));
                        }

                        actionMode.finish();
                        return false;
                    }

                    @Override
                    public void onDestroyActionMode(ActionMode actionMode) {

                    }
                });


            }
        });

        //make listener in case
        adapter.setChangeDesiredListener(new NewRowDialogFragment.ChangeDesiredListener() {
            @Override
            public void changeDesired(TierItem tierItem, int rowId, int original) {
                //change placement of item in
                change(tierItem, rowId, original);
            }
        });
        //make listener for when user wants to change row properties
        adapter.setOnColorClickedListener(new TierRowViewHolder.OnColorClickedListener() {
            @Override
            public void onColorClicked(int row) {

                //make action bar
                getActivity().startActionMode(new ActionMode.Callback() {
                    @Override
                    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                        //make menu
                        actionMode.getMenuInflater().inflate(R.menu.menu_rowoptions, menu);
                        return true;
                    }

                    @Override
                    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                        return false;
                    }

                    @Override
                    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                        //if user wants color change
                        if (menuItem.getItemId() == R.id.color_menuitem){
                            changeColor(row);
                        }
                        //if user wants to remove row
                        else if (menuItem.getItemId() == R.id.removerow_menuitem){
                            removeRow(row);
                        }

                        actionMode.finish();
                        return false;
                    }

                    @Override
                    public void onDestroyActionMode(ActionMode actionMode) {

                    }
                });
            }
        });
        return adapter;
    }

    //resets list shown in fragment
    public void makeList(){

        reorderUnassignedList();
        //set adapter
        tierRows.setAdapter(makeAdapter());
        //set view layout
        tierRows.setLayoutManager(new GridLayoutManager(root.getContext(), 1));
    }


    //increases or decreases the placement of a row
    private  void changePlacement(int currentPosition, boolean pos){
        //if not at first position
        int factor = pos? 1 : -1;
        if ( (currentPosition != 0 && pos) || (currentPosition != tierRowList.size() -1 && !pos) ){
            //change placement
            tierRowList.get(currentPosition).setPlacement(tierRowList.get(currentPosition).getPlacement() - factor);
            tierRowList.get(currentPosition - factor).setPlacement(tierRowList.get(currentPosition).getPlacement() + factor);

        }
    }

    //gets row and placement of image, adds it to unassigned images and removes image from row
    private void addToUnassignedImagesFromRows(int row, int placement){

        //get tier item
        TierRow tierRow = tierRowList.get(row);
        TierItem tierItem = tierRow.getImages().get(placement);

        //remove from database
        tierItem.setRowUuid(null);
        tierItem.setPlacement(-1);

        //make url
        String url = ALL_TIER_ITEMS_URL+"/"+tierItem.getUuid();

        HttpRequest request = new HttpRequest(url, HttpRequest.Method.PUT);

        //send request
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
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        httpRequestTask.execute(request);

        addToUnassignedImages(tierItem);
        //remove item from row
        tierRowList.get(row).getImages().remove(placement);

        makeList();
    }

    public void addToUnassignedImages(TierItem tierItem){
        unassignedTierItems.add(tierItem);
        //orders list
        makeUnassignedList();
    }

    //orders unassigned list and add handlers
    private void makeUnassignedList(){
        //order list
        reorderUnassignedList();

        //make adapter
        TierItemAdapter tierItemAdapter = new TierItemAdapter(unassignedTierItems);
        tierItemAdapter.setMessage("Are you sure you want to remove this from the tier list?");

        //add handler in case user wants to add item to tier row
        tierItemAdapter.setRows(tierRowList);
        tierItemAdapter.setChangeDesiredListener(new NewRowDialogFragment.ChangeDesiredListener() {
            @Override
            public void changeDesired(TierItem tierItem, int rowId, int original) {

                //add item to row
                tierItem.setPlacement(tierRowList.get(rowId).getImages().size());
                tierRowList.get(rowId).getImages().add(tierItem);
                tierItem.setRowUuid(tierRowList.get(rowId).getUuid());

                //remove item form list
                removeItemFromUnassignedList(tierItem);

                //make new adapter
                TierItemAdapter tierItemAdapter = new TierItemAdapter(unassignedTierItems);
                tierImages.setAdapter(tierItemAdapter);
                tierImages.setLayoutManager(new GridLayoutManager(root.getContext(), 5));

                //reset both fragments
                makeList();
                makeUnassignedList();

            }
        });

        //add handler for long click
        tierItemAdapter.setOnItemLongClickListener(new TierItemViewHolder.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(int placement) {

                //make action bar
                getActivity().startActionMode(new ActionMode.Callback2() {
                    @Override
                    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                        actionMode.getMenuInflater().inflate(R.menu.menu_edit_tieritem_options, menu);
                        return true;
                    }

                    @Override
                    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                        return false;
                    }

                    @Override
                    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {

                        //if user wants to remove item
                        if (menuItem.getItemId() == R.id.remove_menuitem){
                            //make alert to confirm remove
                            new AlertDialog.Builder(root.getContext()).setTitle("Are you sure you want to remove this item")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                            //gets item to be deleted
                                            TierItem itemToBeDeleted = unassignedTierItems.get(placement);

                                            //  URL of the Tier Item to be deleted
                                            String url = ALL_TIER_ITEMS_URL+"/"+itemToBeDeleted.getUuid();

                                            // Delete the tier Item from the server
                                            HttpRequest request = new HttpRequest(url, HttpRequest.Method.DELETE);
                                            HttpRequestTask httpRequestTask = new HttpRequestTask();
                                            httpRequestTask.setOnResponseListener(new OnResponseListener<HttpResponse>() {
                                                @Override
                                                public void onResponse(HttpResponse data) {

                                                }
                                            });
                                            httpRequestTask.setOnErrorListener(new OnErrorListener() {
                                                @Override
                                                public void onError(Exception error) {
                                                    Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                                                }
                                            });
                                            httpRequestTask.execute(request);

                                            //remove tier item from list
                                            unassignedTierItems.remove(placement);
                                            //reset fragment
                                            makeUnassignedList();
                                        }
                                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            }).show();
                        }
                        //if user wants to edit item
                        else if (menuItem.getItemId() == R.id.edit_menuitem){
                            //if item listener not null
                            if (onEditChosenListener != null) {
                                //edit item
                                onEditChosenListener.onEditChosen(unassignedTierItems.get(placement));
                            }
                        }

                        actionMode.finish();
                        return true;
                    }

                    @Override
                    public void onDestroyActionMode(ActionMode actionMode) {

                    }
                });


            }
        });

        tierImages.setAdapter(tierItemAdapter);
        tierImages.setLayoutManager(new GridLayoutManager(root.getContext(), 5));
    }

    //remove item from list
    private void removeItemFromUnassignedList(TierItem tierItem){
        //search for item
        for (int i = 0; i < unassignedTierItems.size(); i++){
            //if item is tier item
            if (unassignedTierItems.get(i).equals(tierItem)){
                //remove item
                unassignedTierItems.remove(i);
                //force loop exit
                break;
            }
        }
    }

    private void removeRow(int row){

        // Indexed 0
        row--;

        TierRow tierRow = tierRowList.get(row);

        //get all images from tier rows and put then to an unassigned list
        for (int i = 0; i < tierRow.getImages().size(); i++ ){
            unassignedTierItems.add(tierRow.getImages().get(i));
        }

        for (int i = 0; i < tierRowList.size(); i++){
            tierRowList.get(i).setPlacement(i);
        }

        tierRowList.remove(row);

        //  URL of the Tier row to be deleted
        String url = ALL_TIER_ROWS_URL+"/"+tierRow.getUuid();

        // Delete the tier row from the server
        HttpRequest request = new HttpRequest(url, HttpRequest.Method.DELETE);
        HttpRequestTask httpRequestTask = new HttpRequestTask();
        httpRequestTask.setOnResponseListener(new OnResponseListener<HttpResponse>() {
            @Override
            public void onResponse(HttpResponse data) {

            }
        });
        httpRequestTask.setOnErrorListener(new OnErrorListener() {
            @Override
            public void onError(Exception error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        httpRequestTask.execute(request);


        //reset fragment
        makeUnassignedList();
        makeList();
    }

    private void changeColor(int row){
        //make and show color picker dialog
        ColorPickerDialogFragment colorPickerDialogFragment = new ColorPickerDialogFragment(tierRowList.get(row).getColor());
        colorPickerDialogFragment.setOnColorChosenListener(new ColorPickerDialogFragment.OnColorChosenListener() {
            @Override
            public void onColorChosen(int resFile) {
                //set new color
                tierRowList.get(row).setColor(resFile);
                //reset fragment
                makeList();
            }
        });

        colorPickerDialogFragment.show(getFragmentManager(), "colorpicker");
    }

    //orders tier list based on placement
    public  void orderTierRowList(){
        tierRowList.sort(new Comparator<TierRow>() {
            @Override
            public int compare(TierRow n1, TierRow n2) {
                return ((Integer) n1.getPlacement()).compareTo(n2.getPlacement());
            }
        });
    }

    //order unassigned list placement
    public void reorderUnassignedList(){
        for (int i = 0; i < unassignedTierItems.size();i++){
            unassignedTierItems.get(i).setPlacement(i);
        }
    }

    //adds new tier row
    private void addTierRow(){
        //make new tier row
        TierRow tierRow = new TierRow();
        tierRow.setColor(R.color.NewTier);
        tierRow.setImages(new ArrayList<>());
        tierRow.setTitle("N");
        tierRow.setUuid(UUID.randomUUID().toString());

        if(tierRowList.isEmpty()){
            tierRow.setPlacement(0);
        }
        else {
            tierRow.setPlacement(tierRowList.size());
        }

        // URL to the repo where the Tier Row is going to be saved on the server
        String url = ALL_TIER_ROWS_URL;

        // Save the New Tier Row on the server via a POST request
        HttpRequest request = new HttpRequest(url, HttpRequest.Method.POST);
        request.setRequestBody("application/json", tierRow.toJson());
        HttpRequestTask httpRequestTask = new HttpRequestTask();
        httpRequestTask.setOnResponseListener(new OnResponseListener<HttpResponse>() {
            @Override
            public void onResponse(HttpResponse data) {
                String location = data.getHeaders().toString();
                String[] values = location.split("/");
                String val = values[5];
                String[] values1 = val.split("]");
                String rowUuid = values1[0];

                tierRow.setUuid(rowUuid);
            }
        });
        httpRequestTask.setOnErrorListener(new OnErrorListener() {
            @Override
            public void onError(Exception error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        httpRequestTask.execute(request);

        tierRowList.add(tierRow);
    }

    //sets to check if we edit
    public void setEdit(boolean isEdit){
        this.isEdit = isEdit;
        if (!isEdit){
            add.setVisibility(View.GONE);
        }
        else{
            add.setVisibility(View.VISIBLE);
        }

        makeList();
    }

    //sets tier row
    public void setTier(List<TierRow> tierRows){
        tierRowList.clear();
        //add tier rows
        tierRowList = tierRows;
        //reset fragment
        makeList();
    }

    public  List<TierRow> getRows() {
        return tierRowList;
    }

    //changes where tier item is located
    public void change(TierItem item, int rowId, int original) {

        //id not in original spot
        if(rowId != original) {
            //remove from current row
            tierRowList.get(original).getImages().remove(item.getPlacement());

            //reset placements
            for(int i = 0; i<tierRowList.get(original).getImages().size(); i++) {
                tierRowList.get(original).getImages().get(i).setPlacement(i);
            }

            //add tier item
            tierRowList.get(rowId).getImages().add(item);

            //reset placements
            for(int i = 0; i<tierRowList.get(rowId).getImages().size(); i++) {
                tierRowList.get(rowId).getImages().get(i).setPlacement(i);
            }

            item.setRowUuid(tierRowList.get(rowId).getUuid());
            makeList();
        }
    }

    //set edit listener
    public void setOnEditChosenListener(OnEditChosenListener onEditChosenListener) {
        this.onEditChosenListener = onEditChosenListener;
    }


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

}
