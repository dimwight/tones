package tones.view.paint;
import facets.core.app.avatar.Painter;
import facets.core.app.avatar.PainterSource;
import facets.util.ItemList;
import facets.util.Tracer;
import facets.util.geom.Line;
import facets.util.shade.Shades;
import tones.view.StavePageView;
public abstract class PagePainters extends Tracer{
	static final int TEXT_POINTS=13;
	final StavePageView page;
	final PainterSource p;
	final double pitchHeight,unitWidth;
	PagePainters(StavePageView page,PainterSource p){
		this.page=page;
		this.p=p;
		pitchHeight=page.pitchHeight();
		unitWidth=pitchHeight*page.widthForPitch();
	}
	public abstract Painter[]newViewPainters(boolean selected);
	public abstract Painter[]newPickPainters();
	final protected Painter unscaledText(String text,double x,double y,double dropFactor){
		double scaledPoints=TEXT_POINTS/page.scale(),
			textDrop=scaledPoints*dropFactor;
		return p.textCaption(text,x,y+textDrop,
				"face=Tahoma","shade=red","points="+(int)scaledPoints);
	}
	final protected Painter tooltipText(String text,double x,double y,double dropFactor){
		double scaledPoints=TEXT_POINTS/page.scale(),
			textDrop=scaledPoints*dropFactor;
		return p.textTooltip(text,x,y+textDrop);
	}
	final protected Painter[]staveLinePainters(double fromX,double fromY,double width,
			int count){
		Line[]lines=new Line[Math.abs(count)];
		for(int i=0;i<lines.length;i++){
			double lineY=fromY+pitchHeight*2*i*(count>0?1:-1);
			lines[i]=new Line(new double[]{fromX,lineY,fromX+width,lineY});
		}
		ItemList<Painter>painters=new ItemList(Painter.class);
		painters.add(p.backgroundLines(lines,Shades.gray));
		return painters.items();
	}
}