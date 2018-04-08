package tones.view.paint;
import facets.core.app.avatar.Painter;
import facets.core.app.avatar.PainterSource;
import facets.core.app.avatar.PainterSource.Transform;
import facets.facet.kit.avatar.SwingPainterSource;
import facets.util.geom.Line;
import facets.util.geom.Point;
import facets.util.shade.Shades;
import tones.view.PageView;
import tones.view.pane.PaneItem;
import tones.view.pane.PaneItem.PaneBeam;
public final class BeamPainters extends PagePainters{
	private final Line line;
	private final boolean tailsUp;
	public BeamPainters(PageView page,PaneBeam beam,PainterSource p){
		super(page,p);
		Line geom=beam.geom;
		tailsUp=beam.from.tone.voice.tailsUp;
		line=new Line(new Point(geom.from.at().plus(beam.from.at).scaled(scaleToPage)),
			new Point(geom.to.at().plus(beam.to.at).scaled(scaleToPage)));
	}
	@Override
	public Painter[]newViewPainters(boolean selected){
		Point from=line.from,to=line.to;
		double fromX=from.x(),fromY=from.y();
		double width=to.x()-fromX,height=4;
		Painter bar=p.bar(fromX-(tailsUp?0:0.5),fromY-(tailsUp?0:0.5),
				width+(tailsUp?0:0.5),height,"shadeFill="+Shades.blue.title());
		if(false)return new Painter[]{
				p.line(line,Shades.blue,1,false)
		};
		Transform transform=false?p.transformTurn(0.5,fromX+height,fromY):
			((SwingPainterSource)p).transformShear(-0.002,0.1);
		if(true)p.applyTransforms(new PainterSource.Transform[]{transform},true,bar);
		return new Painter[]{bar};
	}
}