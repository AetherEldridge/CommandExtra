//package studio.mnohrato.cmdextra.commands;
//
//import com.mojang.brigadier.CommandDispatcher;
//import com.mojang.brigadier.context.CommandContext;
//import com.mojang.brigadier.exceptions.CommandSyntaxException;
//import net.minecraft.commands.CommandSourceStack;
//import net.minecraft.commands.Commands;
//import net.minecraftforge.event.RegisterCommandsEvent;
//import net.minecraftforge.eventbus.api.SubscribeEvent;
//import net.minecraftforge.fml.common.Mod;
//import org.jetbrains.annotations.NotNull;
//import studio.mnohrato.cmdextra.TpToPlayer;
//
//@Mod.EventBusSubscriber( modid = TpToPlayer.MOD_ID )
//public class BannedItemsCommand
//{
//
//	@SubscribeEvent
//	public static void onRegisterCommands( @NotNull RegisterCommandsEvent event )
//	{
//		CommandDispatcher< CommandSourceStack > dispatcher = event.getDispatcher( );
//
//		var command = Commands.literal( "banned_items" )
//			.requires( source -> source.hasPermission( 0 ) )
//			.then( Commands.literal( "list" ).executes( BannedItemsCommand::executeList ) );
//
//		dispatcher.register( command );
//	}
//
//	private static int executeList( @NotNull CommandContext< CommandSourceStack > context ) throws CommandSyntaxException
//	{
//		return 1;
//	}
//}
