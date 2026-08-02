package studio.mnohrato.cmdextra.capabilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import oshi.util.tuples.Pair;
import studio.mnohrato.cmdextra.init.ModCapabilities;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class OriPosCap implements IOriPosCap
{
	public static final String                     LAST_TP     = "last_tp";
	public static final String                     LAST_DEATH  = "last_death";
	public static final Map< Player, CompoundTag > LAST_TP_MAP    = new HashMap<>( );
	public static final Map< Player, CompoundTag > LAST_DEATH_MAP = new HashMap<>( );
	private             CompoundTag                posData        = new CompoundTag( );
	
	@Override
	public CompoundTag getPosData( ) { return posData; }
	
	@Override
	public void setPosData( CompoundTag tag )
	{
		posData = tag;
	}
	
	public static void set( @NotNull Player player , String posType , Vec3 pos , Level level )
	{
		player.getCapability( ModCapabilities.ORI_POS ).ifPresent( ( data ) -> {
			
			if( !posType.equals( LAST_TP ) && !posType.equals( LAST_DEATH ) ) return;
			
			CompoundTag posData = data.getPosData( ).getCompound( posType );
			
			if( pos != null )
			{
				posData.putDouble( "x" , pos.x );
				posData.putDouble( "y" , pos.y );
				posData.putDouble( "z" , pos.z );
				posData.putString( "d" , level.dimension( ).location( ).toString( ) );
				
				if( posType.equals( LAST_TP ) ) LAST_TP_MAP.put( player , posData );
				if( posType.equals( LAST_DEATH ) ) LAST_DEATH_MAP.put( player , posData );
			}
			else
			{
				data.getPosData( ).put( posType , new CompoundTag( ) );
			}
			
		} );
	}
	
	@NotNull
	@Contract( "_ , _ -> new")
	public static Pair< Vec3, Level > get( @NotNull Player player , String posType )
	{
		AtomicReference< Vec3 >  pos   = new AtomicReference<>( null );
		AtomicReference< Level > level = new AtomicReference<>( null );
		;
		player.getCapability( ModCapabilities.ORI_POS ).ifPresent( ( data ) -> {
			if( !posType.equals( LAST_TP ) && !posType.equals( LAST_DEATH ) ) return;
			CompoundTag posData = data.getPosData( ).getCompound( posType );
			if( posData.isEmpty( ) || !posData.contains( "x" ) || !posData.contains( "y" ) || !posData.contains( "z" ) ) return;
			
			pos.set( new Vec3( posData.getDouble( "x" ) , posData.getDouble( "y" ) , posData.getDouble( "z" ) ) );
			level.set( player.level( ) );
		} );
		return new Pair<>( pos.get( ) , player.level( ) );
	}
}