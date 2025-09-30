package de.tomalbrc.paintbrush.impl.item;

import de.tomalbrc.paintbrush.impl.ModBlocks;
import de.tomalbrc.paintbrush.impl.ModItems;
import de.tomalbrc.paintbrush.impl.PaintBlockCollection;
import eu.pb4.polymer.core.api.item.PolymerItem;
import net.fabricmc.loader.impl.util.StringUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class PaintBrushItem extends Item implements PolymerItem {
    protected final DyeColor dyeColor;
    protected final ResourceLocation model;

    public PaintBrushItem(DyeColor dyeColor, Properties properties, ResourceLocation model) {
        super(properties.stacksTo(1).component(DataComponents.TOOLTIP_DISPLAY, TooltipDisplay.DEFAULT.withHidden(DataComponents.DYED_COLOR, true)).component(DataComponents.DAMAGE, 0).component(DataComponents.DYED_COLOR, new DyedItemColor(dyeColor.getTextureDiffuseColor())));
        this.model = model;
        this.dyeColor = dyeColor;
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext packetContext) {
        return Items.BRUSH;
    }

    @Override
    public @Nullable ResourceLocation getPolymerItemModel(ItemStack stack, PacketContext context) {
        return model;
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
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack itemStack, int remaining) {
        if (livingEntity instanceof Player player) {
            if (this.hitResult(player) instanceof BlockHitResult blockHitResult) {
                if (canUseTick(remaining)) {
                    dyeForEach((ServerLevel) level, blockHitResult.getBlockPos(), blockHitResult.getDirection(), itemStack, player, success -> {
                        if (success) {
                            var hand = livingEntity.getUsedItemHand();
                            var item = livingEntity.getUseItem();
                            item.hurtAndBreak(1, livingEntity, LivingEntity.getSlotForHand(livingEntity.getUsedItemHand()));
                            if (item.isBroken() || item.isEmpty()) {
                                livingEntity.releaseUsingItem();
                                livingEntity.setItemInHand(hand, asEmpty());
                                return false; // cancel
                            }
                        }

                        return true; // cont
                    });
                } else {
                    var state = level.getBlockState(blockHitResult.getBlockPos());
                    var dyed = dyed(state.getBlock(), dyeColor);
                    if (dyed != null && dyed != state.getBlock()) {
                        sendPaintParticles((ServerLevel) level, blockHitResult.getBlockPos(), dyeColor);
                    }
                }
            }
            else
                livingEntity.releaseUsingItem();
        }
    }

    public @NotNull ItemStack asEmpty() {
        return ModItems.PAINTBRUSH.getDefaultInstance();
    }

    public int useDelay() {
        return 5;
    }

    public void dyeForEach(ServerLevel level, BlockPos pos, Direction face, ItemStack itemStack, Player player, Function<Boolean, Boolean> o) {
        o.apply(dye(level, pos, dyeColor));
    }

    public boolean canUseTick(int remaining) {
        return remaining != Item.APPROXIMATELY_INFINITE_USE_DURATION && remaining % useDelay() == 0;
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext useOnContext) {
        if (useOnContext.getPlayer() != null && !useOnContext.getLevel().isClientSide() && canDye(useOnContext.getLevel().getBlockState(useOnContext.getClickedPos()))) {
            useOnContext.getPlayer().startUsingItem(useOnContext.getHand());
            return InteractionResult.SUCCESS_SERVER;
        }

        return InteractionResult.PASS;
    }

    public static boolean canDye(BlockState blockState) {
        return ModBlocks.getPaintBlockCollections().stream().anyMatch(collection -> collection.isPartOfCollection(blockState.getBlock()));
    }

    public static Block dyed(Block block, DyeColor dyeColor) {
        Optional<PaintBlockCollection> optionalCollection = ModBlocks
                .getPaintBlockCollections()
                .stream()
                .filter(collection -> collection.isPartOfCollection(block))
                .findFirst();

        return optionalCollection.map(paintBlockCollection -> paintBlockCollection.getPaintedBlock(dyeColor)).orElse(null);
    }

    public static boolean dye(ServerLevel level, BlockPos pos, DyeColor dyeColor) {
        var state = level.getBlockState(pos);

        if (canDye(state)) {
            var block = dyed(state.getBlock(), dyeColor);
            var success = level.setBlock(pos, block.withPropertiesOf(state), Block.UPDATE_CLIENTS);
            if (success) {
                sendPaintParticles(level, pos, dyeColor);
            }
            return success;
        }

        return false;
    }

    public static void sendPaintParticles(ServerLevel level, BlockPos pos, DyeColor dyeColor) {
        var p = pos.getCenter();
        level.sendParticles(new DustParticleOptions(dyeColor.getTextureDiffuseColor(), 1f), p.x, p.y, p.z, 20, 0.4, 0.4, 0.4, 0);
    }

    protected HitResult hitResult(Player player) {
        return ProjectileUtil.getHitResultOnViewVector(player, EntitySelector.CAN_BE_PICKED, player.blockInteractionRange());
    }

    @Override
    public @NotNull Component getName(ItemStack itemStack) {
        return Component.literal("Paintbrush");
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, TooltipDisplay tooltipDisplay, Consumer<Component> consumer, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, tooltipContext, tooltipDisplay, consumer, tooltipFlag);

        consumer.accept(Component.literal(StringUtil.capitalize(dyeColor.getName().replace("_", " "))).withColor(dyeColor.getTextColor()));
    }
}
