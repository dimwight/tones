package tones.view;
import static tones.view.StaveItem.StaveTie.TieType.*;
import static tones.view.StavePageView.*;
import facets.core.app.avatar.AvatarContent;
import facets.util.Debug;
import facets.util.ItemList;
import facets.util.Tracer;
import facets.util.Util;
import facets.util.geom.Point;
import facets.util.geom.Vector;
import java.util.Arrays;
import java.util.List;
import tones.Clef;
import tones.Tone;
import tones.Voice;
import tones.bar.Annotation;
import tones.bar.Bar;
import tones.bar.Incipit;
import tones.bar.Annotation.BeamGroup;
import tones.bar.Annotation.Group;
import tones.bar.Annotation.TieGroup;
import tones.view.StaveItem.StaveBar;
import tones.view.StaveItem.StaveTie.TieType;
public class StaveItem extends Tracer implements AvatarContent{
	public abstract static class StaveGroup extends StaveItem{}
	public static final int STAVE_GRID=11;
	public static final class StaveTie extends StaveGroup{
		public enum TieType{FromTo,From,To,ToFrom}
		public final TieGroup content;
		public final StaveBar bar;
		public final Vector fromAt,toAt;
		public final TieType type;
		StaveTie(TieGroup tie,StaveBar bar,List<StaveBar>bars){
			this.content=tie;
			this.bar=bar;
			Vector fromAt=null,toAt=null;
			for(Incipit i:bar.content.incipits)
				for(Tone t:i.tones)
					if(t==tie.tone)fromAt=bar.newAnnotationAt(tie);
					else if(t==tie.end.tone)toAt=bar.newAnnotationAt(tie.end);
			if(fromAt!=null&&toAt!=null)
				type=FromTo;
			else{
				boolean isFrom=bar.content==tie.bar;
				Vector thisAt=bar.newAnnotationAt(isFrom?tie:tie.end),otherAt=null;
				int barCount=bars.size(),staveThisAt=0;
				for(StaveBar b:bars)if(b==bar)break;else staveThisAt++;
				int staveOtherAt=isFrom&&staveThisAt<barCount-1?staveThisAt+1
						:!isFrom&&staveThisAt>0?staveThisAt-1:-1;
				StaveBar otherBar=staveOtherAt<0?null:bars.get(staveOtherAt);
				if(otherBar!=null)for(Annotation a:otherBar.content.annotations)
					if(a==tie)otherAt=otherBar.newAnnotationAt(isFrom?tie.end:tie);
				fromAt=isFrom?thisAt:otherAt;
				toAt=isFrom?otherAt:thisAt;
				type=isFrom?otherAt!=null?FromTo:From:otherAt==null?To:ToFrom;
			}
			this.fromAt=fromAt;this.toAt=toAt;
		}
	}
	static StaveItem[]newGroupItems(Group g,List<StaveBar>bars){
		StaveBar copy=null;
		ItemList<StaveItem>items=new ItemList(StaveItem.class);
		for(StaveBar bar:bars){
			if(g instanceof TieGroup){
				TieGroup tie=(TieGroup)g;
				if(bar.content==g.bar||bar.content==tie.end.bar)
					items.addItem(new StaveTie(tie,bar,bars));
			}
			else if(g instanceof BeamGroup){
				BeamGroup beam=(BeamGroup)g;
				if(bar.content==g.bar)
					Util.printOut("StaveItem.newGroupItems: beam="+beam);
			}
		}
		return items.items();
	}
	public static class StaveNote extends StaveItem{
		public static final int DOT_NONE=0,DOT_LEVEL=1,DOT_BELOW=-1;
		public final double staveX,staveY,ledgerLineShift,dotAt;
		public final Tone content;
		public final Incipit incipit;
		public final int ledgerLines;
		private final String debugString;
		StaveNote(Tone content,StaveIncipit i,double barStaveY,Clef clef){
			this.content=content;
			incipit=i.content;
			staveX=i.scaledStaveX(content);
			final int stavePitch=content.pitch-clef.staveMidPitch,
				staveToMidPitch=STAVE_GRID/2-1;
			staveY=barStaveY+staveToMidPitch-stavePitch;
			boolean aboveMidPitch=stavePitch>0;
			int beyondStave=Math.abs(stavePitch)-staveToMidPitch;
			ledgerLines=beyondStave<=0?0
					:beyondStave/2*(aboveMidPitch?1:-1);
			ledgerLineShift=aboveMidPitch?beyondStave%2+1:(beyondStave+1)%2;
			dotAt=content.duration%3!=0?DOT_NONE:stavePitch%2==0?DOT_BELOW:DOT_LEVEL;
			debugString=content.pitchNote()+
				" stavePitch="+stavePitch+" aboveMidPitch="+aboveMidPitch
				+" beyondStave="+beyondStave;
		}
		public String toString(){
			return debugString;
		}
		public int hashCode(){
			return Arrays.hashCode(intValues());
		}
		public boolean equals(Object obj){
			if(true)throw new RuntimeException("Untested");
			StaveNote that=(StaveNote)obj;
			return this==that||Arrays.equals(intValues(),that.intValues());
		}
		private int[]intValues(){
			return new int[]{content.pitch,content.duration};
		}
	}
	protected boolean marking(){
		return false;
	}
	public static class StaveBar extends StaveItem{
		public final Bar content;
		public final double staveX,staveYs[],staveGap,staveXScale,staveWidth;
		StaveBar(Bar content,double staveX,double staveY,double staveGap,double staveXScale){
			this.content=content;
			this.staveX=staveX;
			this.staveGap=staveGap;
			this.staveXScale=staveXScale;
			staveWidth=content.width*staveXScale;
			staveYs=new double[]{staveY,staveY+STAVE_GRID+staveGap};
		}
		Vector newAnnotationAt(Annotation a){
			StaveNote note=(StaveNote)new StaveBar(a.bar.newAnnotationCopy(a.incipit.newCopy(a.tone)),
					staveX,staveYs[0],staveGap,staveXScale){
				@Override
				protected boolean marking(){
					return true;
				}
			}.items()[0];
			return new Vector(note.staveX,note.staveY);
		}
		StaveItem[]items(){
			final boolean marking=marking();
			ItemList<StaveIncipit>incipits=new ItemList(StaveIncipit.class);
			for(Incipit bar:content.incipits)
				incipits.addItem(new StaveIncipit(bar,staveX));
			for(StaveIncipit incipit:incipits)incipit.scaleStaveX(staveXScale);
			ItemList<StaveItem>items=new ItemList(StaveItem.class),
				voiceNotes=new ItemList(StaveItem.class);
			if(!marking)items.addItem(this);
			final Voice selected=content.selectedVoice();
			for(StaveIncipit incipit:incipits)
				for(Tone tone:incipit.content.tones){
					final Voice voice=tone.voice;
					Clef clef=Clef.forVoice(voice);
					StaveNote note=new StaveNote(tone,incipit,staveYs[clef.staveAt],clef){
						@Override
						public boolean marking(){
							return marking;
						}
					};
					if(marking)return new StaveItem[]{note};
					else if(selected!=null&&voice==selected)voiceNotes.addItem(note);
					else items.addItem(note);
				}
			if(voiceNotes.size()>0)
				items.addItem(new StaveVoiceNotes(voiceNotes.items()));
			return items.items();
		}
		public String toString(){
			return super.toString()+", staveX="+staveX+", staveY="+staveYs;
		}
	}
	final private static class StaveIncipit extends StaveItem{
		private final Incipit content;
		private final double barStaveX;
		private double scaledStaveX=-1;
		StaveIncipit(Incipit content,double barStaveX){
			this.content=content;
			this.barStaveX=barStaveX;
		}
		void scaleStaveX(double by){
			scaledStaveX=(int)(barStaveX+content.barAt*by);
		};
		double scaledStaveX(Tone tone){
			if(scaledStaveX<0)throw new IllegalStateException(
					"Invalid scaledStaveX in "+Debug.info(this));
			double scaled=scaledStaveX;
			for(Tone t:content.tones)
				if(Math.abs(t.pitch-tone.pitch)==1
						&&tone.duration>t.duration)scaled+=Bar.WIDTH_NOTE;
			return scaled;
		}	
	}
	public final class StaveVoiceNotes extends StaveItem{
		public final StaveItem[]items;
		StaveVoiceNotes(StaveItem[]items){
			this.items=items;
		}
	}
	public String toString(){
		return Debug.info(this)+": ";
	}
}