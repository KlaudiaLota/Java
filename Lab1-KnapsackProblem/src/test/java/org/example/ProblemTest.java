package org.example;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
class KnapsackProblemTest {

    @Test
    public void testKnapsackProblem() {
        Problem problem = new Problem(10, 1, 1, 10);
        Result result = problem.solve(10);
        assertNotNull(result);
        Map<Item, Integer> itemCounts = result.getItemCounts();
        boolean anyItemFits = itemCounts.keySet().stream()
                .anyMatch(item -> item.getWeight() <= 10);

        if (anyItemFits) {
            assertFalse(itemCounts.isEmpty());
        }
    }

    @Test
    public void testInstanceSize(){
        Problem problem = new Problem(10, 1, 1, 10);
        List<Item> items = problem.getItems();
        assertEquals(10, items.size());
    }

    @Test
    public void testTooBigItems() {
        List<Item> items = new ArrayList<>();
        items.add(new Item(1, 1,10));
        items.add(new Item(2, 2,10));
        items.add(new Item(3, 3,10));
        Problem problem = new Problem(10, 1, 1, 10);
        Result result = problem.solve(1);
        Map<Item, Integer> itemCounts = result.getItemCounts();
        assertFalse(itemCounts.isEmpty());
    }

    @Test
    public void testItemValueAndItemWeightIsInBounds() {
        int lowerBound = 1;
        int upperBound = 10;
        Problem problem = new Problem(10, 0, lowerBound, upperBound);
        Map<Item, Integer> itemCounts = problem.solve(10).getItemCounts();
        for (Item item : itemCounts.keySet()) {
            assertTrue(item.getWeight() >= lowerBound && item.getWeight() <= upperBound,
                    "Waga przedmiotu poza zakresem");
            assertTrue(item.getValue() >= lowerBound && item.getValue() <= upperBound,
                    "Wartość przedmiotu poza zakresem");
        }
    }

    @Test
    public void testExampleInstance(){
        List<Item> items = new ArrayList<>();
        items.add(new Item(1, 6,3));
        items.add(new Item(2, 2,2));
        Problem problem = new Problem(items);
        Result result = problem.solve(20);

        Map<Item, Integer> expectedItemCounts = new HashMap<>();
        expectedItemCounts.put(items.get(0), 3); // 3 sztuki przedmiotu 1 (6*3=18 wagi)
        expectedItemCounts.put(items.get(1), 1); // 1 sztuka przedmiotu 2 (2 wagi)

        int expectedWeight = 20;
        int expectedValue = 11;
        assertEquals(expectedItemCounts, result.getItemCounts());
        assertEquals(expectedValue, result.getTotalValue());
        assertEquals(expectedWeight, result.getTotalWeight());
    }

    @Test
    public void maxWeightIsInBounds() {
        Problem problem = new Problem(10, 1, 1, 10);
        Result result = problem.solve(10);
        assertTrue(result.getTotalWeight() <= 10);
    }

}