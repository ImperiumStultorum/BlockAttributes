package com.stultorum.quiltmc.blockAttributes

import com.mojang.brigadier.builder.RequiredArgumentBuilder.argument
import net.minecraft.command.argument.BlockPosArgumentType
import net.minecraft.command.argument.IdentifierArgumentType
import net.minecraft.command.argument.PosArgument
import net.minecraft.nbt.NbtString
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.util.Identifier
import org.quiltmc.qkl.library.brigadier.util.world
import org.quiltmc.qsl.command.api.CommandRegistrationCallback
import kotlin.random.Random
import net.minecraft.server.command.CommandManager.literal as commandLiteral
import net.minecraft.text.Text.literal as textLiteral

// TODO refactor and reformat this
internal val attributesCommand = CommandRegistrationCallback callback@{ dispatcher, _, _ ->
   dispatcher.register(commandLiteral("blockattributes").requires { source -> source.hasPermissionLevel(2) }.then(commandLiteral("get").then(argument<ServerCommandSource, PosArgument>("pos", BlockPosArgumentType.blockPos()).executes { ctx ->
        ctx.source.sendFeedback({
            val attributes = ctx.world.getBlockAttributesNbt(BlockPosArgumentType.getBlockPos(ctx, "pos"))
            val reply = StringBuilder()
            attributes.forEach { (id, nbt) -> reply.append(id.toString()).append('=').append(nbt.toDebugString()).append('\n') }
            textLiteral(reply.toString())
        }, false)
        0
    }.then(argument<ServerCommandSource, Identifier>("id", IdentifierArgumentType.identifier()).executes { ctx -> 
        ctx.source.sendFeedback({
            val attribute = ctx.world.getBlockAttribute(BlockPosArgumentType.getBlockPos(ctx, "pos"), IdentifierArgumentType.getIdentifier(ctx, "id"))
            textLiteral(attribute?.toDebugString() ?: "Doesn't exist")
        }, false)
        0
    }))).then(commandLiteral("remove").then(argument<ServerCommandSource, PosArgument>("pos", BlockPosArgumentType.blockPos()).then(argument<ServerCommandSource, Identifier>("id", IdentifierArgumentType.identifier()).executes { ctx -> 
        ctx.world.setBlockAttributeNbt(BlockPosArgumentType.getBlockPos(ctx, "pos"), IdentifierArgumentType.getIdentifier(ctx, "id"), null)
        0
    }))).then(commandLiteral("test").then(argument<ServerCommandSource, PosArgument>("pos", BlockPosArgumentType.blockPos()).then(argument<ServerCommandSource, Identifier>("id", IdentifierArgumentType.identifier()).executes { ctx ->
        ctx.world.setBlockAttributeNbt(BlockPosArgumentType.getBlockPos(ctx, "pos"), IdentifierArgumentType.getIdentifier(ctx, "id"), NbtString.of(Random.nextInt().toString(16)))
        0 
    }))))
}
