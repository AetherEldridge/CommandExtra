package studio.mnohrato.cmdextra.events;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import studio.mnohrato.cmdextra.CmdExtra;
import studio.mnohrato.cmdextra.capabilities.OriPosCap;
import studio.mnohrato.cmdextra.capabilities.OriPosCapProvider;
import studio.mnohrato.cmdextra.init.ModCapabilities;

@Mod.EventBusSubscriber( modid = CmdExtra.MOD_ID )
public class OriPosCapInit
{
	@SubscribeEvent
	public static void onPlayerLoggedIn( @NotNull PlayerEvent.PlayerLoggedInEvent event )
	{
		initPlayerData( event.getEntity( ) );
	}
	
	@SubscribeEvent
	public static void onPlayerRespawn( @NotNull PlayerEvent.PlayerRespawnEvent event )
	{
		initPlayerData( event.getEntity( ) );
	}
	
	private static void initPlayerData( @NotNull Player player )
	{
		player.getCapability( ModCapabilities.ORI_POS , null ).ifPresent( ( data ) -> {
			CompoundTag tag = data.getPosData( );
			if( tag.isEmpty( ) )
			{
				CompoundTag lastTp = OriPosCap.LAST_TP_MAP.getOrDefault( player , new CompoundTag( ) );
				CompoundTag lastDeath = OriPosCap.LAST_DEATH_MAP.getOrDefault( player , new CompoundTag( ) );
				
				tag.put( OriPosCap.LAST_TP , lastTp );
				tag.put( OriPosCap.LAST_DEATH , lastDeath );
				
				data.setPosData( tag );
			}
		} );
	}
	
	@SubscribeEvent
	public static void onAttachCapabilities( @NotNull AttachCapabilitiesEvent< Entity > event )
	{
		if( event.getObject( ) instanceof Player ) event.addCapability( ResourceLocation.fromNamespaceAndPath( CmdExtra.MOD_ID , "ori_pos" ) , new OriPosCapProvider( ) );
	}
	
}