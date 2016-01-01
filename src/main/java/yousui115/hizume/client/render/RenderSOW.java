package yousui115.hizume.client.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;

import yousui115.hizume.Hizume;
import yousui115.hizume.entity.EntitySOW;

/**
 * ■Render Scars of Wind
 */
@SideOnly(Side.CLIENT)
public class RenderSOW extends Render
{
    protected static final ResourceLocation resource = new ResourceLocation(Hizume.MOD_ID, "textures/entity/sow.png");

    //■てせれーたー
    protected static Tessellator tessellator = Tessellator.getInstance();

    //■わーるどれんだらー
    protected static WorldRenderer worldrenderer = tessellator.getWorldRenderer();

    private static double[][] dVec = {  {     0,   1.2,  -0.5},  // 頂点0
                                        {     0,  0.75,     0},  // 頂点1
                                        {   0.1,   0.6, -0.15},  // 頂点2
                                        {     0,   0.5, -0.25},  // 頂点3
                                        {  -0.1,   0.6, -0.15},  // 頂点4
                                        {     0,     0,  0.25},  // 頂点5
                                        {  0.25,     0,     0},  // 頂点6
                                        {     0,     0, -0.25},  // 頂点7
                                        { -0.25,     0,     0},  // 頂点8
                                        {     0, -0.75,     0},  // 頂点9
                                        {   0.1,  -0.6, -0.15},  // 頂点10
                                        {     0,  -0.5, -0.25},  // 頂点11
                                        {  -0.1,  -0.6, -0.15},  // 頂点12
                                        {     0,  -1.2,  -0.5}}; // 頂点13

    private static int[][] nVecPos = {  { 0,  1,  2,  3},  //面1(頂点 0, 1, 2, 3)
                                        { 0,  3,  4,  1},  //面2
                                        { 1,  5,  6,  2},  //面3
                                        { 3,  2,  6,  7},  //面4
                                        { 3,  7,  8,  4},  //面5
                                        { 1,  4,  8,  5},  //面6
                                        { 6,  5,  9, 10},  //面7
                                        { 6, 10, 11,  7},  //面8
                                        { 8,  7, 11, 12},  //面9
                                        { 8, 12,  9,  5},  //面10
                                        {10,  9, 13, 11},  //面11
                                        {12, 11, 13,  9}}; //面12

    /**
     * ■コンストラクタ
     * @param renderManager
     */
    public RenderSOW(RenderManager renderManager)
    {
        super(renderManager);
    }

    /**
     * ■頭の上に名前を表示するか否か
     */
    @Override
    protected boolean canRenderName(Entity entity)
    {
        return false;
    }

    /**
     * ■描画処理
     */
    @Override
    public void doRender(Entity entity, double x, double y, double z, float p_76986_8_, float partialTicks)
    {
        if (entity != null && entity instanceof EntitySOW)
        {
            doRenderSOW((EntitySOW)entity, x, y, z, p_76986_8_, partialTicks);
        }
    }

    /**
     * ■リソース(バインドしたいテクスチャ)
     */
    @Override
    protected ResourceLocation getEntityTexture(Entity entity)
    {
        return resource;
    }

    /* ======================================== イカ、自作   =====================================*/

    protected void doRenderSOW(EntitySOW sow, double dx, double dy, double dz, float f1, float partial)
    {
        //■■■■ 前処理 ■■■■
        //■描画準備

        //GlStateManager.depthMask(false);

        // ▼画像のバインド
        this.bindEntityTexture(sow);

        // ▼テクスチャの貼り付け ON
        GlStateManager.enableTexture2D();

        // ▼ライティング OFF
        //GlStateManager.enableLighting();
        GlStateManager.disableLighting();

        // ▼陰影処理の設定(なめらか)
        //GlStateManager.shadeModel(GL11.GL_SMOOTH);

        // ▼ブレンドモード ON
        GlStateManager.enableBlend();

        // ▼加算+アルファ
//        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        //GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE);
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

        // ▼アルファ
        //GlStateManager.disableAlpha();
        //GlStateManager.enableAlpha();

        // ▼指定のテクスチャユニットとBrightnessX,Y(値を上げれば明るく見える！)
        //OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 150f, 150f);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 61680f, 0f);

        // ▼法線の再スケーリング(?) ON
        GlStateManager.enableRescaleNormal();

        // ▼頂点カラー
        GlStateManager.color(1f, 0.0f, 0.0f, 0.7f);

        //■座標系の調整
        GlStateManager.pushMatrix();

        //■拡縮、回転、位置の調整(FILOなので注意)
        // ▼4.位置
        GlStateManager.translate(dx, dy + 1d, dz);
        // ▼3.回転(Y軸)
        GlStateManager.rotate(-sow.rotationYaw, 0f, 1f, 0f);
        // ▼2.回転(X軸)
        GlStateManager.rotate(sow.rotationPitch, 1f, 0f, 0f);
        // ▼2.回転(Z軸)
        GlStateManager.rotate(sow.fRollRnd, 0f, 0f, 1f);
        // ▼1.縮小
        GlStateManager.scale(0.1f + 0.1f * (float)sow.tickScars, 0.5f, 0.1f);


        //■■■■ 本処理 ■■■■
        //■描画モード
        worldrenderer.startDrawingQuads();

        //■？
        worldrenderer.setNormal(0.0F, 1.0F, 0.0F);

        //◆頂点登録 開始
        for (int scale = 0; scale < 4; scale++)
        {
            double dScale = 1 + scale * 0.3;
            for(int idx = 0; idx < nVecPos.length; idx++)
            {
                worldrenderer.addVertexWithUV(dVec[nVecPos[idx][0]][0] * dScale, dVec[nVecPos[idx][0]][1] * dScale, dVec[nVecPos[idx][0]][2] * dScale, 0, 0);
                worldrenderer.addVertexWithUV(dVec[nVecPos[idx][1]][0] * dScale, dVec[nVecPos[idx][1]][1] * dScale, dVec[nVecPos[idx][1]][2] * dScale, 0, 1);
                worldrenderer.addVertexWithUV(dVec[nVecPos[idx][2]][0] * dScale, dVec[nVecPos[idx][2]][1] * dScale, dVec[nVecPos[idx][2]][2] * dScale, 1, 1);
                worldrenderer.addVertexWithUV(dVec[nVecPos[idx][3]][0] * dScale, dVec[nVecPos[idx][3]][1] * dScale, dVec[nVecPos[idx][3]][2] * dScale, 1, 0);
            }
        }

        //■描画
        tessellator.draw();


        //■■■■ 後処理 ■■■■
        //■座標系の後始末
        // ▼行列の削除
        GlStateManager.popMatrix();

        //■描画後始末
        //  注意:設定した全てを逆に設定し直すのはNG
        //       disableTexture2D()なんてしたら描画がえらい事に！
        // ▼法線の再スケーリング(?) OFF
        GlStateManager.disableRescaleNormal();

        // ▼指定のテクスチャユニットとBrightnessX,Y(値を上げれば明るく見える！)
        //OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 0f, 0f);

        // ▼アルファ
        GlStateManager.enableAlpha();

        // ▼ブレンドモード OFF
        GlStateManager.disableBlend();

        // ▼陰影処理の設定(フラット:一面同じ色)
        GlStateManager.shadeModel(GL11.GL_FLAT);

        // ▼ライティング ON
        GlStateManager.enableLighting();
        //GlStateManager.disableLighting();

        GlStateManager.depthMask(true);


    }

}
