package de.tomalbrc.paintbrush.impl.item;

import de.tomalbrc.paintbrush.impl.ModBlocks;
import eu.pb4.polymer.core.api.item.PolymerItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.Map;

public class PaintBrushItem extends Item implements PolymerItem {
    protected final DyeColor dyeColor;

    public PaintBrushItem(DyeColor dyeColor, Properties properties) {
        super(properties.stacksTo(1).component(DataComponents.MAX_DAMAGE, 100));
        this.dyeColor = dyeColor;
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext packetContext) {
        return Items.FIREWORK_STAR;
    }

    @Override
    public @Nullable ResourceLocation getPolymerItemModel(ItemStack stack, PacketContext context) {
        return Items.BRUSH.components().get(DataComponents.ITEM_MODEL);
    }

    @Override
    public ItemStack getPolymerItemStack(ItemStack itemStack, TooltipFlag tooltipType, PacketContext context) {
        var stack = PolymerItem.super.getPolymerItemStack(itemStack, tooltipType, context);
        stack.set(DataComponents.DYED_COLOR, new DyedItemColor(dyeColor.getTextureDiffuseColor()));
        return stack;
    }

    @Override
    public @NotNull ItemUseAnimation getUseAnimation(ItemStack itemStack) {
        return ItemUseAnimation.BRUSH;
    }

    @Override
    public int getUseDuration(ItemStack itemStack, LivingEntity livingEntity) {
        return Item.APPROXIMATELY_INFINITE_USE_DURATION;
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext useOnContext) {
        Map<DyeColor, Block> map;
        if (useOnContext.getPlayer() != null) {
            if (!useOnContext.getLevel().isClientSide() && dye((ServerLevel) useOnContext.getLevel(), useOnContext.getClickedPos())) {
                var itemStack = useOnContext.getItemInHand();
                itemStack.hurtAndBreak(1, useOnContext.getPlayer(), LivingEntity.getSlotForHand(useOnContext.getHand()));
                if (itemStack.isBroken())
                    return InteractionResult.CONSUME;
            }
        }

        return InteractionResult.CONSUME;
    }

    public boolean dye(ServerLevel level, BlockPos pos) {
        var state = level.getBlockState(pos);

        Map<DyeColor, Block> map;
        if ((map = ModBlocks.BLOCK_COLOR_MAP.get(state.getBlock())) != null) {
            var block = map.get(dyeColor);
            var success = level.setBlock(pos, block.withPropertiesOf(state), Block.UPDATE_CLIENTS);
            if (success) {
                var p = pos.getCenter();
                level.sendParticles(new DustParticleOptions(dyeColor.getTextureDiffuseColor(), 1f), p.x, p.y, p.z, 20, 0.4, 0.4,0.4, 0);
            }
            return success;
        }
        return false;
    }
}
