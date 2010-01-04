/*
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details:
 * http://www.gnu.org/licenses/gpl.txt
 */

package info.bpace.bonsai;

import fr.inria.zvtm.engine.*;
import fr.inria.zvtm.svg.SVGReader;
import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.util.Vector;

import javax.swing.JFrame;
import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.Document;

/**
 * Main render window
 * @author Blaine Pace <blainepace at gmail.com>
 */
public class ZVTMView
{
	VirtualSpaceManager vsm;
	VirtualSpace vs;
	ViewEventHandler eh;

	View mainView;
	Camera detailCamera;

	CameraPortal overviewPortal;
	Camera overviewCamera;

	short viewType = 0;

	String vts;
	String spaceName = "mainSpace";

	/**
	 * Calls init
	 */
	ZVTMView()
	{
		vsm = VirtualSpaceManager.INSTANCE;
		viewType = View.STD_VIEW;
		vts = "View type: Standard";
		switch(viewType)
		{
			case View.OPENGL_VIEW:
			{
				viewType = View.OPENGL_VIEW;
				vts = "View type: OpenGL";
				break;
			}
		}
		init();
	}

	/**
	 * Sets up and starts render window with portal
	 */
	public void init()
	{
		vs = vsm.addVirtualSpace(spaceName);
		eh = new ZVTMEventHandler();

		//add camera to scene
		Vector cameras = new Vector();
		detailCamera = vs.addCamera();
		detailCamera.setZoomFloor(-90);
		cameras.add(detailCamera);

		vsm.addFrameView(cameras, spaceName, viewType, 800, 800, false, true);
		mainView = vsm.getView(spaceName);
		mainView.setBackgroundColor(Color.WHITE);

		JFrame frame = (JFrame)mainView.getFrame();
		frame.setResizable(false);

		mainView.setEventHandler(eh);
		mainView.setNotifyMouseMoved(true);

		overviewCamera = vs.addCamera();
		overviewPortal = new CameraPortal(0, 0, 800, 100, overviewCamera);
		vsm.addPortal(overviewPortal, mainView);
	}

	/**
	 * Loads svg file from disk and renders it, autocentering
	 * @param svg File object pointing to svg to load
	 */
	public void loadSvg(File svg)
	{
		String parser = XMLResourceDescriptor.getXMLParserClassName();
		SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);

		FileInputStream fileInput;
		Document doc;

		try
		{
			fileInput = new FileInputStream(svg);
			doc = f.createSVGDocument( "out.svg", fileInput );
			SVGReader.load(doc, vs, false, "out.svg");
			vsm.repaintNow(mainView);

			// get farmost coords
			long[] glyphCoords = new long[4];
			glyphCoords = vs.findFarmostGlyphCoords();

			// autocenter camera
			mainView.centerOnRegion(detailCamera, 300, glyphCoords[0], glyphCoords[3], glyphCoords[2], glyphCoords[1]);
			overviewPortal.getGlobalView(300);
			
		}
		catch(Exception e)
		{
			System.out.println("Exception: " + e.getMessage());
		}
	}
}
