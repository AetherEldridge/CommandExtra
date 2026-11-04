package studio.mnohrato.cmdextra.events;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import studio.mnohrato.cmdextra.CmdExtra;
import studio.mnohrato.cmdextra.capabilities.OriPosCap;

@Mod.EventBusSubscriber(
	modid = CmdExtra.MOD_ID
)
public class PlayerDeathPosSet
{
	@SubscribeEvent
	public static void onPlayerDeath( @NotNull LivingDeathEvent event )
	{
		if( !( event.getEntity( ) instanceof Player player ) ) return;
		OriPosCap.set( player, OriPosCap.LAST_DEATH, player.position( ), ( ServerLevel )player.level( ) );
	}
}
