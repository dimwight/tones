package tones.view.pane;
import facets.util.ItemList;
import tones.Clef;
import tones.Tone;
import tones.Voice;
import tones.bar.Bar;
import tones.bar.Incipit;
public class PaneBar extends PaneItem{
	public final double staveX,staveYs[],staveGap,staveXScale,staveWidth;
	
	public final Bar content;
	private final Voice selectedVoice;
	PaneBar(Bar content,double staveX,double staveY,double staveGap,
			double staveXScale,Voice selectedVoice){
		this.content=content;
		this.staveX=staveX;
		this.staveGap=staveGap;
		this.staveXScale=staveXScale;
		this.selectedVoice=selectedVoice;
		staveWidth=content.width*staveXScale;
		staveYs=new double[]{staveY,staveY+STAVE_GRID+staveGap};
	}
	PaneItem[]newItems(){
		ItemList<PaneIncipit>incipits=new ItemList(PaneIncipit.class);
		for(Incipit bar:content.incipits)
			incipits.addItem(new PaneIncipit(bar,staveX));
		for(PaneIncipit incipit:incipits)incipit.scaleStaveX(staveXScale);
		ItemList<PaneItem>items=new ItemList(PaneItem.class);
		items.addItem(this);
		for(PaneIncipit incipit:incipits)
			for(Tone tone:incipit.content.tones){
				Voice voice=tone.voice;
				Clef clef=Clef.forVoice(voice);
				PaneNote note=new PaneNote(this,tone,incipit,staveYs[clef.staveAt],clef,
						voice==selectedVoice);
				 items.add(note);
			}
		return items.items();
	}
	public String toString(){
		return super.toString()+", staveX="+staveX+", staveY="+staveYs;
	}
}
