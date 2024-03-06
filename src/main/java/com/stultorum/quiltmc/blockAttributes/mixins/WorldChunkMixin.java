package com.stultorum.quiltmc.blockAttributes.mixins;

import com.stultorum.quiltmc.blockAttributes.mixins.infs.IAttributeWorldChunk;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.data.ChunkData;
import net.minecraft.world.chunk.WorldChunk;
import org.apache.commons.lang3.NotImplementedException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

// TODO pain
@Mixin(WorldChunk.class)
public class WorldChunkMixin implements IAttributeWorldChunk {
    @Inject(method = "runPostProcessing", at = @At("HEAD"))
    public void attributes$runPostProcessing(CallbackInfo ci) {
        // TODO maybe
        throw new NotImplementedException();
    }
    
    @Inject(method = "loadFromPacket", at = @At("HEAD"))
    public void attributes$loadFromPacket(PacketByteBuf buf, NbtCompound nbt, Consumer<ChunkData.BlockEntityVisitor> consumer, CallbackInfo ci) {
        // TODO probably
        throw new NotImplementedException();
    }
}
