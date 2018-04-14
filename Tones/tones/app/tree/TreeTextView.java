package tones.app.tree;

import facets.core.app.TextView;

public class TreeTextView extends TextView{
	private final boolean canEdit;
	public TreeTextView(String title, boolean canEdit){
		super(title);
		this.canEdit=canEdit;
	}
	@Override
	public boolean isLive(){
		return canEdit;
	}
}