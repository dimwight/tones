package tones;
import facets.util.Debug;
import facets.util.Tracer;
import facets.util.Util;
import java.util.ArrayList;
import java.util.List;
public abstract class Mark extends Tracer{
	public static final class Tie extends Mark{
		public final Tone tied,to;
		public Tie(Tone tied,Tone to){
			this.tied=tied;
			this.to=to;	
			if(tied.barAt<4)trace(": ",this);
		}
		@Override
		public String toString(){
			return "tied="+tied+" to="+to;
		}
		@Override
		public boolean equals(Object o){
			Tie that=(Tie)o;
			return that.tied.equals(tied)&&that.to.equals(to);
		}
	}
	public static final class Beam extends Mark{
		public final List<Tone>tones=new ArrayList();
		public final Voice voice;
		Beam(Voice voice){
			this.voice=voice;
		}
		void addTone(Tone add){
			tones.add(add);
			if(false&&add.barAt<4)trace(".addTone: ",add);
		}
		@Override
		protected void traceOutput(String msg){
			Util.printOut(Debug.info(this)+" "+msg);
		}
	}
}
