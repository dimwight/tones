package tones.bar;
import static java.lang.Math.*;
import static tones.Voice.*;
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
final public class Bar extends Tracer{
  public static final int WIDTH_NOTE=8;
  private static final int WIDTH_SPACE_SHRINK=(false?0:WIDTH_NOTE*2/3),
    START_AT=WIDTH_NOTE/2;
  public final int at,rise,staveGap,fall,width;
  public final Set<Incipit>incipits;
  private final Map<Voice,Integer>partAts=new HashMap();
  private int thenAt=0;
  Bar(int barAt,Collection<Incipit>incipits,int barEighths){
    this.at=barAt;
    if(incipits==null)throw new IllegalStateException(
        "Null incipits in "+Debug.info(this));
    else this.incipits=new HashSet(incipits);
    for(Voice voice:voiceList)partAts.put(voice,START_AT);
    int rise=-1,staveGap=-1,fall=-1;
    for(Incipit i:incipits){
      readIncipit(i);
      rise=max(rise,i.rise);
      staveGap=max(staveGap,i.staveGap);
      fall=max(fall,i.fall);
    }
    width=furthestAt(voiceList,barEighths);
    this.rise=rise;
    this.staveGap=staveGap;
    this.fall=fall;
  }
  void readIncipit(Incipit incipit){
    incipit.close();
    List<Voice>toneVoices=new ArrayList();
    for(Tone t:incipit.tones)toneVoices.add(t.voice);
    int furthest=furthestAt(toneVoices,incipit.partAt);
    incipit.barAt=furthest;
    for(Tone t:incipit.tones)partAts.put(t.voice,furthest+t.eighths*WIDTH_NOTE);
  }
  int furthestAt(Iterable<Voice>voices,int eighthAt){
    int jump=eighthAt-thenAt,gap=jump<=1?0:jump-1;
    for(Voice voice:voiceList)
      partAts.put(voice,partAts.get(voice)-WIDTH_SPACE_SHRINK*gap);
    int furthest=0;
    for(Voice voice:voices)furthest=max(furthest,partAts.get(voice));
    thenAt=furthest;
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
