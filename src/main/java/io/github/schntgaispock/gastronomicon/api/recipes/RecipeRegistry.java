package io.github.schntgaispock.gastronomicon.api.recipes;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import io.github.schntgaispock.gastronomicon.api.recipes.components.GroupRecipeComponent;
import io.github.schntgaispock.gastronomicon.core.slimefun.recipes.GastroRecipeType;
import lombok.experimental.UtilityClass;

/**
 * Stores information about all the recipes in Gastronomicon
 * 
 * @author SchnTgaiSpock
 */
@UtilityClass
public class RecipeRegistry {

    private static final Map<GastroRecipeType, Set<GastroRecipe>> recipesByType = new HashMap<>();
    private static final Map<GastroRecipeType, Map<Integer, GastroRecipe>> savedRecipes = new HashMap<>();
    private static final Map<ItemStack, Set<GroupRecipeComponent>> groupsByItemStack = new HashMap<>();
    private static final Map<NamespacedKey, GroupRecipeComponent> groupsById = new HashMap<>();

    public static void registerRecipe(@Nonnull GastroRecipe recipe) {
        final GastroRecipeType type = recipe.getRecipeType();
        if (recipesByType.containsKey(type)) {
            recipesByType.get(type).add(recipe);
        } else {
            final Set<GastroRecipe> newSet = new HashSet<>();
            newSet.add(recipe);
            recipesByType.put(type, newSet);
        }
    }

    public static void registerRecipes(@Nonnull GastroRecipe... recipes) {
        for (GastroRecipe recipe : recipes) {
            registerRecipe(recipe);
        }
    }

    private static void registerGroup(@Nonnull GroupRecipeComponent group) {
        for (final ItemStack itemStack : group.getComponent()) {
            if (groupsByItemStack.containsKey(itemStack)) {
                groupsByItemStack.get(itemStack).add(group);
            } else {
                final Set<GroupRecipeComponent> groups = new HashSet<>();
                groups.add(group);
                groupsByItemStack.put(itemStack, groups);
            }
        }

        groupsById.put(group.getId(), group);
    }

    public static void registerGroups(@Nonnull GroupRecipeComponent... groups) {
        for (final GroupRecipeComponent group : groups) {
            registerGroup(group);
        }
    }

    @Nonnull
    public static Set<GroupRecipeComponent> getGroups(@Nonnull ItemStack item) {
        final Set<GroupRecipeComponent> groups = groupsByItemStack.get(item);
        if (groups == null) {
            final Set<GroupRecipeComponent> newGroups = new HashSet<>();
            groupsByItemStack.put(item, newGroups);
            return Collections.unmodifiableSet(newGroups);
        } else {
            return Collections.unmodifiableSet(groups);
        }

    }

    @Nullable
    public static GroupRecipeComponent getGroupById(@Nonnull NamespacedKey name) {
        return groupsById.get(name);
    }

    @Nonnull
    public static Set<GastroRecipe> getRecipes(@Nonnull GastroRecipeType type) {
        return Collections.unmodifiableSet(recipesByType.getOrDefault(type, new HashSet<>()));
    }

    @ParametersAreNonnullByDefault
    public static void saveRecipe(GastroRecipeType type, int hash, GastroRecipe recipe) {
        if (savedRecipes.containsKey(type)) {
            savedRecipes.get(type).put(hash, recipe);
        } else {
            final Map<Integer, GastroRecipe> saved = new HashMap<>();
            saved.put(hash, recipe);
            savedRecipes.put(type, saved);
        }
    }

    @Nullable
    public static GastroRecipe getSavedRecipe(GastroRecipeType type, int hash) {
        return savedRecipes.containsKey(type) ? savedRecipes.get(type).get(hash) : null;
    }

}
