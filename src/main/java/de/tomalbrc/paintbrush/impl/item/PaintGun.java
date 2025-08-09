package de.tomalbrc.paintbrush.impl.item;

import de.tomalbrc.paintbrush.impl.ModItems;
import de.tomalbrc.paintbrush.impl.entity.Paintball;
import eu.pb4.polymer.core.api.item.PolymerItem;
import net.fabricmc.loader.impl.util.StringUtil;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.List;
import java.util.function.Predicate;

public class PaintGun extends ProjectileWeaponItem implements PolymerItem {
    final private DyeColor color;
    final private ResourceLocation model;

    public PaintGun(DyeColor color, Properties properties, ResourceLocation model) {
        super(properties.durability(64*3).component(DataComponents.DYED_COLOR, new DyedItemColor(color.getTextureDiffuseColor())));
        this.color = color;
        this.model = model;
    }

    @Override
    public @NotNull Component getName(ItemStack itemStack) {
        return Component.literal(StringUtil.capitalize(color.getName().replace("_", " ")) + " Paintgun");
    }

    @Override
    public @Nullable ResourceLocation getPolymerItemModel(ItemStack stack, PacketContext context) {
        return this.model;
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        return Items.BOW;
    }

    @Override
    public int getDefaultProjectileRange() {
        return 10;
    }

    @Override
    protected void shootProjectile(LivingEntity livingEntity, Projectile projectile, int i, float f, float g, float h, @Nullable LivingEntity livingEntity2) {
        projectile.shootFromRotation(livingEntity, livingEntity.getXRot(), livingEntity.getYRot() + h, 0.0F, f, g);
    }


    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack itemStack, int remaining) {
        if (livingEntity instanceof Player player) {
            var projectileItem = DyeItem.byColor(color).getDefaultInstance();
            if (!projectileItem.isEmpty() && !projectileItem.is(ItemTags.ARROWS)) {
                int timeUsed = this.getUseDuration(itemStack, livingEntity) - remaining;
                if (timeUsed == 0 || timeUsed % 2 != 0)
                    return;

                float f = 1f;
                if (level instanceof ServerLevel serverLevel) {
                    var hand = player.getUsedItemHand();

                    createProjectile(serverLevel, player, projectileItem);
                    itemStack.hurtAndBreak(1, livingEntity, LivingEntity.getSlotForHand(livingEntity.getUsedItemHand()));

                    level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SNOWBALL_THROW, SoundSource.PLAYERS, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + f * 0.5F);
                    player.awardStat(Stats.ITEM_USED.get(this));

                    if (itemStack.isBroken() || itemStack.isEmpty()) {
                        livingEntity.releaseUsingItem();
                        livingEntity.setItemInHand(hand, ModItems.EMPTY_PAINT_GUN.getDefaultInstance());
                    }
                }
            }
        }
    }

    @Override
    protected void shoot(ServerLevel serverLevel, LivingEntity livingEntity, InteractionHand interactionHand, ItemStack itemStack, List<ItemStack> list, float f, float g, boolean bl, @Nullable LivingEntity livingEntity2) {

    }

    @Override
    public @NotNull Predicate<ItemStack> getSupportedHeldProjectiles() {
        return x -> x.getItem() instanceof DyeItem;
    }

    @Override
    public @NotNull Predicate<ItemStack> getAllSupportedProjectiles() {
        return getSupportedHeldProjectiles();
    }

    @Override
    public int getUseDuration(ItemStack itemStack, LivingEntity livingEntity) {
        return Item.APPROXIMATELY_INFINITE_USE_DURATION;
    }

    @Override
    public @NotNull ItemUseAnimation getUseAnimation(ItemStack itemStack) {
        return ItemUseAnimation.CROSSBOW;
    }

    @Override
    public @NotNull InteractionResult use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        boolean bl = !player.getProjectile(itemStack).isEmpty();
        if (!player.hasInfiniteMaterials() && !bl) {
            return InteractionResult.FAIL;
        } else {
            player.startUsingItem(interactionHand);
            return InteractionResult.CONSUME;
        }
    }

    public static void createProjectile(Level level, LivingEntity livingEntity, ItemStack itemStack) {
        var paintball = new Paintball((ServerLevel) level, livingEntity, itemStack);
        paintball.shootFromRotation(livingEntity, livingEntity.getXRot(), livingEntity.getYRot(), 1f, 1.5f, 4f);
        paintball.setColor(((DyeItem)itemStack.getItem()).getDyeColor());
        var pos = livingEntity.getEyePosition();
        var right = livingEntity.getForward().normalize().cross(Direction.Axis.Y.getPositive().getUnitVec3()).normalize();
        right = right.scale(livingEntity.getUsedItemHand()==InteractionHand.MAIN_HAND ? 0.25 : -0.25);

        paintball.setPos(pos.subtract(0,0.2,0).add(right).add(livingEntity.getForward().normalize().scale(0.5)));
        level.addFreshEntity(paintball);
    }
}
