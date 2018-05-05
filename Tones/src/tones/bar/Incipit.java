package tones.bar;
import facets.util.Debug;
import facets.util.Tracer;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import tones.Tone;
public final class Incipit extends Tracer implements Comparable<Incipit>{
  static class Soundings{
    Soundings newUpdated(Incipit i){
      for(Voice v:Voice.values()){
        got=voiceEighths.get(v);
        -(;
        if(got!=null)voiceTones.put(v,i.eighthAt-eighthAt);
        else voiceTones.put(v,i.voiceTones.get(v);
            
      }
      return new Soundings(voiceTones);
    }
  }
  public final Collection<Tone>tones=new HashSet();
  public final int eighthAt;
  public int barAt=-1;
  int rise,staveGap,fall;
  Incipit(int eighthAt){
    this.eighthAt=eighthAt;
  }
  public void addTone(Tone tone){
    ((Set)tones).add(tone);
  }
  void close(){
    for(Tone t:tones)t.checkOffset(this);
    rise=6;
    staveGap=10;
    fall=6;
  }
  Soundings readSoundings(Soundings then){
    soundings=then.newUpdated(this);
    return soundings.newCopy();
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
