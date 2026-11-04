package studio.mnohrato.cmdextra.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

/**
 * A single game registry that can be queried through {@code /reglist}.
 *
 * @param id       the registry key, for example {@code minecraft:block}
 * @param name     the human friendly name used for the output file, for example {@code blocks}
 * @param registry the live registry instance
 */
public record RegistryType( ResourceLocation id, String name, Registry< ? > registry )
{
	public RegistryType
	{
		Objects.requireNonNull( id, "id" );
		Objects.requireNonNull( name, "name" );
		Objects.requireNonNull( registry, "registry" );
	}
}
