package com.campus.lostandfound;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.campus.lostandfound.adapter.ItemAdapter;
import com.campus.lostandfound.database.DatabaseHelper;
import com.campus.lostandfound.model.Item;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Main Dashboard Activity:
 * Displays all reported items, provides real-time search, filters by type (Lost/Found/Claimed)
 * and category, displays summary stats, and navigates to details or item creation.
 */
public class MainActivity extends AppCompatActivity implements ItemAdapter.OnItemClickListener {

    private DatabaseHelper dbHelper;
    private ItemAdapter adapter;
    private List<Item> itemList = new ArrayList<>();

    // Views
    private TextView tvStatLostCount;
    private TextView tvStatFoundCount;
    private TextView tvStatClaimedCount;
    private EditText etSearchQuery;
    private ImageView btnClearSearch;
    private ChipGroup chipGroupType;
    private Spinner spinnerCategoryFilter;
    private RecyclerView recyclerViewItems;
    private LinearLayout layoutEmptyState;
    private FloatingActionButton fabAddItem;

    // Filter state
    private String currentTypeFilter = "ALL";
    private String currentCategoryFilter = "All Categories";
    private String currentSearchQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        initViews();
        setupCategorySpinner();
        setupRecyclerView();
        setupListeners();
    }

    private void initViews() {
        tvStatLostCount = findViewById(R.id.tvStatLostCount);
        tvStatFoundCount = findViewById(R.id.tvStatFoundCount);
        tvStatClaimedCount = findViewById(R.id.tvStatClaimedCount);
        etSearchQuery = findViewById(R.id.etSearchQuery);
        btnClearSearch = findViewById(R.id.btnClearSearch);
        chipGroupType = findViewById(R.id.chipGroupType);
        spinnerCategoryFilter = findViewById(R.id.spinnerCategoryFilter);
        recyclerViewItems = findViewById(R.id.recyclerViewItems);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);
        fabAddItem = findViewById(R.id.fabAddItem);
    }

    private void setupCategorySpinner() {
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.filter_categories, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoryFilter.setAdapter(spinnerAdapter);
    }

    private void setupRecyclerView() {
        adapter = new ItemAdapter(this, itemList, this);
        recyclerViewItems.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewItems.setAdapter(adapter);
    }

    private void setupListeners() {
        // Floating Action Button -> Add Item
        fabAddItem.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditItemActivity.class);
            startActivity(intent);
        });

        // Search Bar Text Change
        etSearchQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchQuery = s.toString();
                btnClearSearch.setVisibility(currentSearchQuery.isEmpty() ? View.GONE : View.VISIBLE);
                loadItemsFromDatabase();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Clear Search Button
        btnClearSearch.setOnClickListener(v -> etSearchQuery.setText(""));

        // Type Filter Chips (All, Lost, Found, Claimed)
        chipGroupType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chipLost) {
                currentTypeFilter = Item.TYPE_LOST;
            } else if (checkedId == R.id.chipFound) {
                currentTypeFilter = Item.TYPE_FOUND;
            } else if (checkedId == R.id.chipClaimed) {
                currentTypeFilter = "CLAIMED";
            } else {
                currentTypeFilter = "ALL";
            }
            loadItemsFromDatabase();
        });

        // Category Spinner
        spinnerCategoryFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentCategoryFilter = parent.getItemAtPosition(position).toString();
                loadItemsFromDatabase();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadItemsFromDatabase();
        updateSummaryStats();
    }

    /**
     * Query SQLite database with active filters and update RecyclerView
     */
    private void loadItemsFromDatabase() {
        itemList = dbHelper.getItems(currentTypeFilter, currentCategoryFilter, currentSearchQuery);
        adapter.updateList(itemList);

        if (itemList.isEmpty()) {
            layoutEmptyState.setVisibility(View.VISIBLE);
            recyclerViewItems.setVisibility(View.GONE);
        } else {
            layoutEmptyState.setVisibility(View.GONE);
            recyclerViewItems.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Update header counters
     */
    private void updateSummaryStats() {
        int[] counts = dbHelper.getItemCounts(); // [total, lost, found, claimed]
        tvStatLostCount.setText(String.valueOf(counts[1]));
        tvStatFoundCount.setText(String.valueOf(counts[2]));
        tvStatClaimedCount.setText(String.valueOf(counts[3]));
    }

    @Override
    public void onItemClick(Item item) {
        Intent intent = new Intent(MainActivity.this, ItemDetailActivity.class);
        intent.putExtra("ITEM_ID", item.getId());
        startActivity(intent);
    }
}
