package studio.mnohrato.cmdextra.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class SuicideCommand
{
	public static LiteralArgumentBuilder< CommandSourceStack > build( )
	{
		return Commands.literal( "suicide" )
			.requires( source -> source.hasPermission( 0 ) )
			.executes( SuicideCommand::executeSuicide );
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
