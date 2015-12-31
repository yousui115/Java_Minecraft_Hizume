package yousui115.hizume.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

/**
 * Scars of wind : 風の爪痕    で合ってるよね？ね？
 *
 */
public class MessageScars implements IMessage
{
    private int targetID;
    private int damage;

    /**
     * ■コンストラクタ(必須！)
     */
    public MessageScars(){}

    /**
     * ■コンストラクタ
     * @param magic
     */
    public MessageScars(Entity targetIn, int damageIn)
    {
        this.targetID = targetIn.getEntityId();
        this.damage   = damageIn;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.targetID = buf.readInt();
        this.damage   = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.targetID);
        buf.writeInt(this.damage);
    }

    public int getTargetID() { return this.targetID; }
    public int getDamage()   { return this.damage; }
}
