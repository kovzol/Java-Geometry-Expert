/*
 * $Id: BoundingBox.java,v 1.2 2007/08/26 18:56:35 gil1 Exp $
 *
 * $Date: 2007/08/26 18:56:35 $
 *
 * Copyright (c) Eric Z. Beard, ericzbeard@hotmail.com 
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 */
package pdf;

import java.awt.*;
import java.util.*;

/**
 * This class represents a bounding box.
 * It holds coordinate information and properties used for graphical operations.
 */
public class BoundingBox extends Rectangle
{
  /** Percent f line height to space lines */
  public static final int LINE_SPACING_PERCENTAGE = 20;

  /** Used to a align a String centered vertically */
  public static final int VERT_ALIGN_CENTER  = 0;

  /** Used to align a String at the top of the box */
  public static final int VERT_ALIGN_TOP     = 1;

  /** Used to align a String at the bottom of the box */
  public static final int VERT_ALIGN_BOTTOM  = 2;

  /** Used to align a String horizontally in the center of the box */
  public static final int HORIZ_ALIGN_CENTER = 3;

  /** Used to align a String to the left in the box */
  public static final int HORIZ_ALIGN_LEFT   = 4;

  /** Used to aling a String to the right in a box */
  public static final int HORIZ_ALIGN_RIGHT  = 5;

  /** Used to subtract a child from a box, *leaving* the top portion */
  public static final int SUBTRACT_FROM_TOP    = 6;

  /** Used to subtract a child from a box, *leaving* the bottom portion */
  public static final int SUBTRACT_FROM_BOTTOM = 7;

  /** Used to subtract a child from a box, *leaving* the left portion */
  public static final int SUBTRACT_FROM_LEFT   = 8;

  /** Used to subtract a child from a box, *leaving" the right portion */
  public static final int SUBTRACT_FROM_RIGHT  = 9;

  private static final int[] VERT_ALIGNS = {VERT_ALIGN_CENTER,
                                            VERT_ALIGN_TOP,
                                            VERT_ALIGN_BOTTOM};

  private static final int[] HORIZ_ALIGNS = {HORIZ_ALIGN_CENTER,
                                             HORIZ_ALIGN_LEFT,
                                             HORIZ_ALIGN_RIGHT};

  private static final int[] SUBTRACTS = {SUBTRACT_FROM_TOP,
                                          SUBTRACT_FROM_BOTTOM,
                                          SUBTRACT_FROM_LEFT,
                                          SUBTRACT_FROM_RIGHT};

  /** The point to use for Graphics.drawString() */
  private Point drawingPoint;

  /** The absolute, world location of the box */
  private Point absoluteLocation;

  /** Link to parent box */
  private BoundingBox parent;

  /**
   * <p>Returns true if this box has a parent.  The 'world', or 
   * enclosing canvas is not considered a parent</p>
   *
   * @return a <code>boolean</code> value
   */
  public boolean hasParent() {
	  return parent != null;
  }

  /**
   * <p>Get this box's parent box</p>
   *
   * @return a <code>BoundingBox</code> value
   */
  public BoundingBox getParent() {
    return parent;
  }



  /**
   * <p>Make the specified box this box's child.  Equivalent to 
   * <code>child.setParent(parent)</code> where the specified 'parent' is 
   * this instance</p>
   *
   * @param child a <code>BoundingBox</code>, any box that can fit inside 
   *              this one.  The results of calling 
   *              <code>getAbsoluteLocation()</code> on the child will be 
   *              altered after this to take into account the child's 
   *              new location in the 'world'
   *
   */
  public void add(BoundingBox child) {
    child.setParent(this);
  }



  /**
   * <p>Make the specified box this box's parent</p>
   *
   * @param parent a <code>BoundingBox</code> value
   */
  public void setParent(BoundingBox parent) {
    // Prevent infinite recursion
    if (this == parent) {
      return;
    }
    this.parent = parent;

    // If this box was created empty, without a String inside,
    // determine its absolute location
    if (this.getLocation().equals(this.getAbsoluteLocation())) {
      int ancestorTranslateX = 0;
      int ancestorTranslateY = 0;

      BoundingBox ancestor = this;
      while (ancestor.hasParent()) {
        BoundingBox oldRef = ancestor;
        ancestor = ancestor.getParent();
        // Prevent infinite recursion
        if (ancestor == oldRef) {
          break;
        }
        ancestorTranslateX += (int)ancestor.getLocation().getX();
        ancestorTranslateY += (int)ancestor.getLocation().getY();
      }

      this.getAbsoluteLocation().translate(ancestorTranslateX, 
                                           ancestorTranslateY);
    } // end if
  } // end setParent

  /**
   * <p>Get the absolute upper left location point for this box</p>
   *
   * @return a <code>Point</code> value
   */
  public Point getAbsoluteLocation() {
    return absoluteLocation;
  }
} // end class BoundingBox




