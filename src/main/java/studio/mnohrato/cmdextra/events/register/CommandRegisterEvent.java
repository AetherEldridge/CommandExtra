package studio.mnohrato.cmdextra.events.register;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import studio.mnohrato.cmdextra.CmdExtra;
import studio.mnohrato.cmdextra.commands.*;

import java.util.Arrays;
import java.util.List;

@Mod.EventBusSubscriber(
	modid = CmdExtra.MOD_ID
)
public class CommandRegisterEvent
{
	public static final List< LiteralArgumentBuilder< CommandSourceStack > > COMMANDS = Arrays.asList(
		RegListCommand.build( ),
		SuicideCommand.build( ),
		Tp2pCommand.build( ),
		TpDimCommand.build( )
	);
	
	@SubscribeEvent
	public static void onRegisterCommands( @NotNull RegisterCommandsEvent event )
	{
		CommandDispatcher< CommandSourceStack > disp = event.getDispatcher( );
		for( LiteralArgumentBuilder< CommandSourceStack > command : COMMANDS )
		{
			disp.register( command );
		}
	}
}
