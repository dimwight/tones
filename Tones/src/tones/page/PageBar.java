package tones.page;
import facets.util.ItemList;
import tones.Clef;
import tones.Tone;
import tones.Voice;
import tones.bar.Bar;
import tones.bar.Incipit;
public class PageBar extends PageItem{
	public final double pageX,pageYs[],staveGap,pageXScale,staveWidth;
	public final Bar content;
	private final Voice selectedVoice;
	public PageBar(Bar content,double pageX,double pageY,double staveGap,
			double pageXScale,Voice selectedVoice){
		this.content=content;
		this.pageX=pageX;
		this.staveGap=staveGap;
		this.pageXScale=pageXScale;
		this.selectedVoice=selectedVoice;
		staveWidth=content.width*pageXScale;
		pageYs=new double[]{pageY,pageY+STAVE_GRID+staveGap};
	}
	public PageItem[]newItems(){
		ItemList<PageIncipit>incipits=new ItemList(PageIncipit.class);
		for(Incipit bar:content.incipits)
			incipits.addItem(new PageIncipit(bar,pageX,pageXScale));
		ItemList<PageItem>items=new ItemList(PageItem.class);
		items.addItem(this);
		for(PageIncipit incipit:incipits)
			for(Tone tone:incipit.content.tones){
				Voice voice=tone.voice;
				Clef clef=Clef.forVoice(voice);
				PageNote note=new PageNote(this,tone,incipit,pageYs[clef.staveAt],clef,
						voice==selectedVoice);
				 items.add(note);
			}
		if(false)trace(".newItems: items=",items.size());
		return items.items();
	}
	public String toString(){
		return super.toString()+", pageX="+pageX+", pageY="+pageYs;
	}
}
