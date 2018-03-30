package tones.view;
import static tones.view.StavePageView.*;
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
import tones.view.paint.GroupPainters;
import tones.view.paint.NotePainters;
import tones.view.paint.PagePainters;
import tones.view.stave.StaveBar;
import tones.view.stave.StaveBar.StaveVoiceNotes;
import tones.view.stave.StaveBlock;
import tones.view.stave.StaveGroup;
import tones.view.stave.StaveItem;
import tones.view.stave.StaveNote;
final class StavePagePolicies extends AvatarPolicies{
	@Override
	public SSelection newAvatarSelection(SViewer viewer,SSelection viewable){
		Object content=viewable.content();
		StaveItem[]items=StaveBlock.newPageItems((Bars)content,(StavePageView)viewer.view());
		Object selected=viewable.single();
		if(selected==content||selected instanceof SView)
			return PathSelection.newMinimal(items);
		StaveBar matchBar=null;
		for(StaveItem item:items)
			if(matchBar!=null)break;
			else if(item instanceof StaveBar&&((StaveBar)item).content==selected)
				matchBar=(StaveBar)item;
		if(true||matchBar==null)throw new IllegalStateException(
				"Null matchBar in "+Debug.info(this));
		return new PathSelection(items,new ArrayPath(items,matchBar));
	}
	@Override
	public AvatarPolicy avatarPolicy(SViewer viewer,final AvatarContent content,
			final PainterSource p){
		StavePageView view=(StavePageView)viewer.view();
		final PagePainters painters=content instanceof StaveGroup?
				new GroupPainters(view,(StaveGroup)content,p)
			:content instanceof StaveNote?
				new NotePainters(view,(StaveNote)content,p)
			:content instanceof StaveVoiceNotes?NotePainters.newVoiceNotePainters(view,
						(StaveVoiceNotes)content,p)
			:new BarPainters(view,(StaveBar)content,p);
		return new AvatarPolicy(){
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