package tones.bar;
import facets.util.Titled;
import facets.util.Tracer;
import facets.util.tree.DataNode;
import facets.util.tree.NodeList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import tones.Tone;
public final class Bars extends Tracer implements Titled{
	private static int instances=1;
	private final String title;
	private final List<Bar>bars;
	public Bars(List<Bar>bars){
		title="Tones"+instances++;
		this.bars=bars;
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
