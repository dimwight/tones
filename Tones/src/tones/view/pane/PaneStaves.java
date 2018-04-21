package tones.view.pane;
import static tones.view.PageView.*;
import facets.util.ItemList;
import facets.util.Objects;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import tones.Mark;
import tones.Mark.Beam;
import tones.Mark.Tie;
import tones.Tone;
import tones.Voice;
import tones.bar.Bar;
import tones.bar.Bars;
import tones.view.PageView;
import tones.view.pane.PaneItem.PaneBeam;
import tones.view.pane.PaneItem.PaneTie;
public final class PaneStaves{
	static final double STAVE_X_SCALE_DEFAULT=1.5;
	private final List<Bar>thisBars_=new ArrayList();
	private final Voice selectedVoice;
	double rise=0,staveGap=0,fall=0,staveXUsed=0;
	final Bar endBar;
	PaneStaves(Iterator<Bar>bars,Bar bar,final double useWidth,Voice selectedVoice){
		this.selectedVoice=selectedVoice;
		while(bars.hasNext()||bar!=null){
			if(bar==null)bar=bars.next();
			double barWidth=bar.width;
			if(staveXUsed+barWidth>useWidth)break;
			staveXUsed+=barWidth;
			rise=Math.max(rise,bar.rise);
			staveGap=Math.max(staveGap,bar.staveGap);
			fall=Math.max(fall,bar.fall);
			thisBars_.add(bar);
			bar=null;
		}
		endBar=bar;
	}
	PaneItem[]newItems(double paneY,double paneXScale){
		double paneX=0;
		ItemList<PaneItem>items=new ItemList(PaneItem.class);
		for(Bar bar:thisBars_){
			PaneBar paneBar=new PaneBar(bar,paneX,paneY,staveGap,paneXScale,selectedVoice);
			items.addItems(paneBar.newItems());
			paneX+=paneBar.staveWidth;
		}
		List<PaneItem>all=new ArrayList(items);
		all.removeIf(item->!(item instanceof PaneNote));
		PaneNote[]notes=Objects.newTyped(PaneNote.class,all.toArray());
		for(PaneNote note:notes){
			Collection<Mark>marks=note.tone.marks;
			if(marks.isEmpty())continue;
			if(false&&note.tone.barAt==6&&note.tone.voice==Voice.Alto)
				note.trace(".newItems: marks=",note);
			for(Mark mark:marks)
				if(mark instanceof Tie)items.add(newPaneTie((Tie)mark,note,notes));
				else if(mark instanceof Beam)items.add(newPaneBeam((Beam)mark,notes));
		}
		return items.items();
	}
	private PaneItem newPaneTie(Tie tie,PaneNote tied,PaneNote[]staveNotes){
		boolean isBefore=tie.before==tied.tone;
		PaneNote other=null;
		for(PaneNote check:staveNotes)
			if(check==tied)continue;
			else if(isBefore?check.tone==tie.after:check.tone==tie.before){
				other=check;
				break;
			}
		return new PaneTie(isBefore?tied:other,isBefore?other:tied,tied.bar,
				selectedVoice==tied.tone.voice);
	}
	private PaneItem newPaneBeam(Beam mark,PaneNote[]staveNotes){
		ItemList<PaneNote>beamed=new ItemList(PaneNote.class);
		boolean selected=false;
		for(Tone tone:mark.tones)
			for(PaneNote check:staveNotes)
				if(check.tone==tone){
					selected=selectedVoice==tone.voice;
					beamed.add(check);
				}
		return new PaneBeam(beamed.items(),selected);
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
			PaneStaves block=new PaneStaves(bars,bar,paneWidth/unitWidth,
					content.selectedPart().voice);
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
