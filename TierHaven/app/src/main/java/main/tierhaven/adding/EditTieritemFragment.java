package main.tierhaven.adding;

import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import main.tierhaven.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class EditTieritemFragment extends Fragment {

    //text fields
    private EditText name;
    private EditText desc;

    public EditTieritemFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_edit_tieritem, container, false);
        name = root.findViewById(R.id.edit_tieritem_title_edittext);
        desc = root.findViewById(R.id.edit_tieritem_description_edittext);
        return root;
    }

    public void setName(String name){
        this.name.setText(name);
    }

    public void setDescription(String description){
        this.desc.setText(description);
    }

    public String getName(){
        return name.getText().toString();
    }

    public String getDescription(){
        return desc.getText().toString();
    }
}
