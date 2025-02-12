package com.sobhi.mod.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.sobhi.mod.MyMod;
import com.sobhi.mod.client.model.DroneModel;
import com.sobhi.mod.entity.EntityDrone;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;


public class DroneRenderer extends EntityRenderer<EntityDrone> {
    private final DroneModel<EntityDrone> model;

    public DroneRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new DroneModel<>(context.bakeLayer(DroneModel.LAYER_LOCATION));
    }

    @Override
    public ResourceLocation getTextureLocation(EntityDrone entity) {
        return ResourceLocation.fromNamespaceAndPath(MyMod.MOD_ID, "textures/entity/drone.png");
    }

    @Override
    public void render(EntityDrone entity, float yaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(180 - yaw));
        poseStack.translate(0.0F, -1.5F, 0.0F);

        VertexConsumer vertexConsumer = buffer.getBuffer(this.model.renderType(this.getTextureLocation(entity)));
        this.model.renderToBuffer(poseStack, vertexConsumer, packedLight,
                OverlayTexture.NO_OVERLAY, 1);

        poseStack.popPose();
        super.render(entity, yaw, partialTick, poseStack, buffer, packedLight);
    }
}