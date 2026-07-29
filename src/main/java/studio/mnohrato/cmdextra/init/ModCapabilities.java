package studio.mnohrato.cmdextra.init;

import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import studio.mnohrato.cmdextra.CmdExtra;
import studio.mnohrato.cmdextra.capabilities.IOriPosCap;

@Mod.EventBusSubscriber( modid = CmdExtra.MOD_ID , bus = Mod.EventBusSubscriber.Bus.MOD )
public class ModCapabilities
{
	public static final Capability< IOriPosCap > ORI_POS = CapabilityManager.get( new CapabilityToken<>( ) { } );
	
	@SubscribeEvent
	public static void onRegisterCapabilities( @NotNull RegisterCapabilitiesEvent event )
	{
		event.register( IOriPosCap.class );
	}
}