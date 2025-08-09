package de.tomalbrc.paintbrush.impl.entity;

import de.tomalbrc.paintbrush.impl.item.PaintBrushItem;
import eu.pb4.polymer.core.api.entity.PolymerEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import xyz.nucleoid.packettweaker.PacketContext;

public class Paintball extends Snowball implements PolymerEntity {
    DyeColor color;

    public Paintball(EntityType<Paintball> entityType, Level level) {
        super(entityType, level);
    }

    public Paintball(Level level, double x, double y, double z, ItemStack itemStack, DyeColor color) {
        super(level, x, y, z, itemStack);
        this.color = color;
    }

    public Paintball(ServerLevel level, LivingEntity livingEntity, ItemStack itemStack) {
        super(level, livingEntity, itemStack);
    }

    @Override
    public EntityType<?> getPolymerEntityType(PacketContext context) {
        return EntityType.SNOWBALL;
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        return false;
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);

        if (hitResult instanceof BlockHitResult blockHitResult) {
            var pos = blockHitResult.getBlockPos();
            var face = blockHitResult.getDirection();
            for (BlockPos ipos : BlockPos.betweenClosed(AABB.ofSize(pos.getCenter(), face.getAxis() == Direction.Axis.X ? 0.5 : 1.5, face.getAxis() == Direction.Axis.Y ? 0.5 : 1.5, face.getAxis() == Direction.Axis.Z ? 0.5 : 1.5))) {
                PaintBrushItem.dye((ServerLevel) level(), ipos, color);
            }
        }
    }

    public void setColor(DyeColor dyeColor) {
        this.color = dyeColor;
    }
}
