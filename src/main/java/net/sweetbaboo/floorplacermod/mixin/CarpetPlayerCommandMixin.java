package net.sweetbaboo.floorplacermod.mixin;

import carpet.commands.PlayerCommand;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;
import net.sweetbaboo.floorplacermod.commands.BuildFloorCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerCommand.class)
public abstract class CarpetPlayerCommandMixin {
  @ModifyExpressionValue(method = "register", at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/builder/RequiredArgumentBuilder;then(Lcom/mojang/brigadier/builder/ArgumentBuilder;)Lcom/mojang/brigadier/builder/ArgumentBuilder;", ordinal = 1), remap = false)
  private static ArgumentBuilder<ServerCommandSource, ?> insertBuildFloorLiteral(ArgumentBuilder<ServerCommandSource, ?> original) {
    // Delegate command creation to a separate class for easier debugging
    return original.then(BuildFloorCommand.create());
  }
}
