package de.tomalbrc.paintbrush.impl.item;

import eu.pb4.polymer.core.api.item.PolymerItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class LargePaintBrushItem extends PaintBrushItem implements PolymerItem {
    public LargePaintBrushItem(DyeColor dyeColor, Properties properties) {
        super(dyeColor, properties.stacksTo(1).component(DataComponents.MAX_DAMAGE, 100));
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext useOnContext) {
        Map<DyeColor, Block> map;
        if (useOnContext.getPlayer() != null) {
            for (BlockPos ipos : BlockPos.betweenClosed(AABB.ofSize(useOnContext.getClickedPos().getCenter(), useOnContext.getClickedFace().getAxis() == Direction.Axis.X ? 0.5 : 1.5, useOnContext.getClickedFace().getAxis() == Direction.Axis.Y ? 0.5 : 1.5, useOnContext.getClickedFace().getAxis() == Direction.Axis.Z ? 0.5 : 1.5))) {
                if (!useOnContext.getLevel().isClientSide() && dye((ServerLevel) useOnContext.getLevel(), ipos)) {
                    var itemStack = useOnContext.getItemInHand();
                    itemStack.hurtAndBreak(1, useOnContext.getPlayer(), LivingEntity.getSlotForHand(useOnContext.getHand()));
                    if (itemStack.isBroken())
                        return InteractionResult.CONSUME;
                }
            }
        }

        return InteractionResult.CONSUME;
    }
}
