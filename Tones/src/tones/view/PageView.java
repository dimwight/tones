package tones.view;
import static facets.util.Util.*;
import facets.core.app.avatar.PlaneViewWorks;
import facets.core.superficial.SFrameTarget;
import facets.core.superficial.SIndexing;
import facets.core.superficial.SIndexing.Coupler;
import facets.core.superficial.SNumeric;
import facets.core.superficial.STarget;
import facets.core.superficial.SToggling;
import facets.util.Debug;
import facets.util.NumberPolicy;
import facets.util.app.AppValues;
import facets.util.geom.Vector;
import facets.util.shade.Shades;
import facets.util.tree.ValueNode;
import tones.app.TonesEdit;
import tones.bar.Bar;
public abstract class PageView extends PlaneViewWorks{
	private static final boolean enlarge=System.getProperty("enlarge")!=null;
	public static final double INSET=0.5*INCH_PTS,DEFAULT_HEIGHT=7;
	public static final String KEY_HEIGHT="pageHeight",
		KEY_HEIGHT_SETS="pageHeightSets";
	public static final int TARGET_BAR=0,TARGET_HEIGHT_SETS_PAGE=1,
		TARGET_TIME=2,TARGET_BAR_SIZE=3;
	private int barAt;
	PageView(String title,double width,double height,PagePolicies policies){
		super(title,width,height,new Vector(-width/2+INSET,INSET),policies);
	}
	public Object backgroundStyle(){
		return Shades.lightGray;
	}
	public abstract double pitchHeight();
	public final double widthForPitch(){
		return 3d/Bar.WIDTH_NOTE;
	}
	public final int barAt(){
		return barAt;
	}
	public static SFrameTarget newFramed(final double notePoints,
			final AppValues spec,final int barCount,int barFrom){
		final ValueNode state=spec.state();
		final SIndexing time=new SIndexing("Time Signature",new Object[]{"2:2","4:4","4:2"},0,
				new Coupler(){
			public void indexSet(SIndexing i){
				spec.trace(".indexSet: i=",i.indexed());
			}
		}),
		barSize=new SIndexing("Whole Notes per Bar",new Object[]{1,2,4},2,new Coupler(){
			public void indexSet(SIndexing i){
				spec.trace(".indexSet: i=",i.indexed());
			}
		});
		final SToggling resizeSetsPage=new SToggling("Resize Sets Page",
				state.getOrPutBoolean(KEY_HEIGHT_SETS,true),new SToggling.Coupler(){
			@Override
			public void stateSet(SToggling t){
				state.put(KEY_HEIGHT_SETS,t.isSet());
			}
		});
		final PageView view=new PageView("Page",
				0,state.getOrPutDouble(KEY_HEIGHT,DEFAULT_HEIGHT)*INCH_PTS,new PagePolicies()){
			public boolean scaleToViewer(){
				return showWidth()>0&&!resizeSetsPage.isSet();
			}
			public double pitchHeight(){
				return notePoints/2;
			}
			public void setShowValues(double widthNow,double heightNow,
					Vector plotShiftNow,double scaleNow){
				double heightThen=showHeight();
				if(false)trace(".setShowValues: heightThen="+fx(heightThen)+
						" heightNow="+fx(heightNow)+"\n");
				if(heightThen==heightThen&&scaleToViewer()){
					if(true)throw new RuntimeException("Not implemented in "+Debug.info(this));
					double ratio=heightNow/heightThen;
					scaleNow*=ratio;
					widthNow/=ratio;
					plotShiftNow=new Vector(-widthNow/2+INSET,-heightThen/2+INSET);
				}
				super.setShowValues(widthNow,heightNow,plotShiftNow,scaleNow*(enlarge?2:1));
				state.put(KEY_HEIGHT,heightNow/INCH_PTS);
			}
		};
		final STarget barAt=new SNumeric("Start Bar ",barFrom,
				new SNumeric.Coupler(){		
			public void valueSet(SNumeric n){
				view.barAt=(int)n.value()-1;				
			}		
			public NumberPolicy policy(SNumeric n){
				return new NumberPolicy.Ticked(1,barCount){
				  final public int format(){return FORMAT_DECIMALS_0;}
				  public int labelSpacing(){return TICKS_DEFAULT;}
				};
			}
		});
		view.barAt=barFrom-1;
		return new SFrameTarget(view){
			protected STarget[]lazyElements(){
				return new STarget[]{barAt,resizeSetsPage,time,barSize};
			}
		};
	}
}