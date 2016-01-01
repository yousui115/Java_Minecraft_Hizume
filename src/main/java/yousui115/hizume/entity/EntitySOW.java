package yousui115.hizume.entity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.entity.effect.EntityWeatherEffect;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

/**
 * ■Entity Scars of Wind
 */
public class EntitySOW extends EntityWeatherEffect
{
    //■爪痕をつけたEntityPlayer
    private EntityPlayer trigger;

    //■傷が開いてダメージを与える時間と限界
    public int tickScars = 0;
    protected final int tickScarsMax = 10;

    //■寿命
    protected final int ticksMax = 1000;

    //■傾き(ランダム値)
    public float fRollRnd;

    //■多段Hit防止用リスト
    protected List<Entity> hitEntities = new ArrayList();

    /**
     * ■コンストラクタ
     * @param worldIn
     */
    public EntitySOW(World worldIn)
    {
        super(worldIn);
    }

    /**
     * ■コンストラクタ
     */
    public EntitySOW(World worldIn, EntityPlayer triggerIn)
    {
        this(worldIn);

        //■トリガーEntity
        this.trigger = triggerIn;

        //■当たり判定
        this.setSize(2f, 2f);

        //■位置 及び 回転
        Vec3 look = trigger.getLookVec();
        double d1 = 2f;
        this.setPositionAndRotation(trigger.posX + look.xCoord * d1,
                                    trigger.posY + this.getEyeHeight() - (this.height / 2f) + look.yCoord * d1,
                                    trigger.posZ + look.zCoord * d1,
                                    trigger.rotationYawHead,
                                    trigger.rotationPitch);

        fRollRnd = this.rand.nextFloat() * 360f;
    }

    private final static int ID_SOW_STATE = 10;

    @Override
    protected void entityInit()
    {
        //■傷の状態(0:閉じる 1:開く)
        this.dataWatcher.addObject(ID_SOW_STATE, Byte.valueOf((byte)0));
    }

    public byte getScarsState()
    {
        return this.dataWatcher.getWatchableObjectByte(ID_SOW_STATE);
    }

    public void changeScarsState()
    {
        //■傷が閉じる事は無い
        this.dataWatcher.updateObject(ID_SOW_STATE, Byte.valueOf((byte)1));
    }

    /**
     * ■更新処理
     */
    @Override
    public void onUpdate()
    {
        //■死 ぬ が よ い
        if (this.ridingEntity != null) { this.ridingEntity.setDead(); this.ridingEntity = null; }
        if (this.riddenByEntity != null) { this.riddenByEntity.setDead(); this.riddenByEntity = null; }

        //■初回起動時にだけ行いたい処理
        if (this.firstUpdate)
        {
            //■1.発射音
//            if (!this.worldObj.isRemote && this.soundName != null && this.soundName.length() > 3)
//            {
//                float fVol = soundName.substring(0, 3).contentEquals("kfs") ? 0.5f : 3.0f;
//                trigger.worldObj.playSoundAtEntity(trigger, soundName, fVol, 1.0f);
//            }
        }

        //■位置・回転情報の保存
        lastTickPosX = prevPosX = posX;
        lastTickPosY = prevPosY = posY;
        lastTickPosZ = prevPosZ = posZ;
        prevRotationPitch = rotationPitch;
        prevRotationYaw   = rotationYaw;

        //■位置調整
        posX += motionX;
        posY += motionY;
        posZ += motionZ;

        //■寿命
        if (ticksExisted > ticksMax || tickScars > tickScarsMax)
        {
            this.setDead();
        }

        //■空間の傷が開く(ダメージ発生)
        if(this.getScarsState() == 1)
        {
            //TODO
            //System.out.println((this.worldObj.isRemote ? "[Client]" : "[Server] ") + "Open SOW! : " + this.getEntityId());

            ticksExisted = 0;
            tickScars++;

            if (!worldObj.isRemote)
            {
                //TODO
                //■接触しているEntityを集める。(EntitySOWは含まれない
                List<Entity> entities = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox());

                //■調べなくても良いEntityを除去
                entities.removeAll(this.hitEntities);

                //■新規に取得したEntityを多段Hit防止用リストに追加
                this.hitEntities.addAll(entities);

                //■集めたEntityはどんなものかなー？
                for (Entity target : entities)
                {
                    //■生物系のみ
                    if (!(target instanceof EntityLivingBase) &&
                        !(target instanceof EntityDragonPart))
                    { continue; }

                    //■ダメージ
                    target.hurtResistantTime = 0;
                    target.attackEntityFrom(DamageSource.magic, 10);
                }
            }
        }

        //■初回起動フラグ off
        this.firstUpdate = false;
    }


    @Override
    protected void readEntityFromNBT(NBTTagCompound tagCompund){}
    @Override
    protected void writeEntityToNBT(NBTTagCompound tagCompound){}

    public EntityPlayer getTrigger() { return this.trigger; }
}
