package net.blazemc.better_collapse_indicator.components;

import net.blazemc.better_collapse_indicator.TFCBetterCollapseIndicator;
import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.recipes.CollapseRecipe;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.Support;
import net.dries007.tfc.util.events.CollapseEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Lazy;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public enum CollapseDetectorComponent implements IBlockComponentProvider {
    INSTANCE;

    private static final Lazy<TagKey<Block>> CAN_START_COLLAPSE =
            Lazy.of(() -> TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("tfc", "can_start_collapse")));
    private static final Lazy<TagKey<Block>> CAN_TRIGGER_COLLAPSE =
            Lazy.of(() -> TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("tfc", "can_trigger_collapse")));
    private static final Lazy<TagKey<Block>> CAN_LANDSLIDE =
            Lazy.of(() -> TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("tfc", "can_landslide")));

    @Override
    public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
        addSupportedTooltip(iTooltip, blockAccessor, iPluginConfig);
        addCollapseTooltip(iTooltip, blockAccessor, iPluginConfig);
    }

    //shows if a block is supported / partially supported (i.e. support by block below) / may collapse
    public void addSupportedTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig){
        if(!iPluginConfig.get(TFCBetterCollapseIndicator.COLLAPSE_SHOW_SUPPORT)) return;

        if(!(blockAccessor.getBlockState().is(CAN_START_COLLAPSE.get()) || blockAccessor.getBlockState().is(CAN_LANDSLIDE.get()))){
            return;
        }

        Level level = blockAccessor.getLevel();
        BlockPos pos = blockAccessor.getPosition();

        if(Support.isSupported(level, pos)){
            iTooltip.add(Component.translatable("better_collapse_indicator.supported").withStyle(ChatFormatting.DARK_GREEN));
        }else if(CollapseRecipe.canStartCollapse(level, pos)){
            iTooltip.add(Component.translatable("better_collapse_indicator.unsupported").withStyle(ChatFormatting.RED));
        }else{
            iTooltip.add(Component.translatable("better_collapse_indicator.partial_supported").withStyle(ChatFormatting.YELLOW));
        }
    }

    //shows if a block may trigger a nearby collapse
    public void addCollapseTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig){
        if(!(iPluginConfig.get(TFCBetterCollapseIndicator.COLLAPSE_SHOW_STABLE) || iPluginConfig.get(TFCBetterCollapseIndicator.COLLAPSE_SHOW_LOCATION))) return;
        if(!(blockAccessor.getBlockState().is(CAN_TRIGGER_COLLAPSE.get()))) return;

        Level level = blockAccessor.getLevel();
        BlockPos curPos = blockAccessor.getPosition();

        // from CollapseRecipe.tryTriggerCollapse in TFC
        int radX = (4 + 4) / 2;
        int radY = (2 + 2) / 2;
        int radZ = (4 + 4) / 2;
        List<BlockPos> unstableLocations = new ArrayList<>();

        for(BlockPos checking : Support.findUnsupportedPositions(level, curPos.offset(-radX, -radY, -radZ), curPos.offset(radX, radY, radZ))) {
            if (!checking.equals(curPos) && CollapseRecipe.canStartCollapse(level, checking)) {
                unstableLocations.add(checking.immutable());
            }
        }


        if(iPluginConfig.get(TFCBetterCollapseIndicator.COLLAPSE_SHOW_STABLE)){
            if(unstableLocations.isEmpty()){
                Component.translatable("better_collapse_indicator.wont_collapse").withStyle(ChatFormatting.DARK_GREEN);
            }else{
                iTooltip.add(Component.translatable("better_collapse_indicator.will_cause_collapse").withStyle(ChatFormatting.RED));
            }
        }

        if(!iPluginConfig.get(TFCBetterCollapseIndicator.COLLAPSE_SHOW_LOCATION)) return;
        if(unstableLocations.isEmpty()) return;

        unstableLocations.sort(Comparator.comparingInt(p -> p.distManhattan(curPos)));
        BlockPos nearest = unstableLocations.get(0);
        int totalUnstable = unstableLocations.size();
        Vec3i nearestRelative = nearest.subtract(curPos);

        iTooltip.add(Component.translatable("better_collapse_indicator.unstable", String.valueOf(totalUnstable), nearestRelative.toShortString()).withStyle(ChatFormatting.GRAY));
    }

    @Override
    public ResourceLocation getUid() {
        return TFCBetterCollapseIndicator.COLLAPSE_DETECTOR_ID;
    }
}
