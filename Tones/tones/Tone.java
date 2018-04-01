package tones;
import facets.util.Tracer;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
public final class Tone extends Tracer{
	
	public static final short NOTE_WHOLE=8,NOTE_HALF=NOTE_WHOLE/2,
		NOTE_QUARTER=NOTE_WHOLE/4,NOTE_EIGHTH=NOTE_WHOLE/8,
		NOTE_DOUBLE=NOTE_WHOLE*2,NOTE_NONE=0;
	public final int barAt,eighthAt;
	public final Voice voice;
	public final byte pitch;
	public final short eighths;
	public final Collection<Tag>tags=new HashSet();
	private final int[]intValues;
	public Tone(Voice voice,int barAt,int eighthAt,byte pitch,short eighths,
			Set<Tag>tags){
		this.voice=voice;
		this.barAt=barAt;
		this.eighthAt=eighthAt;
		this.pitch=pitch;
		this.eighths=eighths;
		intValues=new int[]{barAt,eighthAt,pitch,eighths};
		if(tags!=null)this.tags.addAll(tags);
		if(false&&tags.size()>0)trace(".Tone: ",this);
	}
	public String toString(){
		ScaleNote note=pitchNote();
		return voice.code+":"+
			(tags.isEmpty()?"":(tags))+(note==ScaleNote.REST?"*":note)+eighths;
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
