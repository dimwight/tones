package tones.view.pane;
import facets.util.ItemList;
import facets.util.geom.Vector;
import tones.Clef;
import tones.Tone;
import tones.Voice;
import tones.bar.Bar;
import tones.bar.Incipit;
public class PaneBar extends PaneItem{
	public final Bar content;
	public final double staveX,staveYs[],staveGap,staveXScale,staveWidth;
	public final class VoiceNotes extends PaneItem{
		public final PaneItem[]items;
		public VoiceNotes(PaneItem[]items){
			this.items=items;
		}
	}
	PaneBar(Bar content,double staveX,double staveY,double staveGap,
			double staveXScale){
		this.content=content;
		this.staveX=staveX;
		this.staveGap=staveGap;
		this.staveXScale=staveXScale;
		staveWidth=content.width*staveXScale;
		staveYs=new double[]{staveY,staveY+STAVE_GRID+staveGap};
	}
	PaneItem[]newItems(){
		final boolean marking=marking();
		ItemList<PaneIncipit>incipits=new ItemList(PaneIncipit.class);
		for(Incipit bar:content.incipits)
			incipits.addItem(new PaneIncipit(bar,staveX));
		for(PaneIncipit incipit:incipits)incipit.scaleStaveX(staveXScale);
		ItemList<PaneItem>items=new ItemList(PaneItem.class),
			voiceNotes=new ItemList(PaneItem.class);
		if(!marking)items.addItem(this);
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
				else if(selected!=null&&voice==selected)voiceNotes.addItem(note);
				else items.addItem(note);
			}
		if(!voiceNotes.isEmpty())
			items.addItem(new VoiceNotes(voiceNotes.items()));
		return items.items();
	}
	public String toString(){
		return super.toString()+", staveX="+staveX+", staveY="+staveYs;
	}
}