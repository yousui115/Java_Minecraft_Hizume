package yousui115.hizume.network;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import yousui115.hizume.Hizume;
import yousui115.hizume.entity.EntitySOW;

public class MessageOpenSOWHandler implements IMessageHandler<MessageOpenSOW, IMessage>
{
    /**
     * Client -> Server
     * TODO 使いまわしメッセージ
     */
    @Override
    public IMessage onMessage(MessageOpenSOW message, MessageContext ctx)
    {
        //■サーバのプレイヤー
        EntityPlayer player = ctx.getServerHandler().playerEntity;

        if (message.getIsSound())
        {
            player.worldObj.playSoundAtEntity(player, Hizume.MOD_ID + ":shan", 1.0f, 1.0f);
        }
        else
        {
            //Entity target = player.worldObj.getEntityByID(message.getTargetID());
            List<Entity> entities = player.worldObj.weatherEffects;
            if (entities != null && !entities.isEmpty())
            {
                for (Entity entity : entities)
                {
                    //■SOW かつ ID一致
                    if (entity instanceof EntitySOW &&
                        entity.getEntityId() == message.getTargetID())
                    {
                        ((EntitySOW)entity).changeScarsState();
                        break;
                    }
                }
            }
        }

        return null;
    }

}
