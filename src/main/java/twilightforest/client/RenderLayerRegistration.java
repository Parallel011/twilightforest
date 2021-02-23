package twilightforest.client;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import twilightforest.block.TFBlocks;

public class RenderLayerRegistration {
	public static void init() {
		RenderType cutoutMipped = RenderType.getCutoutMipped();
		RenderType cutout = RenderType.getCutout();
		RenderType translucent = RenderType.getTranslucent();
		RenderTypeLookup.setRenderLayer(TFBlocks.oak_leaves.get(), cutoutMipped);
		RenderTypeLookup.setRenderLayer(TFBlocks.rainboak_leaves.get(), cutoutMipped);
		RenderTypeLookup.setRenderLayer(TFBlocks.canopy_leaves.get(), cutoutMipped);
		RenderTypeLookup.setRenderLayer(TFBlocks.mangrove_leaves.get(), cutoutMipped);
		RenderTypeLookup.setRenderLayer(TFBlocks.time_leaves.get(), cutoutMipped);
		RenderTypeLookup.setRenderLayer(TFBlocks.transformation_leaves.get(), cutoutMipped);
		RenderTypeLookup.setRenderLayer(TFBlocks.mining_leaves.get(), cutoutMipped);
		RenderTypeLookup.setRenderLayer(TFBlocks.sorting_leaves.get(), cutoutMipped);
		RenderTypeLookup.setRenderLayer(TFBlocks.twilight_portal.get(), translucent);
		RenderTypeLookup.setRenderLayer(TFBlocks.auroralized_glass.get(), translucent);
		RenderTypeLookup.setRenderLayer(TFBlocks.thorn_rose.get(), cutout);
		RenderTypeLookup.setRenderLayer(TFBlocks.thorn_leaves.get(), cutoutMipped);
		RenderTypeLookup.setRenderLayer(TFBlocks.beanstalk_leaves.get(), cutoutMipped);
		RenderTypeLookup.setRenderLayer(TFBlocks.experiment_115.get(), cutout);
		RenderTypeLookup.setRenderLayer(TFBlocks.wispy_cloud.get(), translucent);
		RenderTypeLookup.setRenderLayer(TFBlocks.uberous_soil.get(), cutout);
		RenderTypeLookup.setRenderLayer(TFBlocks.trollvidr.get(), cutout);
		RenderTypeLookup.setRenderLayer(TFBlocks.unripe_trollber.get(), cutout);
		RenderTypeLookup.setRenderLayer(TFBlocks.trollber.get(), cutout);
		RenderTypeLookup.setRenderLayer(TFBlocks.huge_lilypad.get(), cutout);
		RenderTypeLookup.setRenderLayer(TFBlocks.huge_waterlily.get(), cutout);
		RenderTypeLookup.setRenderLayer(TFBlocks.castle_rune_brick_yellow.get(), cutout);
		RenderTypeLookup.setRenderLayer(TFBlocks.castle_rune_brick_purple.get(), cutout);
		RenderTypeLookup.setRenderLayer(TFBlocks.castle_rune_brick_pink.get(), cutout);
		RenderTypeLookup.setRenderLayer(TFBlocks.castle_rune_brick_blue.get(), cutout);
		RenderTypeLookup.setRenderLayer(TFBlocks.castle_door_yellow.get(), cutout);
		RenderTypeLookup.setRenderLayer(TFBlocks.castle_door_purple.get(), cutout);
		RenderTypeLookup.setRenderLayer(TFBlocks.castle_door_pink.get(), cutout);
		RenderTypeLookup.setRenderLayer(TFBlocks.castle_door_blue.get(), cutout);
		RenderTypeLookup.setRenderLayer(TFBlocks.green_thorns.get(), cutout);
		RenderTypeLookup.setRenderLayer(TFBlocks.brown_thorns.get(), cutout);
		RenderTypeLookup.setRenderLayer(TFBlocks.burnt_thorns.get(), cutout);
		RenderTypeLookup.setRenderLayer(TFBlocks.twilight_portal_miniature_structure.get(), cutout);
//		RenderTypeLookup.setRenderLayer(TFBlocks.hedge_maze_miniature_structure.get(), cutout);
//		RenderTypeLookup.setRenderLayer(TFBlocks.hollow_hill_miniature_structure.get(), cutout);
//		RenderTypeLookup.setRenderLayer(TFBlocks.quest_grove_miniature_structure.get(), cutout);
//		RenderTypeLookup.setRenderLayer(TFBlocks.mushroom_tower_miniature_structure.get(), cutout);
		RenderTypeLookup.setRenderLayer(TFBlocks.naga_courtyard_miniature_structure.get(), cutout);
		RenderTypeLookup.setRenderLayer(TFBlocks.lich_tower_miniature_structure.get(), cutout);
//		RenderTypeLookup.setRenderLayer(TFBlocks.minotaur_labyrinth_miniature_structure.get(), cutout);
//		RenderTypeLookup.setRenderLayer(TFBlocks.hydra_lair_miniature_structure.get(), cutout);
//		RenderTypeLookup.setRenderLayer(TFBlocks.goblin_stronghold_miniature_structure.get(), cutout);
//		RenderTypeLookup.setRenderLayer(TFBlocks.dark_tower_miniature_structure.get(), cutout);
//		RenderTypeLookup.setRenderLayer(TFBlocks.yeti_cave_miniature_structure.get(), cutout);
//		RenderTypeLookup.setRenderLayer(TFBlocks.aurora_palace_miniature_structure.get(), cutout);
//		RenderTypeLookup.setRenderLayer(TFBlocks.troll_cave_cottage_miniature_structure.get(), cutout);
//		RenderTypeLookup.setRenderLayer(TFBlocks.final_castle_miniature_structure.get(), cutout);
		RenderTypeLookup.setRenderLayer(TFBlocks.fiery_block.get(), cutout);
		RenderTypeLookup.setRenderLayer(TFBlocks.firefly_jar.get(), cutout);
		RenderTypeLookup.setRenderLayer(TFBlocks.moss_patch.get(), cutout);
		RenderTypeLookup.setRenderLayer(TFBlocks.mayapple.get(), cutout);
		RenderTypeLookup.setRenderLayer(TFBlocks.clover_patch.get(), cutout);
		RenderTypeLookup.setRenderLayer(TFBlocks.fiddlehead.get(), cutout);
		RenderTypeLookup.setRenderLayer(TFBlocks.mushgloom.get(), cutout);
		RenderTypeLookup.setRenderLayer(TFBlocks.torchberry_plant.get(), cutout);
		RenderTypeLookup.setRenderLayer(TFBlocks.root_strand.get(), cutout);
		RenderTypeLookup.setRenderLayer(TFBlocks.fallen_leaves.get(), cutout);
		RenderTypeLookup.setRenderLayer(TFBlocks.uncrafting_table.get(), cutout);
		RenderTypeLookup.setRenderLayer(TFBlocks.encased_smoker.get(), cutout);
		RenderTypeLookup.setRenderLayer(TFBlocks.encased_fire_jet.get(), cutout);
		RenderTypeLookup.setRenderLayer(TFBlocks.oak_sapling.get(), cutout);
		RenderTypeLookup.setRenderLayer(TFBlocks.rainboak_sapling.get(), cutout);
		RenderTypeLookup.setRenderLayer(TFBlocks.canopy_sapling.get(), cutout);
		RenderTypeLookup.setRenderLayer(TFBlocks.mangrove_sapling.get(), cutout);
		RenderTypeLookup.setRenderLayer(TFBlocks.darkwood_sapling.get(), cutout);
		RenderTypeLookup.setRenderLayer(TFBlocks.hollow_oak_sapling.get(), cutout);
		RenderTypeLookup.setRenderLayer(TFBlocks.time_sapling.get(), cutout);
		RenderTypeLookup.setRenderLayer(TFBlocks.transformation_sapling.get(), cutout);
		RenderTypeLookup.setRenderLayer(TFBlocks.mining_sapling.get(), cutout);
		RenderTypeLookup.setRenderLayer(TFBlocks.sorting_sapling.get(), cutout);
		RenderTypeLookup.setRenderLayer(TFBlocks.built_block.get(), cutout);
		RenderTypeLookup.setRenderLayer(TFBlocks.antibuilt_block.get(), cutout);
		RenderTypeLookup.setRenderLayer(TFBlocks.reactor_debris.get(), cutout);
		RenderTypeLookup.setRenderLayer(TFBlocks.force_field_pink.get(), translucent);
		RenderTypeLookup.setRenderLayer(TFBlocks.force_field_blue.get(), translucent);
		RenderTypeLookup.setRenderLayer(TFBlocks.force_field_green.get(), translucent);
		RenderTypeLookup.setRenderLayer(TFBlocks.force_field_purple.get(), translucent);
		RenderTypeLookup.setRenderLayer(TFBlocks.force_field_orange.get(), translucent);
		RenderTypeLookup.setRenderLayer(TFBlocks.boss_spawner.get(), cutout);
		RenderTypeLookup.setRenderLayer(TFBlocks.reappearing_block.get(), cutout);
		RenderTypeLookup.setRenderLayer(TFBlocks.locked_vanishing_block.get(), cutout);
		RenderTypeLookup.setRenderLayer(TFBlocks.vanishing_block.get(), cutout);
		RenderTypeLookup.setRenderLayer(TFBlocks.carminite_builder.get(), cutout);
		RenderTypeLookup.setRenderLayer(TFBlocks.antibuilder.get(), cutout);
		RenderTypeLookup.setRenderLayer(TFBlocks.carminite_reactor.get(), cutout);
		RenderTypeLookup.setRenderLayer(TFBlocks.ghast_trap.get(), cutout);
		RenderTypeLookup.setRenderLayer(TFBlocks.potted_twilight_oak_sapling.get(), cutout);
		RenderTypeLookup.setRenderLayer(TFBlocks.potted_canopy_sapling.get(), cutout);
		RenderTypeLookup.setRenderLayer(TFBlocks.potted_mangrove_sapling.get(), cutout);
		RenderTypeLookup.setRenderLayer(TFBlocks.potted_darkwood_sapling.get(), cutout);
		RenderTypeLookup.setRenderLayer(TFBlocks.potted_hollow_oak_sapling.get(), cutout);
		RenderTypeLookup.setRenderLayer(TFBlocks.potted_rainboak_sapling.get(), cutout);
		RenderTypeLookup.setRenderLayer(TFBlocks.potted_time_sapling.get(), cutout);
		RenderTypeLookup.setRenderLayer(TFBlocks.potted_trans_sapling.get(), cutout);
		RenderTypeLookup.setRenderLayer(TFBlocks.potted_mine_sapling.get(), cutout);
		RenderTypeLookup.setRenderLayer(TFBlocks.potted_sort_sapling.get(), cutout);
		RenderTypeLookup.setRenderLayer(TFBlocks.potted_mayapple.get(), cutout);
		RenderTypeLookup.setRenderLayer(TFBlocks.potted_fiddlehead.get(), cutout);
		RenderTypeLookup.setRenderLayer(TFBlocks.potted_mushgloom.get(), cutout);
		RenderTypeLookup.setRenderLayer(TFBlocks.potted_thorn.get(), cutout);
		RenderTypeLookup.setRenderLayer(TFBlocks.potted_green_thorn.get(), cutout);
		RenderTypeLookup.setRenderLayer(TFBlocks.potted_dead_thorn.get(), cutout);
	}
}
