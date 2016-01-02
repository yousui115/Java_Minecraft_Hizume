package yousui115.hizume.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageOpenSOW  implements IMessage
{
    private int targetID;
    private boolean isSound;    //使いまわし用フラグ

    /**
     * ■コンストラクタ(必須！)
     */
    public MessageOpenSOW(){}

    /**
     * ■コンストラクタ
     */
    public MessageOpenSOW(Entity entityIn, boolean isSoundIn)
    {
        this.targetID = entityIn.getEntityId();
        this.isSound = isSoundIn;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.targetID = buf.readInt();
        this.isSound = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.targetID);
        buf.writeBoolean(this.isSound);
    }

    public int getTargetID() { return this.targetID; }
    public boolean getIsSound() { return this.isSound; }
}
