package tones.bar;
import static java.lang.Math.*;
import facets.util.Debug;
import facets.util.Objects;
import facets.util.Tracer;
import facets.util.Util;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import tones.bar.Incipit.Soundings;
final public class Bar extends Tracer{
	public static final int WIDTH_NOTE=8;
	private static final int START_AT=WIDTH_NOTE/2;
	public final int at,rise,staveGap,fall,width;
	public final Set<Incipit>incipits;
	public final Soundings endSoundings;
	Bar(int barAt,List<Incipit>incipits,int barEighths){
		if(incipits==null)throw new IllegalStateException(
				"Null incipits in "+Debug.info(this));
		else this.incipits=new HashSet(incipits);
		this.at=barAt;
		if(false)trace(": ",this);
		int rise=-1,staveGap=-1,fall=-1,gridAt=START_AT;
		int eighthsThen=0,jumpBase=0;
		Function<Integer,Integer>jumpMod=base->
			max(WIDTH_NOTE,(int)(pow(WIDTH_NOTE*base,0.55)*3.5));
		for(Incipit i:incipits){
			final int eighthsNow=i.eighthAt;
			jumpBase=eighthsNow-eighthsThen;
			gridAt=i.close(gridAt+(eighthsNow==0?0:jumpMod.apply(jumpBase)));
			eighthsThen=eighthsNow;
			rise=max(rise,i.rise);
			staveGap=max(staveGap,i.staveGap);
			fall=max(fall,i.fall);
		}
		width=gridAt+jumpMod.apply(jumpBase);
		this.rise=rise;
		this.staveGap=staveGap;
		this.fall=fall;
		endSoundings=incipits.get(incipits.size()-1).soundings();
	}
	@Override
	public boolean equals(Object obj){
		Bar that=(Bar)obj;
		return this==that||incipits.equals(that.incipits);
	}
	public String toString(){
		return Debug.info(this)+" at="+at+" incipits="
		+ (true?incipits.size():"\n"+Objects.toLines(((Collection)incipits).toArray()));
	}
}
