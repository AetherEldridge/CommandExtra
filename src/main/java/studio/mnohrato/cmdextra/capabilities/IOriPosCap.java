package studio.mnohrato.cmdextra.capabilities;

import net.minecraft.nbt.CompoundTag;

/**
 *
 */
public interface IOriPosCap
{
	CompoundTag getPosData( );
	
	void setPosData( CompoundTag tag );
	
}