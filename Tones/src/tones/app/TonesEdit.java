package tones.app;
import static facets.facet.FacetFactory.*;
import facets.core.app.AreaRoot;
import facets.core.app.SAreaTarget;
import facets.core.app.SContentAreaTargeter;
import facets.core.app.SViewer;
import facets.core.app.TreeView;
import facets.core.superficial.SFacet;
import facets.core.superficial.SFrameTarget;
import facets.core.superficial.SIndexing;
import facets.core.superficial.STarget;
import facets.core.superficial.STarget.Targeted;
import facets.core.superficial.STargeter;
import facets.facet.AreaFacets;
import facets.facet.FacetFactory;
import facets.facet.SwingPanelFacet;
import facets.facet.ViewerAreaMaster;
import facets.facet.app.FacetAppSurface;
import facets.util.FileSpecifier;
import facets.util.ItemList;
import facets.util.Util;
import facets.util.tree.DataNode;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import applicable.treetext.TreeTextContenter;
import applicable.treetext.TreeTextFeatures;
import applicable.treetext.TreeTextSpecifier;
import applicable.treetext.TreeTextViewable;
import tones.bar.VoicePart;
import tones.view.PageView;
public final class TonesEdit extends TreeTextContenter{
  public static void main(String[]args){
    newSpecifier().buildAndLaunchApp(args);
  }
  public static final String ARG_BAR_FROM="barFrom",ARG_RESCALE="rescale";
  public TonesEdit(Object source,FacetAppSurface app){
    super(source,app);
  }
  @Override
  protected TreeTextViewable newViewable(DataNode tree){
    return new TonesViewable(tree,app.ff.statefulClipperSource(false),app);
  }
  @Override
  protected SFrameTarget[]newViewTargets(TreeView debugTree,boolean liveViews){
    TonesViewable viewable=(TonesViewable)this.contentFrame();
    int barFrom=app.spec.state().getOrPutInt(TonesEdit.ARG_BAR_FROM,1);
    if(viewable.bars.barCount()<barFrom)barFrom=1;
    SFrameTarget page=PageView.newFramed(8,app.spec,
        viewable.bars.barCount(),barFrom);
    return new SFrameTarget[]{page,
        viewable.barsView,
        new SFrameTarget(debugTree)};
  }
  @Override
  protected void attachContentAreaFacets(AreaRoot area){
    final FacetFactory ff=app.ff;
    ViewerAreaMaster vam=new ViewerAreaMaster(){
      protected ViewerAreaMaster newChildMaster(SAreaTarget child){
        return !(((SViewer)child.activeFaceted()).view()instanceof PageView)?null
            :new ViewerAreaMaster(){
          protected SFacet newViewTools(STargeter viewTargeter){
            STargeter elements[]=viewTargeter.elements(),
                barStart=elements[PageView.TARGET_BAR];
            SwingPanelFacet lastBar=(SwingPanelFacet)ff.textualLabel(
                elements[PageView.TARGET_LAST],HINT_PANEL_CENTER);
            ((JLabel)lastBar.components().values().toArray()[0]
                ).setBorder(BorderFactory.createEmptyBorder(0,2,2,0));
            return ff.toolGroups(viewTargeter,HINT_PANEL_CENTER,ff.spacerWide(8),
                false?null:ff.spacerTall(45),
                ff.numericSliders(barStart,200,
                    HINT_SLIDER_TICKS+HINT_SLIDER_LABELS+HINT_SLIDER_LOCAL),
                ff.numericNudgeButtons(barStart,
                    HINT_NUMERIC_FIELDS+HINT_NUMERIC_NUDGERS_FIRST),
                lastBar);
          }
          protected String hintString(){
            return HINT_NO_FLASH+HINT_PANEL_BORDER+HINT_BARE;
          }
        };
      }
    };
    ff.areas().attachViewerAreaPanes(area,vam,AreaFacets.PANE_SPLIT_HORIZONTAL);
  }
  @Override
  protected TreeTextFeatures newFeatures(SContentAreaTargeter area){
    final STargeter selection=area.selection(),
        code=selection.elements()[0];
    return new TreeTextFeatures(app,area){
      @Override
      protected SFacet[]adjustToolbarItems(ItemList<SFacet>facets){
        if(false)facets.clear();
        facets.add(0,rowPanel(selection,textualField(code,45,HINT_NONE)));
        return facets.items();
      }
    };
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
  private static TreeTextSpecifier newSpecifier(){
    return new TreeTextSpecifier(TonesEdit.class){
      protected FileSpecifier[]fileSpecifiers(){
        return new FileSpecifier[]{
          new FileSpecifier("tones.txt","Tones"),
        };
      }
      @Override
      protected Object getInternalContentSource(){
        File file=new File(Util.runDir(),"E major.tones.txt");
        return file.exists()?file:VoicePart.TEST_CODES;
      }
      @Override
      protected TreeTextContenter newContenter(Object source,FacetAppSurface app){
        return new TonesEdit(source,app);
      }
    };
  }
}
