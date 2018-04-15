package tones.app;
import facets.core.app.StatefulViewable.ClipperSource;
import facets.core.superficial.SFrameTarget;
import facets.core.superficial.STarget;
import facets.core.superficial.STextual;
import facets.facet.app.FacetAppSurface;
import facets.util.Debug;
import facets.util.tree.TypedNode;
import facets.util.tree.ValueNode;
import applicable.treetext.TreeTextViewable;
import tones.bar.Bars;
import tones.bar.VoicePart;
final class TonesViewable_ extends TreeTextViewable{
	TonesViewable_(TypedNode tree,ClipperSource clipperSource,
			FacetAppSurface app){
		super(tree,clipperSource,app);
	}
	public SFrameTarget selectionFrame(){
		return new SFrameTarget(selection().single()){
			protected STarget[]lazyElements(){
				ValueNode selected=(ValueNode)framed;
				STextual textual=new STextual("Part",selected.values()[0],
						new STextual.Coupler(){
					@Override
					protected String getText(STextual t){
						return selected.values()[0];
					}
					public void textSet(STextual t){
						String src=t.text();
						try {
							VoicePart.checkSource(src);
							selected.putAt(0,src);
						} catch (Exception e) {
							t.trace(".textSet: "+e.getMessage());
						}
					}
					public boolean updateInterim(STextual t){
						return false;
					}
				});
				return new STextual[]{textual};
			}
		};
	}
}