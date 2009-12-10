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
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

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
    Portal mPortal;

    short translucentMode = 1;

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
    }

    public Component init() {
        vs = vsm.addVirtualSpace(spaceName);
        Vector cameras = new Vector();
        mCamera = vs.addCamera();
        mCamera.setZoomFloor(-90);
        cameras.add(mCamera);

        JPanel tempPanel = vsm.addPanelView(cameras, spaceName, 400, 400);
        demoView = vsm.getView(spaceName);
        //demoView = vsm.addFrameView(cameras, "mainSpace", 400, 400, true, true, viewType, tempPanel, tempFrame);
        demoView.setBackgroundColor(Color.WHITE);

        return tempPanel;
    }

    public void loadSvg(File svg) {
        String parser = XMLResourceDescriptor.getXMLParserClassName();
        SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);

        FileInputStream fileInput;
        Document doc;
        String width;
        String height;

        try {
            fileInput = new FileInputStream(svg);
            doc = f.createSVGDocument( "out.svg", fileInput );
            SVGReader.load(doc, vs, false, "out.svg");
            vsm.repaintNow(demoView);

            // pull svg width and height from dom
            NodeList nodelist = doc.getElementsByTagName("svg");
            NamedNodeMap nodemap = nodelist.item(0).getAttributes();
            width = nodemap.getNamedItem("width").getNodeValue();
            height = nodemap.getNamedItem("height").getNodeValue();

            // strip pt from width and height
            int intwidth = Integer.parseInt(
                    width.substring(0, width.length() - 2));
            int intheight = Integer.parseInt(
                    height.substring(0, height.length() - 2));

            mCamera.moveTo(intwidth / 2, intheight / 2);

            System.out.println(mCamera.getFocal());
            System.out.println(mCamera.getAltitude());
          //  double fov = 2 * Math.atan(400/(2 * mCamera.getFocal()));
          //  System.out.println("fov: " + fov);


           // double distance = Math.sqrt( Math.pow( ((intwidth/2)/Math.sin(fov/2)) * (Math.sin(90)), 2 ) - Math.pow((intwidth/2), 2) );
           // double distance = Math.sqrt( Math.pow( ((intwidth/2)/Math.sin(fov/2)) * (Math.sin(90)), 2 ) - Math.pow((intwidth/2), 2) );

           // float dist = (float)distance;
           // mCamera.setAltitude(vs.);
            
        } catch(Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

    }

}
