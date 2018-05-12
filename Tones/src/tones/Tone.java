package tones;
import static tones.ScaleNote.*;
import static tones.bar.Bars.*;
import facets.util.Objects;
import facets.util.Strings;
import facets.util.Tracer;
import facets.util.tree.DataNode;
import java.util.Arrays;
import java.util.HashSet;
import tones.Mark.Tie;
import tones.bar.Incipit;
public final class Tone extends Tracer{
  public static final short NOTE_WHOLE=8,NOTE_HALF=NOTE_WHOLE/2,
      NOTE_QUARTER=NOTE_WHOLE/4,NOTE_EIGHTH=NOTE_WHOLE/8,
      NOTE_DOUBLE=NOTE_WHOLE*2,NOTE_NONE=0;
  public static class Dissonance{
    public final Interval interval;
    public final Tone sounding;
    public Dissonance(Interval interval,Tone sounding){
      this.interval=interval;
      this.sounding=sounding;
    }
    @Override
    public String toString(){
      return sounding.voice.code+":"+interval;
    }
  }
  public final HashSet<Mark> marks=new HashSet();
  public final int barAt;
  public final Voice voice;
  public final byte pitch;
  public final short eighths;
  private final int eighthAt,intValues[];
  private int offset;
  public static final int WIDTH_NOTE=8;
  public Tone(Voice voice,int barAt,int eighthAt,byte pitch,short eighths){
    this.voice=voice;
    this.barAt=barAt;
    this.eighthAt=eighthAt;
    this.pitch=pitch;
    this.eighths=eighths;
    intValues=pitch==ScaleNote.PITCH_REST?new int[]{barAt,eighthAt,eighths}
        :new int[]{barAt,eighthAt,eighths,pitch};
  }
  public void checkTied(Tone before){
    if(before==null||before.isRest()||isRest()||before.pitch!=pitch
        ||!isOnBeat(NOTE_HALF))return;
    Tie tie=new Tie(before,this);
    marks.add(tie);
    before.marks.add(tie);
  }
  public boolean isOnBeat(short note){
    return eighthAt%note==0;
  }
  public boolean isRest(){
    return this.pitch==PITCH_REST;
  }
  public Tone newSounding(int trim){
    return new Tone(voice,barAt,eighthAt,pitch,(short)(this.eighths-trim));
  }
  public ScaleNote pitchNote(){
    return ScaleNote.pitchNote(pitch);
  }
  public void checkOffset(Incipit i){
    for(Tone that:i.tones)
      if(that!=this&&Math.abs(that.pitch-pitch)<2){
        boolean isOffset=that.eighths>eighths&&that.eighths>NOTE_QUARTER?true
            :that.eighths==eighths?that.pitch==pitch&&eighths<NOTE_WHOLE?false
                :!that.marks.isEmpty():false;
        if(isOffset)offset=WIDTH_NOTE
            *(that.eighths==NOTE_WHOLE?7:that.eighths%3==0?9:5)/5;
      }
  }
  public int getOffset(){
    return offset;
  }
  public int hashCode(){
    return Arrays.hashCode(intValues);
  }
  public boolean equals(Object o){
    Tone that=(Tone)o;
    return this==that||(voice==that.voice
        &&Arrays.equals(intValues,that.intValues));
  }
  public String toString(){
    ScaleNote note=pitchNote();
    return voice+" "+pitchNote()+(true?(" "+eighths):(": "+Strings.intsString(intValues)));
  }
  public DataNode newDebugNode(){
    int markCount=marks.size();
    return true||markCount==0?newDebugRoot(getClass(),
        voice+" "+pitchNote()+" "+eighths)
        :newDebugRoot(Mark.class,"marks="+markCount,
            Objects.toLines(marks.toArray()).split("\n"));
  }
}
