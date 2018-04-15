package tones.app;

import facets.util.tree.ValueNode;
import facets.util.tree.XmlPolicy;
import facets.util.tree.XmlSpecifier;
import applicable.treetext.TreeTextSpecifier;

public final class TonesEdit_ extends TreeTextSpecifier{
	public TonesEdit_(){
		super(TonesEdit.class);
	}
	protected XmlSpecifier[]fileSpecifiers(XmlPolicy policy){
		return new XmlSpecifier[]{
			new XmlSpecifier("tones.xml","Tones",policy),
		};
	}
	@Override
	protected Object getInternalContentSource(){
		return new ValueNode("xml","Tones"+contents++,new Object[]{
						new ValueNode("Tones",new Object[]{
								newPartNode("e:16," 
										+"x,x,x,x,"
										+"x,x,x,x,"
										+"x,x,"
										+"x,x,"
										),
								newPartNode("b:"
										+"sb,-,8,e,4,f,a," 
										+"g,f,2,e,1,d,c,2,d,b," 
										+"2,+,sc,e,f,g,a,1,b,f,4,b,2,a," 
										+"a,1,g,f,4,g,g,4,f," 
										+"g,2,a,1,b,a,2,g,f,e,d," 
										+"8,c,-,4,b,f,"
										+"6,g,2,e,6,a,2,f,"
										+"16,b,"
										+"4,b"),
								newPartNode("t:" 
										+"16,x,8,x,sf,8,b,"
										+"4,c,e,d,c,6,"
										+"b,2,b,a,b,c,d," 
										+"1,e,b,4,e,2,d,4,e,2,g,f,"
										+"sb,-,4,g,e,8,f,"
										+"2,f,b,4,e,2,e,c,4,f,"
										+"2,se,+,f,1,e,f,2,g,a,1,b,f,4,b,2,a,"
										+"4,b"),
								newPartNode("a:" 
										+"16,x,x,x,"
										+"8,sb,-,e,4,f,a,g,f,e,2,x,b,"
										+"se,+,e,f,g,a,1,b,f,4,b,2,a,"
										+"sc,a,1,g,f,4,g,6,c,1,d,e,"
										+"4,d,4,e,2,e,d,4,c,-,f"),
								newPartNode("s:" 
										+"16,x,x,x,x,"
										+"8,x,sg,b," 
										+"4,c,e,d,c,"
										+"8,b,2,b,1,a,g,4,a,"
										+"sc,a,g,f,e,d,")
							})});
	}
	protected ValueNode newPartNode(String src){
		return new ValueNode("VoicePart",new Object[]{src});
	}
}