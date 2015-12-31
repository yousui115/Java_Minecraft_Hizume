package yousui115.hizume.network;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import yousui115.hizume.item.ItemHizume;

public class MessageScarsOfWindHandler implements IMessageHandler<MessageScarsOfWind, IMessage>
{

    @Override
    public IMessage onMessage(MessageScarsOfWind message, MessageContext ctx)
    {
        //■サーバのプレイヤー
        EntityPlayer player = ctx.getServerHandler().playerEntity;
        Entity target = player.worldObj.getEntityByID(message.getTargetID());

        if (target != null && !target.isDead)
        {
            DamageSource damage = DamageSource.causePlayerDamage(player);
            target.attackEntityFrom(damage, (float)MathHelper.clamp_int(message.getDamage(), 1, Integer.MAX_VALUE - 1));
            ItemStack stack = player.getCurrentEquippedItem();
            if (stack != null && stack.getItem() instanceof ItemHizume)
            {
                //((ItemHizume)stack.getItem()).removeSlashTargetFromID(stack, target.getEntityId());
                //((ItemHizume)stack.getItem()).resetSlashTargetFromID(stack, target.getEntityId());
            }
        }

        return null;
    }

}
