package tones.bar;
import static tones.Voice.*;
import facets.util.Debug;
import facets.util.Titled;
import facets.util.Tracer;
import facets.util.tree.DataNode;
import facets.util.tree.NodeList;
import facets.util.tree.TypedNode;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import tones.Tone;
import tones.Voice;
import tones.app.TonesViewable;
public final class Bars extends Tracer implements Titled{
  public static final boolean eighthsCheck=false;
  private final List<Bar>bars=new ArrayList();
  private final Map<Voice,VoicePart>parts=new HashMap();
  private final TonesViewable viewable;
  private VoicePart selectedPart;
  private int barEighths;private Soundings s=new Soundings();
    
  public Bars(TonesViewable viewable){
    this.viewable=viewable;
    TypedNode[]children=viewable.contentTree().children();
    for(TypedNode child:children){
      VoicePart part=new VoicePart((String)child.values()[0]);
      parts.put(part.voice,part);
      if(child==viewable.selection().single())
        selectPart(part.voice);
    }
    if(selectedPart==null)selectPart(Empty);
    int barAt=0;
    barEighths=0;
    while(true){
      Bar bar=newPartsBar(barAt++);
      if(bar!=null)bars.add(bar);
      else break;
    }
    if(false)trace(": bars="+bars.size()+" barAt="+barAt);
  }
  private Bar newPartsBar(int barAt){
    Map<Integer,Incipit>incipits=new HashMap();
    for(VoicePart part:parts.values()){
      List<Tone>partTones=part.getBarTones(barAt);
      if(false&&part.voice==Bass)trace(".newPartsBar: partTones="+partTones.size()+" barAt="+barAt);
      int barEighthsNow=partTones.isEmpty()?barEighths
          :(eighthsCheck?partTones.remove(0):partTones.get(0)).eighths;
      if(partTones.isEmpty())continue;
      if(eighthsCheck&&barEighths!=0&&barEighthsNow!=barEighths)throw new IllegalStateException(
          "New barEighths="+barEighths+", barEighthsNow="+barEighthsNow+
          " in "+Debug.info(part));
      else barEighths=barEighthsNow;
      int eighthAt=0;
      for(Tone tone:partTones){
        Incipit i;
        if((i=incipits.get(eighthAt))==null)
          incipits.put(partAt,i=new Incipit(eighthAt));
        i.addTone(tone);
        eighthAt+=tone.eighths;
      }
      
      for(Incipit i:incipits)s=i.readSoundings(s);
        
    }
    return incipits.isEmpty()?null:new Bar(barAt++,incipits.values(),barEighths);
  }
  public void updatePart(String src){
    VoicePart nowPart=new VoicePart(src),
      thenPart=parts.replace(nowPart.voice,nowPart);
    selectPart(nowPart.voice);
    int count=bars.size();
    for(int start=true?0:count-5,stop=false?2:count,
        barAt=start;true;barAt++){
      List<Tone>thenTones=thenPart.getBarTones(barAt),
          nowTones=nowPart.getBarTones(barAt);
      boolean nowEmpty=nowTones.isEmpty();
      if(nowEmpty&&thenTones.isEmpty())break;     
      barEighths=eighthsCheck&&!nowEmpty?nowTones.remove(0).eighths
          :VoicePart.BAR_EIGHTHS_DEFAULT;
      boolean equals=thenTones.equals(nowTones);
      if(!equals) {
        if(false) {
          trace(".updateSelectedPart: barAt="+barAt
              + " equals="+equals+" now=",nowTones);
          trace(" then=",thenTones);
        }
        if(barAt<bars.size())bars.remove(barAt);
        bars.add(barAt,newPartsBar(barAt));
      }
    }
    int stop=bars.size();
    for(int at=0;at<stop;at++)if(bars.get(at)==null){
      bars.remove(at--);
      stop--;
    }
    if(false)trace(".updatePart: bars=",bars.size());
  }
  public void selectPart(Voice voice){
    selectedPart=parts.get(voice);
    for(TypedNode child:viewable.contentTree().children())
      if(new VoicePart((String)child.values()[0]).voice.equals(selectedPart.voice))
          viewable.defineSelection(child);
  }
  public VoicePart selectedPart(){
    return selectedPart;
  }
  public int barCount(){
    return bars.size();
  }
  public List<Bar>barsFrom(int at){
    return bars.subList(at,bars.size());
  }
  public String title(){
    return viewable.title();
  }
  public DataNode newDebugRoot(int start,int stop){
    if(false)trace(".newDebugRoot: start="+start+" stop="+stop);
    NodeList barsList=new NodeList(newDebugRoot(Bars.class,title()),true);
    for(Bar bar:barsFrom(start)){
      if(bar.at==stop)break;
      NodeList barList=new NodeList(newDebugRoot(Bar.class,
          "at="+bar.at+" width="+bar.width),true);
      barsList.add(barList.parent);
      List<Incipit>incipits=new ArrayList<Incipit>(bar.incipits);
      Collections.sort(incipits);
      for(Incipit incipit:incipits){
        String title="part="+incipit.eighthAt+" bar="+incipit.barAt;
        NodeList incipitList=new NodeList(newDebugRoot(Incipit.class,title),true);
        barList.add(incipitList.parent);
        List<Tone>sortTones=new ArrayList(incipit.tones);
        Collections.sort(sortTones,new Comparator<Tone>(){
          @Override
          public int compare(Tone t1,Tone t2){
            return t1.voice.compareTo(t2.voice);
          }
        });
        for(Tone tone:sortTones){
          incipitList.add(tone.newDebugNode());
        }
      }
    }
    return barsList.parent;
  }
  static public DataNode newDebugRoot(Class type,String title,Object...values){
    return new DataNode(type.getSimpleName(),title,values);
  }
}
