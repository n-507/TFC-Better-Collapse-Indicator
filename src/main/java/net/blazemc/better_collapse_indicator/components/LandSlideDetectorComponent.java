package net.blazemc.better_collapse_indicator.components;

import net.blazemc.better_collapse_indicator.TFCBetterCollapseIndicator;
import net.dries007.tfc.common.entities.misc.TFCFallingBlockEntity;
import net.dries007.tfc.common.recipes.LandslideRecipe;
import net.dries007.tfc.util.Support;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.Lazy;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

import java.util.ArrayList;

public enum LandSlideDetectorComponent implements IBlockComponentProvider {
    INSTANCE;

    private static final Lazy<TagKey<Block>> CAN_LANDSLIDE =
            Lazy.of(() -> TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("tfc", "can_landslide")));

    @Override
    public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
        BlockPos curPos = blockAccessor.getPosition();
        Level level = blockAccessor.getLevel();

        if (blockAccessor.getBlockState().is(CAN_LANDSLIDE.get())) {
            if (willLandSlide(level, curPos, null)) {
                iTooltip.add(Component.translatable("better_collapse_indicator.will_landslide").withStyle(ChatFormatting.RED));
            }
        }

        ArrayList<BlockPos> possibleFallPos = new ArrayList<>(4+4+1);
        for(Direction side : Direction.Plane.HORIZONTAL){
            BlockPos thatPos = curPos.relative(side);
            possibleFallPos.add(thatPos);
            possibleFallPos.add(thatPos.above());
        }
        possibleFallPos.add(curPos.above());

        for(BlockPos pos : possibleFallPos){
            if(level.getBlockState(pos).is(CAN_LANDSLIDE.get()) && willLandSlide(level, pos, curPos)){
                iTooltip.add(Component.translatable("better_collapse_indicator.cause_landslide").withStyle(ChatFormatting.RED));
                break;
            }
        }

    }

    @Override
    public ResourceLocation getUid() {
        return TFCBetterCollapseIndicator.LANDSLIDE_DETECTOR_ID;
    }

    public boolean willLandSlide(Level level, BlockPos pos, @Nullable BlockPos miningPos){
        BlockState fallingState = level.getBlockState(pos);
        if (Support.isSupported(level, pos)) {
            return false;
        } else if (TFCFallingBlockEntity.canFallThrough(level, pos.below(), Direction.DOWN, fallingState)) {
            return true;
        } else if ((miningPos != null) && (miningPos.equals(pos.below()))){
            return true;
        } else {
            if (!LandslideRecipe.isSupportedOnSide(level, pos, Direction.UP)) {
                int supportedDirections = 0;

                boolean hasUnsupportedSide = false;
                for(Direction side : Direction.Plane.HORIZONTAL) {
                    BlockPos supportPos = pos.relative(side);
                    if ((!supportPos.equals(miningPos)) && LandslideRecipe.isSupportedOnSide(level, pos, side)) {
                        ++supportedDirections;
                        if (supportedDirections >= 2) {
                            return false;
                        }
                    } else {
                        BlockPos posSide = pos.relative(side);
                        BlockPos posSideBelow = posSide.below();
                        if (TFCFallingBlockEntity.canFallThrough(level, posSide, side, fallingState) && TFCFallingBlockEntity.canFallThrough(level, posSideBelow, Direction.DOWN)) {
                            hasUnsupportedSide = true;
                        }
                    }
                }
                return hasUnsupportedSide;
            }

            return false;
        }
    }
}
