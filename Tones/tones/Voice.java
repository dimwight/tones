package tones;
import static tones.Octave.*;
import static tones.ScaleNote.*;
public enum Voice{
	Soprano(E,Above,"S",1),
	Alto(C,Above,"A",-1),
	Tenor(A,Below,"T",1),
	Bass(D,Below,"B",-1);
	public final ScaleNote midNote;
	public final Octave octave;
	public final String code;
	public final boolean tailsUp;
	private Voice(ScaleNote midPitch,Octave octave,String code,int tailsUp){
		this.octave=octave;
		this.midNote=midPitch;
		this.code=code;
		this.tailsUp=tailsUp>0;
	}
}