package yousui115.hizume.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import yousui115.hizume.entity.EntitySOW;

public class MessageSOWHandler implements IMessageHandler<MessageSOW, IMessage>
{

    /**
     * ■
     */
    @Override
    public IMessage onMessage(MessageSOW message, MessageContext ctx)
    {
        //TODO
        //EntityPlayer player = Hizume.proxy.getEntityPlayerInstance();
        EntityPlayer player = ctx.getServerHandler().playerEntity;
        if (player == null) { return null; }

        EntitySOW sow = new EntitySOW(player.worldObj, player);

        sow.setEntityId(message.getEntityID());
//        sow.serverPosX = message.getPosX();
//        sow.serverPosY = message.getPosY();
//        sow.serverPosZ = message.getPosZ();

        player.worldObj.addWeatherEffect(sow);

        return null;
    }

}
