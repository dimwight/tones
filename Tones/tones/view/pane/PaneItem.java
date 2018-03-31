package tones.view.pane;
import facets.core.app.avatar.AvatarContent;
import facets.util.Debug;
import facets.util.Tracer;
public abstract class PaneItem extends Tracer implements AvatarContent{
	public static final int STAVE_GRID=11;
	protected boolean marking(){
		return false;
	}
	public String toString(){
		return Debug.info(this)+": ";
	}
}