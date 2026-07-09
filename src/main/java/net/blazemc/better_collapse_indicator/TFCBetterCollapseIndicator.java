package net.blazemc.better_collapse_indicator;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

@Mod(TFCBetterCollapseIndicator.MODID)
public class TFCBetterCollapseIndicator {

    public static final String MODID = "better_collapse_indicator";
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final ResourceLocation COLLAPSE_DETECTOR_ID = ResourceLocation.fromNamespaceAndPath(MODID, "collapse");
    public static final ResourceLocation LANDSLIDE_DETECTOR_ID = ResourceLocation.fromNamespaceAndPath(MODID, "landslide");

    public static final ResourceLocation COLLAPSE_SHOW_SUPPORT = ResourceLocation.fromNamespaceAndPath(MODID, "collapse.show_support");
    public static final ResourceLocation COLLAPSE_SHOW_STABLE = ResourceLocation.fromNamespaceAndPath(MODID, "collapse.show_trigger_collapse");
    public static final ResourceLocation COLLAPSE_SHOW_LOCATION = ResourceLocation.fromNamespaceAndPath(MODID, "collapse.show_unstable_location");

    public TFCBetterCollapseIndicator() {
        MinecraftForge.EVENT_BUS.register(this);
    }
}
