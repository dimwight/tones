package tones.app;
import static facets.core.app.ActionViewerTarget.Action.*;
import static java.lang.Math.*;
import static tones.bar.VoicePart.*;
import facets.core.app.PathSelection;
import facets.core.app.SView;
import facets.core.app.SViewer;
import facets.core.app.TreeView;
import facets.core.app.ViewableAction;
import facets.core.app.avatar.AvatarView;
import facets.core.superficial.SFrameTarget;
import facets.core.superficial.STarget;
import facets.core.superficial.STextual;
import facets.core.superficial.STextual.Coupler;
import facets.core.superficial.app.SSelection;
import facets.facet.app.FacetAppSurface;
import facets.util.Debug;
import facets.util.Regex;
import facets.util.tree.DataNode;
import facets.util.tree.TypedNode;
import facets.util.tree.ValueNode;
import java.util.List;
import java.util.Objects;
import applicable.treetext.TreeTextViewable;
import tones.Voice;
import tones.bar.Bars;
import tones.bar.VoicePart;
import tones.page.PageNote;
import tones.view.PageView;
public final class TonesViewable extends TreeTextViewable{
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
	}){
	};
	TonesViewable(TypedNode tree,ClipperSource clipperSource,FacetAppSurface app){
		super(tree,clipperSource,app);
		bars=new Bars(this);
	}
	private int barStart,checkShowThen[];
	private PageView page;
	final private static String NO_CODES="[No codes]";
	private String before,after,show;
	private Voice voiceThen;
	private STextual textual;
	public SFrameTarget selectionFrame(){
		VoicePart selected=bars.selectedPart();
		Voice voice=selected.voice;
		if(voice!=voiceThen)show=null;
		List<String>codes=selected.barCodes;
		barStart=page.barStart();
		int codesCount=codes.size(),codeStop=min(page.barStop(),codesCount);
		if(false) trace(".selectionFrame: codesCount="+codesCount+" "
				+barStart+","+codeStop);
		int[]checkShow={codesCount,barStart,codeStop};
		if(!Objects.deepEquals(checkShow,checkShowThen)||show==null){
			checkShowThen=checkShow;
			before=mergeBarCodes(codes.subList(0,min(barStart,codesCount)));
			if(show==null)show=barStart>=codeStop?"":mergeBarCodes(codes.subList(barStart,codeStop));
			after=codeStop>=codesCount?"":mergeBarCodes(codes.subList(codeStop,codesCount));
			if(false)trace(".selectionFrame: show=",show);
			if(false)Debug.printStackTrace(50);
		}
		textual=new STextual("Codes",show.isEmpty()?NO_CODES:show,
				new STextual.Coupler(){
			@Override
			public boolean updateInterim(STextual t){
				return true;
			}
			@Override
			public void textSet(STextual t){
				String text=t.text(),edit=Regex.replaceAll(text,
						"([^,])$",",$1",
						",,",",",
						"","");
				if(false)trace(".textSet: text="+text+" edit="+edit);
				String src=(before+","+edit+","+after).trim().replaceAll("^,","");
				try{
					VoicePart.checkSource(src);
					show=edit;
					if(false)trace(".textSet: show=",show);
				}catch(Exception e){
					trace(".textSet: bad edit=",edit);
					show=text;
					return;
				}
				TonesViewable.this.doUndoableEdit((ValueNode)framed,src);
				t.notifyParent(Impact.CONTENT);
			}
		});
		textual.setLive(!textual.text().equals(NO_CODES));
		return new SFrameTarget(selection().single()){
			protected STarget[]lazyElements(){
				return new STarget[]{textual};
			}
		};
	}
	private void doUndoableEdit(ValueNode selected,String src){
		selected.setValues(new String[]{src});
		textViewerEdit=src;
		maybeModify();
		updateAfterEditAction();
		bars.updatePart(src);
	}
	@Override
	protected void editUndoneOrRedone(){
		show=null;
		bars.updatePart(selectedNode().getString(0));
	}
	@Override
	public ViewableAction[] viewerActions(SView view){
		return new ViewableAction[]{UNDO,REDO,
				//        DELETE,
				//        MODIFY
		};
	}
	private ValueNode selectedNode(){
		return (ValueNode)selection().single();
	}
	@Override
	protected SSelection newNonTreeViewerSelection(SViewer viewer){
		ValueNode node = selectedNode();//!
		ValueNode src = (ValueNode)(!node.type().equals("TextLine")? node.children()[0].children()[0]:node);
		bars.selectPart(new VoicePart(src.getString(0)).voice);
		SView view=viewer.view();
		if(true)System.out.println("view = " + view);
		SSelection selection=selection();
		if(view instanceof AvatarView){
			page=(PageView)view;
			return page.avatars().newAvatarSelection(viewer,
					new SSelection(){//?
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
		DataNode root = bars.newDebugRoot(barStart, page == null ? 0 : page.barStop());//?
		return ((TreeView)view).newViewerSelection(viewer,PathSelection.newMinimal(root));
	}
	@Override
	protected void nonTreeViewerSelectionChanged(SViewer viewer,
			SSelection selection){
		Object single=selection.single();
		if(false) traceDebug(".nonTreeViewerSelectionChanged: selection=",single);
		if(single instanceof PageView) bars.selectPart(Voice.Empty);
		else if(single instanceof PageNote)
			bars.selectPart(((PageNote)single).tone.voice);
	}
	public TypedNode contentTree(){
		return ((TypedNode)framed).children()[0];
	}
}
