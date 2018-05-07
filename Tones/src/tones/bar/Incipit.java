package tones.bar;
import static tones.ScaleNote.*;
import facets.util.Debug;
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
import tones.ScaleNote;
import tones.Tone;
import tones.Voice;
public final class Incipit extends Tracer implements Comparable<Incipit>{
	static class Against{
		final int interval;
		Against(Tone t,Tone sounding){
			interval=t.pitch-sounding.pitch;
		}
		static Collection<Against>newToneSet(Tone t,Map<Voice,Tone>soundings){
			Set<Against>set=new HashSet();
			for(Voice v:Voice.values()){
				Tone got=soundings.get(v);
				if(v!=t.voice&&got!=null)
					set.add(new Against(t,got));
			}
			return set; 
		}
		static TypedNode newDebugNode(Tone tone,Map<Tone,Collection<Against>>againsts){
			int count=againsts.size();
			String[]values=Objects.toLines(againsts.get(tone).toArray()).split("\n");
			return Bars.newDebugRoot(Against.class,""+count,values);
		}
		@Override
		public String toString(){
			return ""+interval;
		}
	}
	static class Soundings{
		private final Map<Voice,Tone>soundings;
		private final short eighthAt,barEighths;
		private Soundings(short barEighths,short eighthAt,
				Map<Voice,Tone>soundings){
			this.eighthAt=eighthAt;
			this.barEighths=barEighths;
			this.soundings=soundings;
		}
		Map<Tone,Collection<Against>>newToneAgainsts(Collection<Tone>tones){
			Map<Tone,Collection<Against>>ta=new HashMap();
			for(Tone t:tones)
				ta.put(t,Against.newToneSet(t,soundings));
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
				Tone now=incipitTones.get(v),then=soundings.get(v);
				if(now!=null) nowSoundings.put(v,now);
				else if(then!=null) nowSoundings.put(v,then.newSounding(
						(short)((i.eighthAt>0?i.eighthAt:barEighths)-eighthAt)));
			}
			return new Soundings(barEighths,i.eighthAt,
					Collections.unmodifiableMap(nowSoundings));
		}
		DataNode newDebugRoot(){
			NodeList nodes=new NodeList(
					Bars.newDebugRoot(getClass(),""+soundings.size()+(true?"":eighthAt)),
					true);
			nodes.parent.setValues(
					Objects.toLines(soundings.entrySet().toArray()).split("\n"));
			return nodes.parent;
		}
	}
	public final Collection<Tone>tones=new HashSet();
	public Map<Tone,Collection<Against>>againsts;
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
			if(toneNote(tone)!=Rest){
				DataNode add=tone.newDebugNode();
				nodes.add(add);
				TypedNode againsts_=Against.newDebugNode(tone,againsts);
				if(againsts_.values().length>0)Nodes.appendChild(add,againsts_);
			}
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
