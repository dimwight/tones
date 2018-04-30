package tones.bar;
import facets.util.Debug;
import facets.util.Tracer;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import tones.Tone;
public final class Incipit extends Tracer implements Comparable<Incipit>{
  public final Collection<Tone>tones=new HashSet();
  public final int partAt;
  public int barAt=-1;
  int rise,staveGap,fall;
  Incipit(int partAt){
    this.partAt=partAt;
  }
  public void addTone(Tone tone){
    ((Set)tones).add(tone);
  }
  void close(){
    for(Tone t:tones)tone.checkOffset(tones);
    rise=6;
    staveGap=10;
    fall=6;
  }
  public String toString(){
    return Debug.info(this)+" m"+partAt+" b"+barAt+" "+tones;
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
    return new Integer(partAt).compareTo(new Integer(i.partAt));
  }
  private int[]intValues(){
    return new int[]{partAt,fall,staveGap,rise,barAt};
  }
}
