package tones.view.pane;
import facets.core.app.avatar.AvatarContent;
import facets.util.Debug;
import facets.util.Tracer;
public abstract class PaneItem extends Tracer implements AvatarContent{
	public static final class PaneTie extends PaneItem{
		public final PaneNote from,to;
		PaneTie(PaneNote from,PaneNote to){
			this.from=from;
			this.to=to;
			if(false)trace(":",this);
		}
		@Override
		public String toString(){
			return " from="+from.staveAt()+" to="+(to==null?"null":to.staveAt());
		}
	}
	public static final class PaneBeam extends PaneItem{
		public final PaneNote[]notes;
		PaneBeam(PaneNote[]notes){
			this.notes=notes;
			if(notes[0].tone.barAt<4)
				trace(": ",notes);
		}
	}
	public static final int STAVE_GRID=11;
	protected boolean marking(){
		return false;
	}
	public String toString(){
		return Debug.info(this)+": ";
	}
}