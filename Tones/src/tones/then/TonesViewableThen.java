package tones.then;
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
import tones.view.PageView;
import tones.view.pane.PaneNote;
final class TonesViewableThen extends ViewableFrame{
	private int barAt;
	TonesViewableThen(BarsThen bars){
		super(bars.title(),bars);
		defineSelection(bars);
	}
	public SFrameTarget selectionFrame(){
		return new SFrameTarget(selection().single()){
			protected STarget[]lazyElements(){
				final BarsThen bars=framedBars();
				String src=bars.selectedPart().src;
				STextual textual=new STextual("Text",src,
						new STextual.Coupler(){
					@Override
					protected String getText(STextual t){
						return bars.selectedPart().src;
					}
					public void textSet(STextual t){
						bars.updatePart(t.text());
					}
					public boolean updateInterim(STextual t){
						return false;
					}
				});
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
		else return((TreeView)view).newViewerSelection(viewer,PathSelection.newMinimal(
				((BarsThen)framed).newDebugRoot(barAt)));
	}
	protected void viewerSelectionChanged(SViewer viewer,SSelection selection){
		Object thenSelection=selection().single();
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
				Bar bar=note.bar.content;
				if(bar==null)throw new RuntimeException("Not implemented in "+Debug.info(this));
				framedBars().selectPart(note.tone.voice);
				return bar;
			}
			public Object[]multiple(){
				throw new RuntimeException("Not implemented in "+Debug.info(this));
			}
		});
	}
	BarsThen framedBars(){
		return (BarsThen)TonesViewableThen.this.framed;
	}
}