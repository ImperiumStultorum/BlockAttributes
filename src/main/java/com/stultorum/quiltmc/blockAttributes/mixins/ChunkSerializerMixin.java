package com.stultorum.quiltmc.blockAttributes.mixins;

import com.stultorum.quiltmc.blockAttributes.mixinfs.IAttributeWorldChunk;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ChunkSerializer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.WrapperProtoChunk;
import net.minecraft.world.poi.PointOfInterestStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkSerializer.class)
public abstract class ChunkSerializerMixin {
    @Inject(method = "serialize", at = @At(value = "RETURN"))
    private static void attributes$serialize(ServerWorld world, Chunk chunk, CallbackInfoReturnable<NbtCompound> cir) {
        var compound = cir.getReturnValue();
        var attributeChunk = (IAttributeWorldChunk) chunk;
        compound.put("block_attributes", attributeChunk.serializeBlockAttributes());
    }
    
    @Inject(method = "deserialize", at = @At(value = "RETURN"))
    private static void attributes$deserialize(ServerWorld world, PointOfInterestStorage poiStorage, ChunkPos pos, NbtCompound nbt, CallbackInfoReturnable<ProtoChunk> cir) {
        var ret = cir.getReturnValue();
        var attributeChunk = (IAttributeWorldChunk) (ret instanceof WrapperProtoChunk wrapper ? wrapper.getWrappedChunk() : ret);
        attributeChunk.deserializeBlockAttributes(nbt.getList("block_attributes", NbtElement.COMPOUND_TYPE));
    }
}
