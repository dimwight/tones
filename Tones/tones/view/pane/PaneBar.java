package tones.view.pane;
import facets.util.ItemList;
import facets.util.Objects;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import tones.Clef;
import tones.Mark;
import tones.Mark.Beam;
import tones.Mark.Tie;
import tones.Tone;
import tones.Voice;
import tones.bar.Bar;
import tones.bar.Incipit;
import tones.view.pane.PaneItem.PaneBeam;
public class PaneBar extends PaneItem{
	public final double staveX,staveYs[],staveGap,staveXScale,staveWidth;
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
		for(PaneNote check:notes){
			Collection<Mark>marks=check.tone.marks;
			if(marks.isEmpty())continue;
			for(Mark mark:marks){
				PaneNote[]barNotes=notes.items();
				if(mark instanceof Tie){
					PaneItem.PaneTie add=newPaneTie((Tie)mark,barNotes,check,beforeItems);
					items.add(add);
				}
				else if(mark instanceof Beam){
					PaneItem add=newPaneBeam((Beam)mark,barNotes);
					items.add(add);
				}
			}
		}
		items.addAll(notes);
		if(!marking)items.addItem(this);
		if(!voiceNotes.isEmpty())
			items.addItem(new VoiceNotes(voiceNotes.items()));
		return items.items();
	}
	private PaneItem newPaneBeam(Beam mark,PaneNote[]barNotes){
		ItemList<PaneNote>beamed=new ItemList(PaneNote.class);
		for(PaneNote note:barNotes)
			for(Tone tone:mark.tones)
				if(note.tone==tone)beamed.add(note);
		return new PaneBeam(beamed.items());
	}
	private PaneItem.PaneTie newPaneTie(Tie tie,PaneNote[]barNotes,PaneNote from,
			PaneItem[]beforeItems){
		PaneNote[]checkNotes=tie.to.barAt==content.at
				||beforeItems==null?barNotes
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
		return new PaneItem.PaneTie(from,to,this);
	}
	public String toString(){
		return super.toString()+", staveX="+staveX+", staveY="+staveYs;
	}
}