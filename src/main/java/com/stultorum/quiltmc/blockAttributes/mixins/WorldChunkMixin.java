package com.stultorum.quiltmc.blockAttributes.mixins;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldChunk.class)
public abstract class WorldChunkMixin extends ChunkMixin {
    @Inject(method = "<init>(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/world/chunk/ProtoChunk;Lnet/minecraft/world/chunk/WorldChunk$PostLoadProcessor;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/WorldChunk;setStructureStarts(Ljava/util/Map;)V", shift = At.Shift.BEFORE))
    public void attributes$protoChunkCtor(ServerWorld world, ProtoChunk protoChunk, WorldChunk.PostLoadProcessor loadToWorldConsumer, CallbackInfo ci) {
        var attributes = attributes$castProtoChunk(protoChunk)._rawGetAttributes();
        if (attributes != null) this._rawSetAttributes(attributes);
    }
    
    // This method exists for IDE pleasing only (so it won't say attributes$protoChunkCtor has unreachable code)
    @Unique
    private static ChunkMixin attributes$castProtoChunk(ProtoChunk protoChunk) {
        return (ChunkMixin)(Object) protoChunk;
    }
}
