package org.aselstudios.talequests.apiimpl;

import org.aselstudios.talequests.api.category.Category;
import org.aselstudios.talequests.api.category.CategoryRegistry;
import org.aselstudios.talequests.manager.ConfigManager;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class CategoryRegistryImpl implements CategoryRegistry {

    private final ConcurrentHashMap<String, Category> categories = new ConcurrentHashMap<>();
    private final Object registryLock = new Object();

    @Override
    public void register(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("category must not be null");
        }
        String id = category.getId();

        synchronized (registryLock) {
            if (ConfigManager.getCategories().containsKey(id)) {
                throw new IllegalArgumentException(
                        "Category ID '" + id + "' conflicts with an existing config category");
            }
            if (categories.putIfAbsent(id, category) != null) {
                throw new IllegalArgumentException(
                        "Category ID '" + id + "' is already registered in the API registry");
            }
        }
    }

    @Override
    public void unregister(String categoryId) {
        if (categoryId != null) {
            synchronized (registryLock) {
                categories.remove(categoryId);
            }
        }
    }

    @Override
    public Category getCategory(String categoryId) {
        if (categoryId == null) return null;
        return categories.get(categoryId);
    }

    @Override
    public Collection<Category> getAll() {
        return List.copyOf(categories.values());
    }

    @Override
    public boolean isRegistered(String categoryId) {
        return categoryId != null && categories.containsKey(categoryId);
    }
}
