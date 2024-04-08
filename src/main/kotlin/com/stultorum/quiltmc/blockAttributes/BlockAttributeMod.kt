package com.stultorum.quiltmc.blockAttributes

import com.stultorum.quiltmc.blockAttributes.commands.attributesCommand
import org.quiltmc.loader.api.ModContainer
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer
import org.quiltmc.qsl.command.api.CommandRegistrationCallback
import org.slf4j.Logger
import org.slf4j.LoggerFactory

internal class BlockAttributeMod: ModInitializer {
    override fun onInitialize(mod: ModContainer) {
        Logger = LoggerFactory.getLogger(mod.metadata()!!.name())
        
        CommandRegistrationCallback.EVENT.register { dispatcher, _, _ -> 
            dispatcher.register(attributesCommand)
        }
        
        Logger.info("Hello World!")
    }
    
    companion object {
        internal var Logger: Logger = LoggerFactory.getLogger("???")
            private set
    }
}
