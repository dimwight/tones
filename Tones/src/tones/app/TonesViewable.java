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
  	List<String>codes=bars.selectedPart().barCodes;
  	barStart=page.barStart();
  	int codesCount=codes.size(),codeStop=min(page.barStop(),codesCount);
  	if(false)trace(".selectionFrame: codesCount="+codesCount+" "+
  			+barStart+","+codeStop);
  	String NO_CODES="[No codes]",
			before=mergeBarCodes(codes.subList(0,min(barStart,codesCount))),
			show=barStart>=codeStop?""
					:mergeBarCodes(codes.subList(barStart,codeStop)),
			after=codeStop>=codesCount?""
					:mergeBarCodes(codes.subList(codeStop,codesCount));
		STextual textual=new STextual("Codes",show.isEmpty()?NO_CODES:show,
				new STextual.Coupler(){
			@Override
			public void textSet(STextual t){
				String src=(before+","+t.text()+","+after).trim().replaceAll("^,","");
				try{
					TonesViewable.this.doUndoableEdit((ValueNode)framed,src);
				}catch(Exception e){
					if(false)throw e;
					else TonesViewable.this.trace(".textSet: "+e.getMessage());
				}
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
  public ViewableAction[]viewerActions(SView view){
    return new ViewableAction[]{
        UNDO,REDO,
//        DELETE,
//        MODIFY
      };
  }
  @Override
  protected void editUndoneOrRedone(){
    bars.updatePart(selectedNode().getString(0));
  }
	private ValueNode selectedNode(){
		return (ValueNode)selection().single();
	}
  @Override
  protected SSelection newNonTreeViewerSelection(SViewer viewer){
  	bars.selectPart(new VoicePart(selectedNode().getString(0)).voice);
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
