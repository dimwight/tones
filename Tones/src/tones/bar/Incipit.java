package tones.bar;
import static tones.ScaleNote.*;
import facets.util.Objects;
import facets.util.Tracer;
import facets.util.tree.DataNode;
import facets.util.tree.NodeList;
import facets.util.tree.Nodes;
import facets.util.tree.TypedNode;
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
import tones.Interval;
import tones.ScaleNote;
import tones.Tone;
import tones.Tone.Dissonance;
import tones.Voice;
public final class Incipit extends Tracer implements Comparable<Incipit>{
	static class Soundings{
		private final Map<Voice,Tone> soundings;
		private final short eighthAt,barEighths;
		private Soundings(short barEighths,short eighthAt,
				Map<Voice,Tone> soundings){
			this.eighthAt=eighthAt;
			this.barEighths=barEighths;
			this.soundings=soundings;
		}
		static Soundings newEmpty(short barEighths){
			return new Soundings(barEighths,(short)0,new HashMap());
		}
		Soundings newUpdated(Incipit i){
			Map<Voice,Tone> nowTones=new HashMap(),nowSoundings=new HashMap();
			for(Tone t:i.tones)
				if(!t.isRest()) nowTones.put(t.voice,t);
			int incipitAt=i.eighthAt;
			for(Voice v:Voice.values()){
				Tone now=nowTones.get(v),s=soundings.get(v);
				if(now!=null) nowSoundings.put(v,now);
				else if(s!=null){
					int trim=(incipitAt>0?incipitAt:barEighths)-eighthAt;
					s=s.newSounding(trim);
					if(s.eighths%barEighths>0) nowSoundings.put(v,s);
				}
			}
			return new Soundings(barEighths,(short)incipitAt,nowSoundings);
		}
		DataNode newDebugRoot(){
			NodeList nodes=new NodeList(Bars.newDebugRoot(getClass(),""//+soundings.size()+" "
					+(false?"":eighthAt)),true);
			List<Tone> values=new ArrayList(soundings.values());
			Collections.sort(values,new Comparator<Tone>(){
				@Override
				public int compare(Tone t1,Tone t2){
					return t1.voice.compareTo(t2.voice);
				}
			});
			nodes.parent.setValues(Objects.toLines(values.toArray()).split("\n"));
			return nodes.parent;
		}
	}
	public final Set<Tone> tones=new HashSet();
	public final short eighthAt;
	public int barAt=-1;
	int rise,staveGap,fall;
	final private Map<Tone,Collection<Dissonance>> againsts=new HashMap();
	private Soundings soundings;
	Incipit(short eighthAt){
		this.eighthAt=eighthAt;
	}
	public void addTone(Tone tone){
		tones.add(tone);
	}
	Soundings readSoundings(Soundings then){
		soundings=then.newUpdated(this);
		return soundings;
	}
	public Soundings soundings(){
		if(soundings==null)
			throw new IllegalStateException("Null soundings in "+this);
		return soundings;
	}
	public Collection<Dissonance> getDissonances(Tone t){
		Set<Dissonance> set=new HashSet();
		for(Voice v:Voice.values()){
			Tone sounding=soundings.soundings.get(v);
			if(v!=t.voice&&sounding!=null){
				Interval i=Interval.between(t,sounding);
				if(i.isDissonant(sounding)) set.add(new Dissonance(i,sounding));
			}
		}
		return set;
	}
	void close(){
		for(Tone t:tones)
			t.checkOffset(this);
		rise=6;
		staveGap=10;
		fall=6;
	}
	public String toString(){
		return //Debug.info(this)+
		""+eighthAt
		//        +" b:"+barAt
		//        +" tones:"+tones.size()
		;
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
	DataNode newDebugRoot(){
		List<Tone> sortTones=new ArrayList(tones);
		Collections.sort(sortTones,new Comparator<Tone>(){
			@Override
			public int compare(Tone t1,Tone t2){
				return t1.voice.compareTo(t2.voice);
			}
		});
		NodeList nodes=new NodeList(Bars.newDebugRoot(getClass(),toString()),true);
		if(false) for(Tone tone:sortTones){
			if(tone.isRest()) continue;
			DataNode add=tone.newDebugNode();
			nodes.add(add);
			Collection<Dissonance> got=againsts.get(tone);
			int count=got==null?0:got.size();
			String values=got==null?"":Objects.toLines(got.toArray());
			TypedNode clashes=Bars.newDebugRoot(Dissonance.class,""+count,
					values.split("\n"));
			if(clashes.values().length>0) Nodes.appendChild(add,clashes);
		}
		else nodes.add(soundings.newDebugRoot());
		return nodes.parent;
	}
}
