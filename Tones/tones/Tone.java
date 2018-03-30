package tones;
import facets.util.Tracer;
import facets.util.Util;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
public final class Tone extends Tracer{
	public interface Tag{
		Tag Tie=new Tag(){
			public String toString(){return "Tie";}
		},
		Beam=new Tag(){
			public String toString(){return "Group";}
		};
	}
	public static final short DURATION_WHOLE=8,DURATION_HALF=DURATION_WHOLE/2,
		DURATION_QUARTER=DURATION_WHOLE/4,DURATION_EIGHT=DURATION_WHOLE/8,
		DURATION_DOUBLE=DURATION_WHOLE*2,DURATION_MIN=DURATION_EIGHT,DURATION_NONE=0;
	public final int barAt,measureAt;
	public final Voice voice;
	public final byte pitch;
	public final short duration;
	public final Collection<Tag>tags=new HashSet();
	public Tone(Voice voice,int barAt,int measureAt,byte pitch,short duration,Set<Tag>tags){
		this.voice=voice;
		this.barAt=barAt;
		this.measureAt=measureAt;
		this.pitch=pitch;
		this.duration=duration;
		if(tags!=null)this.tags.addAll(tags);
		if(false&&tags.size()>0)trace(".Tone: ",this);
	}
	public String toString(){
		ScaleNote note=pitchNote();
		return voice.code+":"+
			(tags.isEmpty()?"":(tags))+(note==ScaleNote.REST?"*":note)+duration;
	}
	public ScaleNote pitchNote(){
		return ScaleNote.pitchNote(pitch);
	}
	public int hashCode(){
		return Arrays.hashCode(intValues());
	}
	public boolean equals(Object o){
		Tone that=(Tone)o;
		return voice==that.voice&&Arrays.equals(intValues(),that.intValues());
	}
	private int[]intValues(){
		return new int[]{barAt,measureAt,pitch,duration};
	}
}