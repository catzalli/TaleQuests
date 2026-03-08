package org.aselstudios.talequests.api.category;

/**
 * Fluent builder for creating {@link Category} instances.
 *
 * @see Category#builder(String)
 */
public final class CategoryBuilder {

    final String id;
    String name;
    String description = "";
    String iconId;
    double weight = 0.0;
    String permission;

    CategoryBuilder(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("category id must not be null or blank");
        }
        this.id = id;
    }

    /** Sets the category display name. Required. */
    public CategoryBuilder name(String name) {
        this.name = name;
        return this;
    }

    /** Sets the category description. */
    public CategoryBuilder description(String description) {
        this.description = description != null ? description : "";
        return this;
    }

    /** Sets the icon ID used in the UI. */
    public CategoryBuilder iconId(String iconId) {
        this.iconId = iconId;
        return this;
    }

    /** Sets the sort weight (lower values appear first). Default is 0. */
    public CategoryBuilder weight(double weight) {
        this.weight = weight;
        return this;
    }

    /** Sets the permission node required to see this category. {@code null} = open to all. */
    public CategoryBuilder permission(String permission) {
        this.permission = permission;
        return this;
    }

    /**
     * Builds and returns an immutable {@link Category} instance.
     *
     * @throws IllegalStateException if the name is not set
     */
    public Category build() {
        if (name == null || name.isBlank()) {
            throw new IllegalStateException("category name is required (id=" + id + ")");
        }
        return new Category(this);
    }
}
