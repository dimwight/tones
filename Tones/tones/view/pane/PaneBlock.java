package tones.view.pane;
import static tones.view.PageView.*;
import facets.util.ItemList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import tones.bar.Annotation;
import tones.bar.Annotation.Group;
import tones.bar.Bar;
import tones.bar.Bars;
import tones.view.PageView;
public final class PaneBlock{
	private static final double STAVE_X_SCALE_DEFAULT=1.5;
	private final List<Bar>thisBars=new ArrayList();
	private final List<Group>groups=new ArrayList();
	private double rise=0,staveGap=0,fall=0,staveXUsed=0;
	private final Bar endBar;
	private PaneBlock(Iterator<Bar>bars,Bar bar,final double useWidth){
		while(bars.hasNext()||bar!=null){
			if(bar==null)bar=bars.next();
			double barWidth=bar.width;
			if(staveXUsed+barWidth>useWidth)break;
			staveXUsed+=barWidth;
			rise=Math.max(rise,bar.rise);
			staveGap=Math.max(staveGap,bar.staveGap);
			fall=Math.max(fall,bar.fall);
			thisBars.add(bar);
			for(Annotation a:bar.annotations)
				if(a instanceof Group)groups.add((Group)a);
			bar=null;
		}
		endBar=bar;
	}
	PaneItem[]newBarItems(double staveY,double staveXScale){
		double staveX=0;
		ItemList<PaneItem>items=new ItemList(PaneItem.class);
		ItemList<PaneBar>paneBars=new ItemList(PaneBar.class);
		for(Bar bar:thisBars){
			PaneBar paneBar=new PaneBar(bar,staveX,staveY,staveGap,staveXScale);
			paneBars.add(paneBar);
			items.addItems(paneBar.items());
			staveX+=staveBar.staveWidth;
		}
		for(Group group:groups)
			items.addItems(group.newBarItems(paneBars));
		return items.items();
	}
	public static PaneItem[]newPageItems(Bars content,PageView page){
		Iterator<Bar>bars=content.barsFrom(page.barAt()).iterator();
		final double staveWidth=page.showWidth()-2*INSET,
			useHeight=page.showHeight()-2*INSET,
			pitchHeight=page.pitchHeight(),
			unitWidth=pitchHeight*page.widthForPitch();
		double staveY=0,staveXScale=STAVE_X_SCALE_DEFAULT;
		ItemList<PaneItem>items=new ItemList(PaneItem.class);
		Bar bar=null;
		while(bars.hasNext()||bar!=null){
			PaneBlock block=new PaneBlock(bars,bar,staveWidth/unitWidth);
			bar=block.endBar;
			double blockStaveHeight=PaneItem.STAVE_GRID*2+block.staveGap+block.fall;
			if(((staveY+=block.rise)+blockStaveHeight)*pitchHeight>useHeight)break;
			double scaleUpdate=staveWidth/(block.staveXUsed*unitWidth);
			staveXScale=bars.hasNext()?scaleUpdate:Math.min(scaleUpdate,staveXScale);
			items.addItems(block.newBarItems(staveY,staveXScale));
			staveY+=blockStaveHeight;
		}
		return items.items();
	}
}
