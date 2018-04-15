package tones.app;
import facets.core.app.StatefulViewable.ClipperSource;
import facets.core.superficial.SFrameTarget;
import facets.core.superficial.STarget;
import facets.core.superficial.STextual;
import facets.facet.app.FacetAppSurface;
import facets.util.tree.TypedNode;
import facets.util.tree.ValueNode;
import applicable.treetext.TreeTextViewable;
import tones.bar.Bars;
final class TonesViewable_ extends TreeTextViewable{
	TonesViewable_(TypedNode tree,ClipperSource clipperSource,
			FacetAppSurface app){
		super(tree,clipperSource,app);
	}
	public SFrameTarget selectionFrame(){
		return new SFrameTarget(selection().single()){
			protected STarget[]lazyElements(){
				ValueNode node=(ValueNode)selection().single();
				STextual textual=new STextual("Text",node.values()[0],
						new STextual.Coupler(){
					@Override
					protected String getText(STextual t){
						return node.values()[0];
					}
					public void textSet(STextual t){
						node.putAt(0,t.text());
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