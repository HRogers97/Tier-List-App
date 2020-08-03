package main.tierhaven.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import main.tierhaven.R;
import main.tierhaven.model.Comment;

public class CommentAdapter extends RecyclerView.Adapter<CommentViewHolder> {
    private List<Comment> data;

    public CommentAdapter(List<Comment> data){
        this.data = data;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.list_item_comment, parent, false);

        return new CommentViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, final int position) {
        holder.set(data.get(position));

        holder.adapter = this;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
