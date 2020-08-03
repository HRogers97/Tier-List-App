package main.tierhaven.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatDialogFragment;

import main.tierhaven.R;

public class EditCommentDialog extends AppCompatDialogFragment {
    private EditText commentBodyEditText;
    private CommentDialogListener listener;
    private String comment;

    public EditCommentDialog(String comment){
        this.comment = comment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.edit_comment_dialog, null);

        builder.setView(view)
                .setTitle("Edit comment")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                    }
                })
                .setPositiveButton("submit", new DialogInterface.OnClickListener() {

                    private String commentBody;

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Get the edited comment and apply the edit
                        commentBody = commentBodyEditText.getText().toString();
                        if(!TextUtils.isEmpty(commentBody.trim())){
                            listener.ApplyEdit(commentBody);
                        }
                    }
                });

        // Input field starts with the comment that is being edited
        commentBodyEditText = view.findViewById(R.id.commentBody_editText);
        commentBodyEditText.setText(comment);

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (CommentDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "Must implement Comment Dialog Listener");
        }
    }

    public interface CommentDialogListener{
        void ApplyEdit(String commentBody);
    }
}
