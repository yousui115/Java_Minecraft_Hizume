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

public class MessageScarsHandler implements IMessageHandler<MessageScars, IMessage>
{

    @Override
    public IMessage onMessage(MessageScars message, MessageContext ctx)
    {
        //■サーバのプレイヤー
        EntityPlayer player = ctx.getServerHandler().playerEntity;
        Entity target = player.worldObj.getEntityByID(message.getTargetID());

        if (target != null && !target.isDead)
        {
            DamageSource damagesource = DamageSource.causePlayerDamage(player);
            target.attackEntityFrom(damagesource, (float)MathHelper.clamp_int(message.getDamage(), message.getDamage(), Integer.MAX_VALUE - 1));
            //TODO
            System.out.println("EntityID = " + message.getTargetID() + " : damage = " + message.getDamage());
            ItemStack stack = player.getCurrentEquippedItem();
            if (stack != null && stack.getItem() instanceof ItemHizume)
            {
                //■傷リセット
                ((ItemHizume)stack.getItem()).setHitCount(target.getDataWatcher(), 0);
            }
        }

        return null;
    }

}
