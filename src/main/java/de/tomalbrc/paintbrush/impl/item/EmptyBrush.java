package de.tomalbrc.paintbrush.impl.item;

import eu.pb4.polymer.core.api.item.SimplePolymerItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

public class EmptyBrush extends SimplePolymerItem {
    final private String name;
    public EmptyBrush(Properties settings, String name) {
        super(settings, Items.PAPER, true);
        this.name = name;
    }

    @Override
    public @NotNull Component getName(ItemStack itemStack) {
        return Component.literal(this.name);
    }
}
