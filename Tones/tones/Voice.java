package tones;
import static tones.Octave.*;
import static tones.ScaleNote.*;
public enum Voice{
	Treble(B,Above,"R"),
	Soprano(E,Above,"S"),
	Alto(C,Above,"A"),
	Tenor(A,Below,"T"),
	Bass(D,Below,"B");
	public final ScaleNote midNote;
	public final Octave octave;
	public final String code;
	private Voice(ScaleNote midPitch,Octave octave,String code){
		this.octave=octave;
		this.midNote=midPitch;
		this.code=code;
	}
}