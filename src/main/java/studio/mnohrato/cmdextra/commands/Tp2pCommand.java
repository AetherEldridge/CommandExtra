package studio.mnohrato.cmdextra.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import studio.mnohrato.cmdextra.capabilities.OriPosCap;

import java.util.EnumSet;

public class Tp2pCommand
{
	public static LiteralArgumentBuilder< CommandSourceStack > build( )
	{
		return Commands.literal( "tp2p" )
			.requires( source -> source.hasPermission( 0 ) )
			.then( Commands.argument( "target", EntityArgument.players( ) ).executes( Tp2pCommand::player ) )
			.then( Commands.literal( "back" ).executes( Tp2pCommand::back ) )
			.then( Commands.literal( "memorize" ).executes( Tp2pCommand::memorize ) )
			.then( Commands.literal( "spawnpoint" ).executes( Tp2pCommand::spawnpoint ) )
			.then( Commands.literal( "help" ).executes( Tp2pCommand::help ) )
			//			.then( Commands.literal( "home" ).executes( Tp2pCommand::home ) )
			.then( Commands.literal( "death" ).executes( Tp2pCommand::death ) );
	}
	
	private static int player( @NotNull CommandContext< CommandSourceStack > context )
	throws CommandSyntaxException
	{
		Player executer = context.getSource( ).getPlayer( );
		if( executer == null )
		{
			context.getSource( ).getServer( ).sendSystemMessage( Component.translatable( "commands.mnocmdextra.executer_not_found" ) );
			return 0;
		}
		
		OriPosCap.set( executer, OriPosCap.LAST_TP, executer.position( ), ( ServerLevel )executer.level( ) );
		
		Player target = EntityArgument.getPlayer( context, "target" );
		
		executer.teleportTo(
			( ServerLevel )target.level( ),
			target.position( ).x,
			target.position( ).y,
			target.position( ).z,
			EnumSet.noneOf( RelativeMovement.class ),
			0,
			0
		);
		executer.sendSystemMessage( Component.translatable( "commands.mnocmdextra.tp2p.toplayer", executer.getName( ), target.getName( ) ) );
		
		return 1;
	}
	
	private static int back( @NotNull CommandContext< CommandSourceStack > context )
	{
		Player executer = context.getSource( ).getPlayer( );
		if( executer == null )
		{
			context.getSource( ).getServer( ).sendSystemMessage( Component.translatable( "commands.mnocmdextra.executer_not_found" ) );
			return 0;
		}
		
		Vec3 to = OriPosCap.get( executer, OriPosCap.LAST_TP ).getA( );
		ServerLevel dim = OriPosCap.get( executer, OriPosCap.LAST_TP ).getB( );
		
		executer.teleportTo( dim, to.x, to.y, to.z, EnumSet.noneOf( RelativeMovement.class ), 0, 0 );
		executer.sendSystemMessage( Component.translatable( "commands.mnocmdextra.tp2p.back", executer.getName( ) ) );
		
		return 1;
	}
	
	private static int spawnpoint( @NotNull CommandContext< CommandSourceStack > context )
	{
		ServerPlayer executer = context.getSource( ).getPlayer( );
		if( executer == null )
		{
			context.getSource( ).getServer( ).sendSystemMessage( Component.translatable( "commands.mnocmdextra.executer_not_found" ) );
			return 0;
		}
		
		OriPosCap.set( executer, OriPosCap.LAST_TP, executer.position( ), ( ServerLevel )executer.level( ) );
		
		BlockPos bp = executer.getRespawnPosition( );
		ServerLevel dim = context.getSource( ).getServer( ).getLevel( executer.getRespawnDimension( ) );
		if( dim != null || bp != null ) executer.teleportTo( dim, bp.getX( ), bp.getY( ), bp.getZ( ), EnumSet.noneOf( RelativeMovement.class ), 0, 0 );
		
		executer.sendSystemMessage( Component.translatable( "commands.mnocmdextra.tp2p.spawnpoint", executer.getName( ) ) );
		
		return 1;
	}
	
	private static int memorize( @NotNull CommandContext< CommandSourceStack > context )
	{
		Player executer = context.getSource( ).getPlayer( );
		if( executer == null )
		{
			context.getSource( ).getServer( ).sendSystemMessage( Component.translatable( "commands.mnocmdextra.executer_not_found" ) );
			return 0;
		}
		
		OriPosCap.set( executer, OriPosCap.LAST_TP, executer.position( ), ( ServerLevel )executer.level( ) );
		
		executer.sendSystemMessage( Component.translatable( "commands.mnocmdextra.tp2p.memorized", executer.getName( ) ) );
		
		return 1;
	}
	
	private static int help( @NotNull CommandContext< CommandSourceStack > context )
	{
		Player executer = context.getSource( ).getPlayer( );
		if( executer == null )
		{
			context.getSource( ).getServer( ).sendSystemMessage( Component.translatable( "commands.mnocmdextra.executer_not_found" ) );
			return 0;
		}
		
		executer.sendSystemMessage( Component.translatable( "commands.mnocmdextra.tp2p.help" ) );
		
		return 1;
	}
	//
	//	private static int home( @NotNull CommandContext< CommandSourceStack > context )
	//	{
	//		ServerPlayer executer = context.getSource( ).getPlayer( );
	//		if( executer == null )
	//		{
	//			context.getSource( ).getServer( ).sendSystemMessage( Component.translatable( "commands.mnocmdextra.executer_not_found" ) );
	//			return 0;
	//		}
	//
	//		OriPosCap.set( executer , OriPosCap.HOME , executer.position( ) , executer.level( ) );
	//
	//		BlockPos    bp  = executer.getRespawnPosition( );
	//		ServerLevel dim = context.getSource( ).getServer( ).getLevel( executer.getRespawnDimension( ) );
	//		if( dim != null || bp != null ) executer.teleportTo( dim , bp.getX( ) , bp.getY( ) , bp.getZ( ) , EnumSet.noneOf( RelativeMovement.class ) , 0 , 0 );
	//
	//		executer.sendSystemMessage( Component.translatable( "commands.mnocmdextra.tp2p.home" , executer.getName( ) ) );
	//
	//		return 1;
	//	}
	
	private static int death( @NotNull CommandContext< CommandSourceStack > context )
	{
		Player executer = context.getSource( ).getPlayer( );
		if( executer == null )
		{
			context.getSource( ).getServer( ).sendSystemMessage( Component.translatable( "commands.mnocmdextra.executer_not_found" ) );
			return 0;
		}
		
		Vec3 to = OriPosCap.get( executer, OriPosCap.LAST_DEATH ).getA( );
		ServerLevel dim = OriPosCap.get( executer, OriPosCap.LAST_DEATH ).getB( );
		
		executer.teleportTo( dim, to.x, to.y, to.z, EnumSet.noneOf( RelativeMovement.class ), 0, 0 );
		executer.sendSystemMessage( Component.translatable( "commands.mnocmdextra.tp2p.tolastdeath", executer.getName( ) ) );
		
		return 1;
	}
}
