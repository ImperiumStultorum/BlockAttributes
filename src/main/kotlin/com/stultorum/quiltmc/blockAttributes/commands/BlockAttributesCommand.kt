package com.stultorum.quiltmc.blockAttributes.commands

import com.mojang.brigadier.builder.RequiredArgumentBuilder.argument
import com.mojang.brigadier.context.CommandContext
import com.stultorum.quiltmc.blockAttributes.getBlockAttributeNbt
import com.stultorum.quiltmc.blockAttributes.getBlockAttributesNbt
import com.stultorum.quiltmc.blockAttributes.setBlockAttributeNbt
import com.stultorum.quiltmc.blockAttributes.setBlockAttributesNbt
import net.minecraft.command.argument.BlockPosArgumentType
import net.minecraft.command.argument.IdentifierArgumentType
import net.minecraft.command.argument.NbtElementArgumentType
import net.minecraft.command.argument.PosArgument
import net.minecraft.nbt.NbtElement
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import org.quiltmc.qkl.library.brigadier.util.world
import org.quiltmc.qkl.library.text.*
import net.minecraft.server.command.CommandManager.literal as commandLiteral
import net.minecraft.server.command.ServerCommandSource as SCS
import net.minecraft.text.Text.literal as textLiteral


private val idColor = Color.GOLD
private val posColor = Color.GREEN
private val valColor = Color.AQUA

private fun TextBuilder.addAttribute(id: Identifier, nbt: NbtElement) {
    this.addId(id)
    this.bold { this.literal(" = ") }
    this.nbt(nbt)
}

private fun TextBuilder.addPos(pos: BlockPos) {
    this.color(posColor) { this.literal(pos.toShortString()) }
}

private fun TextBuilder.addId(id: Identifier) {
    this.color(idColor) { this.literal(id.toString()) }
}

private fun TextBuilder.addVal(value: Any) {
    this.color(valColor) { this.literal(value.toString()) }
}


private fun copyAttribute(ctx: CommandContext<SCS>, id1: Identifier, id2: Identifier = id1): Int {
    val pos1 = getPosArg(ctx, "pos1")
    val pos2 = getPosArg(ctx, "pos2")
    val nbt = ctx.world.getBlockAttributeNbt(pos1, id1)
    if (nbt == null) {
        ctx.source.sendError(textLiteral("Block at ${pos1.toShortString()} doesn't have $id1 set"))
        return -1
    }
    ctx.world.setBlockAttributeNbt(pos2, id2, nbt)
    ctx.source.sendFeedback({
        val reply = TextBuilder()
        reply.literal("Copied ")
        reply.addId(id1)
        reply.literal(" at ")
        reply.addPos(pos1)
        reply.literal(" to ")
        if (id1 != id2) {
            reply.addId(id2)
            reply.literal(" at ")
        }
        reply.addPos(pos2)
        return@sendFeedback reply.build()
    }, true)
    return 1
}


internal val attributesCommand = 
    commandLiteral("blockattributes").requires { source -> source.hasPermissionLevel(2) }
        .then(commandLiteral("get")
            .then(argument<SCS, PosArgument>("pos", BlockPosArgumentType.blockPos())
                .executes { ctx ->
                    val pos = getPosArg(ctx)
                    val attributes = ctx.world.getBlockAttributesNbt(pos)
                    if (attributes.isEmpty()) {
                        ctx.source.sendFeedback({
                            val reply = TextBuilder()
                            reply.literal("Block at ")
                            reply.addPos(pos)
                            reply.literal(" has no attributes")
                            return@sendFeedback reply.build()
                        }, true)
                        return@executes 0
                    }
                    ctx.source.sendFeedback({
                        val reply = TextBuilder()
                        reply.literal("Block at ")
                        reply.addPos(pos)
                        reply.literal(" has:\n")
                        var newline = false
                        attributes.forEach { (id, nbt) -> 
                            if (newline) reply.literal("\n") else newline = true
                            reply.addAttribute(id, nbt)
                        }
                        return@sendFeedback reply.build()
                    }, true)
                    return@executes attributes.size
                }
                .then(argument<SCS, Identifier>("id", IdentifierArgumentType.identifier())
                    .executes { ctx ->
                        val id = getIdArg(ctx)
                        val pos = getPosArg(ctx)
                        val nbt = ctx.world.getBlockAttributeNbt(pos, id)
                        if (nbt == null) {
                            ctx.source.sendError(textLiteral("Block at ${pos.toShortString()} doesn't have $id set."))
                            return@executes -1
                        }
                        ctx.source.sendFeedback({
                            val reply = TextBuilder()
                            reply.literal("Block at ")
                            reply.addPos(pos)
                            reply.literal(" has ")
                            reply.addId(id)
                            reply.literal(" of:\n")
                            reply.nbt(nbt)
                            return@sendFeedback reply.build()
                        }, true)
                        return@executes 1
                    }
                )
            )
        ).then(commandLiteral("delete")
            .then(argument<SCS, PosArgument>("pos", BlockPosArgumentType.blockPos())
                .then(argument<SCS, Identifier>("id", IdentifierArgumentType.identifier())
                    .executes { ctx -> 
                        val id = getIdArg(ctx)
                        val pos = getPosArg(ctx)
                        ctx.world.setBlockAttributeNbt(pos, id, null)
                        ctx.source.sendFeedback({
                            val reply = TextBuilder()
                            reply.literal("Deleted ")
                            reply.addId(id)
                            reply.literal(" from ")
                            reply.addPos(pos)
                            return@sendFeedback reply.build()
                        }, true)
                        return@executes 0
                    }
                )
            )
        ).then(commandLiteral("set")
            .then(argument<SCS, PosArgument>("pos", BlockPosArgumentType.blockPos())
                .then(argument<SCS, Identifier>("id", IdentifierArgumentType.identifier())
                    .then(argument<SCS, NbtElement>("nbt", NbtElementArgumentType.nbtElement())
                        .executes { ctx ->
                            val id = getIdArg(ctx)
                            val pos = getPosArg(ctx)
                            val nbt = getNbtArg(ctx)
                            ctx.world.setBlockAttributeNbt(pos, id, nbt)
                            ctx.source.sendFeedback({
                                val reply = TextBuilder()
                                reply.literal("Set ")
                                reply.addId(id)
                                reply.literal(" at ")
                                reply.addPos(pos)
                                reply.literal(" to ")
                                reply.nbt(nbt)
                                return@sendFeedback reply.build()
                            }, true)
                            return@executes 0
                        }
                    )
                )
            )
        ).then(commandLiteral("copy")
            .then(commandLiteral("from")
                .then(argument<SCS, PosArgument>("pos1", BlockPosArgumentType.blockPos())
                    .then(argument<SCS, Identifier>("id1", IdentifierArgumentType.identifier())
                        .then(commandLiteral("to")
                            .then(argument<SCS, PosArgument>("pos2", BlockPosArgumentType.blockPos())
                                .executes { ctx ->
                                    return@executes copyAttribute(ctx, getIdArg(ctx, "id1"))
                                }
                                .then(argument<SCS, Identifier>("id2", IdentifierArgumentType.identifier())
                                    .executes { ctx ->
                                        return@executes copyAttribute(ctx, getIdArg(ctx, "id1"), getIdArg(ctx, "id2"))
                                    }
                                )
                            )
                        )
                    )
                )
            )
        ).then(commandLiteral("overwrite")
            .then(argument<SCS, PosArgument>("pos1", BlockPosArgumentType.blockPos())
                .then(commandLiteral("with")
                    .then(argument<SCS, PosArgument>("pos2", BlockPosArgumentType.blockPos())
                        .executes { ctx ->
                            val pos1 = getPosArg(ctx, "pos1")
                            val pos2 = getPosArg(ctx, "pos2")
                            val attributes = ctx.world.getBlockAttributesNbt(pos1)
                            ctx.world.setBlockAttributesNbt(pos2, attributes)
                            ctx.source.sendFeedback({
                                val reply = TextBuilder()
                                reply.literal("Set ")
                                reply.addVal(attributes.size)
                                reply.literal(" attributes from ")
                                reply.addPos(pos1)
                                reply.literal(" to ")
                                reply.addPos(pos2)
                                return@sendFeedback reply.build()
                            }, true)
                            return@executes attributes.size
                        }
                    )
                )
            )
        )
