package galaxyoyo.unknown.editor;

import galaxyoyo.unknown.api.editor.Case;
import galaxyoyo.unknown.api.editor.RawCase;
import galaxyoyo.unknown.api.editor.RawMap;
import galaxyoyo.unknown.api.editor.sprites.SpriteRegister;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Map
{
	private final EditorFrame frame;
	private int width;
	private int height;
	private List<Case> cases = new ArrayList<Case>();
	private java.util.Map<Point, Case> casesMap = new HashMap<Point, Case>();
	private transient BufferedImage font;
	
	public Map(RawMap raw)
	{
		this.width = raw.getWidth();
		this.height = raw.getHeight();
		this.font = raw.getFont();
		
		for (RawCase rc : raw.getCases())
		{
			cases.add(Case.create(rc.getPosX(), rc.getPosY(), SpriteRegister.getCategory(rc.getCoucheOne().getPrimaryIndex()).getSprites().get(rc.getCoucheOne().getSecondaryIndex()), SpriteRegister.getCategory(rc.getCoucheTwo().getPrimaryIndex()).getSprites().get(rc.getCoucheTwo().getSecondaryIndex()), SpriteRegister.getCategory(rc.getCoucheThree().getPrimaryIndex()).getSprites().get(rc.getCoucheThree().getSecondaryIndex()), rc.getCollision()));
		}
		
		frame = new EditorFrame(this);
		
		getFrame().setVisible(true);
	}
	
	public EditorFrame getFrame()
	{
		return frame;
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}
	
	public Case getCase(int x, int y)
	{
		if (casesMap.isEmpty())
		{
			reorganizeMap();
		}
		
		return casesMap.get(new Point(x, y));
	}

	public BufferedImage getFont()
	{
		return font;
	}

	public void setFont(BufferedImage font)
	{
		this.font = font;
	}

	private void reorganizeMap()
	{
		for (Case c : cases)
		{
			casesMap.put(new Point(c.getPosX() - c.getPosX() % 16, c.getPosY() - c.getPosY() % 16), c);
		}
	}
}
