package tones.then;
import static facets.facet.FacetFactory.*;
import static facets.facet.app.FacetPreferences.*;
import static tones.view.PageView.*;
import facets.core.app.ActionViewerTarget;
import facets.core.app.AppSurface.ContentStyle;
import facets.core.app.AreaRoot;
import facets.core.app.FeatureHost;
import facets.core.app.FeatureHost.LayoutFeatures;
import facets.core.app.MenuFacets;
import facets.core.app.PagedContenter;
import facets.core.app.SAreaTarget;
import facets.core.app.SContentAreaTargeter;
import facets.core.app.SContenter;
import facets.core.app.SurfaceServices;
import facets.core.app.TreeView;
import facets.core.app.ViewableFrame;
import facets.core.app.ViewerContenter;
import facets.core.app.ViewerTarget;
import facets.core.superficial.SFacet;
import facets.core.superficial.SFrameTarget;
import facets.core.superficial.SIndexing;
import facets.core.superficial.STarget;
import facets.core.superficial.STarget.Targeted;
import facets.core.superficial.STargeter;
import facets.core.superficial.SToggling;
import facets.core.superficial.app.FacetedTarget;
import facets.core.superficial.app.SSurface;
import facets.facet.AppFacetsBuilder;
import facets.facet.AreaFacets;
import facets.facet.FacetFactory;
import facets.facet.ViewerAreaMaster;
import facets.facet.app.FacetAppSpecifier;
import facets.facet.app.FacetAppSurface;
import facets.facet.kit.Toolkit;
import facets.facet.kit.swing.KitSwing;
import facets.util.app.HostBounds;
import facets.util.tree.Nodes;
import facets.util.tree.TypedNode;
import facets.util.tree.ValueNode;
import applicable.treetext.TreeTextSpecifier;
import tones.Tone;
import tones.view.PageView;
final public class TonesEditThen extends ViewerContenter{
	public static void main(String[]args){
		if(false)newSpecifier().buildAndLaunchApp(args);
		else tones.app.TonesEdit.main(args);
	}
	public static final String ARG_BAR_FROM="barFrom",ARG_RESCALE="rescale";
	private final FacetAppSurface app;
	private ViewableFrame viewable;
	TonesEditThen(Object source,FacetAppSurface app){
		super(source);
		this.app=app;
	}
	@Override
	protected ViewableFrame newContentViewable(Object source){
		BarsThen bars=new BarsThen((String[])source);
		return new TonesViewableThen(bars);
	}
	@Override
	protected FacetedTarget[]newContentViewers(ViewableFrame viewable){
		SFrameTarget page=PageView.newFramed(8,app.spec,((BarsThen)viewable.framed).barCount()),
		debug=new SFrameTarget(new TreeView("Debug"){
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
		return ActionViewerTarget.newViewerAreas(viewable,
			false?new SFrameTarget[]{
				page,
				debug
			}
			:new SFrameTarget[]{
				page,
			});
	}
	@Override
	protected void attachContentAreaFacets(AreaRoot area){
		final FacetFactory ff=app.ff;
		ViewerAreaMaster vam=new ViewerAreaMaster(){
			protected ViewerAreaMaster newChildMaster(SAreaTarget child){
				return child.title().contains("Debug")?null
						:new ViewerAreaMaster(){
					protected SFacet newViewTools(STargeter viewTargeter){
						return ff.toolGroups(viewTargeter,HINT_NONE,ff.spacerWide(8),
								true?null:ff.spacerTall(45),
					  		ff.numericSliders(viewTargeter.elements()[PageView.TARGET_BAR],
					  				400,HINT_SLIDER_TICKS+HINT_SLIDER_LABELS+HINT_SLIDER_LOCAL));
					}
					protected String hintString(){
						return HINT_NO_FLASH+HINT_PANEL_BORDER+HINT_BARE;
					}
				};
			}
		};
		ff.areas().attachViewerAreaPanes(area,vam,AreaFacets.PANE_SPLIT_VERTICAL);
	}
	@Override
	public LayoutFeatures newContentFeatures(final SContentAreaTargeter area){
		final STargeter selection=area.selection(),
			code=selection.elements()[0],
			view=area.view(),views[]=view.elements(),
			heightSetsPage=views[TARGET_HEIGHT_SETS_PAGE],
			time=views[TARGET_TIME],
			barSize=views[TARGET_BAR_SIZE];
		return new FacetFactory(app.ff){
			@Override
			public SFacet[]header(){
				final SFacet main=menuRoot(new AppFacetsBuilder(this,area).newMenuFacets());
				return app.contentStyle==ContentStyle.SINGLE?false?null:new SFacet[]{main}
					:new SFacet[]{main,menuRoot(windowMenuFacets(area,false))};
			}
			@Override
			public SFacet toolbar(){
				return false?null:false?toolGroups(view,HINT_NONE,
						togglingButtons(heightSetsPage,HINT_BARE)):
					rowPanel(selection,textualField(code,50,HINT_NONE),
						true?null:indexingDropdownList(time,HINT_NONE),
						true?null:indexingDropdownList(barSize,HINT_NONE));
			}
			@Override
			public SurfaceServices services(){
				return app.newFullServices(new MenuFacets(view,""){
					@Override
					public SFacet[]getContextFacets(ViewerTarget viewer,
							SFacet[]viewerFacets){
						return viewer.title().equals("Debug")?viewerFacets 
								:new SFacet[]{togglingCheckboxMenuItems(heightSetsPage,HINT_NONE)};
					}
				});
			}
		};
	}
	@Override
	public boolean hasChanged(){
		return false;
	}
	@Override
	public void wasAdded(){
		STarget[]viewers=app.activeContentTargeter().areaTarget().indexableTargets();
		if(viewers.length==1)return;
		SAreaTarget debug=(SAreaTarget)viewers[1];
		SIndexing expand=(SIndexing)((Targeted)debug.activeFaceted().attachedFacet()
				).targets()[0];
		expand.setIndex(3);
	}
	private static FacetAppSpecifier newSpecifier(){
		return new FacetAppSpecifier(TonesEditThen.class){
			@Override
			protected void addNatureDefaults(ValueNode root){
				super.addNatureDefaults(root);
				Nodes.mergeContents(root,new Object[]{
					HostBounds.NATURE_SIZER_DEFAULT+"=700,500",
					HostBounds.NATURE_SIZE_MIN+"= 400, 200"
				});
			}
			@Override
			public void adjustValues(){
				state(PATH_DEBUG).put(KEY_DRAG_NOTIFY,true);
				super.adjustValues();
			}
			@Override
			public boolean isFileApp(){
				return false;
			}
			@Override
			public ContentStyle contentStyle(){
				return ContentStyle.SINGLE;
			}
			@Override
			public Toolkit newToolkit(){
				return new KitSwing(false,false,true);
			}
			@Override
			public PagedContenter[]adjustPreferenceContenters(
					SSurface surface,PagedContenter[]contenters){
				return new PagedContenter[]{
						contenters[PREFERENCES_GRAPH],
						contenters[PREFERENCES_VALUES],
				};
			}
			@Override
			protected FacetAppSurface newApp(FacetFactory ff, FeatureHost host){
				return new FacetAppSurface(this,ff){
					@Override
					protected SContenter newContenter(Object source){
						return new TonesEditThen(source,this);
					}
					@Override
					public Object getInternalContentSource(){
						throw new RuntimeException("Not implemented in "+this);
					}
					protected void appOpened(){
						SToggling heightSetsPage=(SToggling)activeContentTargeter().view().elements(
								)[TARGET_HEIGHT_SETS_PAGE].target();
						boolean rescale=spec.nature().getBoolean(TonesEditThen.ARG_RESCALE);
						if(rescale)heightSetsPage.set(false);
					}
				};
			}
		};
	}
}
