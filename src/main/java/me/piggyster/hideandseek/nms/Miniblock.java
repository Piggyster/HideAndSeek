package me.piggyster.hideandseek.nms;

import net.minecraft.sounds.SoundEffect;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftArmorStand;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftEntityEquipment;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemFactory;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_17_R1.util.CraftChatMessage;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;

public class Miniblock extends EntityArmorStand {

    public Miniblock(Material material, Location location) {
        super(((CraftWorld) location.getWorld()).getHandle(), location.getX(), location.getY(), location.getZ());
        setInvisible(true);
        setNoGravity(true);
        setBasePlate(true);
        setYRot(location.getYaw());
        setXRot(location.getPitch());
        setHeadRotation(location.getYaw());
        setMarker(true);
        net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(new ItemStack(material));
        setSlot(EnumItemSlot.f, nmsItem);
    }

    public boolean isInvulnerable(DamageSource source) {
        return true;
    }

    public void remove() {
        super.a(Entity.RemovalReason.b);
    }

    public void a(Entity.RemovalReason entity_removalreason) {
    }

    public void setSlot(EnumItemSlot enumitemslot, ItemStack itemstack) {
        // Prevent stand being equipped
    }


    public void playSound(SoundEffect soundeffect, float f, float f1) {
    }

    public EnumInteractionResult a(EntityHuman human, Vec3D vec3d, EnumHand enumhand) {
        return EnumInteractionResult.d;
    }
}
