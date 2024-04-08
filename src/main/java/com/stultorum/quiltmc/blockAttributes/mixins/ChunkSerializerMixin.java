package com.stultorum.quiltmc.blockAttributes.mixins;

import com.stultorum.quiltmc.blockAttributes.mixinfs.IAttributeWorldChunk;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ChunkSerializer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.poi.PointOfInterestStorage;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkSerializer.class)
public abstract class ChunkSerializerMixin {
    @Shadow
    public static ChunkStatus.ChunkType getChunkType(@Nullable NbtCompound nbt) { return null; }

    // I *think* this is when the server serializes the world into packets to send to the client.
    @Inject(method = "serialize", at = @At(value = "TAIL"), cancellable = true)
    private static void attributes$serialize(ServerWorld world, Chunk chunk, CallbackInfoReturnable<NbtCompound> cir) {
        var compound = cir.getReturnValue();
        var attributeChunk = (IAttributeWorldChunk) chunk;
        compound.put("block_attributes", attributeChunk.serializeBlockAttributes());
        cir.setReturnValue(compound);
    }
    
    // interestingly, deserialize seems to only do blockentities on protochunks, while serialize includes them on both. Following blockentities lead-- for now, at least.
    @Inject(method = "deserialize", at = @At(value = "TAIL"))
    private static void attributes$deserialize(ServerWorld world, PointOfInterestStorage poiStorage, ChunkPos pos, NbtCompound nbt, CallbackInfoReturnable<ProtoChunk> cir) {
        if (getChunkType(nbt) != ChunkStatus.ChunkType.PROTOCHUNK) return;
        var attributeChunk = (IAttributeWorldChunk) cir.getReturnValue();
        attributeChunk.deserializeBlockAttributes(nbt.getCompound("block_attributes"));
    }
}
