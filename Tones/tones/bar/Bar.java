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
public class Bar extends Tracer{
	public static final int WIDTH_NOTE=(false?1:8),
		WIDTH_SPACE_SHRINK=(false?0:WIDTH_NOTE*2/3);
	public final int at,rise,staveGap,fall,width;
	public final Iterable<Incipit>incipits;
	final Set<Beam>beams=new HashSet();
	public final Set<Annotation>annotations=new HashSet();
	private Voice selectedVoice;
	public Bar(int barAt,Iterable<Incipit>incipits,final int eighth){
		at=barAt;
		if(incipits==null)throw new IllegalStateException(
				"Null incipits in "+Debug.info(this));
		else this.incipits=incipits;
		final Voice[]satb=Voice.values();
		class VoiceAts{
			private static final int START_AT=WIDTH_NOTE/2;
			private final Map<Voice,Integer>ats=new HashMap();
			private int eighthLastAt=0;
			VoiceAts(){
				for(Voice voice:satb)ats.put(voice,START_AT);
			}
			int furthestAt(Iterable<Voice>voices,int eighthAt){
				eighthAt%=eighth;
				int gap=eighthAt-eighthLastAt;
				if(gap<0)gap+=eighth;
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
		final Map<Voice,Beam>voiceBeams=new HashMap();
		for(Voice voice:satb)voiceBeams.put(voice,new Beam(voice));
		for(Incipit incipit:incipits){
			for(Tone t:incipit.tones){
				Beam then=voiceBeams.get(t.voice),now=then.readTone(t,incipit);
				voiceBeams.put(t.voice,now);
				if(then!=now&&!then.hasTones())beams.add(then);
			}
			incipit.close();
			rise=max(rise,incipit.rise);
			staveGap=max(staveGap,incipit.staveGap);
			fall=max(fall,incipit.fall);
			starts.exchangeAts(incipit);
		}
		for(Voice voice:satb){
			Beam beam=voiceBeams.get(voice);
			if(!beam.hasTones())beams.add(beam);
		}
		for(Beam b:beams)annotations.add(b.newAnnotation(this));
		this.rise=rise;
		this.staveGap=staveGap;
		this.fall=fall;
		width=starts.furthestAt(Arrays.asList(satb),eighth);
	}
	private Bar(int at,Set<Incipit>incipits,int rise,int staveGap,int fall,int width){
		this.at=at;this.incipits=incipits;
		this.rise=rise;this.staveGap=staveGap;this.fall=fall;this.width=width;
	}
	public Bar newAnnotationCopy(Incipit incipit){
		return new Bar(at,Collections.singleton(incipit),rise,staveGap,fall,width);
	}
	public void selectVoice(Voice voice){
		selectedVoice=voice;
	}
	public Voice selectedVoice(){
		return selectedVoice;
	}
	public String toString(){
		return Debug.info(this)+" at="+at+
			" incipits=\n"+Objects.toLines(((Collection)incipits).toArray());
	}
	public void updateSelectedVoiceLine(String line){
		throw new RuntimeException("Not implemented in "+Debug.info(this));
	}
	public String selectedVoiceLine(){
		throw new RuntimeException("Not implemented in "+Debug.info(this));
	}
}
