package net.sweetbaboo.floorplacermod.mixin;

import net.sweetbaboo.floorplacermod.access.ServerPlayerEntityAccess;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin implements ServerPlayerEntityAccess {
  @Unique
  private boolean isBuildingfloor=false;

  @Override
  public boolean isBuildingFloor() {
    return isBuildingfloor;
  }

  @Override
  public void setBuildFloor(boolean shouldBuildFloor) {
    this.isBuildingfloor=shouldBuildFloor;
  }
}
