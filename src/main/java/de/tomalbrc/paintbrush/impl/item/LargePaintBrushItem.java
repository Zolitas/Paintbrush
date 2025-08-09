package de.tomalbrc.paintbrush.impl.item;

import de.tomalbrc.paintbrush.impl.ModItems;
import eu.pb4.polymer.core.api.item.PolymerItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class LargePaintBrushItem extends PaintBrushItem implements PolymerItem {
    public LargePaintBrushItem(DyeColor dyeColor, Properties properties, ResourceLocation model) {
        super(dyeColor, properties.stacksTo(1).component(DataComponents.DAMAGE, 0), model);
    }

    @Override
    public void dyeForEach(ServerLevel level, BlockPos pos, Direction face, ItemStack itemStack, Player player, Function<Boolean, Boolean> o) {
        for (BlockPos ipos : BlockPos.betweenClosed(AABB.ofSize(pos.getCenter(), face.getAxis() == Direction.Axis.X ? 0.5 : 1.5, face.getAxis() == Direction.Axis.Y ? 0.5 : 1.5, face.getAxis() == Direction.Axis.Z ? 0.5 : 1.5))) {
            if (level.isClientSide() || !o.apply(dye(level, ipos, dyeColor))) {
                return;
            }
        }
    }

    @Override
    public int useDelay() {
        return 15;
    }

    @Override
    public @NotNull Component getName(ItemStack itemStack) {
        return Component.literal("Large Paintbrush");
    }

    @Override
    public @NotNull ItemStack asEmpty() {
        return ModItems.LARGE_PAINTBRUSH.getDefaultInstance();
    }
}
