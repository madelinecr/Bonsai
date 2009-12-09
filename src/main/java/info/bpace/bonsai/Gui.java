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

import java.awt.event.*;
import java.io.File;
import javax.swing.*;
import org.swixml.*;
import fr.inria.zvtm.engine.*;

import java.util.Random;
import java.util.Vector;

/**
 *
 * @author Blaine Pace <blainepace at gmail.com>
 */
public class Gui extends WindowAdapter {
    private SwingEngine swix;
    private Tree mTree = new Tree();

    // Instantiated through swixml
    JTextField input;
    JTextArea console;

    JTabbedPane panelview;
    JFrame frameview;

    ZVTMView zvtm;

    public void start() throws Exception {
        File guiFile = new File( "src/main/resources/Gui.xml" );
        swix = new SwingEngine( this );
        swix.render( guiFile );

        zvtm = new ZVTMView();

        panelview.addTab("Render", zvtm.init());
        
        swix.getRootComponent().setVisible( true );
        
       // System.out.println(panelview.getSize());
    }

    public Action random = new AbstractAction() {
        @Override
        public void actionPerformed( ActionEvent e ) {
            Random rand = new Random();
            for(Integer i = 0; i < 1000; i++) {
                mTree.insert( rand.nextInt() % 3000 );
            }

            console.append("Added 1000 entries.\n");
        }
    };

    public Action submit = new AbstractAction() {
        @Override
        public void actionPerformed( ActionEvent e ) {
            String str = input.getText();
            int tempInt = Integer.parseInt(str);
            mTree.insert(tempInt);
            input.setText("");

            zvtm.loadSvg(mTree.graph());

        }
    };

    public Action traverse = new AbstractAction() {
        @Override
        public void actionPerformed( ActionEvent e ) {
            console.append(mTree.traverse() + "\n");
        }
    };

    public Action export = new AbstractAction() {
        @Override
        public void actionPerformed( ActionEvent e ) {
            mTree.graph();
        }
    };

}