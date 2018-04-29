package tones.page;

import facets.util.Debug;
import tones.Tone;
import tones.bar.Bar;
import tones.bar.Incipit;
final class PageIncipit extends PageItem{
	public final Incipit content;
	private final double barStaveX;
	private double scaledStaveX=-1;
	PageIncipit(Incipit content,double barStaveX){
		this.content=content;
		this.barStaveX=barStaveX;
	}
	void scaleStaveX(double by){?
		scaledStaveX=(int)(barStaveX+content.barAt*by);
	};
	double toneStaveX(Tone tone){
		if(scaledStaveX<0)throw new IllegalStateException(
				"Invalid scaledStaveX in "+Debug.info(this));
		double scaled=scaledStaveX;
		for(Tone t:content.tones)
			if(Math.abs(t.pitch-tone.pitch)==1
					&&tone.eighths>t.eighths)scaled+=Bar.WIDTH_NOTE;
		return scaled;
	}	
}
