package com.stultorum.quiltmc.blockAttributes.mixinfs;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public interface IAttributeWorldChunk {
    public static enum AttributeEventType {
        Update, Remove
    }
    
    public void setBlockAttributes(@NotNull BlockPos pos, @NotNull HashMap<Identifier, NbtElement> attributes);
    public void setBlockAttribute(@NotNull BlockPos pos, @NotNull Identifier id, @NotNull NbtElement nbt);
    @NotNull
    public HashMap<Identifier, NbtElement> getBlockAttributes(@NotNull BlockPos pos);
    @Nullable
    public NbtElement getBlockAttribute(@NotNull BlockPos pos, @NotNull Identifier id);
    public void clearBlockAttributes(@NotNull BlockPos pos);
    public void removeBlockAttribute(@NotNull BlockPos pos, @NotNull Identifier id);
    
    public void addAttributeListener(AttributeEventType type, BlockPos pos, Function0<Unit> callback);
    public void addAttributeListener(AttributeEventType type, Function1<BlockPos, Unit> callback);
    public void addAttributeListener(AttributeEventType type, Function1<BlockPos, Boolean> condition, Function1<BlockPos, Unit> callback);
    public void removeAttributeListener(AttributeEventType type, Function0<Unit> callback);
    public void removeAttributeListener(AttributeEventType type, Function1<BlockPos, Unit> callback);
}
