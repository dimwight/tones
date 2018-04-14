package tones.app.tree;
import facets.core.app.ActionViewerTarget;
import facets.core.app.AreaRoot;
import facets.core.app.FeatureHost.LayoutFeatures;
import facets.core.app.NodeViewable;
import facets.core.app.SAreaTarget;
import facets.core.app.SContentAreaTargeter;
import facets.core.app.ViewableFrame;
import facets.core.app.ViewerContenter;
import facets.core.app.ViewerTarget;
import facets.core.superficial.Notice;
import facets.core.superficial.Notifying.Impact;
import facets.core.superficial.SFrameTarget;
import facets.core.superficial.STarget;
import facets.core.superficial.TargetCore;
import facets.core.superficial.app.FacetedTarget;
import facets.facet.AreaFacets;
import facets.facet.app.FacetAppSurface;
import facets.facet.app.FileAppActions;
import facets.util.Debug;
import facets.util.FileSpecifier;
import facets.util.Stateful;
import facets.util.TextLines;
import facets.util.tree.DataNode;
import facets.util.tree.TypedNode;
import facets.util.tree.ValueNode;
import facets.util.tree.XmlDocRoot;
import facets.util.tree.XmlSpecifier;
import java.io.File;
import java.io.IOException;
public final class TreeTextContenter extends ViewerContenter{
	public static final int TARGETS_PANE=0,TARGETS_CONTENT=1;
	public static final String STATE_OFFSETS="selectionOffsets";
	private final FacetAppSurface app;
	private Object stateStamp=null;
	private NodeViewable viewable;
	TreeTextContenter(Object source,FacetAppSurface app){
		super(source);
		this.app=app;
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
		NodeViewable viewable=new TreeTextViewable(?
				tree,app.ff.statefulClipperSource(false),app);
		viewable.readSelectionState(state,STATE_OFFSETS);
		return this.viewable=viewable;
	}
	private TreeTextSpecifier textTreeSpec(){
		return (TreeTextSpecifier)app.spec;
	}
	@Override
	protected FacetedTarget[]newContentViewers(ViewableFrame viewable){
		return ActionViewerTarget.newViewerAreas(viewable,ViewerTarget.newViewFrames(
				textTreeSpec().newContentViews((NodeViewable)viewable)?
		));
	}
	@Override
	protected void attachContentAreaFacets(AreaRoot area){
		app.ff.areas().attachViewerAreaPanes(area,"",AreaFacets.PANE_SPLIT_VERTICAL);
	}
	@Override
	public LayoutFeatures newContentFeatures(SContentAreaTargeter area){
		return new TreeTextFeatures(app,area);?
	}
	@Override
	public STarget[]lazyContentAreaElements(SAreaTarget area){
		return new STarget[]{
				app.ff.areas().panesGetTarget(area),
				new TargetCore("ContentRootTargets",
						textTreeSpec().newContentRootTargets(app)),?
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
		return FileSpecifier.filterByName(app.getFileSpecifiers(),name);?
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
		new XmlDocRoot(tree,textTreeSpec().xmlPolicy()).writeToSink(file);
		stateStamp=tree.updateStateStamp();
		if(false)trace(".setSink: area="+Debug.info(app.activeContentTargeter().target().title())
				+ "\nfile="+name);
		app.notify(new Notice(frame,Impact.DEFAULT));
	}
}
