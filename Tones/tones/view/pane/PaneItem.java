package tones.view.pane;
import static tones.view.pane.PaneItem.PaneTie.TieType.*;
import facets.core.app.avatar.AvatarContent;
import facets.util.Objects;
import facets.util.Tracer;
import facets.util.geom.Line;
import facets.util.geom.Vector;
import tones.Voice;
import tones.bar.Bar;
public abstract class PaneItem extends Tracer implements AvatarContent{
	public static final class PaneTie extends PaneItem{
		public enum TieType{BeforeAfter,BeforeNull,AfterNull}
		public final PaneBar bar;
		public final TieType type;
		public final Voice voice;
		public final Vector beforeAt,afterAt;
		private final PaneNote before;
		private final PaneNote after;
		PaneTie(PaneNote before,PaneNote after,PaneBar bar){
			this.before=before;
			this.after=after;
			this.bar=bar;
			type=after==null?AfterNull:before==null?BeforeNull:BeforeAfter;
			voice=(type==AfterNull?before:after).tone.voice;
			this.beforeAt=before==null?null:before.staveAt();
			this.afterAt=after==null?null:after.staveAt();
			if(false)trace(":",this);
		}
		@Override
		public String toString(){
			return " before="+before==null?"null":before+
					" after="+(after==null?"null":after);
		}
	}
	public static final class PaneBeam extends PaneItem{
		private final PaneNote[]notes;
		public final Line geom;
		public final PaneNote from,to;
		PaneBeam(PaneNote[]notes){
			this.notes=notes;
			if(false&&notes[0].tone.barAt<4)
				trace(": ",notes);
			from=notes[0];
			to=notes[notes.length-1];
			geom=new Line(from.tail.to,to.tail.to);
		}
		public String toString(){
			return Objects.toString(notes);
		}
	}
	public static final int STAVE_GRID=11;
	final static Vector scaleToNoteWidth=new Vector(Bar.WIDTH_NOTE,1);
	protected boolean marking(){
		return false;
	}
}