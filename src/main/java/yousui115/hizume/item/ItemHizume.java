package yousui115.hizume.item;

import java.util.ArrayList;
import java.util.Set;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yousui115.hizume.Hizume;
import yousui115.hizume.network.MessageScarsOfWind;
import yousui115.hizume.network.PacketHandler;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class ItemHizume extends ItemSword
{
    /**
     * ■コンストラクタ
     */
    public ItemHizume(ToolMaterial material)
    {
        super(material);
    }

//    /**
//     * If this function returns true (or the item is damageable), the ItemStack's NBT tag will be sent to the client.
//     */
//    @Override
//    public boolean getShareTag()
//    {
//        return false;
//    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getColorFromItemStack(ItemStack stack, int renderPass)
    {
        return 16777215;
    }

    /**
     * Called each tick as long the item is on a player inventory. Uses by maps to check if is on a player hand and
     * update it's contents.
     */
    @Override
    public void onUpdate(ItemStack stackIn, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
    {
        if (!isSelected)
        {
            //TODO 呼びすぎー！一定期間で適度に呼ばれるメソッドはないものか
            //■武器を切り替えたタイミングで呼ぶ
            arngSlashTarget(stackIn, worldIn);
        }
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

    /**
     * How long it takes to use or consume an item
     * ■モーション持続時間
     */
    @Override
    public int getMaxItemUseDuration(ItemStack stack)
    {
        return 72000;
    }

    /**
     * Called when an ItemStack with NBT data is read to potentially that ItemStack's NBT data
     */
    @Override
    public boolean updateItemStackNBT(NBTTagCompound nbt)
    {
        //TODO ItemStack.readFromNBT()から呼ばれる。(引数はItemStack.stackTagCompound)
        //     もしゲームロード時にのみ呼ばれるなら、nbtのリセットが可能っぽい。
        //     EntityIDの取り扱いに注意。
        if (nbt.hasKey(KEY_SLASH_TARGET))
        {
            nbt.removeTag(KEY_SLASH_TARGET);
        }
        return false;
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     * ■右クリック押した始めの1tickだけ呼ばれる。
     */
    @Override
    public ItemStack onItemRightClick(ItemStack stackIn, World worldIn, EntityPlayer playerIn)
    {
        return super.onItemRightClick(stackIn, worldIn, playerIn);
    }

    /* ======================================== FORGE START =====================================*/

    /**
     * ItemStack sensitive version of getItemAttributeModifiers
     */
    @Override
    public Multimap getAttributeModifiers(ItemStack stack)
    {
        HashMultimap multimap = HashMultimap.create();
        multimap.put(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(), new AttributeModifier(itemModifierUUID, "Weapon modifier", (double)2, 0));
        return multimap;
    }

    /**
     * Called each tick while using an item.
     * ■EnumAction中は毎Tick呼ばれる
     * @param stack The Item being used
     * @param player The Player using the item
     * @param count The amount of time in tick the item has been used for continuously
     */
    @Override
    public void onUsingTick(ItemStack stackIn, EntityPlayer playerIn, int count)
    {
        if (count > this.getMaxItemUseDuration(stackIn) - 20) { return; }

        //■プレイヤーの視線上にEntityがいるか否か
        if (playerIn.worldObj.isRemote)
        {
            if (Hizume.proxy.isPressScars())
            {
                this.doScars(stackIn, playerIn);
            }
        }
    }

    private final static String KEY_SLASH_TARGET= "hizume.slash_target";
//    private final static int dwID = 29;
    /**
     * Called when the player Left Clicks (attacks) an entity.
     * Processed before damage is done, if return value is true further processing is canceled
     * and the entity is not attacked.
     *
     * @param stack The Item being used
     * @param player The player that is attacking
     * @param entity The entity being attacked
     * @return True to cancel the rest of the interaction.
     */
    @Override
    public boolean onLeftClickEntity(ItemStack stackIn, EntityPlayer player, Entity entity)
    {
//        //■クライアントサイドのみで処理
//        if (!player.worldObj.isRemote)
//        {
            //■斬った相手のIDを取得
            int targetID = entity.getEntityId();

            //■傷の数を保持
            this.addSlashHitFromID(stackIn, targetID);

//        }

//        {
//            //※ 一度刻むと残り続ける情報なのでやめとく！
//
//            //■相手に傷をつける(DataWatcherに情報を刻む)
//            DataWatcher dw = entity.getDataWatcher();
//
//            int numHit = 0;
//            try
//            {
//                //■情報が刻まれているなら値を取得できる
//                numHit = dw.getWatchableObjectInt(dwID);
//            }
//            catch(NullPointerException e)
//            {
//                //■初物
//                dw.addObject(dwID, Integer.valueOf(0));
//            }
//
//            //■情報を刻む
//            dw.updateObject(dwID, ++numHit);
//        }

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
     * ■風の爪痕
     * @param stackIn
     * @param playerIn
     */
    protected void doScars(ItemStack stackIn, EntityPlayer playerIn)
    {
        //■クライアント側で検出する
        MovingObjectPosition mop = Hizume.proxy.getEntity(stackIn, playerIn);

        //■モップにEntityが引っかかってる
        if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY)
        {
            //■対象Entityを取得
            Entity target = mop.entityHit;

            int nHit = this.getSlashHitFromID(stackIn, target.getEntityId());

            if (nHit > 0)
            {
                //■総ダメージ
                int damage = 1 * nHit;
                //■サーバへメッセージ
                PacketHandler.INSTANCE.sendToServer(new MessageScarsOfWind(target, damage));
                //■傷は開いたのでリセット
                resetSlashTargetFromID(stackIn, target.getEntityId());
            }
        }
    }

    /**
     * ■KEY_SLASH_TARGET の NBTTag を取得
     * @param stackIn
     * @return
     */
    protected NBTTagCompound getSlashTarget(ItemStack stackIn)
    {
        NBTTagCompound nbt = stackIn.getTagCompound();
        if (nbt == null)
        {
            stackIn.setTagCompound(new NBTTagCompound());
            nbt = stackIn.getTagCompound();
        }

        if (!nbt.hasKey(KEY_SLASH_TARGET))
        {
            nbt.setTag(KEY_SLASH_TARGET, new NBTTagCompound());
        }

        return (NBTTagCompound)nbt.getTag(KEY_SLASH_TARGET);
    }

    /**
     * ■
     * @param stackIn   調査するアイテムスタック
     * @param entityID  対象EntityのID
     * @return nHit     爪痕攻撃回数
     */
    public int getSlashHitFromID(ItemStack stackIn, int entityID)
    {
        int nHit = 0;

        String targetKey = String.valueOf(entityID);
        NBTTagCompound tagSlash = getSlashTarget(stackIn);

        if (tagSlash.hasKey(targetKey))
        {
            nHit = tagSlash.getInteger(targetKey);
        }

        return nHit;
    }

    public void addSlashHitFromID(ItemStack stackIn, int entityID)
    {
        String targetKey = String.valueOf(entityID);
        NBTTagCompound tagSlash = getSlashTarget(stackIn);

        if (!tagSlash.hasKey(targetKey))
        {
            tagSlash.setInteger(targetKey, 1);
        }
        else
        {
            int n = tagSlash.getInteger(targetKey);
            tagSlash.setInteger(targetKey, n >= Integer.MAX_VALUE - 1 ?  Integer.MAX_VALUE - 1 : n + 1);
        }
    }

    public void removeSlashTargetFromID(ItemStack stackIn, int entityID)
    {
        String targetKey = String.valueOf(entityID);
        NBTTagCompound tagSlash = getSlashTarget(stackIn);

        tagSlash.removeTag(targetKey);
    }

    public void resetSlashTargetFromID(ItemStack stackIn, int entityID)
    {
        String targetKey = String.valueOf(entityID);
        NBTTagCompound tagSlash = getSlashTarget(stackIn);

        tagSlash.setInteger(targetKey, 0);
    }

    /**
     * ■存在しないEntity,お陀仏Entityをマップから排除
     *   (コンテナの使い方を勉強しましょう。)
     */
    public void arngSlashTarget(ItemStack stackIn, World worldIn)
    {
        NBTTagCompound tagSlash = getSlashTarget(stackIn);

        Set<String> setKey = tagSlash.getKeySet();
        ArrayList<String> eraseKey = new ArrayList<String>();

        if (setKey != null)
        {
            for (String s : setKey)
            {
                Entity entity = worldIn.getEntityByID(Integer.parseInt(s));
                if (entity == null || entity.isDead)
                {
                    //tagSlash.removeTag(s);
                    eraseKey.add(s);
                }
            }

            for (String s : eraseKey)
            {
                tagSlash.removeTag(s);
                System.out.println((worldIn.isRemote ? "Client" : "Server") + " : remove = " + s);
            }
        }
    }
}
