package main.tierhaven.model;

import android.content.res.Resources;
import android.graphics.BitmapFactory;

import java.util.ArrayList;
import java.util.List;

import main.tierhaven.R;


public class SampleData {
    private static List<Comment> commentData;
    private static List<TierItem> tierItemData;
    private static List<TierRow> tierRowData;

    public static List<Comment> getComments(){
        commentData = new ArrayList<>();

        /*
        commentData.add(new Comment(0, "1", "0", "Wow, great list OP, I agree completely"));
        commentData.add(new Comment(1, "2", "0", "LOL, yeah okay buddy."));
        commentData.add(new Comment(2, "5", "0", "What?! Bubbles is absolutely the best powerpuff girl."));
        commentData.add(new Comment(3, "4", "0", "What is this list? This guy is trolling so hard"));
        commentData.add(new Comment(4, "9", "0", "This list is spot on, other comments are wrong"));
        commentData.add(new Comment(5, "4", "0", "No"));
        commentData.add(new Comment(6, "7", "0", "OP here, you guys need to calm down and accept that Buttercup is the best"));
        commentData.add(new Comment(7, "1", "0", "Exactly, these comments are all crazy, OP is 100% on the dot"));
        */

        return commentData;
    }

    public static void CreateSampleTiers(Resources res){
        // Sample Tier Items
        tierItemData = new ArrayList<>();

        List<TierItem> STiers = new ArrayList<>();

        STiers.add(new TierItem("Buttercup", "Green powerpuff girl", BitmapFactory.decodeResource(res, R.drawable.buttercup), 0));
        STiers.add(new TierItem("Professor", "Creater of the powerpuff girls", BitmapFactory.decodeResource(res, R.drawable.professor), 1));

        List<TierItem> ATiers = new ArrayList<>();
        ATiers.add(new TierItem("Mayor", "Mayor of Townsville", BitmapFactory.decodeResource(res, R.drawable.mayor), 0));
        ATiers.add(new TierItem("Blossom", "Red powerpuff girl", BitmapFactory.decodeResource(res, R.drawable.blosom), 1));

        List<TierItem> BTIers = new ArrayList<>();
        BTIers.add(new TierItem("Mojo Jojo", "The monkey", BitmapFactory.decodeResource(res, R.drawable.mojojojo), 0));
        BTIers.add(new TierItem("Princess Morbucks", "Evil princess", BitmapFactory.decodeResource(res, R.drawable.princessmorbucks), 1));

        List<TierItem> CTiers = new ArrayList<>();
        CTiers.add(new TierItem("Bubbles", "Blue powerpuff girl", BitmapFactory.decodeResource(res, R.drawable.bubbles), 0));

        tierItemData.addAll(STiers);
        tierItemData.addAll(ATiers);
        tierItemData.addAll(BTIers);
        tierItemData.addAll(CTiers);

        // Sample Tier Rows
        tierRowData = new ArrayList<>();
        tierRowData.add(new TierRow("S", STiers, R.color.STier, 0));
        tierRowData.add(new TierRow("A", ATiers, R.color.ATier, 1));
        tierRowData.add(new TierRow("B", BTIers, R.color.BTier, 2));
        tierRowData.add(new TierRow("C", CTiers, R.color.CTier, 3));
    }

    public static List<TierItem> getTierItems(){
        return tierItemData;
    }

    public static List<TierRow> getTierRows(){
        return tierRowData;
    }
}
