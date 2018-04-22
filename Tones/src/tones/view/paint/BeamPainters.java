package tones.view.paint;
import facets.core.app.avatar.Painter;
import facets.core.app.avatar.PainterSource;
import facets.util.ItemList;
import facets.util.geom.Line;
import facets.util.geom.Point;
import facets.util.geom.Vector;
import tones.page.PageItem.PageBeam;
import tones.view.PageView;
public final class BeamPainters extends PagePainters{
	private final Line line;
	private final boolean tailsUp;
	private final boolean paintSelected;
	public BeamPainters(PageView page,PageBeam beam,PainterSource p){
		super(page,p);
		Line geom=beam.geom;
		paintSelected=beam.from.selected;
		tailsUp=beam.from.tone.voice.tailsUp;
		line=new Line(new Point(geom.from.at().plus(beam.from.at).scaled(scaleToPage)),
			new Point(geom.to.at().plus(beam.to.at).scaled(scaleToPage)));
	}
	@Override
	public Painter[]newViewPainters(boolean selected){
		Vector shift=new Vector(0,(tailsUp?-1:1)*.5);
		Point from=new Point(line.from.at().minus(shift)),
				to=new Point(line.to.at().minus(shift));
		ItemList<Painter>painters=new ItemList(Painter.class);
		for(int i=0;i<8;i++){
			from.shift(shift);to.shift(shift);
			painters.addItem(p.line(new Line(from,to),selectionShade(paintSelected),1,false));
		}
		return painters.items();
	}
}