package tones.app.tree;
import static facets.core.app.ActionViewerTarget.Action.*;
import facets.core.app.NodeViewable;
import facets.core.app.PathSelection;
import facets.core.app.SView;
import facets.core.app.SViewer;
import facets.core.app.TextView;
import facets.core.app.TreeView;
import facets.core.app.ValueEdit;
import facets.core.app.ViewableAction;
import facets.core.superficial.app.SSelection;
import facets.facet.app.FacetAppSurface;
import facets.util.OffsetPath;
import facets.util.tree.NodePath;
import facets.util.tree.TypedNode;
public class TreeTextViewable extends NodeViewable{
	private final FacetAppSurface app;
	public TreeTextViewable(TypedNode tree,ClipperSource clipperSource,
			FacetAppSurface app){
		super(tree,clipperSource);
		this.app=app;
	}
	@Override
	public boolean actionIsLive(SViewer viewer,ViewableAction action){
		TypedNode tree=tree();
		SSelection selection=selection();
		Object[]selected=selection.multiple();
		int arrayCount=tree.children().length,selectionCount=selected.length;
		boolean empty=arrayCount==0;
		TypedNode selectedNode=(TypedNode)selected[0];
		OffsetPath firstPath=((PathSelection)selection).paths[0];
		boolean valueSelected=((NodePath)firstPath).valueAt()>=0,
				belowRoot=selectedNode.parent()!=tree,nodeSelected=!valueSelected;
		return action==COPY?nodeSelected||valueSelected
				:action==MODIFY?valueSelected&&selectionCount==1
				:action==CUT||action==DELETE?belowRoot&&nodeSelected
	 			:action==PASTE?canPaste()&&!valueSelected
	 			:action==SELECT_ALL?false
				:super.actionIsLive(viewer,action);
	}
	@Override
	protected SSelection newViewerSelection(SViewer viewer){
		SView view=viewer.view();
		if(view instanceof TreeView)return selection();
		else if(view instanceof TextView)return selection();?
		else throw new RuntimeException("Not implemented in "+this);
	}
	@Override
	protected void viewerSelectionChanged(SViewer viewer,SSelection selection){
		SView view=viewer.view();
		if(view instanceof TreeView)super.viewerSelectionChanged(viewer,selection);
		else if(view instanceof TextView)super.viewerSelectionChanged(viewer,selection);?
		else throw new RuntimeException("Not implemented in "+this);
		putSelectionState(app.spec.state(),TreeTextContenter.STATE_OFFSETS);?
	}
	@Override
	protected void viewerSelectionEdited(SViewer viewer,Object edit,
			boolean interim){
		SView view=viewer.view();
		if(view instanceof TreeView)
			super.viewerSelectionEdited(viewer,edit,interim);
		else if(view instanceof TextView)
			super.viewerSelectionEdited(viewer,edit,interim);?
		else throw new RuntimeException("Not implemented in "+this);
	}
	@Override
	public ViewableAction[]viewerActions(SView view){
		ViewableAction[]all={
				COPY,
		     CUT,
		     PASTE,
		     DELETE,
		     MODIFY,
		     UNDO,
		     REDO};
		return view.isLive()?all:new ViewableAction[]{COPY};
	}
	private TreeTextSpecifier textTreeSpec(){?
		return (TreeTextSpecifier)app.spec;
	}
	@Override
	public String title(){
		String title=super.title();
		int dotAt=title.indexOf(".");
		return dotAt<0?title:title.substring(0,dotAt);
	}
	@Override
	public boolean editSelection(){
		return new ValueEdit(((PathSelection)selection())){
			protected String getDialogInput(String title,String rubric,
					String proposal){
				return app.dialogs().getTextInput(title,rubric,proposal, 0);
			}
		}.dialogEdit();
	}
}
