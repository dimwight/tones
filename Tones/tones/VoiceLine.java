package tones;
import static java.lang.Character.*;
import static tones.ScaleNote.*;
import static tones.Tone.*;
import static tones.Voice.*;
import facets.util.Debug;
import facets.util.Objects;
import facets.util.Tracer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import tones.bar.Bar;
import tones.bar.Bars;
import tones.bar.Incipit;
final public class VoiceLine extends Tracer{
	private static final char CODE_SCALE='s',CODE_OCTAVE_UP='+',CODE_OCTAVE_DOWN='-',
		CODE_TIE='T',CODE_BEAM='B',CODE_BAR_SIZE='Z';
	private static final String CODES_NOTE="abcdefgx";
	public static final String TEST_CODES[]={
			"s:" 
			+"16,x,x,x,x,"
			+"8,x,sg,b," 
			+"4,c,e,d,c,"
			+"8,b,2,b,1,a,g,4,a,"
			+"sc,a,g,f,e,d,"
			,
			"a:" 
			+"16,x,x,x,"
			+"8,sb,-,e,4,f,a,g,f,e,2,x,b,"
			+"se,+,e,f,g,a,1,b,f,4,b,2,a,"
			+"sc,a,1,g,f,4,g,6,c,1,d,e,"
			+"4,d,4,e,2,e,d,4,c,-,f"
			,
			"t:" 
			+"16,x,8,x,sf,8,b,"
			+"4,c,e,d,c,6,"
			+"b,2,b,a,b,c,d," 
			+"1,e,b,4,e,2,d,4,e,2,g,f,"
			+"sb,-,4,g,e,8,f,"
			+"2,f,b,4,e,2,e,c,4,f,"
			+"2,se,+,f,1,e,f,2,g,a,1,b,f,4,b,2,a,"
			+"4,b"
			,
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
	//		+""
		};
	private static final boolean padBar=false;
	final Voice voice;
	final List<Tone>tones=new ArrayList();
	private final List<String>codes=new ArrayList();
	private int codeAt,toneAt;
	private Tone.Context context;
	public VoiceLine(String src){
		String splitVoice[]=src.split(":",2),
			voiceCode=splitVoice[0].substring(0).toLowerCase();
		voice=voiceCode.equals("b")?Bass:voiceCode.equals("t")?Tenor
			:voiceCode.equals("a")?Alto:voiceCode.equals("s")?Soprano:null;
		if(voice==null)throw new IllegalArgumentException(
				"Voice not specified in src="+src);
		codes.addAll(Arrays.asList(splitVoice[1].split(",")));
	}
	final static public Bars newBars(String[]codeLines){
		Set<VoiceLine>voiceLines=new HashSet();
		for(String line:codeLines)voiceLines.add(new VoiceLine(line));
		int barAt=0,barEighths=0;
		List<Bar>bars=new ArrayList();
		while(true){
			Map<Integer,Incipit>incipits=new HashMap();
			for(VoiceLine line:voiceLines){
				List<Tone>tones=line.nextBarTones(barAt);
				int barEighthsNow=tones.remove(0).eighths;
				if(tones.isEmpty())continue;
				if(barEighths!=0&&barEighthsNow!=barEighths)throw new IllegalStateException(
						"New barEighths="+barEighths+", barEighthsNow="+barEighthsNow+" in "+Debug.info(line));
				else barEighths=barEighthsNow;
				int eighthAt=0;
				for(Tone tone:tones){
					Incipit i;
					if((i=incipits.get(eighthAt))==null)
						incipits.put(eighthAt,i=new Incipit(eighthAt));
					i.addTone(tone);
					eighthAt+=tone.eighths;
				}
			}
			if(incipits.isEmpty())break;
			else bars.add(new Bar(barAt++,incipits.values(),barEighths));
		}
		return new Bars(bars);
	}
	protected List<Tone>nextBarTones(int barAt){
		final List<Tone>tones=new ArrayList();
		Tone.Context context=this.context==null?newDefaultContexts().get(voice):this.context;
		ScaleNote scaleNote=context.scaleNote;
		Octave octave=context.octave;
		int eighths=context.eighths,eighthAt=0,barEighths=context.barEighths;
		while(eighthAt<barEighths){
			int[]toneValues=null;
			while(toneValues==null&&codeAt<codes.size()){
				String code=codes.get(codeAt++);
				int charCount=code.length();
				if(charCount==0)throw new IllegalStateException(
						"Empty code at="+codeAt+" in voice="+voice);
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
			else context=new Tone.Context(scaleNote,octave,eighths);
			this.context=context;
			Tone add=new Tone(this,toneAt++,barAt,eighthAt,
					(byte)toneValues[0],(short)toneValues[1], context);
			tones.add(add);
			this.tones.add(add);
			eighthAt+=toneValues[1];
		}		
		tones.add(0,new Tone(this,-1,barAt,-1,(byte)-1,(short)barEighths, context));
		return tones;
	}
	private static Map<Voice,Tone.Context>newDefaultContexts(){
		Map<Voice,Tone.Context>contexts=new HashMap();
		for(Voice voice:Voice.values())contexts.put(voice,
				new Tone.Context(voice.midNote,voice.octave,NOTE_NONE));
		return contexts;
	}
	private ScaleNote codeNote(char noteChar){
		return noteChar=='x'?ScaleNote.REST
				:ScaleNote.pitchNote((byte)((noteChar-0x61+5)%Octave.pitches));
	}
	public String toString(){
		return Debug.info(this)+": "+voice+"\n"+Objects.toString(codes.toArray(),",");
	}
	private VoiceLine(Tone first){
		voice=first.voice;
		context=first.context;
	}
	private void encode(Tone tone){
		int eighths=context.eighths;
		codes.add(tone.pitchNote().name());
	}
	final public static List<String>newCodeLines(Bars bars){
		Map<Voice,VoiceLine>voices=new HashMap();
		Iterator<Bar>all=bars.barsFrom(0).iterator();
		while(all.hasNext()){
			for(Incipit incipit:all.next().incipits){
				for(Tone tone:incipit.tones){
					VoiceLine coded;
					Voice voice=tone.voice;
					if(voice!=Voice.Bass)continue;
					if((coded=voices.get(voice))==null)
							voices.put(voice,coded=new VoiceLine(tone));
					coded.encode(tone);
				}
			}
		}
		List<String>lines=new ArrayList();
		for(VoiceLine voice:voices.values())lines.add(
				(voice.voice.toString().toLowerCase().charAt(0)+":"
						+Objects.toString(voice.codes.toArray())));
		return lines;
	}
}