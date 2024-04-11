package com.stultorum.quiltmc.blockAttributes.mixins;

import com.stultorum.quiltmc.blockAttributes.BlockAttributeMod;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldChunk.class)
public abstract class WorldChunkMixin extends ChunkMixin {
    @Inject(method = "<init>(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/world/chunk/ProtoChunk;Lnet/minecraft/world/chunk/WorldChunk$PostLoadProcessor;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/WorldChunk;setStructureStarts(Ljava/util/Map;)V", shift = At.Shift.BEFORE))
    public void attributes$protoChunkCtor(ServerWorld world, ProtoChunk protoChunk, WorldChunk.PostLoadProcessor loadToWorldConsumer, CallbackInfo ci) {
        var attributes = castProtoChunk(protoChunk)._rawGetAttributes();
        if (attributes != null) this._rawSetAttributes(attributes);
    }
    
    @Inject(method = "setBlockState", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;onStateReplaced(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Z)V", shift = At.Shift.AFTER))
    public void attributes$setBlockState(BlockPos pos, BlockState state, boolean moved, CallbackInfoReturnable<BlockState> cir) {
        if (!this.attributes.containsKey(pos)) return;
        this.clearBlockAttributes(pos);
        if (moved) BlockAttributeMod.Companion.getLogger$blockAttributes().warn("Removed block attributes from moving block");
    }
    
    // This method exists for IDE pleasing only (so it won't say attributes$protoChunkCtor has unreachable code)
    @Unique
    private static ChunkMixin castProtoChunk(ProtoChunk protoChunk) {
        return (ChunkMixin)(Object) protoChunk;
    }
}
