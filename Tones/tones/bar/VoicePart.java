package tones.bar;
import static java.lang.Character.*;
import static tones.ScaleNote.*;
import static tones.Tone.*;
import static tones.Voice.*;
import static tones.app.TonesEdit.*;
import static tones.bar.Bars.*;
import facets.util.Debug;
import facets.util.Objects;
import facets.util.Tracer;
import facets.util.Util;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import tones.Mark;
import tones.Octave;
import tones.ScaleNote;
import tones.Tone;
import tones.Voice;
import tones.Mark.Beam;
import tones.app.TonesEdit;
public final class VoicePart extends Tracer{
	private static final boolean padBar=false;
	final static class Context{
		final ScaleNote scaleNote;
		final Octave octave;
		final int barEighths=TonesEdit.BAR_EIGHTHS_DEFAULT,eighths;
		Context(ScaleNote scaleNote,Octave octave,int eighths){
			if(scaleNote==null)throw new IllegalArgumentException(
					"Null keyPitch in "+Debug.info(this));
			else if(octave==null)throw new IllegalArgumentException(
					"Null octave in "+Debug.info(this));
			else if(eighths<Tone.NOTE_NONE)throw new IllegalArgumentException(
					"Invalid eighths in "+Debug.info(this));
			this.scaleNote=scaleNote;
			this.octave=octave;
			this.eighths=eighths;
		}
		@Override
		public boolean equals(Object o){
			if(true)throw new RuntimeException("Not implemented in "+this);
			Context that=(Context)o;
			return resembles(that)&&that.eighths==eighths;
		}
		public boolean resembles(Context that){
			if(true)throw new RuntimeException("Not implemented in "+this);
			return that.scaleNote==scaleNote&&that.octave==octave
				&&that.barEighths==barEighths;
		}
		@Override
		public String toString(){
			return "<"+octave+","+scaleNote+//","+eighths+
			">";
		}
	}
	public final String src;
	public final Voice voice;
	private final List<List<Tone>>barTones=new ArrayList();
	VoicePart(String src){
		this.src=src;
		String splitVoice[]=src.split(":",2),
			voiceCode=splitVoice[0].substring(0).toLowerCase();
		voice=voiceCode.equals("e")?Empty:
			voiceCode.equals("b")?Bass:voiceCode.equals("t")?Tenor
			:voiceCode.equals("a")?Alto:voiceCode.equals("s")?Soprano:null;
		if(voice==null)throw new IllegalArgumentException(
				"Voice not specified in src="+src);
		final Iterator<String>nextCodes=Arrays.asList(splitVoice[1].split(",")).iterator();
		int codeAt=0,toneAt;
		Context context=null;
		Tone before=null;
		while(nextCodes.hasNext()){
			final List<Tone>tones=new ArrayList();
			Beam beam=new Beam(voice);
			if(context==null)context=newDefaultContexts().get(voice);
			ScaleNote scaleNote=context.scaleNote;
			Octave octave=context.octave;
			int eighths=context.eighths,eighthAt=0,barEighths=context.barEighths;
			int barAt=barTones.size();
			while(eighthAt<barEighths){
				int[]toneValues=null;
				while(toneValues==null&nextCodes.hasNext()){
					String code=nextCodes.next();
					int charCount=code.length();
					if(charCount==0)throw new IllegalStateException(
							"Empty code at="+codeAt+" in voice="+voice);
					else codeAt++;
					char firstChar=code.toLowerCase().charAt(0),secondChar=charCount==1?'z'
							:code.charAt(1);
					if(firstChar==CODE_SCALE)scaleNote=codeNote(toLowerCase(secondChar));
					else if(firstChar==CODE_OCTAVE_UP)octave=octave.above();
					else if(firstChar==CODE_OCTAVE_DOWN)octave=octave.below();
					else if(firstChar==CODE_BAR_SIZE){
						if(true)throw new RuntimeException("Not tested for code="+code);
						else barEighths=Integer.valueOf(code.substring(1));
					}
					else if(isDigit(firstChar))
						eighths=Integer.valueOf(code)*Tone.NOTE_EIGHTH;
					else if(CODES_NOTE.contains(""+firstChar)){
						ScaleNote toneNote=codeNote(firstChar);
						int octaved=toneNote.octaved(octave),
							tonePitch=toneNote==REST?PITCH_REST
								:octaved+(toneNote.pitch<scaleNote.pitch?Octave.pitches:0);
						toneValues=new int[]{tonePitch,eighths};
					}
					if(isUpperCase(secondChar))
						throw new RuntimeException("Not implemented in "+this);
				}
				if(toneValues==null){
					if(padBar&&eighthAt>0)
						toneValues=new int[]{PITCH_REST,barEighths-eighthAt+1};
					else break;
				}
				if(eighths<=NOTE_NONE)throw new IllegalStateException(
						"Invalid eighths in context="+context);
				context=new Context(scaleNote,octave,eighths);
				Tone add=new Tone(voice,barAt,eighthAt,(byte)toneValues[0],
						(short)toneValues[1]);
				add.checkTied(before);
				if(add.eighths==NOTE_EIGHTH)beam.addTone(add);
				else{
					if(beam.tones.size()>1)add.marks.add(beam);
					beam=new Beam(voice);
				}
				tones.add(add);
				eighthAt+=toneValues[1];
				before=add;
			}		
			if(beam.tones.size()>1)tones.get(tones.size()-1).marks.add(beam);
			if(eighthsCheck)tones.add(0,new Tone(voice,barAt,-1,(byte)-1,(short)barEighths));
			barTones.add(tones);
			if(false)trace(".nextBarTones: barAt="+barAt+" barTones="+barTones.size()+
					" nextCodes="+nextCodes.hasNext());
		}
		barTones.add(Collections.EMPTY_LIST);
	}
	public List<Tone>getBarTones(int barAt){
		List<Tone>tones=barAt<barTones.size()?
				barTones.get(barAt):Collections.EMPTY_LIST;
		return tones;
	}
	private static Map<Voice,Context>newDefaultContexts(){
		Map<Voice,Context>contexts=new HashMap();
		for(Voice voice:Voice.values())contexts.put(voice,
				new Context(voice.midNote,voice.octave,NOTE_NONE));
		return contexts;
	}
	private ScaleNote codeNote(char noteChar){
		return noteChar=='x'?ScaleNote.REST
				:ScaleNote.pitchNote((byte)((noteChar-0x61+5)%Octave.pitches));
	}
	@Override
	protected void traceOutput(String msg){
		if(voice==Bass)Util.printOut(voice+msg);
	}
	public String toString(){
		return Debug.info(this)+": "+voice+" barTones="+barTones.size();
	}
}