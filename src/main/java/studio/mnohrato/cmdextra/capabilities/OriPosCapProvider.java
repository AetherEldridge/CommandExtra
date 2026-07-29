package studio.mnohrato.cmdextra.capabilities;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import studio.mnohrato.cmdextra.init.ModCapabilities;

public class OriPosCapProvider implements ICapabilityProvider, INBTSerializable< CompoundTag >
{
	private final LazyOptional< IOriPosCap > instance = LazyOptional.of( OriPosCap::new );
	private       CompoundTag                pos      = new CompoundTag( );
	
	@NotNull
	@Override
	public < T > LazyOptional< T > getCapability( @NotNull Capability< T > cap , @Nullable Direction side )
	{
		return cap == ModCapabilities.ORI_POS ? instance.cast( ) : LazyOptional.empty( );
	}
	
	@Override
	public CompoundTag serializeNBT( )
	{
		return instance.map( IOriPosCap::getPosData ).orElse( new CompoundTag( ) );
	}
	
	@Override
	public void deserializeNBT( CompoundTag nbt )
	{
		instance.ifPresent( data -> data.setPosData( nbt ) );
	}
	
	public CompoundTag getPos( )
	{
		return pos;
	}
}

