package com.campus.lostandfound;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.campus.lostandfound.database.DatabaseHelper;
import com.campus.lostandfound.model.Item;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Activity for Adding a new Lost or Found item, or Editing an existing item record.
 * Handles user input validation, DatePicker dialog, and SQLite database storage.
 */
public class AddEditItemActivity extends AppCompatActivity {

    public static final String EXTRA_EDIT_ITEM_ID = "EDIT_ITEM_ID";

    private DatabaseHelper dbHelper;
    private int editItemId = -1;
    private Item existingItem = null;

    // UI elements
    private Toolbar toolbar;
    private RadioGroup rgItemType;
    private RadioButton rbLost;
    private RadioButton rbFound;
    private TextInputEditText etItemTitle;
    private Spinner spinnerCategory;
    private TextInputEditText etItemLocation;
    private TextInputEditText etItemDate;
    private TextInputEditText etItemDescription;
    private TextInputEditText etContactName;
    private TextInputEditText etContactPhone;
    private MaterialButton btnSaveItem;

    private final Calendar calendar = Calendar.getInstance();
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_item);

        dbHelper = new DatabaseHelper(this);

        initViews();
        setupCategorySpinner();
        setupDatePicker();

        // Check if editing existing item
        if (getIntent().hasExtra(EXTRA_EDIT_ITEM_ID)) {
            editItemId = getIntent().getIntExtra(EXTRA_EDIT_ITEM_ID, -1);
            loadExistingItem(editItemId);
        } else {
            // Default date to today
            etItemDate.setText(dateFormatter.format(calendar.getTime()));
        }

        btnSaveItem.setOnClickListener(v -> saveItemRecord());
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbarAddEdit);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        rgItemType = findViewById(R.id.rgItemType);
        rbLost = findViewById(R.id.rbLost);
        rbFound = findViewById(R.id.rbFound);
        etItemTitle = findViewById(R.id.etItemTitle);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        etItemLocation = findViewById(R.id.etItemLocation);
        etItemDate = findViewById(R.id.etItemDate);
        etItemDescription = findViewById(R.id.etItemDescription);
        etContactName = findViewById(R.id.etContactName);
        etContactPhone = findViewById(R.id.etContactPhone);
        btnSaveItem = findViewById(R.id.btnSaveItem);
    }

    private void setupCategorySpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.item_categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
    }

    private void setupDatePicker() {
        etItemDate.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog(AddEditItemActivity.this,
                    (view, year, month, dayOfMonth) -> {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        etItemDate.setText(dateFormatter.format(calendar.getTime()));
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            dialog.show();
        });
    }

    private void loadExistingItem(int id) {
        existingItem = dbHelper.getItemById(id);
        if (existingItem == null) {
            Toast.makeText(this, "Item record not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_edit_item);
        }
        btnSaveItem.setText(R.string.btn_update);

        if (existingItem.isLost()) {
            rbLost.setChecked(true);
        } else {
            rbFound.setChecked(true);
        }

        etItemTitle.setText(existingItem.getTitle());
        etItemLocation.setText(existingItem.getLocation());
        etItemDate.setText(existingItem.getDate());
        etItemDescription.setText(existingItem.getDescription());
        etContactName.setText(existingItem.getContactName());
        etContactPhone.setText(existingItem.getContactPhone());

        // Select category in spinner
        ArrayAdapter adapter = (ArrayAdapter) spinnerCategory.getAdapter();
        int position = adapter.getPosition(existingItem.getCategory());
        if (position >= 0) {
            spinnerCategory.setSelection(position);
        }
    }

    private void saveItemRecord() {
        String title = etItemTitle.getText() != null ? etItemTitle.getText().toString().trim() : "";
        String location = etItemLocation.getText() != null ? etItemLocation.getText().toString().trim() : "";
        String date = etItemDate.getText() != null ? etItemDate.getText().toString().trim() : "";
        String description = etItemDescription.getText() != null ? etItemDescription.getText().toString().trim() : "";
        String contactName = etContactName.getText() != null ? etContactName.getText().toString().trim() : "";
        String contactPhone = etContactPhone.getText() != null ? etContactPhone.getText().toString().trim() : "";
        String category = spinnerCategory.getSelectedItem() != null ? spinnerCategory.getSelectedItem().toString() : "Other";
        String type = rbLost.isChecked() ? Item.TYPE_LOST : Item.TYPE_FOUND;

        // Form Validation
        if (TextUtils.isEmpty(title)) {
            etItemTitle.setError("Item name is required");
            etItemTitle.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(location)) {
            etItemLocation.setError("Location is required");
            etItemLocation.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(date)) {
            etItemDate.setError("Date is required");
            etItemDate.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(contactName)) {
            etContactName.setError("Contact name is required");
            etContactName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(contactPhone)) {
            etContactPhone.setError("Contact phone number is required");
            etContactPhone.requestFocus();
            return;
        }

        if (existingItem != null) {
            // Update existing record
            existingItem.setTitle(title);
            existingItem.setDescription(description);
            existingItem.setCategory(category);
            existingItem.setType(type);
            existingItem.setLocation(location);
            existingItem.setDate(date);
            existingItem.setContactName(contactName);
            existingItem.setContactPhone(contactPhone);

            int result = dbHelper.updateItem(existingItem);
            if (result > 0) {
                Toast.makeText(this, R.string.item_updated_success, Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to update item", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Insert new record
            Item newItem = new Item(title, description, category, type, location, date, contactName, contactPhone);
            long id = dbHelper.insertItem(newItem);
            if (id > 0) {
                Toast.makeText(this, R.string.item_saved_success, Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to save item", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
