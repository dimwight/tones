package tones.view.pane;
import facets.util.geom.Point;
import facets.util.geom.Vector;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.util.Arrays;
import tones.Clef;
import tones.Tone;
import tones.bar.Incipit;
public abstract class PaneNote extends PaneItem{
	public static final int DOT_NONE=0,DOT_LEVEL=1,DOT_BELOW=-1;
	public final PaneBar bar;
	public final Tone tone;
	public final Incipit incipit;
	public final double staveX,staveY,ledgerLineShift,dotAt;
	public final int ledgerLines;
	public final Double tailBox;
	private final String debugString;
	PaneNote(PaneBar bar,Tone tone,PaneIncipit i,double barStaveY,Clef clef){
		this.bar=bar;
		this.tone=tone;
		incipit=i.content;
		staveX=i.scaledStaveX(tone);
		final int stavePitch=tone.pitch-clef.staveMidPitch,
			staveToMidPitch=STAVE_GRID/2-1;
		staveY=barStaveY+staveToMidPitch-stavePitch;
		boolean aboveMidPitch=stavePitch>0;
		int beyondStave=Math.abs(stavePitch)-staveToMidPitch;
		ledgerLines=beyondStave<=0?0
				:beyondStave/2*(aboveMidPitch?1:-1);
		ledgerLineShift=aboveMidPitch?beyondStave%2+1:(beyondStave+1)%2;
		dotAt=tone.eighths%3!=0?DOT_NONE:stavePitch%2==0?DOT_BELOW:DOT_LEVEL;
		debugString=tone.pitchNote()+
			" stavePitch="+stavePitch+" aboveMidPitch="+aboveMidPitch
			+" beyondStave="+beyondStave;
		double tailHeight=3.5;
		tailBox=tone.eighths>Tone.NOTE_HALF||tone.eighths==Tone.NOTE_EIGHTH?null
				:tone.voice.tailsUp?new Rectangle2D.Double(1.35,-tailHeight-0.2,3,tailHeight)
				:new Rectangle2D.Double(0.09,0,3,tailHeight);
	}
	public String toString(){
		return false?debugString:tone.toString()+" "+staveAt();
	}
	public int hashCode(){
		return Arrays.hashCode(intValues());
	}
	public boolean equals(Object obj){
		if(true)throw new RuntimeException("Untested");
		PaneNote that=(PaneNote)obj;
		return this==that||Arrays.equals(intValues(),that.intValues());
	}
	private int[]intValues(){
		return new int[]{tone.pitch,tone.eighths};
	}
	public Vector staveAt(){
		return new Vector(staveX,staveY);
	}
}