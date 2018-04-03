package tones;
import facets.util.Tracer;
public abstract class Mark extends Tracer{
	public static final class Tie extends Mark{
		public final Tone tied,before;
		public Tie(Tone tied,Tone before){
			this.tied=tied;
			this.before=before;
			trace(".",this);
		}
		@Override
		public String toString(){
			return ""+tied;
		}
	}
}
