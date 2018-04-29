package tones.page;

import facets.util.Debug;
import tones.Tone;
import tones.bar.Bar;
import tones.bar.Incipit;
final class PageIncipit extends PageItem{
	public final Incipit content;
	private final double scaledPageX;
	PageIncipit(Incipit content,double barPageX,double pageXScale){
		this.content=content;
		scaledPageX=(int)(barPageX+content.barAt*pageXScale);
	}
	double tonePageX(Tone tone){
		if(scaledPageX<0)throw new IllegalStateException(
				"Invalid scaledPageX in "+Debug.info(this));
		double scaled=scaledPageX;
		for(Tone that:content.tones)
			if(Math.abs(that.pitch-tone.pitch)<2
					&&(tone.eighths>=that.eighths))scaled+=Bar.WIDTH_NOTE;
		return scaled;
	}	
}
