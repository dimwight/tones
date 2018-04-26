package tones.app;
import static facets.core.app.ActionViewerTarget.Action.*;
import facets.core.app.PathSelection;
import facets.core.app.SView;
import facets.core.app.SViewer;
import facets.core.app.TreeView;
import facets.core.app.ViewableAction;
import facets.core.app.avatar.AvatarView;
import facets.core.superficial.SFrameTarget;
import facets.core.superficial.STarget;
import facets.core.superficial.STextual;
import facets.core.superficial.app.SSelection;
import facets.facet.app.FacetAppSurface;
import facets.util.tree.TypedNode;
import facets.util.tree.ValueNode;
import java.util.List;
import applicable.treetext.TreeTextViewable;
import tones.Voice;
import tones.bar.Bars;
import tones.bar.VoicePart;
import tones.page.PageNote;
import tones.view.PageView;
public final class TonesViewable extends TreeTextViewable{
	private int barStart;
	public final Bars bars;
	SFrameTarget barsView=new SFrameTarget(new TreeView("Bar Contents"){
		@Override
		public boolean hideRoot(){
			return true;
		}
		@Override
		public boolean canChangeSelection(){
			return false;
		}
		@Override
		public String nodeRenderText(TypedNode node){
			return node.title();
		}
	}){};
	private PageView page;
	TonesViewable(TypedNode tree,ClipperSource clipperSource,
			FacetAppSurface app){
		super(tree,clipperSource,app);
		bars=new Bars(this);
	}
	public SFrameTarget selectionFrame(){
		return new SFrameTarget(selection().single()){
			protected STarget[]lazyElements(){
				ValueNode selected=(ValueNode)framed;
				List<String>barCodes=bars.selectedPart().barCodes;
				barStart=page.barStart();
				int codeStop=Math.min(page.barStop(),barCodes.size());
				trace(".selectionFrame: "+barStart+"-"+codeStop);
				String NO_CODES="[No codes]";
				STextual textual=new STextual("Codes",
						barCodes.size()-1<barStart||codeStop<barStart?NO_CODES
								:VoicePart.mergeBarCodes(barCodes.subList(barStart,codeStop)),
						new STextual.Coupler(){
					@Override
					public void textSet(STextual t){
						String src=top+","+t.text()+","+tail;
						try {
							VoicePart.checkSource(src);
							TonesViewable.this.doUndoableEdit(selected,src);
							bars.updatePart(src);
						} catch (Exception e) {
							TonesViewable.this.trace(".textSet: "+e.getMessage());
						}
					}
					public boolean updateInterim(STextual t){
						return false;
					}
				});
				textual.setLive(!noSelection&&!textual.text().equals(NO_CODES));
				return new STextual[]{textual};
			}
		};
	}
	@Override
	public ViewableAction[]viewerActions(SView view){
		return new ViewableAction[]{
				UNDO,REDO,
				DELETE,
				MODIFY
			};
	}
	protected void doUndoableEdit(ValueNode selected,String src){
		selected.putAt(0,src);
		maybeModify();
		updateAfterEditAction();
	}
	@Override
	public boolean editSelection(){
		return true?true:super.editSelection();
	}
	@Override
	protected SSelection newNonTreeViewerSelection(SViewer viewer){
		SView view=viewer.view();
		SSelection selection=selection();
		if(view instanceof AvatarView){
			page=(PageView)view;
			return page.avatars().newAvatarSelection(viewer,new SSelection(){//?
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
		return((TreeView)view).newViewerSelection(viewer,PathSelection.newMinimal(//?
				bars.newDebugRoot(barStart,page==null?0:page.barStop())));
	
	}
	@Override
	protected void nonTreeViewerSelectionChanged(SViewer viewer,SSelection selection){
		Object single=selection.single();
		if(false)traceDebug(".nonTreeViewerSelectionChanged: selection=",single);
		if(single instanceof PageView)bars.selectPart(Voice.Empty);
		else if(single instanceof PageNote)bars.selectPart(((PageNote)single).tone.voice);
	}
	public TypedNode contentTree(){
		return ((TypedNode)framed).children()[0];
	}
}
