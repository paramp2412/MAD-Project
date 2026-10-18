package com.campus.lostandfound.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.campus.lostandfound.R;
import com.campus.lostandfound.model.Item;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView Adapter for displaying Lost and Found item cards.
 */
public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Item item);
    }

    private final Context context;
    private List<Item> items;
    private final OnItemClickListener listener;

    public ItemAdapter(Context context, List<Item> items, OnItemClickListener listener) {
        this.context = context;
        this.items = items != null ? items : new ArrayList<>();
        this.listener = listener;
    }

    public void updateList(List<Item> newItems) {
        this.items = newItems != null ? newItems : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_lost_found_card, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item item = items.get(position);

        holder.tvTitle.setText(item.getTitle());
        holder.tvCategory.setText(item.getCategory());
        holder.tvLocation.setText(item.getLocation());
        holder.tvDate.setText(item.getDate());

        if (item.getDescription() != null && !item.getDescription().trim().isEmpty()) {
            holder.tvDescription.setVisibility(View.VISIBLE);
            holder.tvDescription.setText(item.getDescription());
        } else {
            holder.tvDescription.setVisibility(View.GONE);
        }

        // Set Badge Appearance based on Type and Claimed Status
        if (item.isClaimed()) {
            holder.tvTypeBadge.setText("CLAIMED");
            holder.tvTypeBadge.setBackground(ContextCompat.getDrawable(context, R.drawable.badge_claimed));
            holder.tvTypeBadge.setTextColor(ContextCompat.getColor(context, R.color.badge_claimed_text));
        } else if (item.isLost()) {
            holder.tvTypeBadge.setText("LOST");
            holder.tvTypeBadge.setBackground(ContextCompat.getDrawable(context, R.drawable.badge_lost));
            holder.tvTypeBadge.setTextColor(ContextCompat.getColor(context, R.color.badge_lost_text));
        } else {
            holder.tvTypeBadge.setText("FOUND");
            holder.tvTypeBadge.setBackground(ContextCompat.getDrawable(context, R.drawable.badge_found));
            holder.tvTypeBadge.setTextColor(ContextCompat.getColor(context, R.color.badge_found_text));
        }

        holder.cardRoot.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardRoot;
        TextView tvCategory;
        TextView tvTypeBadge;
        TextView tvTitle;
        TextView tvDescription;
        TextView tvLocation;
        TextView tvDate;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            cardRoot = itemView.findViewById(R.id.cardItemRoot);
            tvCategory = itemView.findViewById(R.id.tvCardCategory);
            tvTypeBadge = itemView.findViewById(R.id.tvCardTypeBadge);
            tvTitle = itemView.findViewById(R.id.tvCardTitle);
            tvDescription = itemView.findViewById(R.id.tvCardDescription);
            tvLocation = itemView.findViewById(R.id.tvCardLocation);
            tvDate = itemView.findViewById(R.id.tvCardDate);
        }
    }
}
