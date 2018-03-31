package tones.view.pane;
import static tones.view.pane.PaneGroup.TieType.*;
import facets.util.ItemList;
import facets.util.Util;
import facets.util.geom.Vector;
import java.util.List;
import tones.Tone;
import tones.bar.Annotation;
import tones.bar.Annotation.BeamGroup;
import tones.bar.Annotation.Group;
import tones.bar.Annotation.TieGroup;
import tones.bar.Incipit;
public abstract class PaneGroup extends PaneItem{
	public enum TieType{FromTo,From,To,ToFrom}
	public static final class PaneTie extends PaneGroup{//?
		public final TieGroup content;
		public final PaneBar bar;
		public final Vector fromAt,toAt;
		public final TieType type;
		PaneTie(TieGroup tie,PaneBar bar,List<PaneBar>bars){
			this.content=tie;
			this.bar=bar;
			Vector fromAt=null,toAt=null;
			for(Incipit i:bar.content.incipits)
				for(Tone t:i.tones)
					if(t==tie.tone)fromAt=bar.newAnnotationAt(tie);
					else if(t==tie.end.tone)toAt=bar.newAnnotationAt(tie.end);
			if(fromAt!=null&&toAt!=null)
				type=FromTo;
			else{
				boolean isFrom=bar.content==tie.bar;
				Vector thisAt=bar.newAnnotationAt(isFrom?tie:tie.end),otherAt=null;
				int barCount=bars.size(),staveThisAt=0;
				for(PaneBar b:bars)if(b==bar)break;else staveThisAt++;
				int staveOtherAt=isFrom&&staveThisAt<barCount-1?staveThisAt+1
						:!isFrom&&staveThisAt>0?staveThisAt-1:-1;
				PaneBar otherBar=staveOtherAt<0?null:bars.get(staveOtherAt);
				if(otherBar!=null)for(Annotation a:otherBar.content.annotations)
					if(a==tie)otherAt=otherBar.newAnnotationAt(isFrom?tie.end:tie);
				fromAt=isFrom?thisAt:otherAt;
				toAt=isFrom?otherAt:thisAt;
				type=isFrom?otherAt!=null?FromTo:From:otherAt==null?To:ToFrom;
			}
			this.fromAt=fromAt;this.toAt=toAt;
		}
	}
	static PaneItem[]newBarItems(Group group,List<PaneBar>bars){//?
		PaneBar copy=null;
		ItemList<PaneItem>items=new ItemList(PaneItem.class);
		for(PaneBar bar:bars){
			if(group instanceof TieGroup){
				TieGroup tie=(TieGroup)group;
				if(bar.content==group.bar||bar.content==tie.end.bar)
					items.addItem(new PaneTie(tie,bar,bars));
			}
			else if(group instanceof BeamGroup){
				BeamGroup beam=(BeamGroup)group;
				if(false&&bar.content==group.bar)
					Util.printOut("StaveGroup.newBarItems: beam="+beam);
			}
		}
		return items.items();
	}}
