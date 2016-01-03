package yousui115.hizume.event;

import java.util.UUID;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import yousui115.hizume.Hizume;
import yousui115.hizume.item.ItemHizume;

import com.google.common.collect.HashMultimap;

public class EventHooks
{
    @SubscribeEvent
    public void onPlayerTickEvent(TickEvent.PlayerTickEvent event)
    {
        UUID uuid = ((ItemHizume)Hizume.itemHizume).getUUID();
        HashMultimap multimap = HashMultimap.create();
        multimap.put(   SharedMonsterAttributes.movementSpeed.getAttributeUnlocalizedName(),
                new AttributeModifier(uuid, "move speed", 0.5f, 0));

        ItemStack stack = event.player.inventory.getCurrentItem();

        if (stack != null && stack.getItem() instanceof ItemHizume && event.player.isUsingItem())
        {
            //event.player.getAttributeMap().applyAttributeModifiers(multimap);
            event.player.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.5d);
            event.player.capabilities.setPlayerWalkSpeed(0.5f);

        }
        else
        {
            //event.player.getAttributeMap().removeAttributeModifiers(multimap);
            event.player.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.1d);
            event.player.capabilities.setPlayerWalkSpeed(0.1f);

        }
    }

    @SubscribeEvent
    public void onFOVUpdateEvent(FOVUpdateEvent event)
    {
        ItemStack stack = event.entity.getCurrentEquippedItem();

        if (stack != null && stack.getItem() instanceof ItemHizume && event.entity.isUsingItem())
        {
            event.newfov = 0.9f;
        }

    }
//    @SubscribeEvent
//    public void onPlayerUseItemEvent(PlayerUseItemEvent event)
//    {
//
//    }
//
//    @SubscribeEvent
//    public void onPlayerUseItemEventStop(PlayerUseItemEvent.Stop event)
//    {
//
//    }
}
