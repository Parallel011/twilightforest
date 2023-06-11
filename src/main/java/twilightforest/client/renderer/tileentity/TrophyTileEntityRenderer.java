package twilightforest.client.renderer.tileentity;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import twilightforest.TwilightForestMod;
import twilightforest.block.AbstractTrophyBlock;
import twilightforest.block.TrophyBlock;
import twilightforest.block.TrophyWallBlock;
import twilightforest.block.entity.TrophyBlockEntity;
import twilightforest.client.model.TFModelLayers;
import twilightforest.client.model.tileentity.*;
import twilightforest.client.model.tileentity.legacy.HydraTrophyLegacyModel;
import twilightforest.client.model.tileentity.legacy.MinoshroomTrophyLegacyModel;
import twilightforest.client.model.tileentity.legacy.QuestRamTrophyLegacyModel;
import twilightforest.client.model.tileentity.legacy.SnowQueenTrophyLegacyModel;
import twilightforest.enums.BossVariant;
import twilightforest.init.TFBlocks;

import java.util.Map;

public class TrophyTileEntityRenderer implements BlockEntityRenderer<TrophyBlockEntity> {

	private final Map<BossVariant, GenericTrophyModel> trophies;

	private static final ResourceLocation textureLocHydra = TwilightForestMod.getModelTexture("hydra4.png");
	private static final ResourceLocation textureLocNaga = TwilightForestMod.getModelTexture("nagahead.png");
	private static final ResourceLocation textureLocLich = TwilightForestMod.getModelTexture("twilightlich64.png");
	private static final ResourceLocation textureLocUrGhast = TwilightForestMod.getModelTexture("towerboss.png");
	private static final ResourceLocation textureLocSnowQueen = TwilightForestMod.getModelTexture("snowqueen.png");
	private static final ResourceLocation textureLocMinoshroom = TwilightForestMod.getModelTexture("minoshroomtaur.png");
	private static final ResourceLocation textureLocKnightPhantom = TwilightForestMod.getModelTexture("phantomskeleton.png");
	private static final ResourceLocation textureLocKnightPhantomArmor = new ResourceLocation(TwilightForestMod.ARMOR_DIR + "phantom_1.png");
	private static final ResourceLocation textureLocYeti = TwilightForestMod.getModelTexture("yetialpha.png");
	private static final ResourceLocation textureLocQuestRam = TwilightForestMod.getModelTexture("questram.png");
	private static final ResourceLocation textureLocQuestRamLines = TwilightForestMod.getModelTexture("questram_lines.png");

	public TrophyTileEntityRenderer(BlockEntityRendererProvider.Context renderer) {
		this.trophies = createTrophyRenderers(renderer.getModelSet());
	}

	public static Map<BossVariant, GenericTrophyModel> createTrophyRenderers(EntityModelSet set) {
		boolean legacy = !TwilightForestMod.isJappaPackLoaded();
		ImmutableMap.Builder<BossVariant, GenericTrophyModel> trophyList = ImmutableMap.builder();
		trophyList.put(BossVariant.NAGA, new NagaTrophyModel(set.bakeLayer(TFModelLayers.NAGA_TROPHY)));
		trophyList.put(BossVariant.LICH, new LichTrophyModel(set.bakeLayer(TFModelLayers.LICH_TROPHY)));
		trophyList.put(BossVariant.MINOSHROOM, legacy ? new MinoshroomTrophyLegacyModel(set.bakeLayer(TFModelLayers.NEW_MINOSHROOM_TROPHY)) : new MinoshroomTrophyModel(set.bakeLayer(TFModelLayers.MINOSHROOM_TROPHY)));
		trophyList.put(BossVariant.HYDRA, legacy ? new HydraTrophyLegacyModel(set.bakeLayer(TFModelLayers.NEW_HYDRA_TROPHY)) : new HydraTrophyModel(set.bakeLayer(TFModelLayers.HYDRA_TROPHY)));
		trophyList.put(BossVariant.KNIGHT_PHANTOM, new KnightPhantomTrophyModel(set.bakeLayer(TFModelLayers.KNIGHT_PHANTOM_TROPHY)));
		trophyList.put(BossVariant.UR_GHAST, new UrGhastTrophyModel(set.bakeLayer(TFModelLayers.UR_GHAST_TROPHY)));
		trophyList.put(BossVariant.ALPHA_YETI, new AlphaYetiTrophyModel(set.bakeLayer(TFModelLayers.ALPHA_YETI_TROPHY)));
		trophyList.put(BossVariant.SNOW_QUEEN, legacy ? new SnowQueenTrophyLegacyModel(set.bakeLayer(TFModelLayers.NEW_SNOW_QUEEN_TROPHY)) : new SnowQueenTrophyModel(set.bakeLayer(TFModelLayers.SNOW_QUEEN_TROPHY)));
		trophyList.put(BossVariant.QUEST_RAM, legacy ? new QuestRamTrophyLegacyModel(set.bakeLayer(TFModelLayers.NEW_QUEST_RAM_TROPHY)) : new QuestRamTrophyModel(set.bakeLayer(TFModelLayers.QUEST_RAM_TROPHY)));
		return trophyList.build();
	}

	public static final ItemStack stack = new ItemStack(TFBlocks.NAGA_TROPHY.get());
	@Override
	public void render(TrophyBlockEntity tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
		float f = tileEntityIn.getAnimationProgress(partialTicks);
		BlockState blockstate = tileEntityIn.getBlockState();
		boolean flag = blockstate.getBlock() instanceof TrophyWallBlock;
		Direction direction = flag ? blockstate.getValue(TrophyWallBlock.FACING) : null;
		float f1 = 22.5F * (flag ? (2 + direction.get2DDataValue()) * 4 : blockstate.getValue(TrophyBlock.ROTATION));
		BossVariant variant = ((AbstractTrophyBlock) blockstate.getBlock()).getVariant();
		GenericTrophyModel trophy = this.trophies.get(variant);
		matrixStackIn.pushPose();
		if (variant == BossVariant.HYDRA) {
			trophy.openMouthForTrophy(flag ? 0.5F : 0.0F);
		}
		if (variant == BossVariant.UR_GHAST) {
			((UrGhastTrophyModel)trophy).setTranslate(matrixStackIn, 0F, 1.0F, 0F);
		}
		render(direction, f1, trophy, variant, f, matrixStackIn, bufferIn, combinedLightIn, ItemDisplayContext.NONE);
		matrixStackIn.popPose();
	}

	public static void render(@Nullable Direction directionIn, float y, GenericTrophyModel trophy, BossVariant variant, float animationProgress, PoseStack matrixStackIn, MultiBufferSource buffer, int combinedLight, ItemDisplayContext camera) {
		boolean legacy = !TwilightForestMod.isJappaPackLoaded();
		matrixStackIn.pushPose();
		if (directionIn == null || variant == BossVariant.UR_GHAST) {
			matrixStackIn.translate(0.5D, 0.01D, 0.5D);
		} else {
			matrixStackIn.translate(0.5F - directionIn.getStepX() * 0.249F, 0.25D, 0.5F - directionIn.getStepZ() * 0.249F);
		}
		matrixStackIn.scale(-1.0F, -1.0F, 1.0F);
		switch (variant) {
			case HYDRA -> {
				matrixStackIn.scale(0.25F, 0.25F, 0.25F);
				if (camera == ItemDisplayContext.GUI) {
					trophy.openMouthForTrophy(0.35F);
				}
				trophy.setRotations(animationProgress * 4.5F, y, 0.0F);
				matrixStackIn.translate(legacy ? 1.0F : 0.0F, legacy ? -1.15F : -1.0F, 0.0F);
				VertexConsumer hydraVertex = buffer.getBuffer(RenderType.entityCutoutNoCull(textureLocHydra));
				trophy.renderToBuffer(matrixStackIn, hydraVertex, combinedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
			}
			case NAGA -> {
				matrixStackIn.scale(0.5f, 0.5f, 0.5f);
				trophy.setRotations(animationProgress * 4.5F, y, 0.0F);
				matrixStackIn.translate(0F, .25F, 0F);
				VertexConsumer nagaVertex = buffer.getBuffer(RenderType.entityCutoutNoCull(textureLocNaga));
				trophy.renderToBuffer(matrixStackIn, nagaVertex, combinedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
			}
			case LICH -> {
				matrixStackIn.translate(0.0F, .25F, 0.0F);
				trophy.setRotations(animationProgress * 4.5F, y, 0.0F);
				VertexConsumer lichVertex = buffer.getBuffer(RenderType.entityCutoutNoCull(textureLocLich));
				trophy.renderToBuffer(matrixStackIn, lichVertex, combinedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
			}
			case UR_GHAST -> {
				matrixStackIn.scale(0.5F, 0.5F, 0.5F);
				trophy.setRotations(animationProgress * 4.5F, y, 0.0F);
				VertexConsumer ghastVertex = buffer.getBuffer(RenderType.entityCutoutNoCull(textureLocUrGhast));
				trophy.renderToBuffer(matrixStackIn, ghastVertex, combinedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
			}
			case SNOW_QUEEN -> {
				trophy.setRotations(animationProgress * 4.5F, y, 0.0F);
				matrixStackIn.translate(0.0F, legacy ? 0.25F : 0.0F, 0.0F);
				VertexConsumer waifuVertex = buffer.getBuffer(RenderType.entityCutoutNoCull(textureLocSnowQueen));
				trophy.renderToBuffer(matrixStackIn, waifuVertex, combinedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
			}
			case MINOSHROOM -> {
				trophy.setRotations(animationProgress * 4.5F, y, 0.0F);
				matrixStackIn.translate(0.0F, legacy ? 0.12F : 0.065F,  legacy ? 0.56F : 0.0F);
				VertexConsumer minoVertex = buffer.getBuffer(RenderType.entityCutoutNoCull(textureLocMinoshroom));
				trophy.renderToBuffer(matrixStackIn, minoVertex, combinedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
			}
			case KNIGHT_PHANTOM -> {
				trophy.setRotations(animationProgress * 4.5F, y, 0.0F);
				matrixStackIn.translate(0.0F, 0.25F, 0.0F);
				VertexConsumer phantomVertex = buffer.getBuffer(RenderType.entityCutoutNoCull(textureLocKnightPhantom));
				trophy.renderToBuffer(matrixStackIn, phantomVertex, combinedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
				matrixStackIn.scale(1.1F, 1.1F, 1.1F);
				matrixStackIn.translate(0.0F, 0.05F, 0.0F);
				trophy.setRotations(animationProgress * 4.5F, y, 0.0F);
				VertexConsumer phantomArmorVertex = buffer.getBuffer(RenderType.entityCutoutNoCull(textureLocKnightPhantomArmor));
				trophy.renderHelmToBuffer(matrixStackIn, phantomArmorVertex, combinedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 0.0625F);
			}
			case ALPHA_YETI -> {
				matrixStackIn.scale(0.2F, 0.2F, 0.2F);
				trophy.setRotations(animationProgress * 4.5F, y, 0.0F);
				matrixStackIn.translate(0.0F, -1.5F, 0.0F);
				VertexConsumer yetiVertex = buffer.getBuffer(RenderType.entityCutoutNoCull(textureLocYeti));
				trophy.renderToBuffer(matrixStackIn, yetiVertex, combinedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
			}
			case QUEST_RAM -> {
				matrixStackIn.scale(0.7f, 0.7f, 0.7f);
				trophy.setRotations(animationProgress * 4.5F, y, 0.0F);
				VertexConsumer ramVertex = buffer.getBuffer(RenderType.entityCutoutNoCull(textureLocQuestRam));
				trophy.renderToBuffer(matrixStackIn, ramVertex, combinedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
			}
			default -> {
			}
		}
		matrixStackIn.popPose();
	}
}
