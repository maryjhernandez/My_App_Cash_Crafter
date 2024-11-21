package co.edu.unipiloto.myappcash.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import co.edu.unipiloto.myappcash.R;
import co.edu.unipiloto.myappcash.models.Comment;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentViewHolder> {

    private List<Comment> comments;

    public CommentsAdapter(List<Comment> comments) {
        this.comments = comments;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.ratingBar.setRating(comment.getRating());
        holder.commentTextView.setText(comment.getComment());
        holder.userTextView.setText(comment.getUserId()); // Ahora esto debería funcionar
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public void updateComments(List<Comment> newComments) {
        comments.clear();
        comments.addAll(newComments);
        notifyDataSetChanged();
    }


    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        RatingBar ratingBar;
        TextView commentTextView;
        TextView userTextView; // Agregado para mostrar el usuario

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            commentTextView = itemView.findViewById(R.id.commentTextView);
            userTextView = itemView.findViewById(R.id.userTextView); // Inicializa userTextView
        }
    }
}
