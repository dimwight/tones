package tones.bar;
import static facets.util.Util.*;
import static java.lang.Character.*;
import static tones.ScaleNote.*;
import static tones.Tone.*;
import static tones.Voice.*;
import static tones.bar.Bars.*;
import facets.util.Debug;
import facets.util.Objects;
import facets.util.Tracer;
import facets.util.Util;
import facets.util.tree.ValueNode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import tones.Mark.Tails;
import tones.Octave;
import tones.ScaleNote;
import tones.Tone;
import tones.Voice;
public final class VoicePart extends Tracer{
	private static final char CODE_SCALE='s',CODE_OCTAVE_UP='+',CODE_OCTAVE_DOWN='-',
			CODE_TIE='T',CODE_BEAM='B',CODE_BAR_SIZE='Z';
	public static final int BAR_EIGHTHS_DEFAULT=16;
	public static final String CODES_NOTE="abcdefgx";
	public static final Object[]TEST_CODES={
			newPartNode(
					"e:16," 
					+"x,x,x,x,"
					+"x,x,x,x,x"
				),
			newPartNode(
					"b:"
					+"sb,-,8,e,4,f,a," 
					+"g,f,2,e,1,d,c,2,d,b," 
					+"2,+,sc,e,f,g,a,1,b,f,4,b,2,a," 
					+"a,1,g,f,4,g,g,4,f," 
					+"g,2,a,1,b,a,2,g,f,e,d," 
					+"8,c,-,4,b,f,"
					+"6,g,2,e,6,a,2,f,"
					+"16,b,"
					+"4,b"
				),
			newPartNode(
					"t:" 
					+"16,x,8,x,sf,8,b,"
					+"4,c,e,d,c,6,"
					+"b,2,b,a,b,c,d," 
					+"1,e,b,4,e,2,d,4,e,2,g,f,"
					+"sb,-,4,g,e,8,f,"
					+"2,f,b,4,e,2,e,c,4,f,"
					+"2,se,+,f,1,e,f,2,g,a,1,b,f,4,b,2,a,"
					+"4,b"
				),
			newPartNode(
					"a:" 
					+"16,x,x,x,"
					+"8,sb,-,e,4,f,a,g,f,e,2,x,b,"
					+"se,+,e,f,g,a,1,b,f,4,b,2,a,"
					+"sc,a,1,g,f,4,g,6,c,1,d,e,"
					+"4,d,4,e,2,e,d,4,c,-,f"
				),
			newPartNode(
					"s:" 
					+"16,x,x,x,x,"
					+"8,x,sg,b," 
					+"4,c,e,d,c,"
					+"8,b,2,b,1,a,g,4,a,"
					+"sc,a,g,f,e,d"
				)
		};
	private static final boolean padBar=false;
	final static class Context{
		final ScaleNote scaleNote;
		final Octave octave;
		final int barEighths=BAR_EIGHTHS_DEFAULT,eighths;
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
	public final List<String>barCodes=new ArrayList();
	public VoicePart(String src){
		this.src=src;
		voice=parseSource(src,barTones,barCodes);
		String checkCodes=Objects.toString(barCodes.toArray()
				).replaceAll(",,",",").replaceAll(",$","");
		if(!checkCodes.equals(src))
			throw new IllegalStateException("Bad checkCodes="+checkCodes);
		else if(false)trace(": check="+checkCodes.length()+" src="+src.length());
		else if(false)trace(".VoicePart: barCodes=",barCodes.size());
	}
	private static Voice parseSource(String src,List<List<Tone>>barTones,
			List<String> barCodes){
		String splitVoice[]=src.split(":",2),
			voiceCode=splitVoice[0].substring(0).toLowerCase();
		Voice voice=voiceCode.equals("e")?Empty:
			voiceCode.equals("b")?Bass:voiceCode.equals("t")?Tenor
			:voiceCode.equals("a")?Alto:voiceCode.equals("s")?Soprano:null;
		if(voice==null)throw new IllegalArgumentException(
				"Voice not specified in src="+src);
		final Iterator<String>nextCodes=Arrays.asList(
				splitVoice[1].split(",",-1)).iterator();
		int codeAt=0,toneAt;
		Context context=null;
		Tone before=null;
		while(nextCodes.hasNext()){
			final List<Tone>tones=new ArrayList();
			String codes=barCodes.isEmpty()?voiceCode+":":"";
			Tails tails=new Tails(voice);
			if(context==null)context=newDefaultContexts().get(voice);
			ScaleNote scaleNote=context.scaleNote;
			Octave octave=context.octave;
			int eighths=context.eighths,eighthAt=0,barEighths=context.barEighths;
			int barAt=barTones.size();
			while(eighthAt<barEighths){
				int[]toneValues=null;
				String code="No code";
				while(toneValues==null&nextCodes.hasNext()){
					code=nextCodes.next();
					codes+=code+",";
					int charCount=code.length();
					if(!code.matches("([abcdefgxs123468-]|\\+)|([s1].)")) 
						throw new IllegalStateException(
							"Bad code '"+code+ "' at="+codeAt+" in voice="+voice);
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
					else if(isUpperCase(secondChar))
						throw new RuntimeException("Not implemented for secondChar="+secondChar);
					else throw new RuntimeException("Unparsed code="+code);
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
				List<Tone>tailTones=tails.tones;
				if(add.eighths==NOTE_EIGHTH){
					boolean onBeat=add.isOnBeat(NOTE_QUARTER);
					if(onBeat||!tailTones.isEmpty()){//Start or add multiple
						tails.addTone(add);
					}
					else if(!onBeat&&tailTones.isEmpty()){//Add single and close
						tails.addTone(add);
						add.marks.add(tails);
						tails=new Tails(voice);
					}
				}
				else{//Close any multiple
					if(tailTones.size()>1){
						add.marks.add(tails);
						tails=new Tails(voice);
					}
				}
				tones.add(add);
				eighthAt+=toneValues[1];
				before=add;
			}		
			if(tails.tones.size()>1)//Close any multiple
				tones.get(tones.size()-1).marks.add(tails);
			if(eighthsCheck)tones.add(0,new Tone(voice,barAt,-1,(byte)-1,(short)barEighths));
			barTones.add(tones);
			barCodes.add(codes);
			if(false)printOut("VoicePart.parseSource: barAt="+barAt+
					" barTones="+barTones.size()+" nextCodes="+nextCodes.hasNext());
		}
		barTones.add(Collections.EMPTY_LIST);
		return voice;
	}
	private static Map<Voice,Context>newDefaultContexts(){
		Map<Voice,Context>contexts=new HashMap();
		for(Voice voice:Voice.values())contexts.put(voice,
				new Context(voice.midNote,voice.octave,NOTE_NONE));
		return contexts;
	}
	private static ScaleNote codeNote(char noteChar){
		return noteChar=='x'?ScaleNote.REST
				:ScaleNote.pitchNote((byte)((noteChar-0x61+5)%Octave.pitches));
	}
	@Override
	protected void traceOutput(String msg){
		if(true||voice==Empty)Util.printOut(voice+msg);
	}
	public List<Tone>getBarTones(int barAt){
		List<Tone>tones=barAt<barTones.size()?
				barTones.get(barAt):Collections.EMPTY_LIST;
		return tones;
	}
	public static void checkSource(String src){
		parseSource(src,new ArrayList(),new ArrayList());
	}
	private static ValueNode newPartNode(String src){
		return new ValueNode("VoicePart",new Object[]{src});
	}
	public String toString(){
		return Debug.info(this)+": "+voice+" barTones="+barTones.size();
	}
}
