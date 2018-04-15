package tones.bar;
import static tones.Voice.*;
import static tones.bar.Bars.*;
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
import tones.app.TonesEdit;
public final class Bars extends Tracer implements Titled{
	public static final boolean eighthsCheck=false;
	private static int instances=1;
	private final List<Bar>bars=new ArrayList();
	private final Map<Voice,VoicePart>parts=new HashMap();
	private final String title;
	private VoicePart selectedPart;
	int barEighths;
	public Bars(String...codeLines){
		title="Tones"+instances++;
		for(String line:codeLines){
			VoicePart voice=new VoicePart(line);
			parts.put(voice.voice,voice);
		}
		selectPart(Tenor);
		int barAt=0;
		barEighths=0;
		while(true){
			Bar bar=newPartsBar(barAt++);
			if(bar!=null)bars.add(bar);
			else break;
		}
		if(true)trace(".readCodes~: bars="+bars.size()+" barAt="+barAt);
	}
	private Bar newPartsBar(int barAt){
		Map<Integer,Incipit>incipits=new HashMap();
		for(VoicePart part:parts.values()){
			List<Tone>tones=part.getBarTones(barAt);
			if(false&&part.voice==Bass)trace(".newVoiceLinesBar: tones="+tones.size()+" barAt="+barAt);
			int barEighthsNow=tones.isEmpty()?barEighths
					:(eighthsCheck?tones.remove(0):tones.get(0)).eighths;
			if(tones.isEmpty())continue;
			if(eighthsCheck&&barEighths!=0&&barEighthsNow!=barEighths)throw new IllegalStateException(
					"New barEighths="+barEighths+", barEighthsNow="+barEighthsNow+
					" in "+Debug.info(part));
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
		return incipits.isEmpty()?null:new Bar(barAt++,incipits.values(),barEighths);
	}
	public void updateSelectedPart(String codes){
		VoicePart nowPart=new VoicePart(codes),
			thenPart=parts.replace(nowPart.voice,nowPart);
		selectPart(nowPart.voice);
		int count=bars.size();
		trace(".updateSelectedPart: count=",count);
		for(int start=true?0:count-5,stop=false?2:count,
				barAt=start;barAt<stop;barAt++){
			List<Tone>thenTones=thenPart.getBarTones(barAt),
					nowTones=nowPart.getBarTones(barAt);
			barEighths=eighthsCheck&&!nowTones.isEmpty()?nowTones.remove(0).eighths
					:TonesEdit.BAR_EIGHTHS_DEFAULT;
			boolean equals=thenTones.equals(nowTones);
			if(!equals) {
				trace(".updateSelectedPart: barAt="+barAt
						+ " equals="+equals+" now=",nowTones);
				trace(" then=",thenTones);
				bars.remove(barAt);
				bars.add(barAt,newPartsBar(barAt));
			}
		}
	}
	public void selectPart(Voice voice){
		selectedPart=parts.get(voice);
	}
	public VoicePart selectedPart(){
		return selectedPart;
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
