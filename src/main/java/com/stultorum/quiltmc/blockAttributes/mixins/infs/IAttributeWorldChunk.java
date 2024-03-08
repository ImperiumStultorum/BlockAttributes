package com.stultorum.quiltmc.blockAttributes.mixins.infs;

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
}
