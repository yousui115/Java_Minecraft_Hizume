package yousui115.hizume;

import java.util.List;

import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;

public class CommonProxy
{
    public static final int arrNum = 13;    //Jsonファイル数

    public void registerKeyBind(){}

    public KeyBinding getKeyScars() { return null; }
    public KeyBinding getKeySOW() { return null; }

    /**
     * ■モデルの登録
     */
    public void registerModels(){}

    /**
     * ■緋爪のモデルを取得
     */
    public ModelResourceLocation getHizumeModelRL(int nNo) { return null; }

    /**
     * ■レンダラの登録
     */
    public void registerRenderers(){}

    /**
     * ■視線の先のEntityを捕捉する
     */
    public List<MovingObjectPosition> getEntity(ItemStack itemStackIn, EntityPlayer playerIn){ return null; }

    public EntityPlayer getEntityPlayerInstance() { return null; }
    public RenderItem getRenderItem() { return null; }
    public RenderManager getRenderManager() { return null; }

}
