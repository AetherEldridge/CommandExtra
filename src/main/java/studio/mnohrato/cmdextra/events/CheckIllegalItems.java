//package studio.mnohrato.cmdextra.events;
//
//import net.minecraft.network.chat.Component;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.server.level.ServerPlayer;
//import net.minecraftforge.event.TickEvent;
//import net.minecraftforge.eventbus.api.SubscribeEvent;
//import net.minecraftforge.fml.common.Mod;
//import net.minecraftforge.registries.ForgeRegistries;
//import org.jetbrains.annotations.NotNull;
//import studio.mnohrato.cmdextra.Config;
//import studio.mnohrato.cmdextra.TpToPlayer;
//
//import java.util.List;
//import java.util.regex.Pattern;
//
//@Mod.EventBusSubscriber( modid = TpToPlayer.MOD_ID )
//public class CheckIllegalItems
//{
//	@SubscribeEvent
//	public static void onServerTick( @NotNull TickEvent.ServerTickEvent event )
//	{
//		List.of( event.getServer( ).getPlayerNames( ) ).forEach( playerName -> {
//			ServerPlayer player = event.getServer( ).getPlayerList( ).getPlayerByName( playerName );
//			if( player != null ) for( String item : Config.BANNED_ITEMS_LIST )
//			{
//				ResourceLocation rl = ForgeRegistries.ITEMS.getKey( player.getMainHandItem( ).getItem( ) );
//				if( rl != null && ( rl.toString( ).equals( item ) || rl.toString( ).matches( item ) ) )
//				{
//					player.getInventory( ).removeItem( player.getMainHandItem( ) );
//					player.sendSystemMessage( Component.translatable( "commands.mnocmdextra.banned_item.illegal_item_removed" ) );
//					return;
//				}
//			}
//		} );
//	}
//}
