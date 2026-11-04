package studio.mnohrato.cmdextra;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.loading.FMLPaths;
import org.jetbrains.annotations.NotNull;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class Config
{
	public static final String DEFAULT_OUTPUT_PATH = "local/registylist";
	
	private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder( );
	private static final ForgeConfigSpec.ConfigValue< String > OUTPUT_PATH = BUILDER
		.comment( "Directory where the reglist command writes its CSV files.", "Relative paths are resolved against the game directory." )
		.define( "output_path", DEFAULT_OUTPUT_PATH );
	
	public static final ForgeConfigSpec SPEC = BUILDER.build( );
	
	/**
	 * @return the configured output directory as an absolute, normalised path
	 */
	@NotNull
	public static Path outputDirectory( )
	{
		String configured;
		try
		{
			configured = OUTPUT_PATH.get( );
		}
		catch( IllegalStateException ignored )
		{
			configured = DEFAULT_OUTPUT_PATH;
		}
		
		if( configured == null || configured.isBlank( ) ) configured = DEFAULT_OUTPUT_PATH;
		
		Path path;
		try
		{
			path = Paths.get( configured );
		}
		catch( InvalidPathException exception )
		{
			throw new IllegalArgumentException( "Invalid output_path: " + configured, exception );
		}
		
		if( !path.isAbsolute( ) ) path = FMLPaths.GAMEDIR.get( ).resolve( path );
		
		return path.normalize( );
	}
}
