package yousui115.hizume.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageOpenSOW  implements IMessage
{
    private int targetID;

    /**
     * ■コンストラクタ(必須！)
     */
    public MessageOpenSOW(){}

    /**
     * ■コンストラクタ
     */
    public MessageOpenSOW(Entity entityIn)
    {
        this.targetID = entityIn.getEntityId();
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.targetID = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.targetID);
    }

    public int getTargetID() { return this.targetID; }
}
