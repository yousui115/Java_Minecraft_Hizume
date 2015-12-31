package yousui115.hizume.client;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Timer;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.ReflectionHelper.UnableToAccessFieldException;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.apache.logging.log4j.Level;
import org.lwjgl.input.Keyboard;

import yousui115.hizume.CommonProxy;
import yousui115.hizume.Hizume;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy
{
    //■緋爪のモデルリソース群
    protected ModelResourceLocation mrHizume[] = new ModelResourceLocation[arrNum];

    //キーのUnlocalizedName、バインドするキーの対応整数値（Keyboardクラス参照のこと）、カテゴリー名
    public static final KeyBinding keyScars = new KeyBinding("key.scars", Keyboard.KEY_R, "Hizume");

    @Override
    public void registerClientInfo()
    {
        ClientRegistry.registerKeyBinding(keyScars);
    }

    @Override
    public boolean isPressScars()
    {
        return keyScars.isPressed();
    }

    /**
     * ■モデルの登録
     */
    @Override
    public void registerModels()
    {
        String strName[] = new String[arrNum];
        for (int idx = 0; idx < strName.length; idx++)
        {
            //■リソース文字列
            strName[idx] = Hizume.MOD_ID + ":" + Hizume.nameHizume + (idx == 0 ? "" : idx);
            //■リソース
            mrHizume[idx] = new ModelResourceLocation(strName[idx], "inventory");
        }

        ModelLoader.setCustomModelResourceLocation(Hizume.itemHizume, 0, mrHizume[0]);

        ModelBakery.addVariantName( Hizume.itemHizume, strName);
    }

    /**
     * ■緋爪のリソースを取得
     */
    public ModelResourceLocation getHizumeModelRL(int nNo)
    {
        nNo = MathHelper.clamp_int(nNo, 0, this.arrNum - 1);
        return mrHizume[nNo];
    }

    /**
     * ■レンダラの登録
     */
    @Override
    public void registerRenderers()
    {

    }

    /**
     * ■視線先のEntityを捕捉する
     *   EntityRender.getMouseOver()をぱくぱく
     */
    @Override
    public MovingObjectPosition getEntity(ItemStack itemStackIn, EntityPlayer playerIn)
    {
        MovingObjectPosition mop = null;

        if (playerIn != null && playerIn.worldObj != null)
        {
            //■りふれくしょん！
            Timer timer;
            try
            {
                timer = ObfuscationReflectionHelper.getPrivateValue(Minecraft.class, Minecraft.getMinecraft(), 17);
            }
            catch (UnableToAccessFieldException e)
            {
                FMLLog.log(Level.ERROR, e, "りふれくしょん！ に失敗しました！><  ClientProxy.getEntity()");
                throw e;
            }

            //■レンジ(チンッ！)
            double range = 20d;
            //■直近のブロックとの距離
            double nearBlockRange = 0;

            //■れいとれーす！
            //  (プレイヤーの視線の先で最寄のブロックまでの距離を返す。
            //   ブロックが無かったら指定したレンジぐらいのアタイ！を返す)
            //  TODO 奈落とか天上だとどうなるか要検証
            mop = playerIn.rayTrace(range, timer.renderPartialTicks);

            //■プレイヤーの視点位置
            //  (renderPartialTicks:0.0 - 1.0) 一回前の描画位置からどれだけ動いたかの比率(?)として使ってる(?)
            Vec3 playerEyePos = playerIn.getPositionEyes(timer.renderPartialTicks);

            //■入手したモップがある
            if (mop != null)
            {
                //■モップとプレイヤー視点との距離を取得
                nearBlockRange = mop.hitVec.distanceTo(playerEyePos);
            }

            //■プレイヤーの視線
            Vec3 playerSight = playerIn.getLook(timer.renderPartialTicks);
            //■プレイヤーの間合い
            Vec3 playerRange = playerEyePos.addVector(  playerSight.xCoord * range,
                                                        playerSight.yCoord * range,
                                                        playerSight.zCoord * range);

            //■プレイヤーの間合いに入っているEntityをかき集める
            float f1 = 1.0f;    //間合い拡張(上下に1.0F)
            AxisAlignedBB aabb1 = playerIn.getEntityBoundingBox().addCoord(  playerSight.xCoord * range,
                                                                            playerSight.yCoord * range,
                                                                            playerSight.zCoord * range).expand(f1, f1, f1);
            List<Entity> list = playerIn.worldObj.getEntitiesWithinAABBExcludingEntity(playerIn, aabb1);

            //■最寄のブロックより近い位置にいるEntityの距離を保存する用
            double nearEntityRange = nearBlockRange;

            //■二人(プレイヤーと対象Entity)の距離
            Vec3 distance = null;

            //■Entity!君に決めた！ 枠
            Entity target = null;

            //■かき集めたEntity共をチェックしていく(candidate:候補者)
            for (Entity candidate : list)
            {
                if (!candidate.canBeCollidedWith()) { continue; }

                //■ターゲットの当たり判定を拡張
                float fExpand = candidate.getCollisionBorderSize();
                AxisAlignedBB aabb2 = candidate.getEntityBoundingBox().expand(fExpand, fExpand, fExpand);

                //■拡張したターゲットの当たり判定と間合いが交差するなら、モップが帰る
                MovingObjectPosition targetMOP = aabb2.calculateIntercept(playerEyePos, playerRange);

                //■ターゲットの中、あったかいナリ(当たり判定が重なってる)
                if (aabb2.isVecInside(playerEyePos))
                {
                    //TODO これ、絶対入（って）るよね？(マイナスが入る可能性はあるのか？)
                    if (0.0D < nearEntityRange || nearEntityRange == 0.0D)
                    {
                        target = candidate;
                        distance = targetMOP == null ? playerEyePos : targetMOP.hitVec;
                        nearEntityRange = 0.0D;
                    }
                }
                //■帰ってきたモップ
                else if (targetMOP != null)
                {
                    double range2 = playerEyePos.distanceTo(targetMOP.hitVec);

                    //■前に調べた奴より距離が近い
                    if (range2 < nearEntityRange || nearEntityRange == 0.0D)
                    {
                        //■現在乗ってるEntity かつ 対象Entityに乗った状態で右クリックが有効か否か(このメソッド、使えそう)
                        if (candidate == playerIn.ridingEntity && !candidate.canRiderInteract())
                        {
                            //TODO このシーケンスには入れるの？
                            if (nearEntityRange == 0.0D)
                            {
                                target = candidate;
                                distance = targetMOP.hitVec;
                            }
                        }
                        else
                        {
                            target = candidate;
                            distance = targetMOP.hitVec;
                            nearEntityRange = range2;
                        }
                    }
                }
            }

            //■調べ終えました。
            //  「ターゲット」が居る かつ
            //   (「ターゲットの間にブロックが無い」 または
            //    「モップがヌルポ(TODO どうやったらこの条件になるか要検証)」)
            if (target != null && (nearEntityRange < nearBlockRange || mop == null))
            {
                mop = new MovingObjectPosition(target, distance);
            }
        }

        return mop;
    }


    @Override
    public EntityPlayer getEntityPlayerInstance()
    {
        return Minecraft.getMinecraft().thePlayer;
    }

    @Override
    public RenderManager getRenderManager()
    {
        return Minecraft.getMinecraft().getRenderManager();
    }

    @Override
    public RenderItem getRenderItem()
    {
        return Minecraft.getMinecraft().getRenderItem();
    }
}
