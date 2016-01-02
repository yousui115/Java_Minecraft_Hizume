package yousui115.hizume.client;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Timer;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.ReflectionHelper.UnableToAccessFieldException;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.apache.logging.log4j.Level;
import org.lwjgl.input.Keyboard;

import yousui115.hizume.CommonProxy;
import yousui115.hizume.Hizume;
import yousui115.hizume.client.render.RenderSOW;
import yousui115.hizume.entity.EntitySOW;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy
{
    //■緋爪のモデルリソース群
    protected ModelResourceLocation mrHizume[] = new ModelResourceLocation[arrNum];

    //キーのUnlocalizedName、バインドするキーの対応整数値（Keyboardクラス参照のこと）、カテゴリー名
    public static final KeyBinding keyScars = new KeyBinding("key.scars", Keyboard.KEY_R, "Hizume");
    public static final KeyBinding keySOW   = new KeyBinding(  "key.sow", Keyboard.KEY_F, "Hizume");

    /**
     * ■バインドするキーの登録
     */
    @Override
    public void registerKeyBind()
    {
        ClientRegistry.registerKeyBinding(keyScars);
        ClientRegistry.registerKeyBinding(keySOW);
    }

    /**
     * ■キー入力の有無
     */
    //public boolean is
    @Override
    public KeyBinding getKeyScars() { return keyScars; }
    @Override
    public KeyBinding getKeySOW() { return keySOW; }

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
        RenderingRegistry.registerEntityRenderingHandler(EntitySOW.class, new RenderSOW(getRenderManager()));
    }

    /**
     * ■視線先のEntityを捕捉する
     *   EntityRender.getMouseOver()をぱくぱく
     */
    @Override
    public List<MovingObjectPosition> getEntity(ItemStack itemStackIn, EntityPlayer playerIn)
    {
        List<MovingObjectPosition> targets = new ArrayList<MovingObjectPosition>(); //new ArrayList<MovingObjectPosition>();

        if (playerIn != null && playerIn.worldObj != null)
        {
            MovingObjectPosition blockMOP = null;

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
            blockMOP = playerIn.rayTrace(range, timer.renderPartialTicks);

            //■プレイヤーの視点位置
            //  (renderPartialTicks:0.0 - 1.0) 一回前の描画位置からどれだけ動いたかの比率(?)として使ってる(?)
            Vec3 playerEyePos = playerIn.getPositionEyes(timer.renderPartialTicks);

            //■入手したモップがある
            if (blockMOP != null)
            {
                //■「プレイヤーの視点」と「最寄ブロック」との距離を取得
                nearBlockRange = blockMOP.hitVec.distanceTo(playerEyePos);
            }

            //■プレイヤーの視線(単位ベクトル)(ベクトルに乗算するメソッドが無い事に驚愕)
            Vec3 playerSight = playerIn.getLook(timer.renderPartialTicks);
            //■プレイヤーの視線(レンジ)
            Vec3 playerRange = playerEyePos.addVector(  playerSight.xCoord * range,
                                                        playerSight.yCoord * range,
                                                        playerSight.zCoord * range);

            //■プレイヤーの視線(立方体)に入っているEntityをかき集める
            float f1 = 1.0f;    //立方体拡張(上下に1.0F)
            AxisAlignedBB aabb1 = playerIn.getEntityBoundingBox().addCoord( playerSight.xCoord * range,
                                                                            playerSight.yCoord * range,
                                                                            playerSight.zCoord * range).expand(f1, f1, f1);
            List<Entity> list = playerIn.worldObj.getEntitiesWithinAABBExcludingEntity(playerIn, aabb1);

            //TODO ごり押し君(SOWはEntityWeatherの為)
            list.addAll(playerIn.worldObj.weatherEffects);

            //■Entity! 君に決めた！ 枠
            Entity mob      = null;
            Vec3   mobDist  = null;
            double mobRange = nearBlockRange;
            Entity sow      = null;
            Vec3   sowDist  = null;
            double sowRange = nearBlockRange;

            //■かき集めたEntity共をチェックしていく(candidate:候補者)
            for (Entity candidate : list)
            {
                //■「判定なし」かつ「SOWでは無い」を満たすEntityはスルー
                //if (!candidate.canBeCollidedWith() && !(candidate instanceof EntitySOW)) { continue; }

                //■ドラゴン(概念)は対象外
                if (candidate instanceof EntityDragon) { continue; }

                //■「not 生物」かつ「not ドラゴンパーツ」かつ「not SOW」を満たすEntityはスルー
                if (!(candidate instanceof EntityLivingBase) &&
                    !(candidate instanceof EntityDragonPart) &&
                    !(candidate instanceof EntitySOW))
                { continue; }

                //■ターゲットの当たり判定を拡張
                float fExpand = candidate.getCollisionBorderSize() * 3f;
                AxisAlignedBB aabb2 = candidate.getEntityBoundingBox().expand(fExpand, fExpand, fExpand);

                //■拡張したターゲットの当たり判定と視線(レンジ)が交差するなら、モップが帰る
                MovingObjectPosition targetMOP = aabb2.calculateIntercept(playerEyePos, playerRange);

                //■ターゲットの中、あったかいナリ(当たり判定が重なってる)
                if (aabb2.isVecInside(playerEyePos))
                {
                    //■Entityの種類によって格納するインスタンスを分ける
                    if (candidate instanceof EntitySOW)
                    {
                        //■距離が近いので更新
                        if (0.0D < sowRange || sowRange == 0.0D)
                        {
                            sow = candidate;
                            sowDist = targetMOP == null ? playerEyePos : targetMOP.hitVec;
                            sowRange = 0.0D;
                        }
                    }
                    else
                    {
                        //■距離が近いので更新
                        if (0.0D < mobRange || mobRange == 0.0D)
                        {
                            mob = candidate;
                            mobDist = targetMOP == null ? playerEyePos : targetMOP.hitVec;
                            mobRange = 0.0D;
                        }
                    }
                }
                //■帰ってきたモップ
                else if (targetMOP != null)
                {
                    double range2 = playerEyePos.distanceTo(targetMOP.hitVec);

                    //■前に調べた奴より距離が近い
                    if (candidate instanceof EntitySOW)
                    {
                        if (range2 < sowRange || sowRange == 0.0D)
                        {
                            sow = candidate;
                            sowDist = targetMOP.hitVec;
                            sowRange = range2;
                        }
                    }
                    else
                    {
                        if (range2 < mobRange || mobRange == 0.0D)
                        {
                            //■現在乗ってるEntity かつ 対象Entityに乗った状態で右クリックが有効か否か(このメソッド、使えそう)
                            if (candidate == playerIn.ridingEntity && !candidate.canRiderInteract())
                            {
                                //TODO このシーケンスには入れるの？
                                if (mobRange == 0.0D)
                                {
                                    mob = candidate;
                                    mobDist = targetMOP.hitVec;
                                }
                            }
                            else
                            {
                                mob = candidate;
                                mobDist = targetMOP.hitVec;
                                mobRange = range2;
                            }
                        }
                    }

                }
            }

            //■調べ終えました。
            //  「ターゲット」が居る かつ
            //   (「ターゲットの間にブロックが無い」 または
            //    「モップがヌルポ(TODO どうやったらこの条件になるか要検証)」)
            if (mob != null && (mobRange < nearBlockRange || blockMOP == null))
            {
                //mop = new MovingObjectPosition(mob, mobDist);
                targets.add(new MovingObjectPosition(mob, mobDist));
                //TODO
                //System.out.println("targets.add(mob) = " + mob.getName());
            }

            if (sow != null && (sowRange < nearBlockRange || blockMOP == null))
            {
                targets.add(new MovingObjectPosition(sow, sowDist));
                //TODO
                //System.out.println("targets.add(sow) = " + sow.getName());
            }
        }

        return targets;
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
