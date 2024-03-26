package com.stultorum.quiltmc.blockAttributes.mixins;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.ChunkSerializer;
import net.minecraft.world.chunk.Chunk;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkSerializer.class)
public class ChunkSerializerMixin {
    @Shadow
    @Final
    private static Logger LOGGER;

    @Inject(method = "serialize", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/ChunkSerializer;appendTickNbt(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/nbt/NbtCompound;Lnet/minecraft/world/chunk/Chunk$TicksToSave;)V", shift = At.Shift.BEFORE))
    private static void serializeAttributes$serialize(ServerWorld world, Chunk chunk, CallbackInfoReturnable<NbtCompound> cir) {
        LOGGER.warn("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    }
}
