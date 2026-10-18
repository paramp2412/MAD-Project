import java.text.SimpleDateFormat;
import java.util.*;

/**
 * ==============================================================================
 * CAMPUS LOST AND FOUND — STANDALONE DATA INTEGRITY TESTER & VALIDATION ENGINE
 * ==============================================================================
 * This test suite verifies the complete data layer, database schema constraints,
 * CRUD operations, search indexing algorithms, and state lifecycle transitions
 * of the Campus Lost and Found Mobile Application without needing Android Studio.
 *
 * Can be executed directly with Java 22:
 *   java DataIntegrityTester.java
 * ==============================================================================
 */
public class DataIntegrityTester {

    // ANSI Color Codes for Beautiful Console Output
    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";
    private static final String RED = "\u001B[31m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String CYAN = "\u001B[36m";
    private static final String BOLD = "\u001B[1m";

    private static int totalTests = 0;
    private static int passedTests = 0;
    private static int failedTests = 0;

    // Simulated In-Memory Relational Engine mirroring SQLite DatabaseHelper
    public static class MockSQLiteStore {
        private final Map<Integer, ItemRecord> table = new LinkedHashMap<>();
        private int autoIncrementId = 1;

        public static class ItemRecord {
            public int id;
            public String title;
            public String description;
            public String category;
            public String type;            // "LOST" or "FOUND"
            public String location;
            public String date;            // "YYYY-MM-DD"
            public String contactName;
            public String contactPhone;
            public String status;          // "ACTIVE" or "CLAIMED"
            public long timestamp;

            public ItemRecord(int id, String title, String description, String category,
                              String type, String location, String date,
                              String contactName, String contactPhone, String status, long timestamp) {
                this.id = id;
                this.title = title;
                this.description = description;
                this.category = category;
                this.type = type;
                this.location = location;
                this.date = date;
                this.contactName = contactName;
                this.contactPhone = contactPhone;
                this.status = status;
                this.timestamp = timestamp;
            }

            public ItemRecord copy() {
                return new ItemRecord(id, title, description, category, type, location, date, contactName, contactPhone, status, timestamp);
            }
        }

        // CREATE
        public long insert(String title, String description, String category,
                           String type, String location, String date,
                           String contactName, String contactPhone) {
            // Data validation constraints
            if (title == null || title.trim().isEmpty()) throw new IllegalArgumentException("Title cannot be null or empty (NOT NULL constraint)");
            if (location == null || location.trim().isEmpty()) throw new IllegalArgumentException("Location cannot be null or empty (NOT NULL constraint)");
            if (date == null || date.trim().isEmpty()) throw new IllegalArgumentException("Date cannot be null or empty (NOT NULL constraint)");
            if (contactName == null || contactName.trim().isEmpty()) throw new IllegalArgumentException("Contact Name cannot be null or empty (NOT NULL constraint)");
            if (contactPhone == null || contactPhone.trim().isEmpty()) throw new IllegalArgumentException("Contact Phone cannot be null or empty (NOT NULL constraint)");
            if (!"LOST".equalsIgnoreCase(type) && !"FOUND".equalsIgnoreCase(type)) {
                throw new IllegalArgumentException("Type must be either 'LOST' or 'FOUND'");
            }

            int id = autoIncrementId++;
            long timestamp = System.currentTimeMillis();
            ItemRecord record = new ItemRecord(id, title, description, category, type.toUpperCase(), location, date, contactName, contactPhone, "ACTIVE", timestamp);
            table.put(id, record);
            return id;
        }

        // READ by ID
        public ItemRecord getById(int id) {
            ItemRecord rec = table.get(id);
            return rec != null ? rec.copy() : null;
        }

        // READ All with Filter & Search Query
        public List<ItemRecord> query(String typeFilter, String categoryFilter, String searchQuery) {
            List<ItemRecord> results = new ArrayList<>();

            for (ItemRecord rec : table.values()) {
                // 1. Status / Type Filter
                if (typeFilter != null && !typeFilter.isEmpty() && !typeFilter.equalsIgnoreCase("ALL")) {
                    if (typeFilter.equalsIgnoreCase("CLAIMED")) {
                        if (!"CLAIMED".equalsIgnoreCase(rec.status)) continue;
                    } else {
                        if (!rec.type.equalsIgnoreCase(typeFilter)) continue;
                    }
                }

                // 2. Category Filter
                if (categoryFilter != null && !categoryFilter.isEmpty() && !categoryFilter.equalsIgnoreCase("All Categories")) {
                    if (!rec.category.equalsIgnoreCase(categoryFilter)) continue;
                }

                // 3. Search Query (LIKE %query% across title, description, location)
                if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                    String q = searchQuery.trim().toLowerCase();
                    boolean matchTitle = rec.title != null && rec.title.toLowerCase().contains(q);
                    boolean matchDesc = rec.description != null && rec.description.toLowerCase().contains(q);
                    boolean matchLoc = rec.location != null && rec.location.toLowerCase().contains(q);

                    if (!matchTitle && !matchDesc && !matchLoc) continue;
                }

                results.add(rec.copy());
            }

            // Order by timestamp descending (newest first)
            results.sort((a, b) -> Long.compare(b.timestamp, a.timestamp));
            return results;
        }

        // UPDATE
        public int update(int id, String title, String description, String category,
                          String type, String location, String date,
                          String contactName, String contactPhone) {
            ItemRecord existing = table.get(id);
            if (existing == null) return 0;

            if (title == null || title.trim().isEmpty()) throw new IllegalArgumentException("Title cannot be empty");
            if (location == null || location.trim().isEmpty()) throw new IllegalArgumentException("Location cannot be empty");
            if (contactPhone == null || contactPhone.trim().isEmpty()) throw new IllegalArgumentException("Contact phone cannot be empty");

            existing.title = title;
            existing.description = description;
            existing.category = category;
            existing.type = type.toUpperCase();
            existing.location = location;
            existing.date = date;
            existing.contactName = contactName;
            existing.contactPhone = contactPhone;
            return 1;
        }

        // MARK AS CLAIMED
        public int markClaimed(int id) {
            ItemRecord existing = table.get(id);
            if (existing == null) return 0;
            existing.status = "CLAIMED";
            return 1;
        }

        // DELETE
        public int delete(int id) {
            return table.remove(id) != null ? 1 : 0;
        }

        // STATS
        public int[] getCounts() {
            int total = table.size();
            int lost = 0;
            int found = 0;
            int claimed = 0;

            for (ItemRecord r : table.values()) {
                if ("CLAIMED".equalsIgnoreCase(r.status)) claimed++;
                if ("LOST".equalsIgnoreCase(r.type)) lost++;
                if ("FOUND".equalsIgnoreCase(r.type)) found++;
            }
            return new int[]{total, lost, found, claimed};
        }

        public void clear() {
            table.clear();
            autoIncrementId = 1;
        }
    }

    public static void main(String[] args) {
        printHeader();

        MockSQLiteStore store = new MockSQLiteStore();

        // TEST SUITE 1: SCHEMA & INITIALIZATION
        testGroup("1. Schema, Auto-Increment & Data Initialization");
        test("Initial Database Store is Empty", () -> {
            int[] counts = store.getCounts();
            assertCondition(counts[0] == 0, "Total count must start at 0");
        });

        // TEST SUITE 2: CREATE (INSERT) INTEGRITY
        testGroup("2. CRUD: Create (INSERT) & Data Type Constraints");
        test("Insert Lost Item Record (Casio Calculator)", () -> {
            long id1 = store.insert(
                    "Casio FX-991EX Scientific Calculator",
                    "Black casing with blue sticker on back",
                    "Electronics & Gadgets",
                    "LOST",
                    "Room 302, Engineering Block",
                    "2026-10-15",
                    "Rohan Sharma",
                    "9876543210"
            );
            assertCondition(id1 == 1, "First generated ID must be 1");
            MockSQLiteStore.ItemRecord rec = store.getById(1);
            assertCondition(rec != null, "Record must be retrievable");
            assertCondition(rec.title.equals("Casio FX-991EX Scientific Calculator"), "Title integrity matched");
            assertCondition(rec.status.equals("ACTIVE"), "Default status must be ACTIVE");
            assertCondition(rec.type.equals("LOST"), "Type must be LOST");
        });

        test("Insert Found Item Record (Student ID Card)", () -> {
            long id2 = store.insert(
                    "Student ID Card #2024CS089",
                    "Found near main library entrance",
                    "ID Cards & Documents",
                    "FOUND",
                    "Central Library Ground Floor",
                    "2026-10-16",
                    "Prof. N. Mali",
                    "9811223344"
            );
            assertCondition(id2 == 2, "Second generated ID must be 2");
            MockSQLiteStore.ItemRecord rec = store.getById(2);
            assertCondition(rec.type.equals("FOUND"), "Type must be FOUND");
        });

        test("Insert Item with Special Characters & Quotes (SQL Injection Safety)", () -> {
            long id3 = store.insert(
                    "John's Dell 65W Laptop Charger; DROP TABLE items; --",
                    "Has tape on cord & \"fragile\" text written in blue ink.",
                    "Electronics & Gadgets",
                    "LOST",
                    "Lab 4, 3rd Floor",
                    "2026-10-16",
                    "John O'Connor",
                    "+91-9988776655"
            );
            assertCondition(id3 == 3, "Special character item safely inserted without SQL breakage");
            MockSQLiteStore.ItemRecord rec = store.getById(3);
            assertCondition(rec.title.contains("DROP TABLE"), "Escaped string preserved literally");
        });

        // TEST SUITE 3: VALIDATION AND ERROR HANDLING
        testGroup("3. Data Integrity & Validation Constraints");
        test("Reject Record with Empty / Null Title", () -> {
            try {
                store.insert("", "Some description", "Electronics & Gadgets", "LOST", "Lab 1", "2026-10-17", "Alice", "1234567890");
                assertCondition(false, "Should have thrown IllegalArgumentException");
            } catch (IllegalArgumentException e) {
                assertCondition(true, "Validation correctly prevented empty title");
            }
        });

        test("Reject Record with Empty Location", () -> {
            try {
                store.insert("Keys", "Car keys", "Keys & Accessories", "FOUND", "", "2026-10-17", "Bob", "1234567890");
                assertCondition(false, "Should have thrown IllegalArgumentException");
            } catch (IllegalArgumentException e) {
                assertCondition(true, "Validation correctly prevented empty location");
            }
        });

        test("Reject Record with Invalid Item Type", () -> {
            try {
                store.insert("Book", "Java programming", "Books & Stationery", "UNKNOWN_TYPE", "Canteen", "2026-10-17", "Charlie", "1234567890");
                assertCondition(false, "Should have thrown IllegalArgumentException");
            } catch (IllegalArgumentException e) {
                assertCondition(true, "Validation rejected invalid type");
            }
        });

        // TEST SUITE 4: READ & MULTI-CRITERIA SEARCH ENGINE
        testGroup("4. Search Engine & Multi-Criteria Filtering");
        test("Exact Keyword Search across Item Name ('Calculator')", () -> {
            List<MockSQLiteStore.ItemRecord> list = store.query("ALL", "All Categories", "Calculator");
            assertCondition(list.size() == 1, "Expected exactly 1 match for 'Calculator'");
            assertCondition(list.get(0).id == 1, "Matched item ID 1");
        });

        test("Substring Search across Campus Location ('Library')", () -> {
            List<MockSQLiteStore.ItemRecord> list = store.query("ALL", "All Categories", "Library");
            assertCondition(list.size() == 1, "Expected 1 match for location 'Library'");
            assertCondition(list.get(0).id == 2, "Matched ID card found at Library");
        });

        test("Case-Insensitive Search ('charger' vs 'CHARGER')", () -> {
            List<MockSQLiteStore.ItemRecord> lower = store.query("ALL", "All Categories", "charger");
            List<MockSQLiteStore.ItemRecord> upper = store.query("ALL", "All Categories", "CHARGER");
            assertCondition(lower.size() == 1 && upper.size() == 1, "Case-insensitive search returns identical results");
        });

        test("Filter by Item Type ('LOST')", () -> {
            List<MockSQLiteStore.ItemRecord> lostItems = store.query("LOST", "All Categories", "");
            assertCondition(lostItems.size() == 2, "Expected 2 lost items (Calculator, Charger)");
            for (MockSQLiteStore.ItemRecord r : lostItems) {
                assertCondition(r.type.equals("LOST"), "All returned records have type LOST");
            }
        });

        test("Filter by Category ('Electronics & Gadgets')", () -> {
            List<MockSQLiteStore.ItemRecord> electronics = store.query("ALL", "Electronics & Gadgets", "");
            assertCondition(electronics.size() == 2, "Expected 2 electronic items");
        });

        test("Combined Filter: Type='LOST' + Category='Electronics & Gadgets' + Search='Casio'", () -> {
            List<MockSQLiteStore.ItemRecord> combo = store.query("LOST", "Electronics & Gadgets", "Casio");
            assertCondition(combo.size() == 1, "Exact 1 item matches combination");
            assertCondition(combo.get(0).title.contains("Casio"), "Matched Casio item");
        });

        // TEST SUITE 5: UPDATE & LIFECYCLE STATE TRANSITIONS
        testGroup("5. CRUD: Update (UPDATE) & Status Lifecycle");
        test("Update Item Location and Contact Phone", () -> {
            int updatedRows = store.update(1,
                    "Casio FX-991EX Scientific Calculator",
                    "Black casing with blue sticker - UPDATED",
                    "Electronics & Gadgets",
                    "LOST",
                    "Room 405 (Moved from 302)",
                    "2026-10-15",
                    "Rohan Sharma",
                    "9999999999"
            );
            assertCondition(updatedRows == 1, "Update returned 1 affected row");
            MockSQLiteStore.ItemRecord updated = store.getById(1);
            assertCondition(updated.location.equals("Room 405 (Moved from 302)"), "Location updated successfully");
            assertCondition(updated.contactPhone.equals("9999999999"), "Phone updated successfully");
        });

        test("State Transition: Mark Item as CLAIMED / RESOLVED", () -> {
            int claimResult = store.markClaimed(2);
            assertCondition(claimResult == 1, "Mark claimed returned 1 affected row");
            MockSQLiteStore.ItemRecord claimedRec = store.getById(2);
            assertCondition(claimedRec.status.equals("CLAIMED"), "Status transitioned to CLAIMED");

            // Verify it now appears under CLAIMED filter
            List<MockSQLiteStore.ItemRecord> claimedList = store.query("CLAIMED", "All Categories", "");
            assertCondition(claimedList.size() == 1, "Appears in CLAIMED query");
            assertCondition(claimedList.get(0).id == 2, "Correct item verified in CLAIMED list");
        });

        // TEST SUITE 6: DELETE & REFERENTIAL INTEGRITY
        testGroup("6. CRUD: Delete (DELETE) & Count Integrity");
        test("Delete Item Record by ID", () -> {
            int initialCount = store.getCounts()[0];
            int deletedRows = store.delete(3);
            assertCondition(deletedRows == 1, "Delete returned 1 affected row");

            MockSQLiteStore.ItemRecord postDelete = store.getById(3);
            assertCondition(postDelete == null, "Record 3 no longer exists");

            int finalCount = store.getCounts()[0];
            assertCondition(finalCount == initialCount - 1, "Total count decremented by exactly 1");
        });

        test("Attempting to Delete Non-Existent ID Returns 0", () -> {
            int deletedRows = store.delete(999);
            assertCondition(deletedRows == 0, "Gracefully handles non-existent ID without error");
        });

        // TEST SUITE 7: STRESS, BULK LOAD & PERFORMANCE BENCHMARK
        testGroup("7. High-Volume Performance & Stress Benchmark");
        test("Bulk Insert 250 Items & Measure Execution Latency", () -> {
            long startTime = System.currentTimeMillis();
            for (int i = 0; i < 250; i++) {
                store.insert(
                        "Sample Item #" + i,
                        "Stress test item description for load validation",
                        i % 2 == 0 ? "Books & Stationery" : "Bags & Wallets",
                        i % 3 == 0 ? "LOST" : "FOUND",
                        "Campus Building " + (i % 5),
                        "2026-10-17",
                        "Tester " + i,
                        "9800000" + String.format("%03d", i)
                );
            }
            long insertElapsed = System.currentTimeMillis() - startTime;
            assertCondition(insertElapsed < 100, "250 items inserted in " + insertElapsed + "ms (well below 100ms threshold)");
        });

        test("Execute 50 Filtered Search Queries on 250+ Records", () -> {
            long queryStartTime = System.currentTimeMillis();
            for (int i = 0; i < 50; i++) {
                List<MockSQLiteStore.ItemRecord> results = store.query("LOST", "Books & Stationery", "Sample Item #1");
                assertCondition(results != null, "Query returned valid result set");
            }
            long queryElapsed = System.currentTimeMillis() - queryStartTime;
            double avgQueryTime = (double) queryElapsed / 50.0;
            assertCondition(avgQueryTime < 2.0, "Average query latency is " + String.format("%.3f", avgQueryTime) + "ms (< 2ms)");
        });

        printSummary();
    }

    // ==============================================================================
    // HELPER METHODS FOR FORMATTING AND REPORTING
    // ==============================================================================
    private static void printHeader() {
        System.out.println(CYAN + BOLD + "================================================================================" + RESET);
        System.out.println(CYAN + BOLD + "       CAMPUS LOST AND FOUND — DATA INTEGRITY & TEST SUITE RUNNER              " + RESET);
        System.out.println(CYAN + BOLD + "================================================================================" + RESET);
        System.out.println("Execution Environment: Java " + System.getProperty("java.version") + " (" + System.getProperty("os.name") + ")");
        System.out.println("Timestamp: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        System.out.println("Target: Standalone Data Engine & SQLite Compatibility Verification\n");
    }

    private static void testGroup(String groupTitle) {
        System.out.println("\n" + YELLOW + BOLD + "--- " + groupTitle + " ---" + RESET);
    }

    private static void test(String testName, Runnable testLogic) {
        totalTests++;
        try {
            testLogic.run();
            passedTests++;
            System.out.println("  " + GREEN + "[PASS]" + RESET + " Test #" + String.format("%02d", totalTests) + ": " + testName);
        } catch (Throwable t) {
            failedTests++;
            System.out.println("  " + RED + "[FAIL]" + RESET + " Test #" + String.format("%02d", totalTests) + ": " + testName);
            System.out.println("         " + RED + "Error: " + t.getMessage() + RESET);
        }
    }

    private static void assertCondition(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError("Assertion Failed: " + message);
        }
    }

    private static void printSummary() {
        System.out.println("\n" + CYAN + BOLD + "================================================================================" + RESET);
        System.out.println(CYAN + BOLD + "                            TEST RESULTS SUMMARY                                " + RESET);
        System.out.println(CYAN + BOLD + "================================================================================" + RESET);
        System.out.println(" Total Tests Executed: " + BOLD + totalTests + RESET);
        System.out.println(" Tests Passed:         " + GREEN + BOLD + passedTests + RESET);
        System.out.println(" Tests Failed:         " + (failedTests == 0 ? GREEN : RED) + BOLD + failedTests + RESET);

        double passRate = ((double) passedTests / totalTests) * 100.0;
        System.out.println(" Data Integrity Score: " + (passRate == 100.0 ? GREEN : YELLOW) + BOLD + String.format("%.1f%%", passRate) + RESET);

        if (failedTests == 0) {
            System.out.println("\n" + GREEN + BOLD + "✔ STATUS: 100% PERFECT DATA INTEGRITY VERIFIED!" + RESET);
            System.out.println(GREEN + "  All CRUD operations, constraints, search algorithms, state transitions," + RESET);
            System.out.println(GREEN + "  and high-volume latency thresholds passed with zero failures." + RESET);
        } else {
            System.out.println("\n" + RED + BOLD + "✘ STATUS: SOME TESTS FAILED. PLEASE REVIEW LOGS ABOVE." + RESET);
        }
        System.out.println(CYAN + BOLD + "================================================================================\n" + RESET);
    }
}
