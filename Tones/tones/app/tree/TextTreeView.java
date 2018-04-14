package tones.app.tree;

import facets.core.app.TextView;

public class TextTreeView extends TextView{
	private final boolean canEdit;
	public TextTreeView(String title, boolean canEdit){
		super(title);
		this.canEdit=canEdit;
	}
	@Override
	public boolean isLive(){
		return canEdit;
	}
}