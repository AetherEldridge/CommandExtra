package studio.mnohrato.cmdextra;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(
	modid = CmdExtra.MOD_ID,
	bus = Mod.EventBusSubscriber.Bus.MOD
)
public class Config
{
	private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder( );
	static final         ForgeConfigSpec         SPEC;
	
	static
	{
		SPEC = BUILDER.build( );
	}
	
	@SubscribeEvent
	public static void onConfigReload( ModConfigEvent.Reloading event )
	{
		if( event.getConfig( ).getSpec( ) == SPEC )
		{
			// ...
		}
	}
	
	@SubscribeEvent
	static void onLoad( ModConfigEvent event )
	{
	}
}
