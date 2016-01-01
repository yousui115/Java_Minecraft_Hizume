package yousui115.hizume.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageSOW implements IMessage
{
    private int entityID;
    private int posX;           //位置X(ただし、32倍(切捨)されている)
    private int posY;           //位置Y(同上)
    private int posZ;           //位置Z(どじょう)

    /**
     * ■コンストラクタ(必須！)
     */
    public MessageSOW(){}

    /**
     * ■コンストラクタ
     * @param magic
     */
    public MessageSOW(Entity entityIn)
    {
        this.entityID = entityIn.getEntityId();
        this.posX = MathHelper.floor_double(entityIn.posX * 32.0D);
        this.posY = MathHelper.floor_double(entityIn.posY * 32.0D);
        this.posZ = MathHelper.floor_double(entityIn.posZ * 32.0D);
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.entityID = buf.readInt();
        this.posX = buf.readInt();
        this.posY = buf.readInt();
        this.posZ = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.entityID);
        buf.writeInt(posX);
        buf.writeInt(posY);
        buf.writeInt(posZ);
    }

    public int getEntityID() { return this.entityID; }
    public int getPosX() { return this.posX; }
    public int getPosY() { return this.posY; }
    public int getPosZ() { return this.posZ; }
}
