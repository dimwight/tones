package tones;
import static tones.ScaleNote.*;
import facets.util.Debug;
import facets.util.Strings;
import facets.util.Tracer;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import tones.Mark.Tie;
public final class Tone extends Tracer{
	public static final short NOTE_WHOLE=8,NOTE_HALF=NOTE_WHOLE/2,
		NOTE_QUARTER=NOTE_WHOLE/4,NOTE_EIGHTH=NOTE_WHOLE/8,
		NOTE_DOUBLE=NOTE_WHOLE*2,NOTE_NONE=0;
	final static class Context{
		final ScaleNote scaleNote;
		final Octave octave;
		final int barEighths=16,eighths;
		Context(ScaleNote scaleNote,Octave octave,int eighths){
			if(scaleNote==null)throw new IllegalArgumentException(
					"Null keyPitch in "+Debug.info(this));
			else if(octave==null)throw new IllegalArgumentException(
					"Null octave in "+Debug.info(this));
			else if(eighths<NOTE_NONE)throw new IllegalArgumentException(
					"Invalid eighths in "+Debug.info(this));
			this.scaleNote=scaleNote;
			this.octave=octave;
			this.eighths=eighths;
		}
		@Override
		public boolean equals(Object o){
			Context that=(Context)o;
			return resembles(that)&&that.eighths==eighths;
		}
		public boolean resembles(Context that){
			return that.scaleNote==scaleNote&&that.octave==octave
				&&that.barEighths==barEighths;
		}
		@Override
		public String toString(){
			return "<"+octave+","+scaleNote+//","+eighths+
			">";
		}
	}
	public final int lineAt,barAt,eighthAt;
	public final Voice voice;
	public final byte pitch;
	public final short eighths;
	public final HashSet<Mark>marks=new HashSet();
	final Context context;
	private final int[]intValues;
	private final VoiceLine line;
	Tone(VoiceLine line,int lineAt,int barAt,int eighthAt,byte pitch,
			short eighths,Context context){
		this.line=line;
		this.context=context;
		this.voice=line.voice;
		this.lineAt=lineAt;
		this.barAt=barAt;
		this.eighthAt=eighthAt;
		this.pitch=pitch;
		this.eighths=eighths;
		intValues=new int[]{lineAt,barAt,eighthAt,pitch,eighths};
		if(lineAt<0)return;
		Tone before=lineAt==0?null:line.tones.get(lineAt-1);
		if(before!=null&&before.pitch==pitch
				&&before.pitch!=PITCH_REST&&pitch!=PITCH_REST&&eighthAt%4==0)
			marks.add(new Tie(this,before));
	}
	public String toString(){
		ScaleNote note=pitchNote();
		return voice+" "+(note==ScaleNote.REST?"*":note)
				+": "+ eighths+Strings.intsString(intValues);
	}
	public ScaleNote pitchNote(){
		return ScaleNote.pitchNote(pitch);
	}
	public int hashCode(){
		return Arrays.hashCode(intValues);
	}
	public boolean equals(Object o){
		Tone that=(Tone)o;
		return voice==that.voice&&Arrays.equals(intValues,that.intValues);
	}
}