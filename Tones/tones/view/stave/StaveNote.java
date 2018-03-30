package tones.view.stave;

import java.util.Arrays;
import tones.Clef;
import tones.Tone;
import tones.bar.Incipit;

public class StaveNote extends StaveItem{
	public static final int DOT_NONE=0,DOT_LEVEL=1,DOT_BELOW=-1;
	public final double staveX,staveY,ledgerLineShift,dotAt;
	public final Tone content;
	public final Incipit incipit;
	public final int ledgerLines;
	private final String debugString;
	StaveNote(Tone content,StaveIncipit i,double barStaveY,Clef clef){
		this.content=content;
		incipit=i.content;
		staveX=i.scaledStaveX(content);
		final int stavePitch=content.pitch-clef.staveMidPitch,
			staveToMidPitch=STAVE_GRID/2-1;
		staveY=barStaveY+staveToMidPitch-stavePitch;
		boolean aboveMidPitch=stavePitch>0;
		int beyondStave=Math.abs(stavePitch)-staveToMidPitch;
		ledgerLines=beyondStave<=0?0
				:beyondStave/2*(aboveMidPitch?1:-1);
		ledgerLineShift=aboveMidPitch?beyondStave%2+1:(beyondStave+1)%2;
		dotAt=content.duration%3!=0?DOT_NONE:stavePitch%2==0?DOT_BELOW:DOT_LEVEL;
		debugString=content.pitchNote()+
			" stavePitch="+stavePitch+" aboveMidPitch="+aboveMidPitch
			+" beyondStave="+beyondStave;
	}
	public String toString(){
		return debugString;
	}
	public int hashCode(){
		return Arrays.hashCode(intValues());
	}
	public boolean equals(Object obj){
		if(true)throw new RuntimeException("Untested");
		StaveNote that=(StaveNote)obj;
		return this==that||Arrays.equals(intValues(),that.intValues());
	}
	private int[]intValues(){
		return new int[]{content.pitch,content.duration};
	}
}