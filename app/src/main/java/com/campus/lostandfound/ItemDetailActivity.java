package com.campus.lostandfound;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.campus.lostandfound.database.DatabaseHelper;
import com.campus.lostandfound.model.Item;
import com.google.android.material.button.MaterialButton;

/**
 * Item Detail Activity:
 * Shows full item details, provides quick dialer intent to contact person,
 * enables editing, status toggle to Claimed/Resolved, and deletion with confirmation dialog.
 */
public class ItemDetailActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private int itemId;
    private Item currentItem;

    // Views
    private Toolbar toolbar;
    private TextView tvDetailCategory;
    private TextView tvDetailStatusBadge;
    private TextView tvDetailTitle;
    private TextView tvDetailDescription;
    private TextView tvDetailLocation;
    private TextView tvDetailDate;
    private TextView tvDetailContactName;
    private TextView tvDetailContactPhone;
    private MaterialButton btnCallContact;
    private MaterialButton btnMarkClaimed;
    private MaterialButton btnEditItem;
    private MaterialButton btnDeleteItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        dbHelper = new DatabaseHelper(this);
        itemId = getIntent().getIntExtra("ITEM_ID", -1);

        initViews();
        setupListeners();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbarDetail);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        tvDetailCategory = findViewById(R.id.tvDetailCategory);
        tvDetailStatusBadge = findViewById(R.id.tvDetailStatusBadge);
        tvDetailTitle = findViewById(R.id.tvDetailTitle);
        tvDetailDescription = findViewById(R.id.tvDetailDescription);
        tvDetailLocation = findViewById(R.id.tvDetailLocation);
        tvDetailDate = findViewById(R.id.tvDetailDate);
        tvDetailContactName = findViewById(R.id.tvDetailContactName);
        tvDetailContactPhone = findViewById(R.id.tvDetailContactPhone);
        btnCallContact = findViewById(R.id.btnCallContact);
        btnMarkClaimed = findViewById(R.id.btnMarkClaimed);
        btnEditItem = findViewById(R.id.btnEditItem);
        btnDeleteItem = findViewById(R.id.btnDeleteItem);
    }

    private void setupListeners() {
        // Dial Phone
        btnCallContact.setOnClickListener(v -> {
            if (currentItem != null && currentItem.getContactPhone() != null && !currentItem.getContactPhone().isEmpty()) {
                Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                dialIntent.setData(Uri.parse("tel:" + currentItem.getContactPhone()));
                startActivity(dialIntent);
            } else {
                Toast.makeText(this, "No valid phone number available", Toast.LENGTH_SHORT).show();
            }
        });

        // Mark as Claimed
        btnMarkClaimed.setOnClickListener(v -> {
            if (currentItem != null && !currentItem.isClaimed()) {
                dbHelper.markAsClaimed(currentItem.getId());
                Toast.makeText(this, R.string.status_marked_claimed, Toast.LENGTH_SHORT).show();
                loadItemDetails();
            }
        });

        // Edit Item
        btnEditItem.setOnClickListener(v -> {
            Intent intent = new Intent(ItemDetailActivity.this, AddEditItemActivity.class);
            intent.putExtra(AddEditItemActivity.EXTRA_EDIT_ITEM_ID, itemId);
            startActivity(intent);
        });

        // Delete Item with Confirmation Dialog
        btnDeleteItem.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.delete_confirm_title)
                    .setMessage(R.string.delete_confirm_msg)
                    .setPositiveButton(R.string.confirm, (dialog, which) -> {
                        int rows = dbHelper.deleteItem(itemId);
                        if (rows > 0) {
                            Toast.makeText(this, R.string.item_deleted_success, Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, "Failed to delete item", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton(R.string.cancel, null)
                    .show();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadItemDetails();
    }

    private void loadItemDetails() {
        currentItem = dbHelper.getItemById(itemId);
        if (currentItem == null) {
            Toast.makeText(this, "Item record no longer exists", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvDetailTitle.setText(currentItem.getTitle());
        tvDetailCategory.setText(currentItem.getCategory());
        tvDetailLocation.setText(currentItem.getLocation());
        tvDetailDate.setText(currentItem.getDate());
        tvDetailContactName.setText(currentItem.getContactName());
        tvDetailContactPhone.setText(currentItem.getContactPhone());

        if (currentItem.getDescription() != null && !currentItem.getDescription().trim().isEmpty()) {
            tvDetailDescription.setText(currentItem.getDescription());
        } else {
            tvDetailDescription.setText("No additional description provided.");
        }

        // Status Badge & Claim button state
        if (currentItem.isClaimed()) {
            tvDetailStatusBadge.setText("CLAIMED");
            tvDetailStatusBadge.setBackground(ContextCompat.getDrawable(this, R.drawable.badge_claimed));
            tvDetailStatusBadge.setTextColor(ContextCompat.getColor(this, R.color.badge_claimed_text));

            btnMarkClaimed.setEnabled(false);
            btnMarkClaimed.setText("Item Recovered / Claimed");
            btnMarkClaimed.setAlpha(0.6f);
        } else if (currentItem.isLost()) {
            tvDetailStatusBadge.setText("LOST");
            tvDetailStatusBadge.setBackground(ContextCompat.getDrawable(this, R.drawable.badge_lost));
            tvDetailStatusBadge.setTextColor(ContextCompat.getColor(this, R.color.badge_lost_text));

            btnMarkClaimed.setEnabled(true);
            btnMarkClaimed.setText(R.string.btn_mark_claimed);
            btnMarkClaimed.setAlpha(1.0f);
        } else {
            tvDetailStatusBadge.setText("FOUND");
            tvDetailStatusBadge.setBackground(ContextCompat.getDrawable(this, R.drawable.badge_found));
            tvDetailStatusBadge.setTextColor(ContextCompat.getColor(this, R.color.badge_found_text));

            btnMarkClaimed.setEnabled(true);
            btnMarkClaimed.setText(R.string.btn_mark_claimed);
            btnMarkClaimed.setAlpha(1.0f);
        }
    }
}
