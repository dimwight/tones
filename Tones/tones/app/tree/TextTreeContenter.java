package tones.app.tree;
import static facets.core.app.ActionViewerTarget.Action.*;
import facets.core.app.ActionViewerTarget;
import facets.core.app.AreaRoot;
import facets.core.app.FeatureHost.LayoutFeatures;
import facets.core.app.NodeViewable;
import facets.core.app.PathSelection;
import facets.core.app.SAreaTarget;
import facets.core.app.SContentAreaTargeter;
import facets.core.app.SView;
import facets.core.app.SViewer;
import facets.core.app.StatefulViewable;
import facets.core.app.ValueEdit;
import facets.core.app.ViewableAction;
import facets.core.app.ViewableFrame;
import facets.core.app.ViewerContenter;
import facets.core.app.ViewerTarget;
import facets.core.superficial.Notice;
import facets.core.superficial.Notifying.Impact;
import facets.core.superficial.SFrameTarget;
import facets.core.superficial.STarget;
import facets.core.superficial.TargetCore;
import facets.core.superficial.app.FacetedTarget;
import facets.core.superficial.app.SSelection;
import facets.core.superficial.app.SelectionView;
import facets.facet.AreaFacets;
import facets.facet.app.FacetAppSurface;
import facets.facet.app.FileAppActions;
import facets.util.Debug;
import facets.util.FileSpecifier;
import facets.util.OffsetPath;
import facets.util.Stateful;
import facets.util.TextLines;
import facets.util.tree.DataNode;
import facets.util.tree.NodePath;
import facets.util.tree.Nodes;
import facets.util.tree.TypedNode;
import facets.util.tree.ValueNode;
import facets.util.tree.XmlDocRoot;
import facets.util.tree.XmlSpecifier;
import java.io.File;
import java.io.IOException;
public final class TextTreeContenter extends ViewerContenter{
	public static final int TARGETS_PANE=0,TARGETS_CONTENT=1;
	public static final String STATE_OFFSETS="selectionOffsets";
	private final FacetAppSurface app;
	private final TextTreeSpecifier spec;
	private Object stateStamp=null;
	private NodeViewable viewable;
	TextTreeContenter(Object source,FacetAppSurface app){
		super(source);
		this.app=app;
		spec=(TextTreeSpecifier)app.spec;
	}
	@Override
	protected ViewableFrame newContentViewable(Object source){
		DataNode tree=null;
		if(source instanceof File){
			File file=(File)source;
			for(FileSpecifier fileType:app.getFileSpecifiers()){
				if(!fileType.specifies(file))continue;
				XmlSpecifier xml=(XmlSpecifier)fileType;
				XmlDocRoot root=xml.newTreeRoot(xml.newRootNode(file));
				root.readFromSource(file);
				tree=root.tree;
				tree.setTitle(file.getName());
				tree.setValidType("File");
				stateStamp=tree.stateStamp();
				break;
			}
			if(tree==null)throw new IllegalStateException("Bad file type in "+file);
		}
		else tree=(DataNode)source;
		final ValueNode state=app.spec.state();
		NodeViewable viewable=new NodeViewable(tree,app.ff.statefulClipperSource(false)){
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
				return ((SelectionView)viewer.view()).newViewerSelection(viewer,selection());
			}
			@Override
			public ViewableAction[]viewerActions(SView view){
				return spec.viewerActions(view);
			}
			@Override
			protected void viewerSelectionChanged(SViewer viewer,SSelection selection){
				if(!spec.viewerSelectionChanged(this,viewer,selection))
					super.viewerSelectionChanged(viewer,selection);
				putSelectionState(state,STATE_OFFSETS);
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
		};
		viewable.readSelectionState(state,STATE_OFFSETS);
		return this.viewable=viewable;
	}
	@Override
	protected FacetedTarget[]newContentViewers(ViewableFrame viewable){
		return ActionViewerTarget.newViewerAreas(viewable,ViewerTarget.newViewFrames(
			spec.newContentViews((NodeViewable)viewable)
		));
	}
	@Override
	protected void attachContentAreaFacets(AreaRoot area){
		app.ff.areas().attachViewerAreaPanes(area,"",AreaFacets.PANE_SPLIT_VERTICAL);
	}
	@Override
	public LayoutFeatures newContentFeatures(SContentAreaTargeter area){
		return new TextTreeFeatures(app,area);
	}
	@Override
	public STarget[]lazyContentAreaElements(SAreaTarget area){
		return new STarget[]{
				app.ff.areas().panesGetTarget(area),
				new TargetCore("ContentRootTargets",
						spec.newContentRootTargets(app)),
		};
	}
	@Override
	public boolean hasChanged(){
		Object framedStamp=((Stateful)contentFrame().framed).stateStamp();
		boolean changed=framedStamp!=stateStamp;
		if(false&&changed)trace(".hasChanged: framedStamp=",framedStamp);
		return (false||app.actions instanceof FileAppActions)&&changed;
	}
	@Override
	public FileSpecifier[]sinkFileSpecifiers(){
		Object sink=sink();
		String name=sink instanceof File?((File)sink).getName()
				:((TypedNode)sink).title()+"."+((TypedNode)sink).type();
		return FileSpecifier.filterByName(app.getFileSpecifiers(),name);
	}
	@Override
	public boolean setSink(Object sink){
		if(sink instanceof File&&((File)sink).getName().startsWith("_")||
			!app.spec.canOverwriteContent())return false;
		else return super.setSink(sink);
	}
	@Override
	public void saveToSink(Object sink)throws IOException{
		File file=(File)sink;
		String name=file.getName();
		if(file.exists()&&!name.startsWith("_"))
			new TextLines(file).copyFile("_"+name);
		SFrameTarget frame=contentFrame();
		DataNode tree=(DataNode)frame.framed;
		tree.setTitle(name);
		tree.setValidType("File");
		new XmlDocRoot(tree,spec.xmlPolicy()).writeToSink(file);
		stateStamp=tree.updateStateStamp();
		if(false)trace(".setSink: area="+Debug.info(app.activeContentTargeter().target().title())
				+ "\nfile="+name);
		app.notify(new Notice(frame,Impact.DEFAULT));
	}
}
