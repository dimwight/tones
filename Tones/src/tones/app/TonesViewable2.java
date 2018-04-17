package tones.app;
import facets.core.app.PathSelection;
import facets.core.app.SView;
import facets.core.app.SViewer;
import facets.core.app.TreeView;
import facets.core.app.avatar.AvatarView;
import facets.core.superficial.SFrameTarget;
import facets.core.superficial.STarget;
import facets.core.superficial.STextual;
import facets.core.superficial.app.SSelection;
import facets.facet.AreaFacets.PaneDialogStyle;
import facets.facet.app.FacetAppSurface;
import facets.util.tree.TypedNode;
import facets.util.tree.ValueNode;
import applicable.treetext.TreeTextViewable;
import tones.Voice;
import tones.bar.Bars;
import tones.bar.Bars2;
import tones.bar.VoicePart;
import tones.view.PageView;
import tones.view.pane.PaneNote;
public final class TonesViewable2 extends TreeTextViewable{
	private int barAt;
	public final Bars2 bars;
	TonesViewable2(TypedNode tree,ClipperSource clipperSource,
			FacetAppSurface app){
		super(tree,clipperSource,app);
		bars=new Bars2(this);
	}
	@Override
	protected SSelection newNonTreeViewerSelection(SViewer viewer){
		SView view=viewer.view();
		SSelection selection=selection();
		if(view instanceof AvatarView){
			PageView page=(PageView)view;
			barAt=page.barAt();
			return page.avatars().newAvatarSelection(viewer,new SSelection(){
				@Override
				public Object content(){
					return bars;
				}
				@Override
				public Object single(){
					throw new RuntimeException("Not implemented in "+this);
				}
				@Override
				public Object[] multiple(){
					throw new RuntimeException("Not implemented in "+this);
				}
			});
		}
		return((TreeView)view).newViewerSelection(viewer,PathSelection.newMinimal(
				bars.newDebugRoot(barAt)));
	
	}
	@Override
	protected void nonTreeViewerSelectionChanged(SViewer viewer,SSelection selection){
		Object single=selection.single();
		if(false)traceDebug(".nonTreeViewerSelectionChanged: selection=",single);
		if(single instanceof PageView)bars.selectPart(Voice.Empty);
		else if(single instanceof PaneNote)bars.selectPart(((PaneNote)single).tone.voice);
	}
	public TypedNode framedTree(){
		return ((TypedNode)framed).children()[0];
	}
	public SFrameTarget selectionFrame(){
		return new SFrameTarget(selection().single()){
			protected STarget[]lazyElements(){
				ValueNode selected=(ValueNode)framed;
				STextual textual=new STextual("Part",selected.values()[0],
						new STextual.Coupler(){
					@Override
					protected String getText(STextual t){
						return selected.values()[0];
					}
					public void textSet(STextual t){
						String src=t.text();
						try {
							VoicePart.checkSource(src);
							selected.putAt(0,src);
							bars.updatePart(src);
						} catch (Exception e) {
							t.trace(".textSet: "+e.getMessage());
						}
					}
					public boolean updateInterim(STextual t){
						return false;
					}
				});
				return new STextual[]{textual};
			}
		};
	}
}