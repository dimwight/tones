package tones.view.pane;
import facets.util.ItemList;
import facets.util.Objects;
import facets.util.geom.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import tones.Clef;
import tones.Mark;
import tones.Mark.Tie;
import tones.Tone;
import tones.Voice;
import tones.bar.Bar;
import tones.bar.Incipit;
public class PaneBar extends PaneItem{
	public final double staveX,staveYs[],staveGap,staveXScale,staveWidth;
	public static final class PaneTie extends PaneItem{
		public final PaneNote from,to;
		public PaneTie(PaneNote from,PaneNote to){
			this.from=from;
			this.to=to;
			trace(":",this);
		}
		@Override
		public String toString(){
			return " from="+from.staveAt()+" to="+(to==null?"null":to.staveAt());
		}
	}
	public final class VoiceNotes extends PaneItem{
		public final PaneItem[]items;
		public VoiceNotes(PaneItem[]items){
			this.items=items;
		}
	}
	public final Bar content;
	PaneBar(Bar content,double staveX,double staveY,double staveGap,
			double staveXScale){
		this.content=content;
		this.staveX=staveX;
		this.staveGap=staveGap;
		this.staveXScale=staveXScale;
		staveWidth=content.width*staveXScale;
		staveYs=new double[]{staveY,staveY+STAVE_GRID+staveGap};
	}
	PaneItem[]newItems(PaneItem[]beforeItems){
		final boolean marking=marking();
		ItemList<PaneIncipit>incipits=new ItemList(PaneIncipit.class);
		for(Incipit bar:content.incipits)
			incipits.addItem(new PaneIncipit(bar,staveX));
		for(PaneIncipit incipit:incipits)incipit.scaleStaveX(staveXScale);
		ItemList<PaneNote>notes=new ItemList(PaneNote.class),
			voiceNotes=new ItemList(PaneItem.class);
		ItemList<PaneItem>items=new ItemList(PaneItem.class);
		final Voice selected=content.selectedVoice();
		for(PaneIncipit incipit:incipits)
			for(Tone tone:incipit.content.tones){
				final Voice voice=tone.voice;
				Clef clef=Clef.forVoice(voice);
				PaneNote note=new PaneNote(tone,incipit,staveYs[clef.staveAt],clef){
					@Override
					public boolean marking(){
						return marking;
					}
				};
				if(marking)return new PaneItem[]{note};
				else if(selected!=null&&voice==selected)voiceNotes.add(note);
				else notes.add(note);
			}
		for(PaneNote from:notes){
			Collection<Mark>marks=from.tone.marks;
			if(marks.isEmpty())continue;
			for(Mark mark:marks){
				if(!(mark instanceof Tie))continue;
				Tie tie=(Tie)mark;
				PaneNote[]checkNotes=tie.to.barAt==content.at
						||beforeItems==null?notes.items()
						:new Function<PaneItem[],PaneNote[]>(){
							@Override
							public PaneNote[]apply(PaneItem[] t){
								List<PaneItem>list=new ArrayList<PaneItem>(Arrays.asList(t));
								list.removeIf(item->!(item instanceof PaneNote));
								return Objects.newTyped(PaneNote.class,list.toArray());
							}}.apply(beforeItems);
				PaneNote to=null;
				for(PaneNote check:checkNotes)
					if(check.tone==tie.to){
						to=check;
						break;
					}
				PaneTie add=new PaneTie(from,to);
				if(false)items.add(add);
			}
		}
		items.addAll(notes);
		if(!marking)items.addItem(this);
		if(!voiceNotes.isEmpty())
			items.addItem(new VoiceNotes(voiceNotes.items()));
		return items.items();
	}
	public String toString(){
		return super.toString()+", staveX="+staveX+", staveY="+staveYs;
	}
}