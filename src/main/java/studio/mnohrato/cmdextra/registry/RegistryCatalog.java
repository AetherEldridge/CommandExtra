package studio.mnohrato.cmdextra.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;

/**
 * A snapshot of every registry that is available to a running server, together
 * with the aliases used to resolve the {@code <type>} argument of
 * {@code /reglist}.
 * <p>
 * The server's {@link net.minecraft.core.RegistryAccess} already contains both
 * the static built-in registries (blocks, items, ...) and the data-pack driven
 * registries (biomes, dimensions, ...), so it is the primary source. The
 * built-in root registry is added as a fallback so that registries which are
 * not part of the layered access are still visible.
 */
public final class RegistryCatalog
{
	private static final Map< ResourceLocation, String > PREFERRED_NAMES = createPreferredNames( );
	
	private final Map< ResourceLocation, Registry< ? > > registries;
	private final Map< ResourceLocation, RegistryType > typesById;
	private final List< RegistryType > types;
	private final Map< String, Set< RegistryType > > aliases;
	private Set< String > namespaces;
	
	private RegistryCatalog( Map< ResourceLocation, Registry< ? > > registries )
	{
		this.registries = Collections.unmodifiableMap( registries );
		this.typesById = new LinkedHashMap<>( registries.size( ) );
		this.aliases = new HashMap<>( );
		
		List< RegistryType > discovered = new ArrayList<>( registries.size( ) );
		Set< String > usedNames = new HashSet<>( registries.size( ) );
		
		for( Map.Entry< ResourceLocation, Registry< ? > > entry : registries.entrySet( ) )
		{
			ResourceLocation id = entry.getKey( );
			String baseName = PREFERRED_NAMES.get( id );
			if( baseName == null ) baseName = pluralize( sanitize( id.getPath( ) ) );
			
			String name = baseName;
			int suffix = 2;
			while( !usedNames.add( name.toLowerCase( Locale.ROOT ) ) ) name = baseName + "_" + suffix++;
			
			RegistryType type = new RegistryType( id, name, entry.getValue( ) );
			discovered.add( type );
			this.typesById.put( id, type );
			this.registerAliases( type );
		}
		
		discovered.sort( Comparator.comparing( RegistryType::name ) );
		this.types = Collections.unmodifiableList( discovered );
	}
	
	/**
	 * Collects all registries visible to the given server.
	 */
	@NotNull
	@Contract(
		value = "_ -> new"
	)
	public static RegistryCatalog of( @NotNull MinecraftServer server )
	{
		Map< ResourceLocation, Registry< ? > > registries = new TreeMap<>( Comparator.comparing( ResourceLocation::toString ) );
		
		server.registryAccess( ).registries( ).forEach( entry -> registries.put( entry.key( ).location( ), entry.value( ) ) );
		
		// The static layer should already be part of the access above. Keep this
		// fallback so that a registry which is only present in the root registry
		// (for example a registry added directly by Forge) is still listed.
		for( Registry< ? > registry : BuiltInRegistries.REGISTRY ) registries.putIfAbsent( registry.key( ).location( ), registry );
		
		return new RegistryCatalog( registries );
	}
	
	public List< RegistryType > types( )
	{
		return this.types;
	}
	
	/**
	 * Resolves a user supplied type token. Both the friendly name
	 * ({@code blocks}), the registry path ({@code block}), the full registry id
	 * ({@code minecraft:block}) and the obvious singular/plural variants are
	 * accepted.
	 *
	 * @return every type matching the token; empty if there is no match
	 */
	public List< RegistryType > findTypes( String rawToken )
	{
		if( rawToken == null ) return List.of( );
		
		String token = rawToken.trim( );
		if( token.isEmpty( ) ) return List.of( );
		
		// An explicit resource location always wins, e.g. "minecraft:block".
		ResourceLocation direct = ResourceLocation.tryParse( token.toLowerCase( Locale.ROOT ) );
		if( direct != null )
		{
			RegistryType exact = this.typesById.get( direct );
			if( exact != null ) return List.of( exact );
		}
		
		Set< RegistryType > matches = this.aliases.get( token.toLowerCase( Locale.ROOT ) );
		if( matches == null )
		{
			String normalized = token.toLowerCase( Locale.ROOT ).replace( ' ', '_' ).replace( '-', '_' );
			matches = this.aliases.get( normalized );
		}
		
		if( matches == null || matches.isEmpty( ) ) return List.of( );
		
		if( matches.size( ) == 1 ) return List.of( matches.iterator( ).next( ) );
		
		List< RegistryType > sorted = new ArrayList<>( matches );
		sorted.sort( Comparator.comparing( RegistryType::id, Comparator.comparing( ResourceLocation::toString ) ) );
		return Collections.unmodifiableList( sorted );
	}
	
	/**
	 * @return the real namespace matching the token, or {@code null} if it does
	 * not exist. Matching is case insensitive.
	 */
	public String findNamespace( String rawToken )
	{
		if( rawToken == null ) return null;
		
		String token = rawToken.trim( );
		if( token.isEmpty( ) ) return null;
		
		Set< String > known = this.namespaces( );
		if( known.contains( token ) ) return token;
		
		for( String namespace : known ) if( namespace.equalsIgnoreCase( token ) ) return namespace;
		
		return null;
	}
	
	/**
	 * @return every namespace that currently owns at least one registry entry
	 */
	@NotNull
	public Set< String > namespaces( )
	{
		Set< String > cached = this.namespaces;
		if( cached == null )
		{
			Set< String > discovered = new TreeSet<>( );
			for( Registry< ? > registry : this.registries.values( ) ) for( ResourceLocation id : registry.keySet( ) ) discovered.add( id.getNamespace( ) );
			
			cached = Collections.unmodifiableSet( discovered );
			this.namespaces = cached;
		}
		
		return cached;
	}
	
	private void registerAliases( @NotNull RegistryType type )
	{
		ResourceLocation id = type.id( );
		String path = id.getPath( );
		String underscorePath = sanitize( path );
		String lastSegment = lastSegment( path );
		
		this.addAlias( type.name( ), type );
		this.addAlias( singularize( type.name( ) ), type );
		this.addAlias( id.toString( ), type );
		this.addAlias( sanitize( id.toString( ) ), type );
		this.addAlias( path, type );
		this.addAlias( path.replace( '/', '_' ), type );
		this.addAlias( underscorePath, type );
		this.addAlias( pluralize( underscorePath ), type );
		this.addAlias( lastSegment, type );
		this.addAlias( pluralize( lastSegment ), type );
		this.addAlias( lastSegment.replace( '-', '_' ), type );
	}
	
	private void addAlias( String token, RegistryType type )
	{
		if( token == null ) return;
		
		String key = token.trim( ).toLowerCase( Locale.ROOT );
		if( key.isEmpty( ) ) return;
		
		this.aliases.computeIfAbsent( key, ignored -> new LinkedHashSet<>( ) ).add( type );
	}
	
	@NotNull
	private static String sanitize( @NotNull String value )
	{
		return value.toLowerCase( Locale.ROOT ).replace( '/', '_' ).replaceAll( "[^a-z0-9_]+", "_" );
	}
	
	@NotNull
	private static String lastSegment( @NotNull String path )
	{
		int index = path.lastIndexOf( '/' );
		return index < 0 ? path : path.substring( index + 1 );
	}
	
	@NotNull
	private static String pluralize( @NotNull String value )
	{
		if( value.isEmpty( ) || value.endsWith( "s" ) ) return value;
		
		if( value.endsWith( "y" ) && value.length( ) > 1 )
		{
			char before = value.charAt( value.length( ) - 2 );
			if( before != 'a' && before != 'e' && before != 'i' && before != 'o' && before != 'u' ) return value.substring( 0, value.length( ) - 1 ) + "ies";
		}
		
		return value + "s";
	}
	
	@NotNull
	private static String singularize( @NotNull String value )
	{
		if( value.endsWith( "ies" ) && value.length( ) > 3 ) return value.substring( 0, value.length( ) - 3 ) + "y";
		
		if( value.endsWith( "ses" ) || value.endsWith( "xes" ) || value.endsWith( "zes" ) || value.endsWith( "ches" ) || value.endsWith( "shes" ) )
			return value.substring( 0, value.length( ) - 2 );
		
		if( value.endsWith( "s" ) && !value.endsWith( "ss" ) ) return value.substring( 0, value.length( ) - 1 );
		
		return value;
	}
	
	@NotNull
	@UnmodifiableView
	private static Map< ResourceLocation, String > createPreferredNames( )
	{
		Map< ResourceLocation, String > names = new HashMap<>( );
		
		names.put( Registries.BLOCK.location( ), "blocks" );
		names.put( Registries.ITEM.location( ), "items" );
		names.put( Registries.ENTITY_TYPE.location( ), "entities" );
		names.put( Registries.CREATIVE_MODE_TAB.location( ), "creative_tabs" );
		names.put( Registries.FLUID.location( ), "fluids" );
		names.put( Registries.SOUND_EVENT.location( ), "sounds" );
		names.put( Registries.PARTICLE_TYPE.location( ), "particles" );
		names.put( Registries.MOB_EFFECT.location( ), "mob_effects" );
		names.put( Registries.POTION.location( ), "potions" );
		names.put( Registries.ENCHANTMENT.location( ), "enchantments" );
		names.put( Registries.BLOCK_ENTITY_TYPE.location( ), "block_entity_types" );
		names.put( Registries.MENU.location( ), "menus" );
		names.put( Registries.RECIPE_TYPE.location( ), "recipe_types" );
		names.put( Registries.RECIPE_SERIALIZER.location( ), "recipe_serializers" );
		names.put( Registries.ATTRIBUTE.location( ), "attributes" );
		names.put( Registries.BIOME.location( ), "biomes" );
		names.put( Registries.LEVEL_STEM.location( ), "dimensions" );
		names.put( Registries.DIMENSION_TYPE.location( ), "dimension_types" );
		names.put( Registries.FEATURE.location( ), "features" );
		names.put( Registries.CONFIGURED_FEATURE.location( ), "configured_features" );
		names.put( Registries.PLACED_FEATURE.location( ), "placed_features" );
		names.put( Registries.CARVER.location( ), "carvers" );
		names.put( Registries.CONFIGURED_CARVER.location( ), "configured_carvers" );
		names.put( Registries.STRUCTURE.location( ), "structures" );
		names.put( Registries.STRUCTURE_SET.location( ), "structure_sets" );
		names.put( Registries.TEMPLATE_POOL.location( ), "template_pools" );
		names.put( Registries.PROCESSOR_LIST.location( ), "processor_lists" );
		names.put( Registries.WORLD_PRESET.location( ), "world_presets" );
		names.put( Registries.NOISE_SETTINGS.location( ), "noise_settings" );
		names.put( Registries.NOISE.location( ), "noise" );
		names.put( Registries.DENSITY_FUNCTION.location( ), "density_functions" );
		names.put( Registries.DAMAGE_TYPE.location( ), "damage_types" );
		names.put( Registries.TRIM_MATERIAL.location( ), "trim_materials" );
		names.put( Registries.TRIM_PATTERN.location( ), "trim_patterns" );
		names.put( Registries.CHAT_TYPE.location( ), "chat_types" );
		names.put( Registries.PAINTING_VARIANT.location( ), "paintings" );
		names.put( Registries.BANNER_PATTERN.location( ), "banner_patterns" );
		names.put( Registries.INSTRUMENT.location( ), "instruments" );
		names.put( Registries.CAT_VARIANT.location( ), "cat_variants" );
		names.put( Registries.FROG_VARIANT.location( ), "frog_variants" );
		names.put( Registries.VILLAGER_TYPE.location( ), "villager_types" );
		names.put( Registries.VILLAGER_PROFESSION.location( ), "villager_professions" );
		names.put( Registries.POINT_OF_INTEREST_TYPE.location( ), "point_of_interest_types" );
		names.put( Registries.MEMORY_MODULE_TYPE.location( ), "memory_module_types" );
		names.put( Registries.SENSOR_TYPE.location( ), "sensor_types" );
		names.put( Registries.ACTIVITY.location( ), "activities" );
		names.put( Registries.SCHEDULE.location( ), "schedules" );
		names.put( Registries.STAT_TYPE.location( ), "stat_types" );
		names.put( Registries.CUSTOM_STAT.location( ), "custom_stats" );
		names.put( Registries.CHUNK_STATUS.location( ), "chunk_statuses" );
		names.put( Registries.COMMAND_ARGUMENT_TYPE.location( ), "command_argument_types" );
		names.put( Registries.DECORATED_POT_PATTERNS.location( ), "decorated_pot_patterns" );
		
		return Collections.unmodifiableMap( names );
	}
}
