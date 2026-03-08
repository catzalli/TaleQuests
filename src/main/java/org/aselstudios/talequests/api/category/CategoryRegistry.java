package org.aselstudios.talequests.api.category;

import java.util.Collection;

/**
 * Registry for API-registered quest categories.
 *
 * <p>Config-based categories (defined in YAML) are not exposed here.
 * Category IDs must not conflict with existing config category IDs.</p>
 */
public interface CategoryRegistry {

    /**
     * Registers a category.
     *
     * @param category the category to register
     * @throws IllegalArgumentException if the category ID conflicts with an
     *         existing config or API category, or if the category is null
     */
    void register(Category category);

    /**
     * Unregisters an API category by ID.
     *
     * @param categoryId the category ID to unregister
     */
    void unregister(String categoryId);

    /**
     * Returns the API category with the given ID, or {@code null} if not found.
     */
    Category getCategory(String categoryId);

    /**
     * Returns an unmodifiable collection of all API-registered categories.
     */
    Collection<Category> getAll();

    /**
     * Returns {@code true} if a category with the given ID is registered in this registry.
     */
    boolean isRegistered(String categoryId);
}
