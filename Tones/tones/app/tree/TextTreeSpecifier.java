package tones.app.tree;
import static facets.core.app.ActionViewerTarget.Action.*;
import static facets.facet.app.FacetPreferences.*;
import static facets.util.tree.DataConstants.*;
import facets.core.app.ActionAppSurface;
import facets.core.app.AppActions;
import facets.core.app.FeatureHost;
import facets.core.app.NodeViewable;
import facets.core.app.PagedContenter;
import facets.core.app.SContenter;
import facets.core.app.SView;
import facets.core.app.SViewer;
import facets.core.app.TreeView;
import facets.core.app.ViewableAction;
import facets.core.superficial.STarget;
import facets.core.superficial.app.SSelection;
import facets.core.superficial.app.SSurface;
import facets.facet.FacetFactory;
import facets.facet.app.FacetAppSpecifier;
import facets.facet.app.FacetAppSurface;
import facets.util.FileSpecifier;
import facets.util.tree.DataNode;
import facets.util.tree.Nodes;
import facets.util.tree.TypedNode;
import facets.util.tree.ValueNode;
import facets.util.tree.XmlPolicy;
import facets.util.tree.XmlSpecifier;
import java.io.File;
public abstract class TextTreeSpecifier extends FacetAppSpecifier{
	public TextTreeSpecifier(Class appClass){
		super(appClass);
	}
	@Override
	public PagedContenter[]adjustPreferenceContenters(SSurface surface,
			PagedContenter[]contenters){
		return false?contenters:new PagedContenter[]{
			contenters[PREFERENCES_TRACE],
			contenters[PREFERENCES_GRAPH],
			contenters[PREFERENCES_VALUES],
			contenters[PREFERENCES_VIEW],
		};
	}
	@Override
	protected final AppActions newActions(ActionAppSurface app){
		return super.newActions(app);
	}
	@Override
	public boolean headerIsRibbon(){
		return args().getOrPutBoolean(ARG_RIBBON,false);
	}
	@Override
	final protected FacetAppSurface newApp(FacetFactory ff,FeatureHost host){
		return new FacetAppSurface(this,ff){
			@Override
			public FileSpecifier[]getFileSpecifiers(){
				return((TextTreeSpecifier)spec).xmlPolicy().fileSpecifiers();
			}
			@Override
			protected Object getInternalContentSource(){
				return((TextTreeSpecifier)spec).getInternalContentSource();
			}
			@Override
			protected SContenter newContenter(Object source){
				return new TextTreeContenter(source,this);
			}
		};
	}
	protected XmlPolicy xmlPolicy(){
		return new XmlPolicy(){
			@Override
			protected boolean treeAsXmlRoot(){
				return false?true:super.treeAsXmlRoot();
			}
			@Override
			protected boolean dataUsesAttributes(){
				return false?false:super.dataUsesAttributes();
			}
			public XmlSpecifier[]fileSpecifiers(){
				return new XmlSpecifier[]{
					new XmlSpecifier("txt.xml","Text in XML",this),
				};
			};
		};
	}
	int contents;
	protected Object getInternalContentSource(){
		return false?new File("Test.txt.xml")
				:new ValueNode("xml","Content"+contents++,new Object[]{
						new ValueNode("TextTree","Test",new Object[]{
							new ValueNode("TextLine",TypedNode.UNTITLED,
									new Object[]{"First line"}),
							new ValueNode("TextLine",TypedNode.UNTITLED,
									new Object[]{"Second line"})})});
	}
	protected SView[]newContentViews(NodeViewable viewable){
		TypedNode root=(TypedNode)viewable.framed;
		final String rootCheck=false?root.title():root.type();
		final boolean liveViews=canEditContent();
		SView tree=new TreeView("Tree"){
			@Override
			public boolean allowMultipleSelection(){
				return true;
			}
			@Override
			public boolean hideRoot(){
				return rootCheck.endsWith(TYPE_XML);
			}
			@Override
			public boolean isLive(){
				return liveViews;
			}
		};
		return new SView[]{tree};
	}
	protected STarget[]newContentRootTargets(FacetAppSurface app){
		return new STarget[]{};
	}
	protected ViewableAction[]viewerActions(SView view){
		ViewableAction[]all={COPY,
				     CUT,
				     PASTE,
				     DELETE,
				     MODIFY,
				     UNDO,
				     REDO};
		return view.isLive()?all:new ViewableAction[]{COPY};
	}
	public boolean viewerSelectionChanged(NodeViewable viewable,SViewer viewer,
			SSelection selection){
		return false;
	}
}
