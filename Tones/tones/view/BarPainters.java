package tones.view;
import static tones.view.StaveItem.*;
import facets.core.app.avatar.Painter;
import facets.core.app.avatar.PainterSource;
import facets.util.ItemList;
import facets.util.geom.Line;
import facets.util.shade.Shades;
import tones.Clef;
import tones.view.StaveItem.StaveBar;
final class BarPainters extends PagePainters{
	private final int at;
	private final double rise,height,width,x,trebleY,bassY;
	BarPainters(StavePageView page,StaveBar bar,PainterSource p){
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
	Painter[]newViewPainters(boolean selected){
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
	Painter[]newPickPainters(){
		return new Painter[]{p.rectangle(x,trebleY-rise*pitchHeight,width,height,Shades.green)};
	}
}