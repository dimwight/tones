package tones.bar;
import facets.util.Debug;
import facets.util.Titled;
import facets.util.Tracer;
import facets.util.tree.DataNode;
import facets.util.tree.NodeList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import tones.Tone;
import tones.Voice;
import tones.VoiceLine;
public final class Bars extends Tracer implements Titled{
	private static int instances=1;
	private final String title;
	private final List<Bar>bars=new ArrayList();
	private final Map<Voice,VoiceLine>voiceLines=new HashMap();
	private VoiceLine selectedVoice;
	public Bars(){
		title="Tones"+instances++;
	}
	final public void readCodes(String...codeLines){
		for(String line:codeLines){
			VoiceLine voice=new VoiceLine(line);
			voiceLines.put(voice.voice,voice);
		}
		selectedVoice=voiceLines.get(Voice.Bass);
		int barAt=0,barEighths=0;
		while(true){
			Map<Integer,Incipit>incipits=new HashMap();
			for(VoiceLine line:voiceLines.values()){
				List<Tone>tones=line.nextBarTones(barAt);
				int barEighthsNow=tones.remove(0).eighths;
				if(tones.isEmpty())continue;
				if(barEighths!=0&&barEighthsNow!=barEighths)throw new IllegalStateException(
						"New barEighths="+barEighths+", barEighthsNow="+barEighthsNow+
						" in "+Debug.info(line));
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
			else{
				Bar bar=new Bar(barAt++,incipits.values(),barEighths);
				if(bars.size()<barAt)bars.add(bar);
				else {
					bars.remove(barAt);
					bars.add(barAt,bar);
				}
			}
		}
		trace(".readCodes: bars="+bars.size()+" barAt="+barAt);
	}
	public void updateSelectedVoiceLine(String codes){
		readCodes(codes);
	}
	public void selectVoice(Voice voice){
		selectedVoice=voiceLines.get(voice);
	}
	public VoiceLine selectedVoice(){
		return selectedVoice;
	}
	public int barCount(){
		return bars.size();
	}
	public List<Bar>barsFrom(int at){
		return bars.subList(at,bars.size());
	}
	public String title(){
		return title;
	}
	public DataNode newDebugRoot(int at){
		NodeList barsList=new NodeList(new DataNode(Bars.class.getSimpleName(),title()),true);
		for(Bar bar:barsFrom(at)){
			NodeList barList=new NodeList(new DataNode(Bar.class.getSimpleName(),""+bar.at),true);
			barsList.add(barList.parent);
			for(Incipit incipit:bar.incipits){
				NodeList incipitList=new NodeList(new DataNode(Incipit.class.getSimpleName(),
						""+incipit.eighthAt),true);
				barList.add(incipitList.parent);
				List<Tone>sortTones=new ArrayList(incipit.tones);
				Collections.sort(sortTones,new Comparator<Tone>(){
					@Override
					public int compare(Tone t1,Tone t2){
						return t1.voice.compareTo(t2.voice);
					}
				});
				for(Tone tone:sortTones){
					NodeList toneList=new NodeList(new DataNode(Tone.class.getSimpleName(),
							tone.toString()),true);
					incipitList.add(toneList.parent);
				}
			}
		}
		return barsList.parent;
	}
}
