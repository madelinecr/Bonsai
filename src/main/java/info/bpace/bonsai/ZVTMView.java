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
import fr.inria.zvtm.lens.*;
import fr.inria.zvtm.svg.SVGReader;
import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.util.Vector;

import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.Document;

/**
 *
 * @author Blaine Pace <blainepace at gmail.com>
 */
public class ZVTMView {
    VirtualSpaceManager vsm;
    VirtualSpace vs;
    ViewEventHandler eh;

    View demoView;
    Camera mCamera;

    short translucentMode = 0;

    short viewType = 0;

    String vts;
    String spaceName = "mainSpace";

    ZVTMView() {
        vsm = VirtualSpaceManager.INSTANCE;
        viewType = View.STD_VIEW;
	vts = "View type: Standard";
	switch(viewType) {
            case View.OPENGL_VIEW: {
                viewType = View.OPENGL_VIEW;
                vts = "View type: OpenGL";
                break;
            }
	}
        init();
    }

    public void init() {
        vs = vsm.addVirtualSpace(spaceName);
        eh = new ZVTMEventHandler();

        //add camera to scene
        Vector cameras = new Vector();
        mCamera = vs.addCamera();
        mCamera.setZoomFloor(-90);
        cameras.add(mCamera);

        //JPanel tempPanel = vsm.addPanelView(cameras, spaceName, 400, 400);
        vsm.addFrameView(cameras, spaceName, viewType, 600, 800, true, true);
        demoView = vsm.getView(spaceName);
        demoView.setBackgroundColor(Color.WHITE);
        demoView.setEventHandler(eh);
        demoView.setNotifyMouseMoved(true);

        //System.out.println(vts);
    }

    public void loadSvg(File svg) {
        String parser = XMLResourceDescriptor.getXMLParserClassName();
        SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);

        FileInputStream fileInput;
        Document doc;

        try {
            fileInput = new FileInputStream(svg);
            doc = f.createSVGDocument( "out.svg", fileInput );
            SVGReader.load(doc, vs, false, "out.svg");
            vsm.repaintNow(demoView);

            // get farmost coords
            long[] glyphCoords = new long[4];
            glyphCoords = vs.findFarmostGlyphCoords();

            // autocenter camera
            demoView.centerOnRegion(mCamera, 300, glyphCoords[0], glyphCoords[3], glyphCoords[2], glyphCoords[1]);
            
        } catch(Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

    }

}
