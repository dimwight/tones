package tones.app;
import facets.core.app.PathSelection;
import facets.core.app.SView;
import facets.core.app.SViewer;
import facets.core.app.TreeView;
import facets.core.app.ViewableFrame;
import facets.core.app.avatar.AvatarView;
import facets.core.superficial.SFrameTarget;
import facets.core.superficial.STarget;
import facets.core.superficial.STextual;
import facets.core.superficial.app.SSelection;
import facets.util.Debug;
import tones.bar.Bar;
import tones.bar.Bars;
import tones.view.PageView;
import tones.view.pane.PaneNote;
final class TonesViewable extends ViewableFrame{
	private int barAt;
	TonesViewable(Bars tones){
		super(tones.title(),tones);
		defineSelection(tones);
	}
	public SFrameTarget selectionFrame(){
		return new SFrameTarget(selection().single()){
			protected STarget[]lazyElements(){
				final Bar bar=framed instanceof Bar?(Bar)framed:null;
				STextual textual=new STextual("Text",
						bar==null?"[No selection]":bar.selectedVoiceLine(),
						new STextual.Coupler(){
					public void textSet(STextual t){
						bar.updateSelectedVoiceLine(t.text());
					}
					public boolean updateInterim(STextual t){
						return true;
					}
				});
				textual.setLive(bar!=null);
				return new STextual[]{textual};
			}
		};
	}
	protected SSelection newViewerSelection(SViewer viewer){
		SView view=viewer.view();
		SSelection selection=selection();
		if(view instanceof AvatarView){
			PageView page=(PageView)view;
			barAt=page.barAt();
			return page.avatars().newAvatarSelection(viewer,selection);
		}
		return((TreeView)view).newViewerSelection(viewer,PathSelection.newMinimal(
				((Bars)framed).newDebugRoot(barAt)));
	}
	protected void viewerSelectionChanged(SViewer viewer,SSelection selection){
		Object thenSelection=selection().single();
		if(thenSelection instanceof Bar)
			((Bar)thenSelection).selectVoice(null);
		defineSelection(selection.single());
	}
	public SSelection defineSelection(final Object definition){
		return setSelection(new SSelection(){
			public Object content(){
				return framed;
			}
			public Object single(){
				if(!(definition instanceof PaneNote))return framed;
				PaneNote note=(PaneNote)definition;
				Bar bar=null;
				if(bar==null)throw new RuntimeException("Not implemented in "+Debug.info(this));
				bar.selectVoice(note.content.voice);
				return bar;
			}
			public Object[]multiple(){
				throw new RuntimeException("Not implemented in "+Debug.info(this));
			}
		});
	}
}