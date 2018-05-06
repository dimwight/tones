package tones;
public enum ScaleNote{C(0),D(1),E(2),F(3),G(4),A(5),B(6),Rest(ScaleNote.PITCH_REST);
	final public static byte PITCH_REST=Byte.MIN_VALUE;
	final public byte pitch;
	private ScaleNote(int pitch){
		this.pitch=(byte)pitch;
	}
	@Override
	public String toString(){
		return this==Rest?"*":super.toString();
	}
	public static ScaleNote pitchNote(byte pitch){
		if(pitch==PITCH_REST)return Rest;
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
		return this==Rest?PITCH_REST:(byte)(pitch+octave.pitchAt);
	}
}
