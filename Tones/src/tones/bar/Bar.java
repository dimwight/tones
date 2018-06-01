package tones.bar;
import static java.lang.Math.*;
import static tones.Tone.*;
import static tones.Voice.*;
import static tones.bar.Bar.*;
import facets.util.Debug;
import facets.util.Objects;
import facets.util.Tracer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import tones.Tone;
import tones.Voice;
import tones.bar.Incipit.Soundings;
final public class Bar extends Tracer{
	public static final int WIDTH_NOTE=8;
	private static final int START_AT=Bar.WIDTH_NOTE/2;
	public final int at,rise,staveGap,fall,width;
	public final Set<Incipit>incipits;
	public final Soundings endSoundings;
	Bar(int barAt,List<Incipit>incipits,int barEighths){
		this.at=barAt;
		if(incipits==null)throw new IllegalStateException(
				"Null incipits in "+Debug.info(this));
		else this.incipits=new HashSet(incipits);
		for(Voice voice:voiceList)voiceAts.put(voice,START_AT);
		int rise=-1,staveGap=-1,fall=-1,gridAt=false?0:START_AT;
		for(Incipit i:incipits){
			gridAt+=i.close(gridAt);
			rise=max(rise,i.rise);
			staveGap=max(staveGap,i.staveGap);
			fall=max(fall,i.fall);
			}
		int spread=gridAt
		width=gridAt;
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
