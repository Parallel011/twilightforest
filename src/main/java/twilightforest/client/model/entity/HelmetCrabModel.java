// Date: 4/14/2013 12:59:03 PM
// Template version 1.1
// Java generated by Techne
// Keep in mind that you still need to fill in some blanks
// - ZeuX

package twilightforest.client.model.entity;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import twilightforest.entity.monster.HelmetCrab;

/**
 * Helmet crab model, based partially on some of the spider code
 */
public class HelmetCrabModel extends HierarchicalModel<HelmetCrab> {
	//fields
	final ModelPart root;
	final ModelPart body;
	final ModelPart rightArm;
	final ModelPart leg1;
	final ModelPart leg2;
	final ModelPart leg3;
	final ModelPart leg4;
	final ModelPart leg5;

	public HelmetCrabModel(ModelPart root) {
		this.root = root;

		this.body = root.getChild("body");
		this.rightArm = root.getChild("right_arm");

		this.leg1 = root.getChild("leg_1");
		this.leg2 = root.getChild("leg_2");
		this.leg3 = root.getChild("leg_3");
		this.leg4 = root.getChild("leg_4");
		this.leg5 = root.getChild("leg_5");
	}

	public static LayerDefinition create() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition partRoot = mesh.getRoot();

		var body = partRoot.addOrReplaceChild("body", CubeListBuilder.create()
						.texOffs(32, 4)
						.addBox(-2.5F, -2.5F, -5F, 5, 5, 5),
				PartPose.offset(0F, 19F, 0F));

		body.addOrReplaceChild("right_eye", CubeListBuilder.create()
						.texOffs(10, 0)
						.addBox(-1F, -3F, -1F, 2, 3, 2),
				PartPose.offsetAndRotation(-1F, -1F, -4F, (Mth.PI / 4.0F), 0.0F, -(Mth.PI / 4.0F)));

		body.addOrReplaceChild("left_eye", CubeListBuilder.create()
						.texOffs(10, 0)
						.addBox(-1F, -3F, -1F, 2, 3, 2),
				PartPose.offsetAndRotation(1F, -1F, -4F, (Mth.PI / 4.0F), 0.0F, (Mth.PI / 4.0F)));

		var helmetBase = partRoot.addOrReplaceChild("helmet_base", CubeListBuilder.create(),
				PartPose.offsetAndRotation(0F, 18F, 0F, -100F / (180F / Mth.PI), -30F / (180F / Mth.PI), 0.0F));

		helmetBase.addOrReplaceChild("helmet", CubeListBuilder.create()
						.texOffs(0, 14)
						.addBox(-3.5F, -11.0F, -3.5F, 7, 11, 7),
				PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 45F / (180F / Mth.PI), 0.0F));

		var rightHorn = helmetBase.addOrReplaceChild("right_horn_1", CubeListBuilder.create()
						.texOffs(28, 14)
						.addBox(-6F, -1.5F, -1.5F, 7, 3, 3),
				PartPose.offsetAndRotation(-3.5F, -9F, 0.0F, 0.0F, -15F / (180F / Mth.PI), 10F / (180F / Mth.PI)));

		rightHorn.addOrReplaceChild("right_horn_2", CubeListBuilder.create()
						.texOffs(28, 20)
						.addBox(-3.0F, -1.0F, -1.0F, 3, 2, 2),
				PartPose.offsetAndRotation(-5.5F, 0.0F, 0.0F, 0.0F, -15F / (180F / Mth.PI), 10F / (180F / Mth.PI)));

		var leftHorn = helmetBase.addOrReplaceChild("left_horn_1", CubeListBuilder.create()
						.texOffs(28, 14)
						.addBox(-6F, -1.5F, -1.5F, 7, 3, 3),
				PartPose.offsetAndRotation(3.5F, -9F, 0.0F, 0.0F, 15F / (180F / Mth.PI), -10F / (180F / Mth.PI)));

		leftHorn.addOrReplaceChild("left_horn_2", CubeListBuilder.create()
						.texOffs(28, 20)
						.addBox(-3.0F, -1.0F, -1.0F, 3, 2, 2),
				PartPose.offsetAndRotation(5.5F, 0.0F, 0.0F, 0.0F, 15F / (180F / Mth.PI), -10F / (180F / Mth.PI)));

		var rightArm = partRoot.addOrReplaceChild("right_arm", CubeListBuilder.create()
						.texOffs(38, 0)
						.addBox(-7F, -1F, -1F, 8, 2, 2),
				PartPose.offsetAndRotation(-3F, 20F, -3F, 0F, -1.319531F, -0.1919862F));

		var clawBase = rightArm.addOrReplaceChild("claw_base", CubeListBuilder.create()
						.texOffs(0, 0)
						.addBox(0F, -1.5F, -1F, 3, 3, 2),
				PartPose.offsetAndRotation(-6F, 0F, -0.5F, 0.0F, (Mth.PI / 2.0F), 0));

		clawBase.addOrReplaceChild("claw_bottom", CubeListBuilder.create()
						.texOffs(0, 8)
						.addBox(0F, -0.5F, -1F, 3, 2, 2),
				PartPose.offsetAndRotation(3F, 0F, 0F, 0F, 0F, 0.2602503F));

		clawBase.addOrReplaceChild("claw_top", CubeListBuilder.create()
						.texOffs(0, 5)
						.addBox(0F, -0.5F, -1F, 3, 1, 2),
				PartPose.offsetAndRotation(3F, -1F, 0F, 0F, 0F, -0.1858931F));

		partRoot.addOrReplaceChild("leg_1", CubeListBuilder.create()
						.texOffs(18, 0)
						.addBox(-7F, -1F, -1F, 8, 2, 2),
				PartPose.offsetAndRotation(-3F, 20F, -1F, 0F, 0.2792527F, -0.1919862F));

		partRoot.addOrReplaceChild("leg_2", CubeListBuilder.create()
						.texOffs(18, 0)
						.addBox(-1F, -1F, -1F, 8, 2, 2),
				PartPose.offsetAndRotation(3F, 20F, -1F, 0F, -0.2792527F, 0.1919862F));

		partRoot.addOrReplaceChild("leg_3", CubeListBuilder.create()
						.texOffs(18, 0)
						.addBox(-7F, -1F, -1F, 8, 2, 2),
				PartPose.offsetAndRotation(-3F, 20F, -2F, 0F, -0.2792527F, -0.1919862F));

		partRoot.addOrReplaceChild("leg_4", CubeListBuilder.create()
						.texOffs(18, 0)
						.addBox(-1F, -1F, -1F, 8, 2, 2),
				PartPose.offsetAndRotation(3F, 20F, -2F, 0F, 0.2792527F, 0.1919862F));

		partRoot.addOrReplaceChild("leg_5", CubeListBuilder.create()
						.texOffs(18, 0)
						.addBox(-1F, -1F, -1F, 8, 2, 2),
				PartPose.offsetAndRotation(3F, 20F, -3F, 0F, 0.5759587F, 0.1919862F));

		return LayerDefinition.create(mesh, 64, 32);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}

	@Override
	public void setupAnim(HelmetCrab entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

		//leg3 -> leg1, leg4 -> leg2, leg5 -> leg3, leg6 -> leg4, leg8 -> leg5
		this.body.yRot = netHeadYaw / (180F / (float) Math.PI);
		this.body.xRot = headPitch / (180F / (float) Math.PI);

		float f6 = ((float) Math.PI / 4F);
		this.leg1.zRot = -f6 * 0.74F;
		this.leg2.zRot = f6 * 0.74F;
		this.leg3.zRot = -f6 * 0.74F;
		this.leg4.zRot = f6 * 0.74F;
		this.leg5.zRot = f6;

		float f7 = -0.0F;
		float f8 = 0.3926991F;
		this.leg1.yRot = f8 + f7;
		this.leg2.yRot = -f8 - f7;
		this.leg3.yRot = -f8 + f7;
		this.leg4.yRot = f8 - f7;
		this.leg5.yRot = f8 * 2.0F - f7;

		float f10 = -(Mth.cos(limbSwing * 0.6662F * 2.0F + (float) Math.PI) * 0.4F) * limbSwingAmount;
		float f11 = -(Mth.cos(limbSwing * 0.6662F * 2.0F + ((float) Math.PI / 2F)) * 0.4F) * limbSwingAmount;
		float f12 = -(Mth.cos(limbSwing * 0.6662F * 2.0F + ((float) Math.PI * 3F / 2F)) * 0.4F) * limbSwingAmount;
		float f14 = Math.abs(Mth.sin(limbSwing * 0.6662F + (float) Math.PI) * 0.4F) * limbSwingAmount;
		float f15 = Math.abs(Mth.sin(limbSwing * 0.6662F + ((float) Math.PI / 2F)) * 0.4F) * limbSwingAmount;
		float f16 = Math.abs(Mth.sin(limbSwing * 0.6662F + ((float) Math.PI * 3F / 2F)) * 0.4F) * limbSwingAmount;
		this.leg1.yRot += f10;
		this.leg2.yRot -= f10;
		this.leg3.yRot += f11;
		this.leg4.yRot -= f11;
		this.leg5.yRot -= f12;

		this.leg1.zRot += f14;
		this.leg2.zRot -= f14;
		this.leg3.zRot += f15;
		this.leg4.zRot -= f15;
		this.leg5.zRot -= f16;

		// swing right arm as if it were an arm, not a leg
		this.rightArm.yRot = -1.319531F;
		this.rightArm.yRot += Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 2.0F * limbSwingAmount * 0.5F;
	}
}
