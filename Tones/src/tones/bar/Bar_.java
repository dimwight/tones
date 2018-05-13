package tones.bar;
import static java.lang.Math.*;
import static tones.Tone.*;
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
import tones.bar.Incipit.Soundings;
final public class Bar_ extends Tracer{
  private static final int WIDTH_SPACE_SHRINK=(false?0:Tone.WIDTH_NOTE*2/3),
    START_AT=Tone.WIDTH_NOTE/2;
  public final int at,rise,staveGap,fall,width;
  public final Set<Incipit>incipits;
  public final Soundings endSoundings;
  private final Map<Voice,Integer>partAts=new HashMap();
  private int gridAt=0;
  Bar_(int barAt,List<Incipit>incipits,int barEighths){
    this.at=barAt;
    if(incipits==null)throw new IllegalStateException(
        "Null incipits in "+Debug.info(this));
    else this.incipits=new HashSet(incipits);
    for(Voice voice:voiceList)partAts.put(voice,START_AT);
    int rise=-1,staveGap=-1,fall=-1;
    for(Incipit i:incipits){
      gridAt=gridAtFromIncipit(i);
      rise=max(rise,i.rise);
      staveGap=max(staveGap,i.staveGap);
      fall=max(fall,i.fall);
    }
    width=gridAt=furthestAt(voiceList,barEighths);
    this.rise=rise;
    this.staveGap=staveGap;
    this.fall=fall;
    endSoundings=incipits.get(incipits.size()-1).soundings();
    
  }
  int gridAtFromIncipit(Incipit i){
    i.close();
    List<Voice>toneVoices=new ArrayList();
    for(Tone t:i.tones)toneVoices.add(t.voice);
    int gridAt=i.barAt=furthestAt(toneVoices,i.eighthAt);
    for(Tone t:i.tones)partAts.put(t.voice,gridAt+t.eighths*WIDTH_NOTE);
		return gridAt;
  }
  int furthestAt(Iterable<Voice>voices,int eighthAt){
    int atUnit=true?1:WIDTH_NOTE,jump=eighthAt*atUnit-gridAt,
    		gap=jump<=atUnit?0:jump-atUnit;
    if(false&&jump<0)throw new IllegalStateException("Bad jump in "+this);
    for(Voice voice:voiceList)
      partAts.put(voice,partAts.get(voice)-gap*WIDTH_SPACE_SHRINK);
    int furthestNow=0;
    for(Voice voice:voices)
      furthestNow=max(furthestNow,partAts.get(voice));
    return furthestNow;
  }
  @Override
  public boolean equals(Object obj){
    Bar_ that=(Bar_)obj;
    return this==that||incipits.equals(that.incipits);
  }
  public String toString(){
    return Debug.info(this)+" at="+at+" incipits="
    + (true?incipits.size():"\n"+Objects.toLines(((Collection)incipits).toArray()));
  }
}
