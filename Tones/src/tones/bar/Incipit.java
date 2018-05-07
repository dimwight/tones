package tones.bar;
import static tones.ScaleNote.*;
import facets.util.Debug;
import facets.util.Objects;
import facets.util.Tracer;
import facets.util.tree.DataNode;
import facets.util.tree.NodeList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import tones.ScaleNote;
import tones.Tone;
import tones.Voice;
public final class Incipit extends Tracer implements Comparable<Incipit>{
	static class Againsts{
		Againsts(Tone t,Map<Voice,Tone>voiceSoundings){
			for(Voice v:Voice.values()){
				got=voiceSoundings.get(v);
				if(v!=t.voice&&got!=null)
					intervals.add(new Interval(t,got));
			}
		}
		static Collection<Againsts>newToneSet(Tone t,Map<Voice,Tone>voiceSoundings){
			Set<Againsts>set=new HashSet();
			for(Voice v:Voice.values()){
				got=voiceSoundings.get(v);
				if(v!=t.voice&&got!=null)
					set.add(new Againsts(t,voiceSoundings));
			}
			return set; 
		}
	}
	static class Soundings{
		private final Map<Voice,Tone>voiceSoundings;
		private final short eighthAt,barEighths;
		private Soundings(short barEighths,short eighthAt,
				Map<Voice,Tone>voiceSoundings){
			this.eighthAt=eighthAt;
			this.barEighths=barEighths;
			this.voiceSoundings=voiceSoundings;
		}
		Map<Tone,Collection<Againsts>>newToneAgainsts(Collection<Tone>tones){
			Map<Tone,Collection<Againsts>>ta=new HashMap();
			for(Tone t:tones)
				ta.put(t,Againsts.newToneSet(t,voiceSoundings));
			return ta;
		}
		static Soundings newStarting(short barEighths){
			return new Soundings(barEighths,(short)0,new HashMap());
		}
		Soundings newUpdated(Incipit i,short eighthAt){
			Map<Voice,Tone>incipitTones=new HashMap(),nowSoundings=new HashMap();
			for(Tone t:i.tones)
				if(t.pitch!=ScaleNote.Rest.pitch) incipitTones.put(t.voice,t);
			for(Voice v:Voice.values()){
				Tone now=incipitTones.get(v),then=voiceSoundings.get(v);
				if(now!=null) nowSoundings.put(v,now);
				else if(then!=null) nowSoundings.put(v,then.newSounding(
						(short)((i.eighthAt>0?i.eighthAt:barEighths)-eighthAt)));
			}
			return new Soundings(barEighths,i.eighthAt,
					Collections.unmodifiableMap(nowSoundings));
		}
		DataNode newDebugRoot(){
			NodeList nodes=new NodeList(
					Bars.newDebugRoot(getClass(),""+voiceTones.size()+(true?"":eighthAt)),
					true);
			nodes.parent.setValues(
					Objects.toLines(voiceTones.entrySet().toArray()).split("\n"));
			return nodes.parent;
		}
	}
	public final Collection<Tone>tones=new HashSet();
	public final Map<Tone,Collection<Againsts>>againsts;
	public final short eighthAt;
	public int barAt=-1;
	int rise,staveGap,fall;
	private Soundings soundings;
	public Soundings soundings(){
		if(soundings==null)
			throw new IllegalStateException("Null soundings in "+this);
		return soundings;
	}
	DataNode newDebugRoot(){
		NodeList nodes=new NodeList(Bars.newDebugRoot(getClass(),
				"eighth="+eighthAt+(true?"":(" bar="+barAt))),true);
		List<Tone>sortTones=new ArrayList(tones);
		Collections.sort(sortTones,new Comparator<Tone>(){
			@Override
			public int compare(Tone t1,Tone t2){
				return t1.voice.compareTo(t2.voice);
			}
		});
		for(Tone tone:sortTones)
			if(toneNote(tone)!=Rest) nodes.add(tone.newDebugNode());
		nodes.add(soundings.newDebugRoot());
		return nodes.parent;
	}
	Incipit(short eighthAt){
		this.eighthAt=eighthAt;
	}
	public void addTone(Tone tone){
		((Set)tones).add(tone);
	}
	Soundings readSoundings(Soundings then){
		soundings=then.newUpdated(this,eighthAt); 
		againsts=soundings.newToneAgainsts(tones);
		return soundings;
	}
	void close(){
		for(Tone t:tones)
			t.checkOffset(this);
		rise=6;
		staveGap=10;
		fall=6;
	}
	public String toString(){
		return Debug.info(this)+" m"+eighthAt+" b"+barAt+" "+tones;
	}
	public int hashCode(){
		return Arrays.hashCode(intValues());
	}
	public boolean equals(Object obj){
		Incipit that=(Incipit)obj;
		return this==that||Arrays.equals(intValues(),that.intValues());
	}
	@Override
	public int compareTo(Incipit i){
		return new Integer(eighthAt).compareTo(new Integer(i.eighthAt));
	}
	private int[] intValues(){
		return new int[]{eighthAt,fall,staveGap,rise,barAt};
	}
}
