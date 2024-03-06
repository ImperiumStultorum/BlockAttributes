package com.stultorum.quiltmc.blockAttributes

import org.quiltmc.loader.api.ModContainer
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

internal class BlockAttributeMod: ModInitializer {
    internal var Logger: Logger = LoggerFactory.getLogger("???")
        private set

    override fun onInitialize(mod: ModContainer) {
        Logger = LoggerFactory.getLogger(mod.metadata()!!.name())

        Logger.info("Hello World!")
    }
}
