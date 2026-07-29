package studio.mnohrato.cmdextra.commands;

import com.mojang.brigadier.CommandDispatcher;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import studio.mnohrato.cmdextra.CmdExtra;
import studio.mnohrato.cmdextra.capabilities.OriPosCap;

import java.util.EnumSet;

@Mod.EventBusSubscriber( modid = CmdExtra.MOD_ID )
public class Tp2pCommand
{
	@SubscribeEvent
	public static void onRegisterCommands( @NotNull RegisterCommandsEvent event )
	{
		CommandDispatcher< CommandSourceStack > dispatcher = event.getDispatcher( );
		
		LiteralArgumentBuilder< CommandSourceStack > command = Commands.literal( "tp2p" )
			.requires( source -> source.hasPermission( 0 ) )
			.then( Commands.argument( "target" , EntityArgument.players( ) ).executes( Tp2pCommand::executeTp2p ) )
			.then( Commands.literal( "back" ).executes( Tp2pCommand::executeBack ) )
			.then( Commands.literal( "memorize" ).executes( Tp2pCommand::executeMem ) )
			.then( Commands.literal( "spawnpoint" ).executes( Tp2pCommand::executeSpawnpoint ) )
			.then( Commands.literal( "help" ).executes( Tp2pCommand::executeHelp ) )
//			.then( Commands.literal( "death" ).executes( Tp2pCommand::executeDeath ) )
			;
		
		dispatcher.register( command );
	}
	
	private static int executeTp2p( @NotNull CommandContext< CommandSourceStack > context ) throws CommandSyntaxException
	{
		Player executer = context.getSource( ).getPlayer( );
		if( executer == null )
		{
			context.getSource( ).getServer( ).sendSystemMessage( Component.translatable( "commands.mnocmdextra.executer_not_found" ) );
			return 0;
		}
		
		OriPosCap.set( executer , OriPosCap.LAST_TP , executer.position( ) , executer.level( ) );
		
		Player target = EntityArgument.getPlayer( context , "target" );
		
		executer.teleportTo( ( ServerLevel )target.level( ) ,
			target.position( ).x ,
			target.position( ).y ,
			target.position( ).z ,
			EnumSet.noneOf( RelativeMovement.class ) ,
			0 ,
			0 );
		executer.sendSystemMessage( Component.translatable( "commands.mnocmdextra.tp2p.teleported_to" , executer.getName( ) , target.getName( ) ) );
		
		return 1;
	}
	
	private static int executeBack( @NotNull CommandContext< CommandSourceStack > context )
	{
		Player executer = context.getSource( ).getPlayer( );
		if( executer == null )
		{
			context.getSource( ).getServer( ).sendSystemMessage( Component.translatable( "commands.mnocmdextra.executer_not_found" ) );
			return 0;
		}
		
		Vec3  to  = OriPosCap.get( executer , OriPosCap.LAST_TP ).getA( );
		Level dim = OriPosCap.get( executer , OriPosCap.LAST_TP ).getB( );
		
		executer.teleportTo( ( ServerLevel )dim , to.x , to.y , to.z , EnumSet.noneOf( RelativeMovement.class ) , 0 , 0 );
		executer.sendSystemMessage( Component.translatable( "commands.mnocmdextra.tp2p.teleported_back" , executer.getName( ) ) );
		
		return 1;
	}
	
	private static int executeSpawnpoint( @NotNull CommandContext< CommandSourceStack > context )
	{
		ServerPlayer executer = context.getSource( ).getPlayer( );
		if( executer == null )
		{
			context.getSource( ).getServer( ).sendSystemMessage( Component.translatable( "commands.mnocmdextra.executer_not_found" ) );
			return 0;
		}
		
		OriPosCap.set( executer , OriPosCap.LAST_TP , executer.position( ) , executer.level( ) );
		
		BlockPos    bp  = executer.getRespawnPosition( );
		ServerLevel dim = context.getSource( ).getServer( ).getLevel( executer.getRespawnDimension( ) );
		if( dim != null || bp != null ) executer.teleportTo( dim , bp.getX( ) , bp.getY( ) , bp.getZ( ) , EnumSet.noneOf( RelativeMovement.class ) , 0 , 0 );
		
		executer.sendSystemMessage( Component.translatable( "commands.mnocmdextra.tp2p.spawnpoint" , executer.getName( ) ) );
		
		return 1;
	}
	
	private static int executeMem( @NotNull CommandContext< CommandSourceStack > context )
	{
		Player executer = context.getSource( ).getPlayer( );
		if( executer == null )
		{
			context.getSource( ).getServer( ).sendSystemMessage( Component.translatable( "commands.mnocmdextra.executer_not_found" ) );
			return 0;
		}
		
		OriPosCap.set( executer , OriPosCap.LAST_TP , executer.position( ) , executer.level( ) );
		
		executer.sendSystemMessage( Component.translatable( "commands.mnocmdextra.tp2p.memorized" , executer.getName( ) ) );
		
		return 1;
	}
	
	private static int executeHelp( @NotNull CommandContext< CommandSourceStack > context )
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
	
//	private static int executeDeath( @NotNull CommandContext< CommandSourceStack > context )
//	{
//		Player executer = context.getSource( ).getPlayer( );
//		if( executer == null )
//		{
//			context.getSource( ).getServer( ).sendSystemMessage( Component.translatable( "commands.mnocmdextra.executer_not_found" ) );
//			return 0;
//		}
//
//		Vec3  to  = OriPosCap.get( executer , OriPosCap.LAST_DEATH ).getA( );
//		Level dim = OriPosCap.get( executer , OriPosCap.LAST_TP ).getB( );
//
//		executer.teleportTo( ( ServerLevel )dim , to.x , to.y , to.z , EnumSet.noneOf( RelativeMovement.class ) , 0 , 0 );
//		executer.sendSystemMessage( Component.translatable( "commands.mnocmdextra.tp2p.teleported_death" , executer ) );
//
//		return 1;
//	}
}
