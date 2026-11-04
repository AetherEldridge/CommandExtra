package studio.mnohrato.cmdextra.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Writes the entries of one or more {@link RegistryType}s as comma separated
 * files below a directory.
 * <p>
 * Layout:
 * <ul>
 *     <li>{@code <output>/<namespace>/<type>.csv} for a single namespace</li>
 *     <li>{@code <output>/all_namespace/<type>.csv} when every namespace is
 *     requested; those files additionally contain the owning mod</li>
 * </ul>
 */
public final class RegistryExporter
{
	public static final String ALL_NAMESPACE_DIRECTORY = "all_namespace";
	public static final String UTF8_BOM = "\uFEFF";
	
	private static final Comparator< ResourceLocation > ID_ORDER = Comparator.comparing( ResourceLocation::getNamespace ).thenComparing( ResourceLocation::getPath );
	
	private RegistryExporter( )
	{
	}
	
	/**
	 * @param outputDirectory      directory the files are written below
	 * @param namespace            namespace to filter on, or {@code null} for all
	 * @param types                registry types to export
	 * @param includeAllNamespaces whether every namespace is being exported
	 * @param skipEmptyTypes       skip types without any matching entry (used for
	 *                             {@code all_type} so that dozens of empty files
	 *                             are not created)
	 */
	
	@NotNull
	@Contract(
		value = "_, _, _, _, _ -> new"
	)
	public static Result export( Path outputDirectory, String namespace, @NotNull List< RegistryType > types, boolean includeAllNamespaces, boolean skipEmptyTypes )
	throws IOException
	{
		int filesWritten = 0;
		int entriesWritten = 0;
		
		for( RegistryType type : types )
		{
			List< ResourceLocation > ids = matchingIds( type, includeAllNamespaces ? null : namespace );
			if( ids.isEmpty( ) && skipEmptyTypes ) continue;
			
			ids.sort( ID_ORDER );
			
			String directoryName = includeAllNamespaces ? ALL_NAMESPACE_DIRECTORY : safeSegment( namespace );
			Path directory = outputDirectory.resolve( directoryName );
			Path file = directory.resolve( safeSegment( type.name( ) ) + ".csv" );
			Files.createDirectories( directory );
			
			Map< String, ModSource > mods = new HashMap<>( );
			try( BufferedWriter writer = Files.newBufferedWriter( file, StandardCharsets.UTF_8 ) )
			{
				// A BOM makes Excel detect UTF-8, which matters for localised mod names.
				writer.write( UTF8_BOM );
				
				if( includeAllNamespaces )
				{
					writer.write( "namespace,id,mod_id,mod_name\n" );
					for( ResourceLocation id : ids )
					{
						ModSource mod = mods.computeIfAbsent( id.getNamespace( ), RegistryExporter::findMod );
						writer.write( csv( id.getNamespace( ), id.toString( ), mod.id( ), mod.name( ) ) );
						writer.write( '\n' );
					}
				}
				else
				{
					writer.write( "namespace,id\n" );
					for( ResourceLocation id : ids )
					{
						writer.write( csv( id.getNamespace( ), id.toString( ) ) );
						writer.write( '\n' );
					}
				}
			}
			
			filesWritten++;
			entriesWritten += ids.size( );
		}
		
		return new Result( filesWritten, entriesWritten );
	}
	
	@NotNull
	private static List< ResourceLocation > matchingIds( @NotNull RegistryType type, String namespace )
	{
		List< ResourceLocation > ids = new ArrayList<>( );
		for( ResourceLocation id : type.registry( ).keySet( ) ) if( namespace == null || namespace.equals( id.getNamespace( ) ) ) ids.add( id );
		return ids;
	}
	
	private static ModSource findMod( String namespace )
	{
		return ModList
			.get( )
			.getModContainerById( namespace )
			.map( container -> new ModSource( container.getModId( ), container.getModInfo( ).getDisplayName( ) ) )
			.orElseGet( ( ) -> new ModSource( namespace, namespace ) );
	}
	
	private static String csv( String... values )
	{
		StringBuilder builder = new StringBuilder( );
		for( int i = 0; i < values.length; i++ )
		{
			if( i > 0 ) builder.append( ',' );
			builder.append( escape( values[ i ] ) );
		}
		return builder.toString( );
	}
	
	private static String escape( String value )
	{
		if( value == null ) return "";
		
		boolean quote = value.indexOf( ',' ) >= 0 || value.indexOf( '"' ) >= 0 || value.indexOf( '\n' ) >= 0 || value.indexOf( '\r' ) >= 0;
		if( !quote ) return value;
		
		return '"' + value.replace( "\"", "\"\"" ) + '"';
	}
	
	private static String safeSegment( String value )
	{
		if( value == null || value.isBlank( ) ) return "_";
		
		String cleaned = value.replaceAll( "[^A-Za-z0-9._-]", "_" );
		if( cleaned.equals( "." ) || cleaned.equals( ".." ) ) return cleaned.replace( '.', '_' );
		return cleaned;
	}
	
	public record Result( int files, int entries ) { }
	
	private record ModSource( String id, String name ) { }
}
