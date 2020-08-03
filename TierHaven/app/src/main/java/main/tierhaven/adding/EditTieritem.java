package main.tierhaven.adding;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import main.tierhaven.R;

public class EditTieritem extends AppCompatActivity {

    //params to send in
    public static class params {
        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
    }

    private EditTieritemFragment fragment;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //make menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit_tieritem, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_tieritem);
        Toolbar toolbar = findViewById(R.id.toolbar);

        toolbar.setTitle("Edit Tier Item");
        setSupportActionBar(toolbar);

        fragment = (EditTieritemFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //if user wants to save
        if (item.getItemId() == R.id.edit_tieritem_save_menuitem){
            //return name and description and finish intent
            Intent intent = getIntent();
            intent.putExtra(Edit.params.NAME, fragment.getName());
            intent.putExtra(Edit.params.DESCRIPTION, fragment.getDescription());
            setResult(Activity.RESULT_OK, intent);
            EditTieritem.this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        //get intent data
        Intent intent = getIntent();
        String desc = intent.getStringExtra(params.DESCRIPTION);
        String name = intent.getStringExtra(params.NAME);

        //set data
        fragment.setDescription(desc);
        fragment.setName(name);

    }

}
