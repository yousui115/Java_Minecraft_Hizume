package yousui115.hizume.item;

import java.util.List;
import java.util.UUID;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yousui115.hizume.Hizume;
import yousui115.hizume.entity.EntitySOW;
import yousui115.hizume.network.MessageOpenSOW;
import yousui115.hizume.network.MessageSOW;
import yousui115.hizume.network.MessageScars;
import yousui115.hizume.network.PacketHandler;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class ItemHizume extends ItemSword
{
    protected float attackDamage2;

    /**
     * ■コンストラクタ
     */
    public ItemHizume(ToolMaterial material)
    {
        super(material);
        this.attackDamage2 = 4.0F + material.getDamageVsEntity();

    }

    /**
     * Called each tick as long the item is on a player inventory. Uses by maps to check if is on a player hand and
     * update it's contents.
     */
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
    {
        //■対象はプレイヤー
        if (!(entityIn instanceof EntityPlayer)) { return; }
        EntityPlayer player = (EntityPlayer)entityIn;

        //■カレントアイテムでないなら処理をかえす
        if (!isSelected) { return; }

        //■アイテム使ってるなら空間に傷はつけられない
        if (player.getItemInUseCount() != 0) { return; }

        //■キー入力関連
        // ▼爪痕設置キーを押しただの押してないだの
        boolean isPress = false;
        // ▼押した回数を覚えてるので発散させる
        while (Hizume.proxy.getKeySOW().isPressed()) { isPress = true; }
        // ▼押した形跡 + 現在押されている(継続)
        isPress = isPress && Hizume.proxy.getKeySOW().isKeyDown();

        //■クライアント側での処理
        if (worldIn.isRemote && isPress)
        {
            //■腕を振る
            player.swingItem();

            //■空間へ爪痕をつける
            EntitySOW[] magic = createSOW(stack, worldIn, player);
            if (magic != null)
            {
                for (EntitySOW base : magic)
                {
                    worldIn.addWeatherEffect(base);
                    //PacketHandler.INSTANCE.sendToAll(new MessageSOW(base));
                    PacketHandler.INSTANCE.sendToServer(new MessageSOW(base));
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getColorFromItemStack(ItemStack stack, int renderPass)
    {
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 220f, 220f);
        //OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 61680f, 0f);
        return 16777215;
    }

    /**
     * returns the action that specifies what animation to play when the items is being used
     * ■モーション
     */
    @Override
    public EnumAction getItemUseAction(ItemStack stack)
    {
        return EnumAction.BOW;
    }

    /* ======================================== FORGE START =====================================*/

    /**
     * ItemStack sensitive version of getItemAttributeModifiers
     */
    @Override
    public Multimap getAttributeModifiers(ItemStack stack)
    {
        HashMultimap multimap = HashMultimap.create();
        multimap.put(   SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(),
                        new AttributeModifier(itemModifierUUID, "Weapon modifier", (double)this.attackDamage2, 0));
//        multimap.put(   SharedMonsterAttributes.movementSpeed.getAttributeUnlocalizedName(),
//                        new AttributeModifier(itemModifierUUID, "move speed", 0.1f, 0));
        return multimap;
//        return super.getAttributeModifiers(stack);
    }

    public UUID getUUID()
    {
        return this.itemModifierUUID;
    }

    /**
     * Called each tick while using an item.
     * ■EnumAction中は毎Tick呼ばれるはず
     * @param stack The Item being used
     * @param player The Player using the item
     * @param count The amount of time in tick the item has been used for continuously
     */
    @Override
    public void onUsingTick(ItemStack stackIn, EntityPlayer playerIn, int count)
    {
        //■無駄に蓄積されてるかもしれない入力回数の発散
        boolean isPress = false;
        while(Hizume.proxy.getKeyScars().isPressed()) { isPress = true; }

        //■最初の20tickは何も起きない
        if (count > this.getMaxItemUseDuration(stackIn) - 20) { return; }

        //■
        if (playerIn.worldObj.isRemote && isPress)
        {
            //■Entityの傷を開く試み
            this.doScars(stackIn, playerIn);
        }
    }

    /**
     * Called when the player Left Clicks (attacks) an entity.
     * Processed before damage is done, if return value is true further processing is canceled
     * and the entity is not attacked.
     * ■左クリックでEntityを殴ると呼ばれる。
     *   (return : 相手にダメージを [true = 与えない : false = 与える])
     *
     * @param stack The Item being used
     * @param player The player that is attacking
     * @param entity The entity being attacked
     * @return True to cancel the rest of the interaction.
     */
    @Override
    public boolean onLeftClickEntity(ItemStack stackIn, EntityPlayer player, Entity entity)
    {
        //■相手に傷をつける(DataWatcherに情報を刻む)
        if (entity instanceof EntityDragonPart)
        {
            //■DragonPartからDragonを取得
            entity = (Entity)((EntityDragonPart)entity).entityDragonObj;
        }
        DataWatcher dw = entity.getDataWatcher();

        int countHit = getHitCount(dw);

        //■情報を刻む
        dw.updateObject(Hizume.getDWID(), ++countHit);

        //TODO
        if (!player.worldObj.isRemote)
        {
            //System.out.println("Entity = " + entity.getName() + " : Scars!");
        }

        return false;
    }

    /**
     * Player, Render pass, and item usage sensitive version of getIconIndex.
     *
     * @param stack The item stack to get the icon for.
     * @param player The player holding the item
     * @param useRemaining The ticks remaining for the active item. アイテム使用時間
     * @return Null to use default model, or a custom ModelResourceLocation for the stage of use.
     */
    @SideOnly(Side.CLIENT)
    @Override
    public net.minecraft.client.resources.model.ModelResourceLocation getModel(ItemStack stack, EntityPlayer player, int useRemaining)
    {
        //TODO これをごり押しと言わず何と言う！
        //     モデルの回転をJsonを使わずに出来るように(ICustomModelの出番か)

        //■お返しするリソースロケーション(nullも返せる)
        ModelResourceLocation mr = null;

        //■useRemaining == 0 はアクションしてないので入れない
        if (useRemaining != 0)
        {
            int maxDuration = this.getMaxItemUseDuration(stack);
            int region = 1;

            mr = Hizume.proxy.getHizumeModelRL(Hizume.proxy.arrNum - 1);

            for(int idx = 1; idx < Hizume.proxy.arrNum; idx++)
            {
                if (maxDuration - (idx - 1) * region >= useRemaining && useRemaining > maxDuration - idx * region)
                {
                    mr = Hizume.proxy.getHizumeModelRL(idx);
                    break;
                }
            }
        }

        return mr;
    }

    /* ======================================== FORGE END   =====================================*/

    /* ======================================== イカ、自作   =====================================*/

    /**
     * ■空間へ爪痕をつける
     * @param stackIn
     * @param worldIn
     * @param playerIn
     * @return
     */
    protected EntitySOW[] createSOW(ItemStack stackIn, World worldIn, EntityPlayer playerIn)
    {
        //TODO
        EntitySOW[] sows = { new EntitySOW(worldIn, playerIn) };
        return sows;
    }

    /**
     * ■相手の傷を開く(クライアント側のみ)
     * @param stackIn
     * @param playerIn
     */
    protected Entity doScars(ItemStack stackIn, EntityPlayer playerIn)
    {
        Entity target = null;

        boolean isSoundSE = false;

        //■クライアント側で検出する
        List<MovingObjectPosition> mops = Hizume.proxy.getEntity(stackIn, playerIn);

        //■モップにEntityが引っかかってる
        //if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY)
        if (mops != null && !mops.isEmpty())
        {
            for (MovingObjectPosition mop : mops)
            {
                //■対象Entityを取得
                target = mop.entityHit;
                if (target == null) { continue; }


                //■EntitySOWはこちら
                if (target instanceof EntitySOW)
                {
                    //■接触しているEntityをかき集める
                    List<Entity> entities = playerIn.worldObj.getEntitiesWithinAABBExcludingEntity(target, target.getEntityBoundingBox());
                    if (entities == null || entities.isEmpty()) { continue; }

                    for (Entity entity : entities)
                    {
                        //■ドラゴン(概念)はスルー
                        if (entity instanceof EntityDragon) { continue; }

                        //■生物系のみ
                        if (!(entity instanceof EntityLivingBase) &&
                            !(entity instanceof EntityDragonPart))
                        { continue; }

                        //■爪痕をつけた本人
                        if (entity.equals(playerIn)) { continue; }

                        //■1体でも接触していれば傷が開く
                        // ▼サーバ側
                        PacketHandler.INSTANCE.sendToServer(new MessageOpenSOW(target, false));

                        // ▼クライアント側
                        ((EntitySOW)target).changeScarsState();

                        //■効果音ならすよー
                        isSoundSE = true;

                        break;
                    }
                }
                //■EntityLivingBase等はこちら
                else
                {
                    DataWatcher dw;
                    if (target instanceof EntityDragonPart)
                    {
                        //target = ((Entity)((EntityDragonPart)target).entityDragonObj);
                        //■EnderDragonの傷は本体に蓄積してるので、そっちから取得
                        dw = ((Entity)((EntityDragonPart)target).entityDragonObj).getDataWatcher();
                    }
                    else
                    {
                        dw = target.getDataWatcher();
                    }

                    //■対象Entityの傷を開く
                    int nHit = this.getHitCount(dw);
                    if (nHit > 0)
                    {
                        //■総ダメージ
                        float damage = attackDamage2 * (float)nHit;
                        //■サーバへメッセージ
                        PacketHandler.INSTANCE.sendToServer(new MessageScars(target, damage));
                        //■傷は開いたのでリセット
                        this.setHitCount(dw, 0);

                        //■効果音ならすよー
                        isSoundSE = true;
                    }

                    //■Entityに接触しているSOWを開く
                    List<Entity> entities = playerIn.worldObj.weatherEffects;
                    if (entities == null || entities.isEmpty()) { continue; }

                    for (Entity entity : entities)
                    {
                        if (entity instanceof EntitySOW)
                        {
                            EntitySOW sow = (EntitySOW)entity;
                            if (sow.getEntityBoundingBox().intersectsWith(target.getEntityBoundingBox()))
                            {
                                //■重なってるので炸裂
                                // ▼サーバ側
                                PacketHandler.INSTANCE.sendToServer(new MessageOpenSOW(sow, false));

                                // ▼クライアント側
                                sow.changeScarsState();

                                //■効果音ならすよー
                                isSoundSE = true;
                            }
                        }
                    }
                }
            }
        }

        if (isSoundSE)
        {
            //playerIn.worldObj.playSoundAtEntity(playerIn, Hizume.MOD_ID + ":shan", 1.0f, 3.0f);
            PacketHandler.INSTANCE.sendToServer(new MessageOpenSOW(playerIn, true));
        }

        return target;
    }

    /**
     * ■対象EntityのDataWatcherについてる傷を取得
     * @param dwIn
     * @return
     */
    public int getHitCount(DataWatcher dwIn)
    {
        int countHit = 0;
        try
        {
            //■情報が刻まれているなら値を取得できる
            countHit = dwIn.getWatchableObjectInt(Hizume.getDWID());
        }
        catch(NullPointerException e)
        {
            //■初物
            dwIn.addObject(Hizume.getDWID(), Integer.valueOf(0));
        }

        return countHit;
    }

    /**
     * ■対象Entityに傷を指定数つける
     * @param dwIn
     * @param countHit
     */
    public void setHitCount(DataWatcher dwIn, int countHit)
    {
        //■確認用
        getHitCount(dwIn);

        //■設定
        dwIn.updateObject(Hizume.getDWID(), MathHelper.clamp_int(countHit, 0, Integer.MAX_VALUE - 1));
    }

    /**
     * ■対象Entityに傷を一つつける
     * @param dwIn
     */
    public void addHitCount(DataWatcher dwIn)
    {
        int countHit = MathHelper.clamp_int(getHitCount(dwIn), 0, Integer.MAX_VALUE - 1);

        setHitCount(dwIn, countHit);
    }

}
