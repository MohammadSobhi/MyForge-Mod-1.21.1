package com.sobhi.mod.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.sobhi.mod.MyMod;
import com.sobhi.mod.entity.EntityDrone;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;

import javax.swing.*;

public class DroneModel<T extends EntityDrone> extends HierarchicalModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(MyMod.MOD_ID, "drone"), "main");
    private final ModelPart body;

    // Constructor
    public DroneModel(ModelPart root) {
        this.body = root.getChild("body");
    }

    // Define model parts (cubes)
    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition parts = mesh.getRoot();

        // Define the drone's body (adjust as needed)
        parts.addOrReplaceChild("body", CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(0f, 0f, 0f, 10, 10, 10), // Size: 8x4x8
                PartPose.offset(0f, 0f, 0f)
        );

        return LayerDefinition.create(mesh, 16, 16); // Texture size (16x16)
    }

    // Fix: Updated renderToBuffer signature
    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        body.render(poseStack, buffer, packedLight, packedOverlay, color);
    }

    // Animation method (required but unused)
    @Override
    public void setupAnim(EntityDrone entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        // No animations needed for a drone
    }

    @Override
    public ModelPart root() {
        return body;
    }
}
