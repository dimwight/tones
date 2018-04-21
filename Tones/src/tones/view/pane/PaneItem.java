package tones.view.pane;
import static tones.view.pane.PaneItem.PaneTie.TieType.*;
import facets.core.app.avatar.AvatarContent;
import facets.util.Objects;
import facets.util.Tracer;
import facets.util.geom.Line;
import facets.util.geom.Vector;
import tones.Tone;
import tones.Voice;
import tones.bar.Bar;
public abstract class PaneItem extends Tracer implements AvatarContent{
	public static final class PaneTie extends PaneItem{
		public enum TieType{BeforeAfter,BeforeNull,AfterNull}
		public final PaneBar bar;
		public final TieType type;
		public final Voice voice;
		public final Vector beforeAt,afterAt;
		public final PaneNote before,after;
		public final boolean selected;
		public PaneTie(PaneNote before,PaneNote after,PaneBar bar,boolean selected){
			this.before=before;
			this.after=after;
			this.bar=bar;
			this.selected=selected;
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
		public final boolean selected;
		public PaneBeam(PaneNote[]notes,boolean selected){
			this.notes=notes;
			this.selected=selected;
			Tone tone=notes[0].tone;
			if(false&&tone.barAt==6&&tone.voice==Voice.Alto)trace(": ",notes);
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
	
}
