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
import fr.inria.zvtm.glyphs.Glyph;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 *
 * @author Blaine Pace <blainepace at gmail.com>
 */
public class ZVTMEventHandler extends DefaultEventHandler
{
    VirtualSpaceManager vsm = VirtualSpaceManager.INSTANCE;
    
    // wheel speed factor
    static final float WSF = 5f;
    // rate-based scrolling speed factor
    static final float RBSSF = 50;

    int lastJPX, lastJPY;

    @Override
    public void mouseWheelMoved(ViewPanel v,short wheelDirection,int jpx,int jpy, MouseWheelEvent e)
    {
        Camera c = VirtualSpaceManager.INSTANCE.getActiveCamera();
        float a = (c.focal + Math.abs(c.altitude)) / c.focal;

        if (wheelDirection == WHEEL_UP)
        {
            c.altitudeOffset(a*WSF);
            VirtualSpaceManager.INSTANCE.repaintNow();
        }
        else
        {
            c.altitudeOffset(-a*WSF); VirtualSpaceManager.INSTANCE.repaintNow();
        }
    }

    @Override
    public void press1(ViewPanel v, int mod, int jpx, int jpy, MouseEvent e)
    {
        lastJPX = jpx;
        lastJPY = jpy;
        v.setDrawDrag(true);
    }

    @Override
    public void release1(ViewPanel v, int mod, int jpx, int jpy, MouseEvent e)
    {
        vsm.getAnimationManager().setXspeed(0);
        vsm.getAnimationManager().setYspeed(0);
        vsm.getAnimationManager().setZspeed(0);
        v.setDrawDrag(false);
    }

    @Override
    public void mouseDragged(ViewPanel v, int mod, int buttonNumber, int jpx, int jpy, MouseEvent e)
    {
        if(buttonNumber == 1)
        {
            Camera mCamera = vsm.getActiveCamera();
            float a = (mCamera.focal + Math.abs(mCamera.altitude)) / mCamera.focal;

            vsm.getAnimationManager().setXspeed(
                    (mCamera.altitude>0) ? (long) ((jpx-lastJPX)*(a/RBSSF))
                                         : (long) ((jpx-lastJPX)/(a*RBSSF)));

            vsm.getAnimationManager().setYspeed(
                    (mCamera.altitude>0) ? (long) ((lastJPY-jpy)*(a/RBSSF))
                                         : (long) ((lastJPY-jpy)/(a*RBSSF)));

            vsm.getAnimationManager().setZspeed(0);

        }
    }

    @Override
    public void enterGlyph(Glyph g)
    {
    }
}
