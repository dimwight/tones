package tones.view.paint;
import facets.core.app.avatar.Painter;
import facets.core.app.avatar.PainterSource;
import facets.core.app.avatar.PainterSource.Transform;
import facets.facet.kit.avatar.SwingPainterSource;
import facets.util.ItemList;
import facets.util.geom.Line;
import facets.util.geom.Point;
import facets.util.geom.Vector;
import facets.util.shade.Shades;
import tones.view.PageView;
import tones.view.pane.PaneItem;
import tones.view.pane.PaneItem.PaneBeam;
public final class BeamPainters extends PagePainters{
	private final Line line;
	private final boolean tailsUp;
	private final boolean paintSelected;
	public BeamPainters(PageView page,PaneBeam beam,PainterSource p){
		super(page,p);
		Line geom=beam.geom;
		paintSelected=beam.from.selected;
		tailsUp=beam.from.tone.voice.tailsUp;
		line=new Line(new Point(geom.from.at().plus(beam.from.at).scaled(scaleToPage)),
			new Point(geom.to.at().plus(beam.to.at).scaled(scaleToPage)));
	}
	@Override
	public Painter[]newViewPainters(boolean selected){
		Vector shift=new Vector(0,tailsUp?-1:1);
		Point from=new Point(line.from.at().minus(shift)),
				to=new Point(line.to.at().minus(shift));
		ItemList<Painter>painters=new ItemList(Painter.class);
		for(int i=0;i<4;i++){
			from.shift(shift);to.shift(shift);
			painters.addItem(p.line(new Line(from,to),selectionShade(paintSelected),1,false));
		}
		return painters.items();
	}
}