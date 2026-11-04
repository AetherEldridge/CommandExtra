package studio.mnohrato.cmdextra;

import com.mojang.logging.LogUtils;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

@Mod(
	value = CmdExtra.MOD_ID
)
public class CmdExtra
{
	public static final String MOD_ID = "cmdextra";
	public static final Logger LOGGER = LogUtils.getLogger( );
	
	public CmdExtra( @NotNull FMLJavaModLoadingContext context )
	{
		IEventBus modEventBus = context.getModEventBus( );
		
		context.registerConfig( ModConfig.Type.COMMON, Config.SPEC );
	}
}
