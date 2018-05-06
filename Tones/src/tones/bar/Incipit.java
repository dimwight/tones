package tones.bar;
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
import tones.Tone;
import tones.Voice;
public final class Incipit extends Tracer implements Comparable<Incipit>{
  static class Soundings{
  	private final Map<Voice,Short>voiceEighths;
    private final short eighthAt;
    private Soundings(short eighthAt,Map<Voice,Short>voiceEighths){
      this.eighthAt=eighthAt;
      this.voiceEighths=voiceEighths;
    }
    static Soundings newStarting(){
    	Map<Voice,Short>voiceEighths=new HashMap();
    	for(Voice v:Voice.values())voiceEighths.put(v,(short)0);
      return new Soundings((short)0,voiceEighths);
    }
    Soundings newUpdated(Incipit i,short barEighths){
    	Map<Voice,Short>incipitEighths=new HashMap(),
    			nowEighths=new HashMap();
      for(Tone t:i.tones)
	if(t.pitch!=Rest)
	  incipitEighths.put(t.voice,t.eighths);
      for(Voice v:Voice.values()){
        Short now=incipitEighths.get(v),
	  then=voiceEighths.get(v);
	if(now!=null)
          nowEighths.put(v,now);
	else if(then!=null)
		nowEighths.put(v,(short)(then
          		-((i.eighthAt>0?i.eighthAt:barEighths)
      				-eighthAt)
        		));
            
      }
      return new Soundings(i.eighthAt,Collections.unmodifiableMap(nowEighths));
    }
		DataNode newDebugRoot(){
			NodeList nodes=new NodeList(Bars.newDebugRoot(getClass(),
					"Soundings: "+eighthAt),true);
			nodes.parent.setValues(Objects.toLines(voiceEighths.entrySet().toArray()).split("\n"));
			return nodes.parent;
		}
  }
  public final Collection<Tone>tones=new HashSet();
  public final short eighthAt;
  public int barAt=-1;
  int rise,staveGap,fall;
  private Soundings soundings;
  public Soundings soundings(){
  	if(soundings==null)throw new IllegalStateException(
				"Null soundings in "+this);
		return soundings;
	}
	DataNode newDebugRoot(){
		NodeList nodes=new NodeList(Bars.newDebugRoot(getClass(),
				"eighth="+eighthAt+" bar="+barAt),true);
		List<Tone>sortTones=new ArrayList(tones);
		Collections.sort(sortTones,new Comparator<Tone>(){
		  @Override
		  public int compare(Tone t1,Tone t2){
		    return t1.voice.compareTo(t2.voice);
		  }
		});
		for(Tone tone:sortTones)nodes.add(tone.newDebugNode());
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
	  return soundings=then.newUpdated(this,eighthAt);
	}
	void close(){
    for(Tone t:tones)t.checkOffset(this);
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
  private int[]intValues(){
    return new int[]{eighthAt,fall,staveGap,rise,barAt};
  }
}
