package tones.app;
import static java.lang.Character.*;
import static tones.ScaleNote.*;
import static tones.Tone.*;
import static tones.Voice.*;
import facets.util.Debug;
import facets.util.Objects;
import facets.util.Tracer;
import facets.util.Util;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import tones.Octave;
import tones.ScaleNote;
import tones.Tone;
import tones.Tone.Tag;
import tones.Voice;
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
			+"8,bT,2,b,1,a,g,4,aT,"
			+"sc,a,g,f,e,d,"
			,
			"a:" 
			+"16,x,x,x,"
			+"8,sb,-,e,4,f,a,g,f,e,2,x,b,"
			+"se,+,e,f,g,a,1,b,f,4,b,2,aT,"
			+"sc,a,1,g,f,4,g,6,c,1,d,e,"
			+"4,d,4,eT,2,e,d,4,c,-,f"
			,
			"t:" 
			+"16,x,8,x,sf,8,b,"
			+"4,c,e,d,c,6,"
			+"b,2,b,a,b,c,d," 
			+"1,e,b,4,e,2,d,4,e,2,g,f,"
			+"sb,-,4,g,e,8,f,"
			+"2,f,b,4,eT,2,e,c,4,fT,"
			+"2,se,+,f,1,e,f,2,g,a,1,b,f,4,b,2,a,"
			+"4,b"
			,
			"b:"
			+"sb,-,8,e,4,f,a," 
			+"g,f,2,e,1,d,c,2,d,b," 
			+"2,+,sc,e,f,g,a,1,b,f,4,b,2,aT," 
			+"a,1,g,f,4,gT,g,4,f," 
			+"g,2,a,1,b,a,2,g,f,e,d," 
			+"8,c,-,4,b,f,"
			+"6,g,2,e,6,a,2,f,"
			+"16,bT,"
			+"4,b"
	//		+""
		};
	private static final boolean padBar=false;
	private final static class Context implements Tone.Tag{
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
	private final Voice voice;
	private final List<String>codes=new ArrayList();
	private int codeAt;
	private Context context;
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
		List<Tone>tones=new ArrayList();
		Context context=this.context==null?newDefaultContexts().get(voice):this.context;
		ScaleNote scaleNote=context.scaleNote;
		Octave octave=context.octave;
		int eighths=context.eighths,eighthAt=0,barEighths=context.barEighths;
		while(eighthAt<barEighths){
			int[]toneValues=null;
			Set<Tag>tags=new HashSet();
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
				if(isUpperCase(secondChar))for(char c:code.substring(1).toCharArray()){
					Tag tag=c==CODE_TIE?Tag.Tie:c=='B'?Tag.Beam:null;
					if(tag!=null)tags.add(tag);
				}
			}
			if(toneValues==null){
				if(padBar&&eighthAt>0)
					toneValues=new int[]{PITCH_REST,barEighths-eighthAt+1};
				else break;
			}
			if(eighths<=NOTE_NONE)throw new IllegalStateException(
					"Invalid eighths in context="+context);
			else context=new Context(scaleNote,octave,eighths);
			if(this.context==null||!context.resembles(this.context))tags.add(context);
			this.context=context;
			tones.add(new Tone(voice,barAt,eighthAt,(byte)toneValues[0],(short)toneValues[1],
					tags));
			eighthAt+=toneValues[1];
		}		
		tones.add(0,new Tone(null,barAt,-1,(byte)-1,(short)barEighths,null));
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
	public String toString(){
		return Debug.info(this)+": "+voice+"\n"+Objects.toString(codes.toArray(),",");
	}
	private VoiceLine(Tone first){
		voice=first.voice;
		for(Tag tag:first.tags)if(tag instanceof Context)context=(Context)tag;
		if(context==null)throw new IllegalArgumentException("No context in first="+first);
	}
	private void encode(Tone tone){
		int eighths=context.eighths;
		for(Tag tag:tone.tags)if(tag instanceof Context){
				context=(Context)tag;
				if(context.eighths==NOTE_NONE)throw new IllegalStateException(
						"Invalid eighths in context="+context);
				else if(eighths!=context.eighths)codes.add(""+(eighths=context.eighths));
			}
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