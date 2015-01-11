package galaxyoyo.unknown.frame.listeners;

import galaxyoyo.unknown.editor.EditorFrame;
import galaxyoyo.unknown.editor.SpriteComp;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SpriteMouseListener extends MouseAdapter
{
	private final SpriteComp sprite;
	private final EditorFrame frame;
	
	public SpriteMouseListener(SpriteComp sprc, EditorFrame frame)
	{
		this.sprite = sprc;
		this.frame = frame;
	}
	
	@Override
	public void mouseReleased(MouseEvent event)
	{
		if (frame.getSelectedSprite() != null)
		{
			frame.getSelectedSprite().setSelected(false);
			frame.getSelectedSprite().repaint();
		}
		frame.setSelectedSprite(sprite);
		sprite.setSelected(true);
		sprite.repaint();
	}
}
