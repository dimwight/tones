package tones.view.paint;
import static facets.util.shade.Shades.*;
import static tones.view.stave.StaveGroup.TieType.*;
import facets.core.app.avatar.Painter;
import facets.core.app.avatar.PainterMaster.Outlined;
import facets.core.app.avatar.PainterSource;
import facets.core.app.avatar.PainterSource.Transform;
import facets.util.Debug;
import facets.util.geom.Vector;
import facets.util.shade.Shade;
import path.SvgPath;
import tones.bar.Bar;
import tones.view.StavePageView;
import tones.view.stave.StaveGroup;
import tones.view.stave.StaveGroup.StaveTie;
import tones.view.stave.StaveGroup.TieType;
public final class GroupPainters extends PagePainters{
	private static final SvgPath 
		TieFromTo=new SvgPath("TieFromTo","M213.0 72.13c7.665,7.444 1.127,13.46 -6.062,6.475 -22.96,-18.09 -48.72,-30.72 -98.74,-31.47 -50.02,0.7481 -75.77,13.38 -98.74,31.47 -7.203,6.997 -13.67,0.9138 -6.062,-6.475 26.2,-29.19 66.25,-42.94 104.8,-42.56 38.75,0.0 78.4,13.15 104.8,42.56z",2),
		TieFromLong=new SvgPath("TieFromLong","M173.4 58.53c-16.6,-6.798 -37.06,-10.98 -65.2,-11.4 -50.02,0.7481 -75.77,13.38 -98.74,31.47 -7.203,6.997 -13.67,0.9138 -6.062,-6.475 26.2,-29.19 66.25,-42.94 104.8,-42.56 23.01,0.0 46.33,4.635 67.01,14.56l-1.816 14.41z",2),
		TieFromShort=new SvgPath("TieFromShort","M108.2 29.57c-38.55,-0.3749 -78.6,13.37 -104.8,42.56 -7.609,7.389 -1.141,13.47 6.062,6.475 22.96,-18.09 48.72,-30.72 98.74,-31.47l0.0 -17.57z",2),
		TieTo=new SvgPath("TieTo","M0.0 29.57c38.55,-0.3749 78.6,13.37 104.8,42.56 7.609,7.389 1.141,13.47 -6.062,6.475 -22.96,-18.09 -48.72,-30.72 -98.74,-31.47l0.0 -17.57z",2);
	private final double noteWidth,noteHeight;
	private final StaveGroup group;
	public GroupPainters(StavePageView page,StaveGroup group,PainterSource p){
		super(page,p);
		this.group=group;
		noteWidth=Bar.WIDTH_NOTE*unitWidth;
		noteHeight=pitchHeight*2;
	}
	@Override
	public
	Painter[]newViewPainters(boolean selected){
		StaveTie tie=(StaveTie)group;
		return tie.type==ToFrom?new Painter[]{}:new Painter[]{
				tie.type==To?tieTo(tie):tieFrom(tie)
			};
	}
	private Painter tieTo(StaveTie tie){
		double x=tie.bar.staveX,y=tie.toAt.y;
		Shade shade=true?blue:red;
		Painter painter=p.mastered(TieTo.newOutlined(shade,null,false));
		p.applyTransforms(new Transform[]{
			p.transformAt(x*unitWidth,y*pitchHeight-noteHeight),
			p.transformScale(noteHeight,noteHeight),
		},true,painter);
		return painter;
	}
	private Painter tieFrom(StaveTie tie){
		double pastNote=noteWidth*.7,x=tie.fromAt.x+pastNote,y=tie.fromAt.y;
		TieType type=tie.type;
		final boolean isTo=type==FromTo;
		Shade shade=true?blue:isTo?magenta:cyan;
		Outlined path=(isTo?TieFromTo:false?TieFromLong:TieFromShort
				).newOutlined(shade,null,false);
		Vector bounds=path.bounds().scaled(noteHeight);
		double toTo=(!isTo?0:tie.toAt.x-x)*unitWidth, 
			toBarEnd=(tie.bar.staveX+tie.bar.staveWidth-x)*unitWidth,
			stretch=(!isTo?toBarEnd:toTo)/bounds.x;
		Painter painter=p.mastered(path);
		p.applyTransforms(new Transform[]{
			p.transformAt(x*unitWidth,y*pitchHeight-noteHeight),
			p.transformScale(noteHeight*stretch,noteHeight),
		},true,painter);
		return painter;
	}
	public Painter[]newPickPainters(){
		throw new RuntimeException("Not implemented in "+Debug.info(this));
	}
}
