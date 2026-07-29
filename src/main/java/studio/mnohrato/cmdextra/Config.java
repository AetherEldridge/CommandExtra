package studio.mnohrato.cmdextra;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(
	modid = CmdExtra.MOD_ID,
	bus = Mod.EventBusSubscriber.Bus.MOD
)
public class Config
{
	private static final ForgeConfigSpec.Builder                                 BUILDER = new ForgeConfigSpec.Builder( );
//	public static final  ForgeConfigSpec.ConfigValue< List< ? extends String > > BANNED_ITEMS;
	static final         ForgeConfigSpec                                         SPEC;
	
	static
	{
//		BUILDER.comment( "About items cannot obtained in game" ).push( "banned_items" );
//		{
//			BANNED_ITEMS = BUILDER.comment( "List of the banned items" ).defineList( "ids" , List.of( "minecraft:command_block" ) , o -> o instanceof String );
//		}
		SPEC = BUILDER.build( );
	}
	
//	public static List< ? extends String > BANNED_ITEMS_LIST = null;
	
	@SubscribeEvent
	static void onLoad( ModConfigEvent event )
	{
//		BANNED_ITEMS_LIST = BANNED_ITEMS.get( );
	}
}
