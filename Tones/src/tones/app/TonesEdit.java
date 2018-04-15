package tones.app;
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
import tones.bar.Bars;
import tones.view.PageView;
final public class TonesEdit extends ViewerContenter{
	public static final String ARG_BAR_FROM="barFrom",ARG_RESCALE="rescale";
	public static final char CODE_SCALE='s',CODE_OCTAVE_UP='+',CODE_OCTAVE_DOWN='-',
			CODE_TIE='T',CODE_BEAM='B',CODE_BAR_SIZE='Z';
	public static final int BAR_EIGHTHS_DEFAULT=16;
	public static final String CODES_NOTE="abcdefgx";
	public static final String TEST_CODES[]={
			"e:16," 
			+"x,x,x,x,"
			+"x,x,x,x,"
			+"x,x,"
		+"x,x,"
			,
			"s:" 
			+"16,x,x,x,x,"
			+"8,x,sg,b," 
			+"4,c,e,d,c,"
			+"8,b,2,b,1,a,g,4,a,"
			+"sc,a,g,f,e,d,"
			,
			"a:" 
			+"16,x,x,x,"
			+"8,sb,-,e,4,f,a,g,f,e,2,x,b,"
			+"se,+,e,f,g,a,1,b,f,4,b,2,a,"
			+"sc,a,1,g,f,4,g,6,c,1,d,e,"
			+"4,d,4,e,2,e,d,4,c,-,f"
			,
			"t:" 
			+"16,x,8,x,sf,8,b,"
			+"4,c,e,d,c,6,"
			+"b,2,b,a,b,c,d," 
			+"1,e,b,4,e,2,d,4,e,2,g,f,"
			+"sb,-,4,g,e,8,f,"
			+"2,f,b,4,e,2,e,c,4,f,"
			+"2,se,+,f,1,e,f,2,g,a,1,b,f,4,b,2,a,"
			+"4,b"
			,
			"b:"
			+"sb,-,8,e,4,f,a," 
			+"g,f,2,e,1,d,c,2,d,b," 
			+"2,+,sc,e,f,g,a,1,b,f,4,b,2,a," 
			+"a,1,g,f,4,g,g,4,f," 
			+"g,2,a,1,b,a,2,g,f,e,d," 
			+"8,c,-,4,b,f,"
			+"6,g,2,e,6,a,2,f,"
			+"16,b,"
			+"4,b"
		+""
		};
	private final FacetAppSurface app;
	private ViewableFrame viewable;
	TonesEdit(Object source,FacetAppSurface app){
		super(source);
		this.app=app;
	}
	@Override
	protected ViewableFrame newContentViewable(Object source){
		Bars bars=new Bars((String[])source);
		return new TonesViewable(bars);
	}
	@Override
	protected FacetedTarget[]newContentViewers(ViewableFrame viewable){
		SFrameTarget page=PageView.newFramed(8,app.spec,((Bars)viewable.framed).barCount()),
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
	public void wasAdded(){
		STarget[]viewers=app.activeContentTargeter().areaTarget().indexableTargets();
		if(viewers.length==1)return;
		SAreaTarget debug=(SAreaTarget)viewers[1];
		SIndexing expan_d=(SIndexing)((Targeted)debug.activeFaceted().attachedFacet()
				).targets()[0];
		expan_d.setIndex(3);
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
	public static void main(String[]args){
		FacetAppSpecifier then=new FacetAppSpecifier(TonesEdit.class){
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
						return new TonesEdit(source,this);
					}
					@Override
					public Object getInternalContentSource(){
						return TEST_CODES;
					}
					protected void appOpened(){
						SToggling heightSetsPage=(SToggling)activeContentTargeter().view().elements(
								)[TARGET_HEIGHT_SETS_PAGE].target();
						boolean rescale=spec.nature().getBoolean(TonesEdit.ARG_RESCALE);
						if(rescale)heightSetsPage.set(false);
					}
				};
			}
		};
		if(false)then.buildAndLaunchApp(args);
		else TonesEdit_.main(args);
	}
}
