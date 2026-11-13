package studio.mnohrato.cmdextra.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;
import org.jetbrains.annotations.NotNull;
import studio.mnohrato.cmdextra.CmdExtra;
import studio.mnohrato.cmdextra.Config;
import studio.mnohrato.cmdextra.registry.RegistryCatalog;
import studio.mnohrato.cmdextra.registry.RegistryExporter;
import studio.mnohrato.cmdextra.registry.RegistryType;

import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class RegListCommand
{
	public static final String ALL_NAMESPACES = "all_namespace";
	public static final String ALL_TYPES = "all_type";
	
	private static RegistryCatalog cachedCatalog;
	private static RegistryAccess cachedRegistryAccess;
	
	public static LiteralArgumentBuilder< CommandSourceStack > build( )
	{
		return Commands
			.literal( "reglist" )
			.requires( source -> source.hasPermission( 2 ) )
			.then( Commands
				.argument( "namespace", StringArgumentType.word( ) )
				.suggests( RegListCommand::suggestNamespaces )
				.then( Commands.argument( "type", StringArgumentType.greedyString( ) ).suggests( RegListCommand::suggestTypes ).executes( RegListCommand::execute ) ) );
	}
	
	private static int execute( @NotNull CommandContext< CommandSourceStack > context )
	{
		CommandSourceStack source = context.getSource( );
		MinecraftServer server = source.getServer( );
		
		String namespaceArgument = unquote( StringArgumentType.getString( context, "namespace" ) );
		String typeArgument = unquote( StringArgumentType.getString( context, "type" ) );
		
		boolean allNamespaces = namespaceArgument.equalsIgnoreCase( ALL_NAMESPACES );
		boolean allTypes = typeArgument.equalsIgnoreCase( ALL_TYPES );
		
		RegistryCatalog catalog = RegistryCatalog.of( server );
		
		String namespace = null;
		if( !allNamespaces )
		{
			namespace = catalog.findNamespace( namespaceArgument );
			if( namespace == null )
			{
				source.sendFailure( Component.translatable( "commands.mnocmdextra.unknown_namespace", namespaceArgument ) );
				return 0;
			}
		}
		
		List< RegistryType > types;
		if( allTypes )
		{
			types = catalog.types( );
		}
		else
		{
			List< RegistryType > matches = catalog.findTypes( typeArgument );
			if( matches.isEmpty( ) )
			{
				source.sendFailure( Component.translatable( "commands.mnocmdextra.unknown_type", typeArgument ) );
				return 0;
			}
			
			if( matches.size( ) > 1 )
			{
				String candidates = matches.stream( ).map( RegistryType::id ).map( ResourceLocation::toString ).sorted( ).collect( Collectors.joining( ", " ) );
				source.sendFailure( Component.translatable( "commands.mnocmdextra.ambiguous_type", typeArgument, candidates ) );
				return 0;
			}
			
			types = List.of( matches.get( 0 ) );
		}
		
		try
		{
			Path output = Config.outputDirectory( );
			RegistryExporter.Result result = RegistryExporter.export( output, namespace, types, allNamespaces, allTypes );
			
			String displayPath = displayPath( output );
			int entries = result.entries( );
			int files = result.files( );
			source.sendSuccess( ( ) -> Component.translatable( "commands.mnocmdextra.success", entries, files, displayPath ), false );
			return 1;
		}
		catch( Exception exception )
		{
			CmdExtra.LOGGER.error( "Failed to write the registry list", exception );
			String message = exception.getMessage( ) == null ? exception.getClass( ).getSimpleName( ) : exception.getMessage( );
			source.sendFailure( Component.translatable( "commands.mnocmdextra.write_failed", message ) );
			return 0;
		}
	}
	
	private static CompletableFuture< Suggestions > suggestNamespaces( @NotNull CommandContext< CommandSourceStack > context, @NotNull SuggestionsBuilder builder )
	{
		String remaining = builder.getRemainingLowerCase( );
		if( ALL_NAMESPACES.startsWith( remaining ) ) builder.suggest( ALL_NAMESPACES );
		
		RegistryCatalog catalog = cachedCatalog( context.getSource( ).getServer( ) );
		for( String namespace : catalog.namespaces( ) ) if( namespace.toLowerCase( Locale.ROOT ).startsWith( remaining ) ) builder.suggest( namespace );
		
		return builder.buildFuture( );
	}
	
	private static CompletableFuture< Suggestions > suggestTypes( @NotNull CommandContext< CommandSourceStack > context, @NotNull SuggestionsBuilder builder )
	{
		String remaining = builder.getRemainingLowerCase( );
		if( ALL_TYPES.startsWith( remaining ) ) builder.suggest( ALL_TYPES );
		
		RegistryCatalog catalog = cachedCatalog( context.getSource( ).getServer( ) );
		for( RegistryType type : catalog.types( ) ) if( type.name( ).startsWith( remaining ) ) builder.suggest( type.name( ) );
		
		return builder.buildFuture( );
	}
	
	@NotNull
	private static String unquote( String value )
	{
		String trimmed = value == null ? "" : value.trim( );
		if( trimmed.length( ) >= 2 )
		{
			char first = trimmed.charAt( 0 );
			char last = trimmed.charAt( trimmed.length( ) - 1 );
			if( ( first == '"' && last == '"' ) || ( first == '\'' && last == '\'' ) ) return trimmed.substring( 1, trimmed.length( ) - 1 ).trim( );
		}
		return trimmed;
	}
	
	private static RegistryCatalog cachedCatalog( @NotNull MinecraftServer server )
	{
		RegistryAccess access = server.registryAccess( );
		synchronized( RegListCommand.class )
		{
			if( cachedCatalog == null || access != cachedRegistryAccess )
			{
				cachedCatalog = RegistryCatalog.of( server );
				cachedRegistryAccess = access;
			}
			return cachedCatalog;
		}
	}
	
	@NotNull
	private static String displayPath( @NotNull Path path )
	{
		try
		{
			Path gameDirectory = FMLPaths.GAMEDIR.get( ).toAbsolutePath( ).normalize( );
			Path absolute = path.toAbsolutePath( ).normalize( );
			if( absolute.startsWith( gameDirectory ) )
			{
				Path relative = gameDirectory.relativize( absolute );
				if( !relative.toString( ).isEmpty( ) ) return relative.toString( );
			}
			return absolute.toString( );
		}
		catch( Exception ignored )
		{
			return path.toString( );
		}
	}
}
