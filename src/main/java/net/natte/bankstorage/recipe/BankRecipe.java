package net.natte.bankstorage.recipe;

import java.util.Optional;

import com.google.gson.JsonObject;

import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.natte.bankstorage.item.BankItem;

public class BankRecipe extends ShapedRecipe {

    public BankRecipe(ShapedRecipe recipe) {
        super(recipe.getGroup(), recipe.getCategory(), recipe.getWidth(), recipe.getHeight(),
                recipe.getIngredients(), recipe.getResult(null));
    }

    @Override
    public ItemStack craft(RecipeInputInventory recipeInputInventory, DynamicRegistryManager dynamicRegistryManager) {
        Optional<ItemStack> maybeBankItemStack = recipeInputInventory.getInputStacks().stream()
                .filter(stack -> (stack.getItem() instanceof BankItem)).findFirst();

        if (maybeBankItemStack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack result = super.craft(recipeInputInventory, dynamicRegistryManager);
        result.setNbt(maybeBankItemStack.get().getNbt());

        return result;
    }

    public static class Serializer extends ShapedRecipe.Serializer {
        @Override
        public ShapedRecipe read(PacketByteBuf packetByteBuf) {
            return new BankRecipe(super.read(packetByteBuf));
        }
    }

}
