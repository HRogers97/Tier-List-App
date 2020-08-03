package main.tierhaven;

import android.app.AlertDialog;
import android.app.Application;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

import main.tierhaven.model.ServerObject;
import main.tierhaven.model.TierItem;
import main.tierhaven.model.TierList;
import main.tierhaven.model.TierRow;
import main.tierhaven.networking.HttpRequest;
import main.tierhaven.networking.HttpRequestTask;
import main.tierhaven.networking.HttpResponse;
import main.tierhaven.networking.OnErrorListener;
import main.tierhaven.networking.OnResponseListener;

/**
 *  Application class that handles the server interaction for most of the Tier related GET's and PUT's
 */
public class TierListApplication extends Application {

    // Constant for the GetTierListByUuid method for the intent
    public static final String GET_TIER_LIST_UUID="tierListUUID";

    // URL constants to build a url
    public static final String LOCALHOST = "10.0.2.2";
    public static final String PORT = ":9999";
    public static final String PROTOCOL = "http://";
    public static final String TIER_ITEMS_REPO = "/tieritem";
    public static final String TIER_ROWS_REPO = "/tierrow";
    public static final String TIER_LISTS_REPO = "/tierlist";

    // URL for all the Tier related repos
    public static final String ALL_TIER_ITEMS_URL = PROTOCOL+LOCALHOST+PORT+TIER_ITEMS_REPO;
    public static final String ALL_TIER_ROWS_URL = PROTOCOL+LOCALHOST+PORT+TIER_ROWS_REPO;
    public static final String ALL_TIER_LISTS_URL = PROTOCOL+LOCALHOST+PORT+TIER_LISTS_REPO;

    // Lists of Tier related objects to be retrieved from the server
    public List<TierItem> tierItems = new LinkedList<>();
    public List<TierRow> tierRows = new LinkedList<>();
    public List<TierList> tierLists = new LinkedList<>();

    // Saves the response data
    public HttpResponse dataReceived;

    // List of objects to be saved on the server
    public List<ServerObject> serverObjects= new LinkedList();


    /**
     *  GETS objects from the server
     * @param url
     * @param activity
     * @param errorMessage
     * @param responseHandler
     * @param errorHandler
     */
    public void getDataFromServer(String url, FragmentActivity activity, String errorMessage,Callable<Void> responseHandler,@Nullable Callable<Void> errorHandler){
        // Set the GET request
        HttpRequest request = new HttpRequest(url, HttpRequest.Method.GET);
        HttpRequestTask httpRequestTask = new HttpRequestTask();
        httpRequestTask.setOnResponseListener(data -> {
            if(data.getResponseCode()!=200)
                return;
            // Save the received data
            dataReceived = data;

            // Handle the response
            try {
                responseHandler.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        httpRequestTask.setOnErrorListener(error -> {
            AlertDialog dialog = new AlertDialog.Builder(activity)
                    .setMessage(errorMessage)
                    .setNegativeButton("Cancel", (dialog1, which) -> {
                        //Handle the cancel event
                        try {
                            errorHandler.call();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    })
                    .setPositiveButton("Try Again", (dialog12, which) -> {
                        // Re-run the method
                        getDataFromServer(url,activity,errorMessage,responseHandler,errorHandler);
                    })
                    .create();
            dialog.show();
        });
        httpRequestTask.execute(request);
    }

    /**
     * Takes a list of Server Objects and PUT's all of them on the server recursively
     * @param count
     */
    public void putDataToServer(final int count){
        // Build the url
        String url = PROTOCOL+LOCALHOST+PORT+serverObjects.get(count).getObjectModel()+serverObjects.get(count).getUuid();

        // Set the request
        HttpRequest request = new HttpRequest(url, HttpRequest.Method.PUT);
        request.setRequestBody("application/json", serverObjects.get(count).toJson());
        HttpRequestTask httpRequestTask = new HttpRequestTask();
        httpRequestTask.setOnResponseListener(data -> {

            // Save the next Server Object on the list
            // Notify the user that the list is saved at the end
            int newCount=count;
            newCount++;
            if(newCount!=serverObjects.size()){
                putDataToServer(newCount);
            }
            else
                Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_LONG).show();
        });
        httpRequestTask.setOnErrorListener(error -> {
            // Simply show the error message
            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
        });
        httpRequestTask.execute(request);
    }

    /**
     *  Returns the Tier List to which the provided Uuid belongs
     *  Otherwise returns null
     * @param uuid
     * @return TierList
     */
    public TierList getTierListByUuid(String uuid){
        for(TierList tl: this.tierLists){
            if(tl.getUuid().equals(uuid))
                return tl;
        }
        return null;
    }



    /**
     *  Empty onCreate()
     */
    @Override
    public void onCreate() {
        super.onCreate();
    }
}
