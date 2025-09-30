package de.tomalbrc.paintbrush.mixin;

import de.tomalbrc.paintbrush.impl.ModBlocks;
import de.tomalbrc.paintbrush.impl.PaintBlockCollection;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(AxeItem.class)
public class AxeCustomScrapingMixin {
    @Inject(method = "evaluateNewBlockState", at = @At("RETURN"), cancellable = true)
    private void addCustomAxeInteraction(Level level, BlockPos pos, Player player, BlockState state, CallbackInfoReturnable<Optional<BlockState>> cir) {
        if (cir.getReturnValue().isEmpty()) {
            Optional<BlockState> customResult = getCustomBlockState(state);
            if (customResult.isPresent()) {
                //todo: sound + particles dont work
                level.playSound(player, pos, SoundEvents.AXE_SCRAPE, SoundSource.BLOCKS, 1.0f, 1.0f);
                level.levelEvent(player, 3005, pos, Block.getId(state));
                cir.setReturnValue(customResult);
            }
        }
    }

    @Unique
    private Optional<BlockState> getCustomBlockState(BlockState state) {
        Block block = state.getBlock();

        Optional<PaintBlockCollection> collectionOptional = ModBlocks
                .getPaintBlockCollections()
                .stream()
                .filter(collection ->
                        collection.isPaintedBlock(block)
                                && collection.getOriginalBlock().defaultBlockState() != block.defaultBlockState()
                                && !collection.isVanillaCollection()
                )
                .findFirst();

        if (collectionOptional.isPresent()) {
            PaintBlockCollection collection = collectionOptional.get();
            return Optional.of(collection.getOriginalBlock().defaultBlockState());
        }
        return Optional.empty();
    }
}
