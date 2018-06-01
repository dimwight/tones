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
	private static final int WIDTH_SPACE_SHRINK=(false?0:WIDTH_NOTE*2/3),
		START_AT=Bar.WIDTH_NOTE/2;
	public final int at,rise,staveGap,fall,width;
	public final Set<Incipit>incipits;
	public final Soundings endSoundings;
	private final Map<Voice,Integer>voiceAts=new HashMap();
	Bar(int barAt,List<Incipit>incipits,int barEighths){
		this.at=barAt;
		if(incipits==null)throw new IllegalStateException(
				"Null incipits in "+Debug.info(this));
		else this.incipits=new HashSet(incipits);
		for(Voice voice:voiceList)voiceAts.put(voice,START_AT);
		int rise=-1,staveGap=-1,fall=-1,gridAt=false?0:START_AT;
		for(Incipit i:incipits){
			i.close();
			rise=max(rise,i.rise);
			staveGap=max(staveGap,i.staveGap);
			fall=max(fall,i.fall);
			List<Voice>toneVoices=new ArrayList();
			for(Tone t:i.tones)toneVoices.add(t.voice);
			gridAt=i.barAt=nextAt(toneVoices);
			for(Tone t:i.tones)
				voiceAts.put(t.voice,gridAt+t.gridAfter(WIDTH_NOTE));
		}
		width=gridAt=nextAt(voiceList);
		this.rise=rise;
		this.staveGap=staveGap;
		this.fall=fall;
		endSoundings=incipits.get(incipits.size()-1).soundings();
		
	}
	private int nextAt(Iterable<Voice>voices){
		int furthest=0;
		for(Voice voice:voices)
			furthest=max(furthest,voiceAts.get(voice));
		return furthest;
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
