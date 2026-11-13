package studio.mnohrato.cmdextra.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

public class TpDimCommand
{
	public static LiteralArgumentBuilder< CommandSourceStack > build( )
	{
		return Commands
			.literal( "tpdim" )
			.requires( source -> source.hasPermission( 2 ) )
			.then( Commands
				.argument( "target", DimensionArgument.dimension( ) )
				.then( Commands.argument( "pos", Vec3Argument.vec3( ) ).executes( context -> to( context, false ) ) )
				.executes( context -> to( context, true ) ) );
	}
	
	private static int to( @NotNull CommandContext< CommandSourceStack > context, boolean withoutPos )
	throws CommandSyntaxException
	{
		Player executer = context.getSource( ).getPlayer( );
		if( executer == null )
		{
			context.getSource( ).getServer( ).sendSystemMessage( Component.translatable( "commands.mnocmdextra.executer_not_found" ) );
			return 0;
		}
		
		Vec3 pos = withoutPos ? executer.position( ) : Vec3Argument.getVec3( context, "pos" );
		ServerLevel target = DimensionArgument.getDimension( context, "target" );
		
		executer.teleportTo( target, pos.x, pos.y, pos.z, EnumSet.noneOf( RelativeMovement.class ), 0, 0 );
		executer.sendSystemMessage( Component.translatable( "commands.mnocmdextra.tpdim.to", executer.getName( ), pos.toString( ), target.dimension( ).location( ).toString( ) ) );
		
		return 1;
	}
}
