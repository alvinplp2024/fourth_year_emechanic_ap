package com.example.fixeridetest.ADMIN;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fixeridetest.ADMIN.NewsMech;
import com.example.fixeridetest.R;
import java.util.List;

public class NewsAdapterManage extends RecyclerView.Adapter<NewsAdapterManage.ViewHolder> {

    private List<NewsMech> newsList;
    private OnDeleteClickListener onDeleteClickListener;

    public NewsAdapterManage(List<NewsMech> newsList) {
        this.newsList = newsList;
    }

    // Interface for delete button click listener
    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }

    // Method to set the delete button click listener
    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.onDeleteClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news_manage, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NewsMech news = newsList.get(position);
        holder.titleTextView.setText("Title: " + news.getTitle());
        holder.contentTextView.setText("News Content: " + news.getContent());
        holder.startDateTextView.setText("Start Date: " + news.getStartDate());
        holder.endDateTextView.setText("End Date: " + news.getEndDate());
        holder.startTimeTextView.setText("Start Time: " + news.getStartTime());
        holder.endTimeTextView.setText("End Time: " + news.getEndTime());

        // Set click listener for delete button
        holder.deleteButton.setOnClickListener(v -> {
            if (onDeleteClickListener != null) {
                onDeleteClickListener.onDeleteClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, contentTextView, startDateTextView, endDateTextView, startTimeTextView, endTimeTextView;
        Button deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            contentTextView = itemView.findViewById(R.id.contentTextView);
            startDateTextView = itemView.findViewById(R.id.startDateTextView);
            endDateTextView = itemView.findViewById(R.id.endDateTextView);
            startTimeTextView = itemView.findViewById(R.id.startTimeTextView);
            endTimeTextView = itemView.findViewById(R.id.endTimeTextView);
            deleteButton = itemView.findViewById(R.id.delete_news);
        }
    }
}
