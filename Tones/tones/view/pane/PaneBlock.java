package tones.view.pane;
import static tones.view.PageView.*;
import facets.util.ItemList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import tones.bar.Bar;
import tones.bar.Bars;
import tones.view.PageView;
public final class PaneBlock{
	static final double STAVE_X_SCALE_DEFAULT=1.5;
	private final List<Bar>thisBars=new ArrayList();
	double rise=0,staveGap=0,fall=0,staveXUsed=0;
	final Bar endBar;
	PaneBlock(Iterator<Bar>bars,Bar bar,final double useWidth){
		while(bars.hasNext()||bar!=null){
			if(bar==null)bar=bars.next();
			double barWidth=bar.width;
			if(staveXUsed+barWidth>useWidth)break;
			staveXUsed+=barWidth;
			rise=Math.max(rise,bar.rise);
			staveGap=Math.max(staveGap,bar.staveGap);
			fall=Math.max(fall,bar.fall);
			thisBars.add(bar);
			bar=null;
		}
		endBar=bar;
	}
	PaneItem[]newItems(double paneY,double paneXScale){
		double paneX=0;
		ItemList<PaneItem>items=new ItemList(PaneItem.class);
		ItemList<PaneBar>paneBars=new ItemList(PaneBar.class);
		int barAt=-1;
		PaneItem[]beforeItems=null;
		for(Bar bar:thisBars){
			PaneBar paneBar=new PaneBar(bar,paneX,paneY,staveGap,paneXScale);
			paneBars.add(paneBar);
			items.addItems(beforeItems=paneBar.newItems(beforeItems));
			paneX+=paneBar.staveWidth;
		}
		return items.items();
	}
	public static PaneItem[]newPageItems(Bars content,PageView page){
		Iterator<Bar>bars=content.barsFrom(page.barAt()).iterator();
		final double paneWidth=page.showWidth()-2*INSET,
			useHeight=page.showHeight()-2*INSET,
			pitchHeight=page.pitchHeight(),
			unitWidth=pitchHeight*page.widthForPitch();
		double paneY=0,paneXScale=STAVE_X_SCALE_DEFAULT;
		ItemList<PaneItem>items=new ItemList(PaneItem.class);
		Bar bar=null;
		while(bars.hasNext()||bar!=null){
			PaneBlock block=new PaneBlock(bars,bar,paneWidth/unitWidth);
			bar=block.endBar;
			double blockStaveHeight=PaneItem.STAVE_GRID*2+block.staveGap+block.fall;
			if(((paneY+=block.rise)+blockStaveHeight)*pitchHeight>useHeight)break;
			double scaleUpdate=paneWidth/(block.staveXUsed*unitWidth);
			paneXScale=bars.hasNext()?scaleUpdate:Math.min(scaleUpdate,paneXScale);
			items.addItems(block.newItems(paneY,paneXScale));
			paneY+=blockStaveHeight;
		}
		return items.items();
	}
}
