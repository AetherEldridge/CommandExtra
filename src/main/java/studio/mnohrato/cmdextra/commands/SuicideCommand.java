package studio.mnohrato.cmdextra.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import studio.mnohrato.cmdextra.TpToPlayer;
import studio.mnohrato.cmdextra.capabilities.OriPosCap;

@Mod.EventBusSubscriber( modid = TpToPlayer.MOD_ID )
public class SuicideCommand
{
	
	@SubscribeEvent
	public static void onRegisterCommands( @NotNull RegisterCommandsEvent event )
	{
		CommandDispatcher< CommandSourceStack > dispatcher = event.getDispatcher( );
		
		var command = Commands.literal( "suicide" )
			.requires( source -> source.hasPermission( 0 ) )
			.executes( context -> executeSuicide( context , false ) )
			.then( Commands.literal( "ignore_warning" ).executes( context1 -> executeSuicide( context1 , true ) ) );
		
		dispatcher.register( command );
	}
	
	private static int executeSuicide( @NotNull CommandContext< CommandSourceStack > context , boolean ignoreWarning ) throws CommandSyntaxException
	{
		Player executer = context.getSource( ).getPlayer( );
		if( executer == null )
		{
			context.getSource( ).getServer( ).sendSystemMessage( Component.translatable( "commands.mnocmdextra.executer_not_found" ) );
			return 0;
		}
		
		if( !ignoreWarning )
		{
			executer.sendSystemMessage( Component.translatable( "commands.mnocmdextra.suicide.warning" ) );
			return 0;
		}
		
		executer.kill( );
		
		return 1;
	}
}
