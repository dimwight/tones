package tones;
import static tones.ScaleNote.*;
import facets.util.Strings;
import facets.util.Tracer;
import java.util.Arrays;
import java.util.HashSet;
import tones.Mark.Tie;
public final class Tone extends Tracer{
	public static final char CODE_SCALE='s',CODE_OCTAVE_UP='+',CODE_OCTAVE_DOWN='-',
			CODE_TIE='T',CODE_BEAM='B',CODE_BAR_SIZE='Z';
	public static final int BAR_EIGHTHS_DEFAULT=16;
	public static final String CODES_NOTE="abcdefgx";
	public static final short NOTE_WHOLE=8,NOTE_HALF=NOTE_WHOLE/2,
		NOTE_QUARTER=NOTE_WHOLE/4,NOTE_EIGHTH=NOTE_WHOLE/8,
		NOTE_DOUBLE=NOTE_WHOLE*2,NOTE_NONE=0;
	public final int barAt,eighthAt;
	public final Voice voice;
	public final byte pitch;
	public final short eighths;
	public final HashSet<Mark>marks=new HashSet();
	private final int[]intValues;
	public Tone(Voice voice,int barAt,int eighthAt,byte pitch,short eighths){
		this.voice=voice;
		this.barAt=barAt;
		this.eighthAt=eighthAt;
		this.pitch=pitch;
		this.eighths=eighths;
		intValues=new int[]{barAt,eighthAt,pitch,eighths};
	}
	public void checkTied(Tone before){
		if(before==null||before.isRest()||isRest()
				||before.pitch!=pitch||eighthAt%4!=0)return;
			Tie tie=new Tie(before,this);
			marks.add(tie);
			before.marks.add(tie);
	}
	private boolean isRest(){
		return this.pitch==PITCH_REST;
	}
	public String toString(){
		ScaleNote note=pitchNote();
		return voice+" "+pitchNote()+": "+Strings.intsString(intValues);
	}
	public ScaleNote pitchNote(){
		return ScaleNote.pitchNote(pitch);
	}
	public int hashCode(){
		return Arrays.hashCode(intValues);
	}
	public boolean equals(Object o){
		Tone that=(Tone)o;
		return this==that||(voice==that.voice&&Arrays.equals(intValues,that.intValues));
	}
}