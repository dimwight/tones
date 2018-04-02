package tones.view.paint;
import static tones.view.pane.PaneItem.*;
import facets.core.app.avatar.Painter;
import facets.core.app.avatar.PainterSource;
import facets.util.ItemList;
import facets.util.geom.Line;
import facets.util.shade.Shades;
import path.SvgPath;
import tones.Clef;
import tones.view.PageView;
import tones.view.pane.PaneBar;
public final class BarPainters extends PagePainters{
	private static final SvgPath 
		TieFromTo=new SvgPath("TieFromTo","M213.0 72.13c7.665,7.444 1.127,13.46 -6.062,6.475 -22.96,-18.09 -48.72,-30.72 -98.74,-31.47 -50.02,0.7481 -75.77,13.38 -98.74,31.47 -7.203,6.997 -13.67,0.9138 -6.062,-6.475 26.2,-29.19 66.25,-42.94 104.8,-42.56 38.75,0.0 78.4,13.15 104.8,42.56z",2),
		TieFromLong=new SvgPath("TieFromLong","M173.4 58.53c-16.6,-6.798 -37.06,-10.98 -65.2,-11.4 -50.02,0.7481 -75.77,13.38 -98.74,31.47 -7.203,6.997 -13.67,0.9138 -6.062,-6.475 26.2,-29.19 66.25,-42.94 104.8,-42.56 23.01,0.0 46.33,4.635 67.01,14.56l-1.816 14.41z",2),
		TieFromShort=new SvgPath("TieFromShort","M108.2 29.57c-38.55,-0.3749 -78.6,13.37 -104.8,42.56 -7.609,7.389 -1.141,13.47 6.062,6.475 22.96,-18.09 48.72,-30.72 98.74,-31.47l0.0 -17.57z",2),
		TieTo=new SvgPath("TieTo","M0.0 29.57c38.55,-0.3749 78.6,13.37 104.8,42.56 7.609,7.389 1.141,13.47 -6.062,6.475 -22.96,-18.09 -48.72,-30.72 -98.74,-31.47l0.0 -17.57z",2);
	private final int at;
	private final double rise,height,width,x,trebleY,bassY;
	public BarPainters(PageView page,PaneBar bar,PainterSource p){
		super(page,p);
		at=bar.content.at;
		x=bar.staveX*unitWidth;
		trebleY=bar.staveYs[Clef.TREBLE.staveAt]*pitchHeight;
		bassY=bar.staveYs[Clef.BASS.staveAt]*pitchHeight;
		width=bar.content.width*bar.staveXScale*unitWidth;
		rise=bar.content.rise;
		height=(rise+STAVE_GRID*2+bar.staveGap+bar.content.fall)*pitchHeight;
		NotePainters.firstInBar=true;
	}
	public Painter[]newViewPainters(boolean selected){
		ItemList<Painter>painters=new ItemList(Painter.class);
		if(false)painters.addItem(unscaledText(at+"",x,trebleY,.7));
		painters.addItems(staveLinePainters(x,trebleY,width,STAVE_GRID/2));
		painters.addItems(staveLinePainters(x,bassY,width,STAVE_GRID/2));
		painters.addItem(barLine(trebleY));
		if(false)painters.addItem(p.rectangle(x,trebleY-rise*pitchHeight,width,height,
				selected?Shades.blue:Shades.red));
		return painters.items();
}
	private Painter barLine(double y){
		return p.line(new Line(new double[]{x+width,y,x+width,bassY+8*pitchHeight}),Shades.lightGray,
				PainterSource.HAIRLINE,false);
	}
	public Painter[]newPickPainters(){
		return new Painter[]{p.rectangle(x,trebleY-rise*pitchHeight,width,height,Shades.green)};
	}
}