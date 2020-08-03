package main.tierhaven.adding.DialogFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import main.tierhaven.R;
import main.tierhaven.util.CircleView;

public class ColorPickerDialogFragment extends DialogFragment {

    public interface OnColorChosenListener{
        void onColorChosen(int resFile);
    }

    //current resfile chosen
    private int resFileId;


    private TextView colorHolder;
    //circle views
    private CircleView circleViewS;
    private CircleView circleViewA;
    private CircleView circleViewB;
    private CircleView circleViewC;
    private CircleView circleViewD;
    private CircleView circleViewE;
    private CircleView circleViewF;
    private CircleView circleViewG;
    private CircleView circleViewH;
    private CircleView circleViewNewTier;
    private View root;

    private OnColorChosenListener onColorChosenListener;

    public ColorPickerDialogFragment(int resFileId){
        this.resFileId = resFileId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_colorpicker, container, false);

        colorHolder = root.findViewById(R.id.colorHolder);

        colorHolder.setBackgroundColor(ContextCompat.getColor(root.getContext(), resFileId));

        ImageButton colorChosen = root.findViewById(R.id.colorchosen_imageButton);

        //get circle views
        circleViewS = root.findViewById(R.id.circleView);
        circleViewA = root.findViewById(R.id.circleView2);
        circleViewB = root.findViewById(R.id.circleView3);
        circleViewC = root.findViewById(R.id.circleView4);
        circleViewD = root.findViewById(R.id.circleView5);
        circleViewE = root.findViewById(R.id.circleView6);
        circleViewF = root.findViewById(R.id.circleView7);
        circleViewG = root.findViewById(R.id.circleView8);
        circleViewH = root.findViewById(R.id.circleView9);
        circleViewNewTier = root.findViewById(R.id.circleView10);

        //set colors
        circleViewS.setColor(ContextCompat.getColor(root.getContext() ,R.color.STier));
        circleViewA.setColor(ContextCompat.getColor(root.getContext() ,R.color.ATier));
        circleViewB.setColor(ContextCompat.getColor(root.getContext() ,R.color.BTier));
        circleViewC.setColor(ContextCompat.getColor(root.getContext() ,R.color.CTier));
        circleViewD.setColor(ContextCompat.getColor(root.getContext() ,R.color.DTier));
        circleViewE.setColor(ContextCompat.getColor(root.getContext() ,R.color.ETier));
        circleViewF.setColor(ContextCompat.getColor(root.getContext() ,R.color.FTier));
        circleViewG.setColor(ContextCompat.getColor(root.getContext() ,R.color.GTier));
        circleViewH.setColor(ContextCompat.getColor(root.getContext() ,R.color.HTier));
        circleViewNewTier.setColor(ContextCompat.getColor(root.getContext() ,R.color.NewTier));

        //set handlers
        circleViewS.setOnClickListener(new ColorClicked());
        circleViewA.setOnClickListener(new ColorClicked());
        circleViewB.setOnClickListener(new ColorClicked());
        circleViewC.setOnClickListener(new ColorClicked());
        circleViewD.setOnClickListener(new ColorClicked());
        circleViewE.setOnClickListener(new ColorClicked());
        circleViewF.setOnClickListener(new ColorClicked());
        circleViewG.setOnClickListener(new ColorClicked());
        circleViewH.setOnClickListener(new ColorClicked());
        circleViewNewTier.setOnClickListener(new ColorClicked());

        //if user presses ok button
        colorChosen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if color chosen listener is set
                if (onColorChosenListener != null){
                    //call on color chosen
                    onColorChosenListener.onColorChosen(resFileId);
                }
                dismiss();
            }
        });

        return root;
    }

    public void setOnColorChosenListener(OnColorChosenListener onColorChosenListener) {
        this.onColorChosenListener = onColorChosenListener;
    }

    private class ColorClicked implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            CircleView circleView = (CircleView) view;

            //if color view given has the same color of a circle view
            //set resfile to the color given
            //do this for all circle views
            if(circleView.getColor() == circleViewS.getColor()){
                resFileId = R.color.STier;
            }
            else if (circleView.getColor() == circleViewA.getColor()){
                resFileId = R.color.ATier;
            }

            else if (circleView.getColor() == circleViewB.getColor()){
                resFileId = R.color.BTier;
            }

            else if (circleView.getColor() == circleViewC.getColor()){
                resFileId = R.color.CTier;
            }

            else if (circleView.getColor() == circleViewD.getColor()){
                resFileId = R.color.DTier;
            }

            else if (circleView.getColor() == circleViewE.getColor()){
                resFileId = R.color.ETier;
            }

            else if (circleView.getColor() == circleViewF.getColor()){
                resFileId = R.color.FTier;
            }

            else if (circleView.getColor() == circleViewG.getColor()){
                resFileId = R.color.GTier;
            }
            else if (circleView.getColor() == circleViewH.getColor()){
                resFileId = R.color.HTier;
            }

            else if (circleView.getColor() == circleViewNewTier.getColor()){
                resFileId = R.color.NewTier;
            }

            //set color display to current color
            colorHolder.setBackgroundColor(ContextCompat.getColor(root.getContext(), resFileId));
        }
    }
}
