package com.stultorum.quiltmc.blockAttributes.mixinfs;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface IAttributeWorldChunk {
    enum AttributeEventType {
        Update, Remove
    }
    
    void setBlockAttributes(@NotNull BlockPos pos, @NotNull Map<Identifier, NbtCompound> attributes);
    void setBlockAttribute(@NotNull BlockPos pos, @NotNull Identifier id, @NotNull NbtCompound nbt);
    @NotNull Map<Identifier, NbtCompound> getBlockAttributes(@NotNull BlockPos pos);
    @Nullable NbtCompound getBlockAttribute(@NotNull BlockPos pos, @NotNull Identifier id);
    void clearBlockAttributes(@NotNull BlockPos pos);
    void removeBlockAttribute(@NotNull BlockPos pos, @NotNull Identifier id);
    
    void addAttributeListener(AttributeEventType type, BlockPos pos, Function0<Unit> callback);
    void addAttributeListener(AttributeEventType type, Function1<BlockPos, Unit> callback);
    void addAttributeListener(AttributeEventType type, Function1<BlockPos, Boolean> condition, Function1<BlockPos, Unit> callback);
    void removeAttributeListener(AttributeEventType type, Function0<Unit> callback);
    void removeAttributeListener(AttributeEventType type, Function1<BlockPos, Unit> callback);

    NbtElement serializeBlockAttributes();
    void deserializeBlockAttributes(NbtList nbt);
}
