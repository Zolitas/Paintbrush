package de.tomalbrc.paintbrush.impl;

import de.tomalbrc.paintbrush.PaintBrushMod;
import de.tomalbrc.paintbrush.impl.entity.Paintball;
import eu.pb4.polymer.core.api.entity.PolymerEntityUtils;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class MobEntities {
    public static final EntityType<Paintball> PAINTBALL = registerEntity("projectile", EntityType.Builder.<Paintball>of(Paintball::new, MobCategory.MISC).sized(0.5f, 0.5f).noSummon());

    private static <T extends Entity> EntityType<T> registerEntity(String str, EntityType.Builder<T> type) {
        var id = ResourceLocation.fromNamespaceAndPath(PaintBrushMod.MODID, str);
        return registerEntity(id, type);
    }

    private static <T extends Entity> EntityType<T> registerEntity(ResourceLocation id, EntityType.Builder<T> type) {
        var res = Registry.register(BuiltInRegistries.ENTITY_TYPE, id, type.build(ResourceKey.create(Registries.ENTITY_TYPE, id)));
        PolymerEntityUtils.registerType(res);

        return res;
    }
}
