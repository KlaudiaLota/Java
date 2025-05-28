package org.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Result {
    private final List<Item> allItems;
    private final Map<Item, Integer> itemCounts;
    private final int totalValue;
    private final int totalWeight;

    public Result(Map<Item, Integer> itemCounts, List<Item> allItems, int totalValue, int totalWeight) {
        this.itemCounts = itemCounts != null ? itemCounts : new HashMap<>();
        this.allItems = allItems != null ? allItems : new ArrayList<>();
        this.totalValue = totalValue;
        this.totalWeight = totalWeight;
    }

    public List<Item> getAllItems() {
        return allItems;
    }

    public Map<Item, Integer> getItemCounts() {
        return itemCounts;
    }

    public int getTotalValue() {
        return totalValue;
    }

    public int getTotalWeight() {
        return totalWeight;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Instancja problemu plecakowego:\n");
        for (Item item : allItems) {
            sb.append(item).append("\n");
        }

        sb.append("\nWynik aproksymacyjny (Dantzig 1957):\n");
        for (Map.Entry<Item, Integer> entry : itemCounts.entrySet()) {
            sb.append("Przedmiot ").append(entry.getKey().getId())
                    .append(" - ilość: ").append(entry.getValue()).append("\n");
        }

        sb.append("Suma wag: ").append(totalWeight).append("\n");
        sb.append("Suma wartości: ").append(totalValue).append("\n");

        return sb.toString();
    }
}

