package org.example;

import java.util.*;
import java.util.stream.IntStream;

public class Problem {
    private final List<Item> items;

    public Problem(int numberOfItems, int seed, int min, int max) {
        items = new ArrayList<>();
        Random random = new Random(seed);
        for (int i = 0; i < numberOfItems; i++) {
            int value = random.nextInt(max - min + 1) + min;
            int weight = random.nextInt(max - min + 1) + min;
            items.add(new Item(i + 1, value, weight));
        }
    }

    public Problem(List<Item> items) {
        this.items = new ArrayList<>(items);
    }

    public List<Item> getItems() {
        return items;
    }

    public Result solve(int capacity) {
        List<Item> sorted = new ArrayList<>(items);
        sorted.sort((a, b) -> Double.compare(
                (double) b.getValue() / b.getWeight(),
                (double) a.getValue() / a.getWeight()));

        Map<Item, Integer> itemCounts = new HashMap<>();
        int remainingCapacity = capacity;
        int totalWeight = 0, totalValue = 0;

        for (Item item : sorted) {
            int maxCount = remainingCapacity / item.getWeight();
            if (maxCount > 0) {
                itemCounts.put(item, maxCount);
                int weightAdded = maxCount * item.getWeight();
                int valueAdded = maxCount * item.getValue();

                totalWeight += weightAdded;
                totalValue += valueAdded;
                remainingCapacity -= weightAdded;

                if (remainingCapacity == 0) break;
            }
        }

        return new Result(itemCounts, sorted, totalValue, totalWeight);
    }
}



