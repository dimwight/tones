package tones;
import facets.util.Tracer;
public abstract class Mark extends Tracer{
	public static final class Tie extends Mark{
		public final Tone tied,to;
		public Tie(Tone tied,Tone to){
			this.tied=tied;
			this.to=to;
		}
		@Override
		public String toString(){
			return "tied="+tied+" to="+to;
		}
	}
}
