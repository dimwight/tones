package tones.page;
import facets.util.ItemList;
import tones.Clef;
import tones.Tone;
import tones.Voice;
import tones.bar.Bar;
import tones.bar.Incipit;
public class PageBar extends PageItem{
	public final double staveX,staveYs[],staveGap,staveXScale,staveWidth;
	
	public final Bar content;
	private final Voice selectedVoice;
	public PageBar(Bar content,double staveX,double staveY,double staveGap,
			double staveXScale,Voice selectedVoice){
		this.content=content;
		this.staveX=staveX;
		this.staveGap=staveGap;
		this.staveXScale=staveXScale;
		this.selectedVoice=selectedVoice;
		staveWidth=content.width*staveXScale;
		staveYs=new double[]{staveY,staveY+STAVE_GRID+staveGap};
	}
	public PageItem[]newItems(){
		ItemList<PageIncipit>incipits=new ItemList(PageIncipit.class);
		for(Incipit bar:content.incipits)
			incipits.addItem(new PageIncipit(bar,staveX));
		for(PageIncipit incipit:incipits)incipit.scaleStaveX(staveXScale);
		ItemList<PageItem>items=new ItemList(PageItem.class);
		items.addItem(this);
		for(PageIncipit incipit:incipits)
			for(Tone tone:incipit.content.tones){
				Voice voice=tone.voice;
				Clef clef=Clef.forVoice(voice);
				PageNote note=new PageNote(this,tone,incipit,staveYs[clef.staveAt],clef,
						voice==selectedVoice);
				 items.add(note);
			}
		if(false)trace(".newItems: items=",items.size());
		return items.items();
	}
	public String toString(){
		return super.toString()+", staveX="+staveX+", staveY="+staveYs;
	}
}
