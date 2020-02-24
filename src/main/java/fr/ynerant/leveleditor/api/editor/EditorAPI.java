package fr.ynerant.leveleditor.api.editor;

import fr.ynerant.leveleditor.editor.Map;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class EditorAPI
{
	private static File LAST_FILE;

	public static RawMap toRawMap(int width, int height)
	{
		List<RawCase> cases = new ArrayList<RawCase>();
		
		for (int y = 1; y < height; y += 16)
		{
			for (int x = 1; x < width; x += 16)
			{
				RawCase c = RawCase.create(x / 16, y / 16, RawSprite.BLANK, RawSprite.BLANK, RawSprite.BLANK, Collision.ANY);
				cases.add(c);
			}
		}
		
		return RawMap.create(cases, width, height);
	}
	
	public static Gson createGson()
	{
		GsonBuilder builder = new GsonBuilder();
		
		builder.enableComplexMapKeySerialization();
		builder.serializeNulls();
		builder.setPrettyPrinting();
		
		return builder.create();
	}

	public static JFileChooser createJFC()
	{
		JFileChooser jfc = new JFileChooser();
		
		jfc.setFileFilter(new FileNameExtensionFilter("Fichiers monde (*.gmap, *.dat)", "gmap", "dat"));
		jfc.setFileHidingEnabled(true);
		jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		File dir = new File("maps");
		dir.mkdirs();
		jfc.setCurrentDirectory(dir);
		
		return jfc;
	}

	public static void saveAs(RawMap map)
	{		
		JFileChooser jfc = createJFC();
		File file = null;
		jfc.showSaveDialog(null);
		file = jfc.getSelectedFile();
		
		if (file == null)
			return;
		
		if (!file.getName().toLowerCase().endsWith(".gmap") && !file.getName().toLowerCase().endsWith(".dat"))
		{
			file = new File(file.getParentFile(), file.getName() + ".gmap");
		}
		
		LAST_FILE = file;
		
		save(file, map);
	}
	
	public static void save(RawMap map)
	{
		if (LAST_FILE != null)
			save(LAST_FILE, map);
		else
			saveAs(map);
	}
	
	public static void save(File file, RawMap map)
	{
		String json = createGson().toJson(map);
		
		try
		{
			file.createNewFile();
			BufferedOutputStream bos = new BufferedOutputStream(new GZIPOutputStream(new FileOutputStream(file)));
			
			bos.write(json.getBytes("UTF-8"));
			
			bos.close();
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
	}
	
	public static Map open()
	{
		JFileChooser jfc = createJFC();
		File file = null;
		
		jfc.showOpenDialog(null);
		file = jfc.getSelectedFile();
		
		if (file == null)
			return null;
		
		LAST_FILE = file;
		
		return open(file);
	}
	
	public static Map open(File f)
	{
		String json = null;
		try
		{
			GZIPInputStream gis = new GZIPInputStream(new BufferedInputStream(new FileInputStream(f)));
			byte[] bytes = new byte[512*1024];
			int count = 0;
			String text = "";
			while ((count = gis.read(bytes)) != -1)
			{
				text += new String(bytes, 0, count, "UTF-8");
			}
			gis.close();
			bytes = null;
			
			json = text;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		RawMap rm = createGson().fromJson(json, RawMap.class);
		
		return open(rm);
	}

	public static Map open(RawMap map)
	{
		if (map.getFont() == null)
		{
			int baseWidth = map.getWidth();
			int baseHeight = map.getHeight();
			int width = baseWidth + ((int) baseWidth / 16) + 1;
			int height = baseHeight + ((int) baseHeight / 16) + 1;
			BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = image.createGraphics();
			g.setColor(Color.white);
			g.fillRect(0, 0, width, height);
			g.setColor(Color.black);
			g.drawLine(0, 0, width, 0);
			g.drawLine(0, 0, 0, height);
			for (int x = 17; x <= width; x += 17)
			{
				g.drawLine(x, 0, x, height);
			}
			
			for (int y = 17; y <= height; y += 17)
			{
				g.drawLine(0, y, width, y);
			}
			
			map.setFont(image);
		}
		
		return new Map(map);
	}
}