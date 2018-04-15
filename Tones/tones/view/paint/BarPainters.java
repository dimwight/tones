package tones.view.paint;
import static tones.view.pane.PaneItem.*;
import facets.core.app.avatar.Painter;
import facets.core.app.avatar.PainterSource;
import facets.util.ItemList;
import facets.util.geom.Line;
import facets.util.shade.Shades;
import applicable.path.SvgPath;
import tones.Clef;
import tones.view.PageView;
import tones.view.pane.PaneBar;
public final class BarPainters extends PagePainters{
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
		painters.addItem(barLine());
		if(false)painters.addItem(p.rectangle(x,trebleY-rise*pitchHeight,width,height,
				selected?Shades.blue:Shades.red));
		return painters.items();
}
	private Painter barLine(){
		Line line=new Line(new double[]{x+width,trebleY,x+width,bassY+8*pitchHeight});
		return false?p.backgroundLines(new Line[]{line},Shades.gray)
				:p.line(line,Shades.lightGray,
					false?PainterSource.HAIRLINE:(int)unitWidth,false);
	}
	public Painter[]newPickPainters(){
		return new Painter[]{p.rectangle(x,trebleY-rise*pitchHeight,width,height,Shades.green)};
	}
}
