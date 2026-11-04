package studio.mnohrato.cmdextra.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import studio.mnohrato.cmdextra.CmdExtra;

@Mod.EventBusSubscriber( modid = CmdExtra.MOD_ID )
public class SuicideCommand
{
	
	@SubscribeEvent
	public static void onRegisterCommands( @NotNull RegisterCommandsEvent event )
	{
		CommandDispatcher< CommandSourceStack > dispatcher = event.getDispatcher( );
		
		LiteralArgumentBuilder< CommandSourceStack > command = Commands.literal( "suicide" )
			.requires( source -> source.hasPermission( 0 ) )
			.executes( SuicideCommand::executeSuicide );
		
		dispatcher.register( command );
	}
	
	private static int executeSuicide( @NotNull CommandContext< CommandSourceStack > context )
	{
		Player executer = context.getSource( ).getPlayer( );
		if( executer == null )
		{
			context.getSource( ).getServer( ).sendSystemMessage( Component.translatable( "commands.mnocmdextra.executer_not_found" ) );
			return 0;
		}
		
		executer.kill( );
		
		return 1;
	}
}
