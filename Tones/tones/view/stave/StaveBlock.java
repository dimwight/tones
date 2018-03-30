package tones.view.stave;
import static tones.view.StavePageView.*;
import facets.util.ItemList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import tones.bar.Annotation;
import tones.bar.Annotation.Group;
import tones.bar.Bar;
import tones.bar.Bars;
import tones.view.StavePageView;
public final class StaveBlock{
	private static final double STAVE_X_SCALE_DEFAULT=1.5;
	private final List<Bar>bars=new ArrayList();
	private final List<Group>groups=new ArrayList();
	private double rise=0,staveGap=0,fall=0,staveXUsed=0;
	Bar readBars(Iterator<Bar>bars,Bar bar,double useWidth){
		while(bars.hasNext()||bar!=null){
			if(bar==null)bar=bars.next();
			double barWidth=bar.width;
			if(staveXUsed+barWidth>useWidth)break;
			staveXUsed+=barWidth;
			this.bars.add(bar);
			for(Annotation a:bar.annotations)if(a instanceof Group)groups.add((Group)a);
			rise=Math.max(rise,bar.rise);
			staveGap=Math.max(staveGap,bar.staveGap);
			fall=Math.max(fall,bar.fall);
			bar=null;
		}
		return bar;
	}
	StaveItem[]newBarItems(double staveY,double staveXScale){
		double staveX=0;
		ItemList<StaveItem>items=new ItemList(StaveItem.class);
		ItemList<StaveBar>staveBars=new ItemList(StaveBar.class);
		for(Bar bar:bars){
			StaveBar staveBar=new StaveBar(bar,staveX,staveY,staveGap,staveXScale);
			staveBars.add(staveBar);
			items.addItems(staveBar.items());
			staveX+=staveBar.staveWidth;
		}
		for(Group group:groups)items.addItems(StaveGroup.newBarItems(group,staveBars));
		return items.items();
	}
	public static StaveItem[]newPageItems(Bars content,StavePageView page){
		Iterator<Bar>bars=content.barsFrom(page.barAt()).iterator();
		final double staveWidth=page.showWidth()-2*INSET,
			useHeight=page.showHeight()-2*INSET,
			pitchHeight=page.pitchHeight(),
			unitWidth=pitchHeight*page.widthForPitch();
		double staveY=0,staveXScale=STAVE_X_SCALE_DEFAULT;
		ItemList<StaveItem>items=new ItemList(StaveItem.class);
		Bar bar=null;
		while(bars.hasNext()||bar!=null){
			StaveBlock block=new StaveBlock();
			bar=block.readBars(bars,bar,staveWidth/unitWidth);
			double blockStaveHeight=StaveItem.STAVE_GRID*2+block.staveGap+block.fall;
			if(((staveY+=block.rise)+blockStaveHeight)*pitchHeight>useHeight)break;
			double scaleUpdate=staveWidth/(block.staveXUsed*unitWidth);
			staveXScale=bars.hasNext()?scaleUpdate:Math.min(scaleUpdate,staveXScale);
			items.addItems(block.newBarItems(staveY,staveXScale));
			staveY+=blockStaveHeight;
		}
		return items.items();
	}
}