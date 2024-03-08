package com.stultorum.quiltmc.blockAttributes.mixins;

import com.stultorum.quiltmc.blockAttributes.mixins.infs.IAttributeWorldChunk;
import com.stultorum.quiltmc.blunders.events.DataEvent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.data.ChunkData;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.WorldChunk;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static com.stultorum.quiltmc.blockAttributes.mixins.infs.IAttributeWorldChunk.AttributeEventType.Remove;
import static com.stultorum.quiltmc.blockAttributes.mixins.infs.IAttributeWorldChunk.AttributeEventType.Update;

// TODO pain
@Mixin(WorldChunk.class)
public class WorldChunkMixin implements IAttributeWorldChunk {
    @Unique
    private Map<BlockPos, HashMap<Identifier, NbtElement>> attributes = new HashMap<>();
    @Unique
    private Map<AttributeEventType, DataEvent<BlockPos>> totalityEvents = Map.ofEntries(
        Map.entry(Update, new DataEvent()),
        Map.entry(Remove, new DataEvent())
    );
    
    
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

    @Override
    public void setBlockAttributes(@NotNull BlockPos pos, @NotNull HashMap<Identifier, NbtElement> attributes) {
        this.attributes.put(pos, attributes);
    }

    @Override
    public void setBlockAttribute(@NotNull BlockPos pos, @NotNull Identifier id, @NotNull NbtElement nbt) {
        this.attributes.get(pos).put(id, nbt);
    }

    @Override
    public @NotNull HashMap<Identifier, NbtElement> getBlockAttributes(@NotNull BlockPos pos) {
        return this.attributes.computeIfAbsent(pos, (key) -> new HashMap<Identifier, NbtElement>());
    }

    @Override
    public @Nullable NbtElement getBlockAttribute(@NotNull BlockPos pos, @NotNull Identifier id) {
        return getBlockAttributes(pos).get(id);
    }

    @Override
    public void clearBlockAttributes(@NotNull BlockPos pos) {
        setBlockAttributes(pos, new HashMap<>());
    }

    @Override
    public void removeBlockAttribute(@NotNull BlockPos pos, @NotNull Identifier id) {
        this.attributes.get(pos).remove(id);
    }
}
