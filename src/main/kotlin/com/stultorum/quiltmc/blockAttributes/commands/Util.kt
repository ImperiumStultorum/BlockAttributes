package com.stultorum.quiltmc.blockAttributes.commands

import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.argument.BlockPosArgumentType
import net.minecraft.command.argument.IdentifierArgumentType
import net.minecraft.command.argument.NbtElementArgumentType
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtHelper
import net.minecraft.server.command.ServerCommandSource
import org.quiltmc.qkl.library.text.TextBuilder
import org.quiltmc.qkl.library.text.text

internal fun getPosArg(ctx: CommandContext<ServerCommandSource>, name: String = "pos") = BlockPosArgumentType.getBlockPos(ctx, name)
internal fun getIdArg(ctx: CommandContext<ServerCommandSource>, name: String = "id") = IdentifierArgumentType.getIdentifier(ctx, name)
internal fun getNbtArg(ctx: CommandContext<ServerCommandSource>, name: String = "nbt") = NbtElementArgumentType.getNbtElement(ctx, name)

// TODO consider adding to classic blunders along with a few other brigadier helpers
internal fun TextBuilder.nbt(nbt: NbtElement) {
    this.text(NbtHelper.toPrettyPrintedText(nbt))
}
