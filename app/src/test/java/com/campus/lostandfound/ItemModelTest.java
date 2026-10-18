package com.campus.lostandfound;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.campus.lostandfound.model.Item;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit Tests verifying business logic and state transitions of the Item entity.
 */
public class ItemModelTest {

    private Item sampleItem;

    @Before
    public void setUp() {
        sampleItem = new Item(
                1,
                "Blue Umbrella",
                "Left near auditorium entrance gate",
                "Other Belongings",
                Item.TYPE_LOST,
                "Auditorium Block A",
                "2026-10-17",
                "Rahul Sharma",
                "9876543210",
                Item.STATUS_ACTIVE,
                System.currentTimeMillis()
        );
    }

    @Test
    public void testItemInitialState() {
        assertEquals("Blue Umbrella", sampleItem.getTitle());
        assertEquals("Auditorium Block A", sampleItem.getLocation());
        assertTrue(sampleItem.isLost());
        assertFalse(sampleItem.isClaimed());
        assertEquals(Item.STATUS_ACTIVE, sampleItem.getStatus());
    }

    @Test
    public void testMarkAsClaimedState() {
        sampleItem.setStatus(Item.STATUS_CLAIMED);
        assertTrue(sampleItem.isClaimed());
        assertEquals("CLAIMED", sampleItem.getStatus());
    }

    @Test
    public void testItemTypeFound() {
        sampleItem.setType(Item.TYPE_FOUND);
        assertFalse(sampleItem.isLost());
        assertEquals(Item.TYPE_FOUND, sampleItem.getType());
    }

    @Test
    public void testUpdateProperties() {
        sampleItem.setTitle("Updated Red Umbrella");
        sampleItem.setLocation("Library Lawn");
        assertEquals("Updated Red Umbrella", sampleItem.getTitle());
        assertEquals("Library Lawn", sampleItem.getLocation());
    }
}
