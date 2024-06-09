package net.einsteinsci.betterbeginnings;

import cpw.mods.fml.common.*;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.einsteinsci.betterbeginnings.config.BBConfig;
import net.einsteinsci.betterbeginnings.crafttweaker.MineTweakerCompat;
import net.einsteinsci.betterbeginnings.event.BBEventHandler;
import net.einsteinsci.betterbeginnings.event.Worldgen;
import net.einsteinsci.betterbeginnings.network.*;
import net.einsteinsci.betterbeginnings.register.*;
import net.einsteinsci.betterbeginnings.register.achievement.RegisterAchievements;
import net.einsteinsci.betterbeginnings.util.LogUtil;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.common.AchievementPage;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

@Mod(modid = ModMain.MODID, version = ModMain.VERSION, name = ModMain.NAME,
     guiFactory = "net.einsteinsci.betterbeginnings.config.BBConfigGuiFactory")
public class ModMain
{
	public static final String MODID = "betterbeginnings";
	public static final String VERSION = "b0.9.6-R2a";
	public static final String NAME = "BetterBeginnings";
	public static final CreativeTabs tabBetterBeginnings = new CreativeTabs("tabBetterBeginnings")
	{
		@Override
		@SideOnly(Side.CLIENT)
		public Item getTabIconItem()
		{
			return RegisterItems.flintKnife;
		}
	};

	@Instance(ModMain.MODID)
	public static ModMain modInstance;
	public static Configuration configFile;
	public static SimpleNetworkWrapper network;
	@SidedProxy(clientSide = "net.einsteinsci.betterbeginnings.network.ClientProxy",
	            serverSide = "net.einsteinsci.betterbeginnings.network.ServerProxy")
	public static ServerProxy proxy;
	public BBEventHandler eventHandler = new BBEventHandler();

	@EventHandler
	public void preInit(FMLPreInitializationEvent e)
	{
		LogUtil.log("Starting pre-initialization...");

		configFile = new Configuration(e.getSuggestedConfigurationFile());
		configFile.load();
		BBConfig.initialize();
		BBConfig.syncConfig(configFile);

		proxy.registerNetworkStuff();
		proxy.registerRenderThings();


		FMLCommonHandler.instance().bus().register(eventHandler);
		MinecraftForge.EVENT_BUS.register(eventHandler);

		network = NetworkRegistry.INSTANCE.newSimpleChannel("bbchannel");
		network.registerMessage(PacketNetherBrickOvenFuelLevel.PacketHandler.class,
		    PacketNetherBrickOvenFuelLevel.class, 0, Side.CLIENT);
		network.registerMessage(PacketCampfireState.PacketHandler.class,
			PacketCampfireState.class, 1, Side.CLIENT);

		RegisterItems.register();
		RegisterBlocks.register();
		RegisterTileEntities.register();
	}

	@EventHandler
	public void init(FMLInitializationEvent e)
	{
		RemoveRecipes.remove();
		RegisterRecipes.addShapelessRecipes();
		RegisterRecipes.addShapedRecipes();
		RegisterRecipes.addAdvancedRecipes();
		RegisterRecipes.addFurnaceRecipes();

		if (BBConfig.moduleFurnaces)
		{
			RemoveRecipes.removeFurnaceRecipes();
		}
		if(Loader.isModLoaded("MineTweaker3"))
		{
			MineTweakerCompat.register();
		}
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent e)
	{
		BBConfig.fillAlwaysBreakable();
		BBConfig.fillAlsoPickaxes();
		BBConfig.fillAlsoAxes();

		RegisterItems.tweakVanilla();
		Worldgen.addWorldgen();
		AchievementPage.registerAchievementPage(new AchievementPage(NAME, RegisterAchievements.getAchievements()));
		LogUtil.log("Finished post-initialization.");
	}
}
