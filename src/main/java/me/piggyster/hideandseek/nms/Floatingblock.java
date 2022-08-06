package me.piggyster.hideandseek.nms;

import net.minecraft.network.protocol.game.PacketPlayOutScoreboardDisplayObjective;
import net.minecraft.network.protocol.game.PacketPlayOutScoreboardScore;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.item.EntityFallingBlock;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.BlockDirectional;
import net.minecraft.world.level.block.BlockFurnace;
import net.minecraft.world.level.block.state.IBlockData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.Directional;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_17_R1.block.data.CraftDirectional;

public class Floatingblock extends EntityFallingBlock {

    public Floatingblock(Location location, Material material) {
        super(((CraftWorld) location.getWorld()).getHandle(), location.getX(), location.getY(), location.getZ(), CraftBlockData.newData(material, null).getState());
        setNoGravity(true);
        setInvulnerable(true);
    }

    public void tick() {

    }
}
