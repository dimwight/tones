package tones.view.pane;
import static tones.view.pane.PaneItem.PaneTie.TieType.*;
import facets.core.app.avatar.AvatarContent;
import facets.util.Objects;
import facets.util.Tracer;
import facets.util.geom.Vector;
public abstract class PaneItem extends Tracer implements AvatarContent{
	public static final class PaneTie extends PaneItem{
		public enum TieType{FromTo,From,To,ToFrom}
		public final PaneBar bar;
		public final TieType type;
		public final Vector fromAt,toAt;
		PaneTie(PaneNote from,PaneNote to,PaneBar bar){
			this.bar=bar;
			type=to==null?To:ToFrom;
			this.fromAt=from.staveAt();
			this.toAt=to==null?null:to.staveAt();
			if(false)trace(":",this);
		}
		@Override
		public String toString(){
			return " from="+fromAt+" to="+(toAt==null?"null":toAt);
		}
	}
	public static final class PaneBeam extends PaneItem{
		public final PaneNote[]notes;
		PaneBeam(PaneNote[]notes){
			this.notes=notes;
			if(false&&notes[0].tone.barAt<4)
				trace(": ",notes);
		}
		public String toString(){
			return Objects.toString(notes);
		}
	}
	public static final int STAVE_GRID=11;
	protected boolean marking(){
		return false;
	}
}