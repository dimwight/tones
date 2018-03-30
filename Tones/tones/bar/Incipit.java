package tones.bar;
import static tones.Tone.*;
import facets.util.Debug;
import facets.util.Tracer;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import tones.Tone;
public final class Incipit extends Tracer implements Comparable<Incipit>{
	public final Collection<Tone>tones=new HashSet();
	final int measureAt;
	public int barAt=-1;
	int rise,staveGap,fall;
	public Incipit(int measureAt){
		this.measureAt=measureAt;
	}
	public Incipit newCopy(Tone tone){
		Incipit copy=new Incipit(measureAt);
		copy.addTone(tone);
		copy.close();
		copy.barAt=barAt;
		return copy;
	}
	public void addTone(Tone tone){
		((Set)tones).add(tone);
	}
	void close(){
		rise=6;
		staveGap=10;
		fall=6;
	}
	public String toString(){
		return Debug.info(this)+" m"+measureAt+" b"+barAt+" "+tones;
	}
	public int hashCode(){
		return Arrays.hashCode(intValues());
	}
	public boolean equals(Object obj){
		if(true)throw new RuntimeException("Untested");
		Incipit that=(Incipit)obj;
		return this==that||Arrays.equals(intValues(),that.intValues());
	}
	@Override
	public int compareTo(Incipit i){
		return new Integer(measureAt).compareTo(new Integer(i.measureAt));
	}
	private int[]intValues(){
		return new int[]{measureAt,fall,staveGap,rise,barAt};
	}
}