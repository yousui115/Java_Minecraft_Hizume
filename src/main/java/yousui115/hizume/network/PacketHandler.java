package yousui115.hizume.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import yousui115.hizume.Hizume;

public class PacketHandler
{
    //■このMOD用のSimpleNetworkWrapperを生成
    //  チャンネルの文字列は固有であれば何でも良い。MODIDの利用を推奨。
    public static final SimpleNetworkWrapper INSTANCE
                        = NetworkRegistry.INSTANCE.newSimpleChannel(Hizume.MOD_ID);

    /**
     * ■
     */
    public static void init()
    {
        /*IMesssageHandlerクラスとMessageクラスの登録。
        *第三引数：MessageクラスのMOD内での登録ID。256個登録できる
        *第四引数：送り先指定。クライアントかサーバーか、Side.CLIENT Side.SERVER*/
        INSTANCE.registerMessage(     MessageScarsHandler.class,      MessageScars.class, 0, Side.SERVER);

    }
}
