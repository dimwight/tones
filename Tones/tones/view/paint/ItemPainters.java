package tones.view.paint;
import facets.core.app.avatar.Painter;
import facets.core.app.avatar.PainterSource;
import path.SvgPath;
import tones.view.PageView;
import tones.view.pane.PaneItem.PaneBeam;
public abstract class ItemPainters{
	public static final class BeamPainters extends PagePainters{
		public BeamPainters(PageView page,PaneBeam beam,PainterSource p){
			super(page,p);
			trace(": beam=",beam);
		}
		@Override
		public Painter[]newViewPainters(boolean selected){
			return new Painter[]{};
		}
		@Override
		public Painter[]newPickPainters(){
			return new Painter[]{};
		}
	}
}
