package net.blazemc.better_collapse_indicator;

import net.blazemc.better_collapse_indicator.components.CollapseDetectorComponent;
import net.blazemc.better_collapse_indicator.components.LandSlideDetectorComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class CollapseIndicatorJadePlugin implements IWailaPlugin {
    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(CollapseDetectorComponent.INSTANCE, Block.class);
        registration.registerBlockComponent(LandSlideDetectorComponent.INSTANCE, Block.class);
        registration.markAsClientFeature(TFCBetterCollapseIndicator.COLLAPSE_DETECTOR_ID);
        registration.markAsClientFeature(TFCBetterCollapseIndicator.LANDSLIDE_DETECTOR_ID);


        registration.addConfig(TFCBetterCollapseIndicator.COLLAPSE_SHOW_STABLE, true);
        registration.addConfig(TFCBetterCollapseIndicator.COLLAPSE_SHOW_SUPPORT, true);
        registration.addConfig(TFCBetterCollapseIndicator.COLLAPSE_SHOW_LOCATION, true);
        registration.markAsClientFeature(TFCBetterCollapseIndicator.COLLAPSE_SHOW_STABLE);
        registration.markAsClientFeature(TFCBetterCollapseIndicator.COLLAPSE_SHOW_SUPPORT);
        registration.markAsClientFeature(TFCBetterCollapseIndicator.COLLAPSE_SHOW_LOCATION);
    }
}
