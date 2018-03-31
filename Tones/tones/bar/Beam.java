package tones.bar;
import facets.util.Tracer;
import java.util.ArrayList;
import java.util.List;
import tones.Tone;
import tones.Voice;
import tones.bar.Annotation.BeamGroup;
final class Beam extends Tracer{//?
	private final Voice voice;
	private final List<Tone>tones=new ArrayList();
	private Incipit incipit;
	Beam(Voice voice){
		this.voice=voice;
	}
	Beam readTone(Tone tone,Incipit incipit){
		if(tone.voice!=voice)return this;
		boolean adding=tone.eighths<Tone.NOTE_QUARTER;
		if(adding){
			if(tones.isEmpty())this.incipit=incipit;
			tones.add(tone);
		}
		else if(tones.size()==1)tones.clear();
		return adding?this:new Beam(tone.voice);
	}
	boolean hasTones(){
		return tones.isEmpty();
	}
	BeamGroup newAnnotation(Bar bar){
		return new BeamGroup(bar,incipit,tones);
	}
}
