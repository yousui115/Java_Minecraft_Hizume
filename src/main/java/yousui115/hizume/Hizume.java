package yousui115.hizume;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import yousui115.hizume.item.ItemHizume;
import yousui115.hizume.network.PacketHandler;

@Mod(modid = Hizume.MOD_ID, version = Hizume.VERSION)
public class Hizume
{
    public static final String MOD_ID = "hizume";
    public static final String MOD_DOMAIN = "yousui115." + Hizume.MOD_ID;
    public static final String VERSION = "1.0";

    //■このクラスのインスタンス
    @Mod.Instance(Hizume.MOD_ID)
    public static Hizume INSTANCE;

    //■クライアント側とサーバー側で異なるインスタンスを生成
    @SidedProxy(clientSide = Hizume.MOD_DOMAIN + ".client.ClientProxy",
                serverSide = Hizume.MOD_DOMAIN + ".CommonProxy")
    public static CommonProxy proxy;

    //■アイテム
    // ▼緋爪
    public static Item itemHizume;
    public static String nameHizume = "hizume";

    //■コンフィグデータ
    private static int nNoDW = 29;
    /**
     * ■初期化処理(前処理)
     * @param event
     */
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        //■みんな だいすき こんふぃぐれーしょん
        // ▼DataWatcherのNo
        Configuration config = new Configuration(event.getSuggestedConfigurationFile());
        nNoDW = MathHelper.clamp_int(config.get(Configuration.CATEGORY_GENERAL, "DataWatcherID", nNoDW, "Entity DataWatcher ID (25 - 31)").getInt(), 25, 31);
        config.save();

        //■1.アイテムのインスタンス生成
        itemHizume = new ItemHizume(ToolMaterial.EMERALD)
                            .setUnlocalizedName(this.nameHizume)
                            .setCreativeTab(CreativeTabs.tabCombat)
                            .setNoRepair();
        //■2.アイテムの登録
        GameRegistry.registerItem(itemHizume, nameHizume);
        //■3.テクスチャ・モデル指定JSONファイル名の登録
        proxy.registerModels();


        //■メッセージの初期設定
        PacketHandler.init();
    }

    //■相手に与える傷を与える場所(DataWatcher)
    public static int getNoDW()
    {
        return nNoDW;
    }
}
