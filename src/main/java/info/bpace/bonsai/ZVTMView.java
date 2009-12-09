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
import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JPanel;

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

    short translucentMode = 1;

    short viewType = 0;

    String vts;

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
    }

    public Component init() {
        vs = vsm.addVirtualSpace("mainSpace");
        Vector cameras = new Vector();
        mCamera = vs.addCamera();
        mCamera.setZoomFloor(-90);
        cameras.add(mCamera);

        JPanel tempPanel = new JPanel();
        JFrame tempFrame = new JFrame();

        demoView = vsm.addFrameView(cameras, "mainSpace", 100, 100, true, true, viewType, tempPanel, tempFrame);
        demoView.setBackgroundColor(Color.GRAY);
        demoView.setSize(400, 400);
        demoView.setResizable(true);

        return tempPanel.getComponent(0);
    }

    public void loadSvg(File svg) {
        String parser = XMLResourceDescriptor.getXMLParserClassName();
        SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);

        try {
            FileInputStream fileInput = new FileInputStream(svg);
            Document doc = f.createSVGDocument("out.svg", fileInput);
            SVGReader.load(doc, vs, false, "out.svg");
            vsm.repaintNow(demoView);
        } catch(Exception e) {
            System.out.println("Exception!");
        }


    }

}
