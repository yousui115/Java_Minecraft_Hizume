package yousui115.hizume.network;

import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityDragonPart;
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

    /**
     * ■Client -> Server
     */
    @Override
    public IMessage onMessage(MessageScars message, MessageContext ctx)
    {
        //■サーバのプレイヤー
        EntityPlayer player = ctx.getServerHandler().playerEntity;
        Entity target = player.worldObj.getEntityByID(message.getTargetID());

        if (target != null && !target.isDead)
        {
            DamageSource damagesource = DamageSource.causePlayerDamage(player);
            target.attackEntityFrom(damagesource, MathHelper.clamp_float(message.getDamage(), 0f, Float.MAX_VALUE - 1));
            //TODO
            System.out.println("Entity = " + target.getName() + " : ScarsDamage = " + message.getDamage());
            ItemStack stack = player.getCurrentEquippedItem();
            if (stack != null && stack.getItem() instanceof ItemHizume)
            {
                //■傷リセット
                DataWatcher dw;
                if (target instanceof EntityDragonPart)
                {
                    //■EnderDragonの傷は本体に蓄積してるので、そっちから取得
                    dw = ((Entity)((EntityDragonPart)target).entityDragonObj).getDataWatcher();
                }
                else
                {
                    dw = target.getDataWatcher();
                }

                ((ItemHizume)stack.getItem()).setHitCount(dw, 0);
            }
        }

        return null;
    }

}
