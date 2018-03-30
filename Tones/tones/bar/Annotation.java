package tones.bar;
import facets.util.Debug;
import facets.util.Tracer;
import java.util.List;
import tones.Tone;
public class Annotation extends Tracer{
	public final Bar bar;
	public final Incipit incipit;
	public final Tone tone;
	Annotation(Bar bar,Incipit incipit,Tone tone){
		this.bar=bar;
		this.incipit=incipit;
		this.tone=tone;
	}
	@Override
	public String toString(){
		return getClass().getSimpleName()+" bar="+bar.at+" incipit="+incipit.measureAt+" tone="+tone;
	}
	public static abstract class Group extends Annotation{
		Group(Bar bar,Incipit incipit,Tone tone){
			super(bar,incipit,tone);
		}
	}
	public static final class TieGroup extends Group{
		public final Annotation end;
		TieGroup(Bar bar,Incipit incipit,Tone tone,List<Bar>bars){
			super(bar,incipit,tone);
			Annotation end=null;
			Bar search=bar;
			for(Incipit i:search.incipits)
				if(end==null&&i.measureAt>incipit.measureAt)for(Tone t:i.tones)
						if(end!=null)break;
						else if(t.pitch==tone.pitch)end=new Annotation(bar,i,t);
			if(end==null)search=bars.get(bar.at+1);
			if(search!=bar)for(Incipit i:search.incipits)
				if(end==null)for(Tone t:i.tones)
						if(end!=null)break;
						else if(t.pitch==tone.pitch)end=new Annotation(search,i,t);
			if(end==null)throw new IllegalStateException("Null to in "+Debug.info(this));
			else this.end=end;
			search.annotations.add(this);
		}
	}
	public static final class BeamGroup extends Group{
		public final Iterable<Tone>tones;
		public BeamGroup(Bar bar,Incipit incipit,List<Tone>tones){
			super(bar,incipit,tones.get(0));
			this.tones=tones;
		}
	}
}