package tones.bar;
import static java.lang.Math.*;
import facets.util.Debug;
import facets.util.Objects;
import facets.util.Tracer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import tones.Tone;
import tones.Voice;
final public class Bar extends Tracer{
	public static final int WIDTH_NOTE=8,
		WIDTH_SPACE_SHRINK=(false?0:WIDTH_NOTE*2/3);
	public final int at,rise,staveGap,fall,width;
	public final Set<Incipit>incipits;
	@Override
	public boolean equals(Object obj){
		Bar that=(Bar)obj;
		return this==that||incipits.equals(that.incipits);
	}
	public Bar(int barAt,Collection<Incipit>incipits,int sizeInEighths){
		at=barAt;
		if(incipits==null)throw new IllegalStateException(
				"Null incipits in "+Debug.info(this));
		else this.incipits=new HashSet(incipits);
		final Voice[]satb=Voice.values();
		class VoiceAts{
			private static final int START_AT=WIDTH_NOTE/2;
			private final Map<Voice,Integer>ats=new HashMap();
			private int eighthLastAt=0;
			VoiceAts(){
				for(Voice voice:satb)ats.put(voice,START_AT);
			}
			int furthestAt(Iterable<Voice>voices,int eighthAt){
				eighthAt%=sizeInEighths;
				int gap=eighthAt-eighthLastAt;
				if(gap<0)gap+=sizeInEighths;
				int spaces=gap<=1?0:gap-1;
				for(Voice voice:satb)
					ats.put(voice,ats.get(voice)-WIDTH_SPACE_SHRINK*spaces);
				int furthestAt=0;
				for(Voice voice:voices)furthestAt=max(furthestAt,ats.get(voice));
				eighthLastAt=eighthAt;
				return furthestAt;
			}
			void exchangeAts(Incipit incipit){
				List<Voice>voices=new ArrayList();
				for(Tone i:incipit.tones)voices.add(i.voice);
				int barAt=furthestAt(voices,incipit.eighthAt);
				incipit.barAt=barAt;
				for(Tone i:incipit.tones)ats.put(i.voice,barAt+i.eighths*WIDTH_NOTE);
			}
		}
		VoiceAts starts=new VoiceAts();
		int rise=-1,staveGap=-1,fall=-1;
		for(Incipit i:incipits){
			i.close();
			rise=max(rise,i.rise);
			staveGap=max(staveGap,i.staveGap);
			fall=max(fall,i.fall);
			starts.exchangeAts(i);
		}
		this.rise=rise;
		this.staveGap=staveGap;
		this.fall=fall;
		width=starts.furthestAt(Arrays.asList(satb),sizeInEighths);
	}
	private Bar(int at,Set<Incipit>incipits,int rise,int staveGap,int fall,int width){
		this.at=at;this.incipits=incipits;
		this.rise=rise;this.staveGap=staveGap;this.fall=fall;this.width=width;
	}
	public String toString(){
		return Debug.info(this)+" at="+at+" incipits="
		+ (true?incipits.size():"\n"+Objects.toLines(((Collection)incipits).toArray()))
			;
	}
}
