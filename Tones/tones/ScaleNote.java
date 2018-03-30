package tones;

public enum ScaleNote{C(0),D(1),E(2),F(3),G(4),A(5),B(6),REST(ScaleNote.PITCH_REST);
	final public static byte PITCH_REST=Byte.MIN_VALUE;
	final public byte pitch;
	private ScaleNote(int pitch){
		this.pitch=(byte)pitch;
	}
	public static ScaleNote pitchNote(byte pitch){
		if(pitch==PITCH_REST)return REST;
		while(pitch<0)pitch+=Octave.pitches;
		switch(pitch%Octave.pitches){
		case 0:return C;
		case 1:return D;
		case 2:return E;
		case 3:return F;
		case 4:return G;
		case 5:return A;
		case 6:return B;
		default:throw new IllegalArgumentException(
				"Invalid (impossible) pitch="+pitch);
		}
	}
	public byte octaved(Octave octave){
		return this==REST?PITCH_REST:(byte)(pitch+octave.pitchAt);
	}
}
