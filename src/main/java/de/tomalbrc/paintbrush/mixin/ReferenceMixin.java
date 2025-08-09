package de.tomalbrc.paintbrush.mixin;

import de.tomalbrc.paintbrush.util.Data;
import it.unimi.dsi.fastutil.objects.ReferenceArraySet;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.Set;

@Mixin(Holder.Reference.class)
public abstract class ReferenceMixin<T> implements Holder<T> {
    @Shadow @Nullable private T value;

    @Shadow @Nullable private Set<TagKey<T>> tags;

    @Inject(method = "bindTags", at = @At("HEAD"), cancellable = true)
    private void pb$bind(Collection<TagKey<T>> collection, CallbackInfo ci) {
        Reference x = Data.TAGS.get(value);
        if (x != null) {
            MappedRegistry.TagSet<Block> set = ((MappedRegistryAccessor)(Object)BuiltInRegistries.BLOCK).getAllTags();
            var sett = new ReferenceArraySet<TagKey<T>>();
            set.forEach((tagKey,blockNamed) -> {
                for (Holder<Block> holder : blockNamed) {
                    if (holder == x) {
                        sett.add((TagKey<T>) tagKey);
                    }
                }
            });

            this.tags = Set.copyOf(sett);
            ci.cancel();
        }
    }
}
