package tones.view;
import static tones.view.PageView.*;
import facets.core.app.PathSelection;
import facets.core.app.SView;
import facets.core.app.SViewer;
import facets.core.app.avatar.AvatarContent;
import facets.core.app.avatar.AvatarPolicies;
import facets.core.app.avatar.AvatarPolicy;
import facets.core.app.avatar.AvatarView;
import facets.core.app.avatar.DragPolicy;
import facets.core.app.avatar.Painter;
import facets.core.app.avatar.PainterSource;
import facets.core.app.avatar.PlaneView;
import facets.core.superficial.app.SSelection;
import facets.util.ArrayPath;
import facets.util.Debug;
import facets.util.shade.Shades;
import tones.bar.Bars;
import tones.view.paint.BarPainters;
import tones.view.paint.BeamPainters;
import tones.view.paint.NotePainters;
import tones.view.paint.PagePainters;
import tones.view.paint.TiePainters;
import tones.view.pane.PaneBar;
import tones.view.pane.PaneBar.VoiceNotes;
import tones.view.pane.PaneStaves;
import tones.view.pane.PaneItem;
import tones.view.pane.PaneItem.PaneBeam;
import tones.view.pane.PaneItem.PaneTie;
import tones.view.pane.PaneNote;
final class PagePolicies extends AvatarPolicies{
	@Override
	public SSelection newAvatarSelection(SViewer viewer,SSelection viewable){
		Object content=viewable.content();
		PaneItem[]items=PaneStaves.newPageItems((Bars)content,(PageView)viewer.view());
		Object selected=viewable.single();
		if(selected==content||selected instanceof SView)
			return PathSelection.newMinimal(items);
		PaneBar matchBar=null;
		for(PaneItem item:items)
			if(matchBar!=null)break;
			else if(item instanceof PaneBar&&((PaneBar)item).content==selected)
				matchBar=(PaneBar)item;
		if(matchBar==null)throw new IllegalStateException(
				"Null matchBar in "+Debug.info(this));
		return new PathSelection(items,new ArrayPath(items,matchBar));
	}
	@Override
	public AvatarPolicy avatarPolicy(SViewer viewer,final AvatarContent content,
			final PainterSource p){
		PageView view=(PageView)viewer.view();
		PaneItem item=(PaneItem)content;
		final PagePainters painters=
			item instanceof PaneNote?
				new NotePainters(view,(PaneNote)item,p)
			:false&&item instanceof VoiceNotes?
					NotePainters.newVoiceNotePainters(view,(VoiceNotes)item,p)
			:item instanceof PaneBar?new BarPainters(view,(PaneBar)item,p)
			:item instanceof PaneTie?new TiePainters(view,(PaneTie)item,p)
			:item instanceof PaneBeam?new BeamPainters(view,(PaneBeam)item,p)
					:null;
		if(painters==null)
			throw new IllegalStateException("Null painters for "+Debug.info(item));
		else return new AvatarPolicy(){
			public Painter[]newViewPainters(boolean selected,boolean active){
				return painters.newViewPainters(selected);
			}
			public Painter[]newPickPainters(Object hit,boolean selected){
				return painters.newPickPainters();
			}
		};
	}
	@Override
	public DragPolicy dragPolicy(AvatarView view,AvatarContent[]content,
			Object hit,PainterSource p){
		return null;
	}
	@Override
	public Painter getBackgroundPainter(SViewer viewer, 
			PainterSource p){
		PlaneView plane=(PlaneView)viewer.view();
		double margin=INSET*0.75;
		return false?Painter.EMPTY:p.bar(
				-margin,-margin,plane.showWidth()-2*(INSET-margin),
				plane.showHeight()-2*(INSET-margin),Shades.white,false
			);
	}
}