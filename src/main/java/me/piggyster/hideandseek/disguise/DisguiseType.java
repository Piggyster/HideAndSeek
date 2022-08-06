package me.piggyster.hideandseek.disguise;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

public enum DisguiseType {
    FERN(Material.FERN),
    DANDELION(Material.DANDELION),
    CRAFTING_TABLE(Material.CRAFTING_TABLE),
    FURNACE(Material.FURNACE),
    BOOKCASE(Material.BOOKSHELF),
    SCAFFOLDING(Material.SCAFFOLDING),
    OAK_LEAVES(Material.OAK_LEAVES),
    ARMOR_STAND(EntityType.ARMOR_STAND);

    private Material material;
    private EntityType entityType;

    DisguiseType(Material material) {
        this.material = material;
    }

    DisguiseType(EntityType entityType) {
        this.entityType = entityType;
    }

    public Material getMaterial() {
        return material;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public boolean isBlock() {
        return material != null;
    }
}
