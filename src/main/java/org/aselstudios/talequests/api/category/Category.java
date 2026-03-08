package org.aselstudios.talequests.api.category;

/**
 * An immutable quest category definition registered via the API.
 *
 * <p>Use {@link #builder(String)} to create instances:</p>
 * <pre>{@code
 * Category cat = Category.builder("fishing")
 *     .name("Fishing Quests")
 *     .description("Complete fishing challenges")
 *     .iconId("fishing_rod")
 *     .build();
 * }</pre>
 */
public final class Category {

    private final String id;
    private final String name;
    private final String description;
    private final String iconId;
    private final double weight;
    private final String permission;

    Category(CategoryBuilder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.description = builder.description;
        this.iconId = builder.iconId;
        this.weight = builder.weight;
        this.permission = builder.permission;
    }

    /** Returns the unique category ID. */
    public String getId() { return id; }

    /** Returns the category display name. */
    public String getName() { return name; }

    /** Returns the category description. */
    public String getDescription() { return description; }

    /** Returns the icon ID, or {@code null} if not set. */
    public String getIconId() { return iconId; }

    /** Returns the sort weight (lower values appear first). */
    public double getWeight() { return weight; }

    /** Returns the permission node required to see this category, or {@code null} if open to all. */
    public String getPermission() { return permission; }

    /**
     * Creates a new category builder with the given ID.
     *
     * @param id the unique category ID
     */
    public static CategoryBuilder builder(String id) {
        return new CategoryBuilder(id);
    }

    @Override
    public String toString() {
        return "Category{id=" + id + ", name=" + name + "}";
    }
}
