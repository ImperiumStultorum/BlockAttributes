package com.stultorum.quiltmc.blockAttributes.mixins;

import com.stultorum.quiltmc.blockAttributes.mixinfs.IAttributeWorldChunk;
import com.stultorum.quiltmc.blunders.events.PreconditionalEvent;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.gen.chunk.BlendingData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

import static com.stultorum.quiltmc.blockAttributes.mixinfs.IAttributeWorldChunk.AttributeEventType.Remove;
import static com.stultorum.quiltmc.blockAttributes.mixinfs.IAttributeWorldChunk.AttributeEventType.Update;

// TODO pain
@Mixin(Chunk.class)
public class ChunkMixin implements IAttributeWorldChunk {
    @Unique // FINAL FOR ALL INTENTS AND PURPOSES
    private Map<BlockPos, HashMap<Identifier, NbtElement>> attributes;
    @Unique // FINAL FOR ALL INTENTS AND PURPOSES
    private Map<AttributeEventType, PreconditionalEvent<BlockPos>> attributeEvents;

    @Override @Unique @SuppressWarnings("AddedMixinMembersNamePattern")
    public void setBlockAttributes(@NotNull BlockPos pos, @NotNull HashMap<Identifier, NbtElement> attributes) {
        this.attributes.put(pos, attributes);
    }

    @Override @Unique @SuppressWarnings("AddedMixinMembersNamePattern")
    public void setBlockAttribute(@NotNull BlockPos pos, @NotNull Identifier id, @NotNull NbtElement nbt) {
        this.attributes.get(pos).put(id, nbt);
    }

    @Override @Unique @SuppressWarnings("AddedMixinMembersNamePattern")
    public @NotNull HashMap<Identifier, NbtElement> getBlockAttributes(@NotNull BlockPos pos) {
        return this.attributes.computeIfAbsent(pos, (key) -> new HashMap<>());
    }

    @Override @Unique @SuppressWarnings("AddedMixinMembersNamePattern")
    public @Nullable NbtElement getBlockAttribute(@NotNull BlockPos pos, @NotNull Identifier id) {
        return getBlockAttributes(pos).get(id);
    }

    @Override @Unique @SuppressWarnings("AddedMixinMembersNamePattern")
    public void clearBlockAttributes(@NotNull BlockPos pos) {
        setBlockAttributes(pos, new HashMap<>());
    }

    @Override @Unique @SuppressWarnings("AddedMixinMembersNamePattern")
    public void removeBlockAttribute(@NotNull BlockPos pos, @NotNull Identifier id) {
        this.attributes.get(pos).remove(id);
    }
    
    @Override @Unique @SuppressWarnings("AddedMixinMembersNamePattern")
    public void addAttributeListener(AttributeEventType type, BlockPos pos, Function0<Unit> callback) {
        attributeEvents.get(type).addCallback(pos, callback);
    }

    @Override @Unique @SuppressWarnings("AddedMixinMembersNamePattern")
    public void addAttributeListener(AttributeEventType type, Function1<BlockPos, Unit> callback) {
        attributeEvents.get(type).addCallback((pos) -> true, callback);
    }

    @Override @Unique @SuppressWarnings("AddedMixinMembersNamePattern")
    public void addAttributeListener(AttributeEventType type, Function1<BlockPos, Boolean> condition, Function1<BlockPos, Unit> callback) {
        attributeEvents.get(type).addCallback(condition, callback);
    }

    @Override @Unique @SuppressWarnings("AddedMixinMembersNamePattern")
    public void removeAttributeListener(AttributeEventType type, Function0<Unit> callback) {
        attributeEvents.get(type).removeCallback(callback);
    }

    @Override @Unique @SuppressWarnings("AddedMixinMembersNamePattern")
    public void removeAttributeListener(AttributeEventType type, Function1<BlockPos, Unit> callback) {
        attributeEvents.get(type).removeCallback(callback);
    }

    @Override @Unique @SuppressWarnings("AddedMixinMembersNamePattern")
    public NbtElement serializeBlockAttributes() {
        var list = new NbtList();
        attributes.forEach((pos, attributes) -> {
            var attributeCompound = new NbtCompound();
            attributes.forEach((k, v) -> attributeCompound.put(k.toString(), v));
            var blockCompound = new NbtCompound();
            blockCompound.put("pos", NbtHelper.fromBlockPos(pos));
            blockCompound.put("attributes", attributeCompound);
            list.add(blockCompound);
        });
        return list;
    }

    @Override @Unique @SuppressWarnings("AddedMixinMembersNamePattern")
    public void deserializeBlockAttributes(NbtElement nbt) {
        if (!(nbt instanceof NbtList list)) throw new ClassCastException();
        list.forEach(ele -> {
            if (!(ele instanceof NbtCompound compound)) throw new ClassCastException();
            var attributeCompound = compound.getCompound("attributes");
            var attributes = new HashMap<Identifier, NbtElement>();
            attributeCompound.getKeys().forEach(attribute -> attributes.put(new Identifier(attribute), attributeCompound.get(attribute)));
            this.attributes.put(NbtHelper.toBlockPos(compound.getCompound("pos")), attributes);
        });
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    public void attributes$ctor(ChunkPos pos, UpgradeData upgradeData, HeightLimitView heightLimitView, Registry<Biome> biomeRegistry, long inhabitedTime, ChunkSection[] sectionArrayInitializer, BlendingData blendingData, CallbackInfo ci) {
        this.attributes = new HashMap<>();
        this.attributeEvents = Map.of(
            Update, new PreconditionalEvent<>(),
            Remove, new PreconditionalEvent<>()
        );
    }
}