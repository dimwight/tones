package tones.view.stave;
import facets.util.ItemList;
import facets.util.geom.Vector;
import tones.Clef;
import tones.Tone;
import tones.Voice;
import tones.bar.Annotation;
import tones.bar.Bar;
import tones.bar.Incipit;
public class StaveBar extends StaveItem{
	public final Bar content;
	public final double staveX,staveYs[],staveGap,staveXScale,staveWidth;
	public final class StaveVoiceNotes extends StaveItem{
		public final StaveItem[]items;
		public StaveVoiceNotes(StaveItem[]items){
			this.items=items;
		}
	}
	StaveBar(Bar content,double staveX,double staveY,double staveGap,double staveXScale){
		this.content=content;
		this.staveX=staveX;
		this.staveGap=staveGap;
		this.staveXScale=staveXScale;
		staveWidth=content.width*staveXScale;
		staveYs=new double[]{staveY,staveY+STAVE_GRID+staveGap};
	}
	Vector newAnnotationAt(Annotation a){
		StaveNote note=(StaveNote)new StaveBar(a.bar.newAnnotationCopy(a.incipit.newCopy(a.tone)),
				staveX,staveYs[0],staveGap,staveXScale){
			@Override
			protected boolean marking(){
				return true;
			}
		}.items()[0];
		return new Vector(note.staveX,note.staveY);
	}
	StaveItem[]items(){
		final boolean marking=marking();
		ItemList<StaveIncipit>incipits=new ItemList(StaveIncipit.class);
		for(Incipit bar:content.incipits)
			incipits.addItem(new StaveIncipit(bar,staveX));
		for(StaveIncipit incipit:incipits)incipit.scaleStaveX(staveXScale);
		ItemList<StaveItem>items=new ItemList(StaveItem.class),
			voiceNotes=new ItemList(StaveItem.class);
		if(!marking)items.addItem(this);
		final Voice selected=content.selectedVoice();
		for(StaveIncipit incipit:incipits)
			for(Tone tone:incipit.content.tones){
				final Voice voice=tone.voice;
				Clef clef=Clef.forVoice(voice);
				StaveNote note=new StaveNote(tone,incipit,staveYs[clef.staveAt],clef){
					@Override
					public boolean marking(){
						return marking;
					}
				};
				if(marking)return new StaveItem[]{note};
				else if(selected!=null&&voice==selected)voiceNotes.addItem(note);
				else items.addItem(note);
			}
		if(voiceNotes.size()>0)
			items.addItem(new StaveVoiceNotes(voiceNotes.items()));
		return items.items();
	}
	public String toString(){
		return super.toString()+", staveX="+staveX+", staveY="+staveYs;
	}
}