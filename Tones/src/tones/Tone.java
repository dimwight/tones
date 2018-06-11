	package tones;
	import static tones.ScaleNote.*;
	import static tones.bar.Bars.*;
import facets.util.Debug;
import facets.util.Objects;
	import facets.util.Strings;
	import facets.util.Tracer;
import facets.util.Util;
import facets.util.tree.DataNode;
	import java.util.Arrays;
	import java.util.HashSet;
	import tones.Mark.Tie;
	import tones.bar.Bar;
	import tones.bar.Incipit;
	public final class Tone extends Tracer{
		public static final short NOTE_WHOLE=false?8:16,NOTE_HALF=NOTE_WHOLE/2,
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
		public final HashSet<Mark>marks=new HashSet();
		public final int barAt;
		public final Voice voice;
		public final byte pitch;
		public final short eighths;
		private final int eighthAt,intValues[];
		private int offset=-1;
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
		public int checkBarOffset(Incipit i,int noteWidth){
			if(offset>0)throw new IllegalStateException("Existing offset="+offset);
			else offset=0;
			if(!isRest())for(Tone that:i.tones)
				if(that!=this&&Math.abs(that.pitch-pitch)<2){
					Tie thatTie=that.getMark(Tie.class);
					boolean isOffset=this.eighths<that.eighths
							||(thatTie!=null&&that==thatTie.before);
					if(true){
						isOffset&=!(that.eighths>eighths&&that.eighths==NOTE_QUARTER);
						if(that.eighths==eighths)isOffset&=!(that.pitch==pitch&&eighths<NOTE_WHOLE);
					}
					if(false)trace(".checkBarOffset: isOffset="+isOffset);
					offset=!isOffset?0:noteWidth
							*(that.eighths==NOTE_WHOLE?7:that.eighths%3==0?9:5)/5;
					if(false&&isOffset)trace(".checkBarOffset: offset=",offset);
				} 
			return offset;
		}
		public String toString(){
			ScaleNote note=pitchNote();
			return //Debug.info(this)+" "+
					voice+" "+pitch+(false?(" "+eighths):(": "+Strings.intsString(intValues)));
		}
		@Override
		protected void traceOutput(String msg){
			if(barAt==6)Util.printOut(this+msg);
		}
		public <T extends Mark> T getMark(Class<T>type){
			for(Mark mark:marks)
				if(mark.getClass()==type)return (T)mark;
			return null;
		}
		public DataNode newDebugNode(){
			int markCount=marks.size();
			Class type=getClass();
			String title=toString()+" offset="+getOffset();
			return true?newDebugRoot(type,title)
					:newDebugRoot(type,title,
							newDebugRoot(Mark.class,"marks="+markCount,
							Objects.toLines(marks.toArray()).split("\n")));
		}
		public int getOffset(){
			if(offset<0)throw new IllegalStateException("Offset not checked in "+this);
			else return offset;
		}
		public int gridAfter(int noteWidth){
			int basic=eighths*noteWidth,shrink=0;
			switch (eighths) {
			case NOTE_DOUBLE:shrink=noteWidth*6/1;break;
			case NOTE_WHOLE:shrink=noteWidth*4/1;break;
			case NOTE_HALF:shrink=noteWidth*3/2;break;
			case NOTE_QUARTER:shrink=noteWidth*1/3;break;
			case NOTE_EIGHTH:shrink=noteWidth*-1/3;break;
			}
			return basic-(true?0:shrink)+getOffset();
		}
		public int hashCode(){
			return Arrays.hashCode(intValues);
		}
		public boolean equals(Object o){
			Tone that=(Tone)o;
			return this==that||(voice==that.voice
					&&Arrays.equals(intValues,that.intValues));
		}
	}
