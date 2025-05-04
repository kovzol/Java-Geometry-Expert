/**
 * $ $ License.
 *
 * Copyright $ L2FProd.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package UI;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.border.AbstractBorder;

/**
 * ButtonBorder. <br>
 */
public class ButtonBorder extends AbstractBorder
{

/**
       * Paints the border of the specified component.
       * Determines the state of the button (pressed, rollover, enabled) and calls the appropriate paint method.
       *
       * @param c the component for which this border is being painted
       * @param g the Graphics context in which to paint
       * @param x the x position of the painted border
       * @param y the y position of the painted border
       * @param width the width of the painted border
       * @param height the height of the painted border
       */
      public void paintBorder(Component c, Graphics g, int x, int y, int width,
                              int height)
      {
            if (c instanceof AbstractButton)
            {
                  AbstractButton b = (AbstractButton) c;
                  ButtonModel model = b.getModel();

                  boolean isPressed;
                  boolean isRollover;
                  boolean isEnabled;

                  isPressed = model.isPressed() && model.isArmed();
                  isRollover = b.isRolloverEnabled() && model.isRollover();
                  isEnabled = b.isEnabled();

                  if (!isEnabled)
                  {
                        paintDisabled(b, g, x, y, width, height);
                  } else
                  {
                        if (isPressed)
                        {
                              paintPressed(b, g, x, y, width, height);
                        } else if (isRollover)
                        {
                              paintRollover(b, g, x, y, width, height);
                        } else
                        {
                              paintNormal(b, g, x, y, width, height);
                        }
                  }
            }
      }

      /**
       * Paints the border for a normal (default) button state.
       *
       * @param b the button being painted
       * @param g the Graphics context in which to paint
       * @param x the x position of the painted border
       * @param y the y position of the painted border
       * @param width the width of the painted border
       * @param height the height of the painted border
       */
      protected void paintNormal(AbstractButton b, Graphics g, int x, int y,
                                 int width, int height)
      {
      }

      /**
       * Paints the border for a disabled button state.
       *
       * @param b the button being painted
       * @param g the Graphics context in which to paint
       * @param x the x position of the painted border
       * @param y the y position of the painted border
       * @param width the width of the painted border
       * @param height the height of the painted border
       */
      protected void paintDisabled(AbstractButton b, Graphics g, int x, int y,
                                   int width, int height)
      {
      }

      /**
       * Paints the border for a rollover button state.
       *
       * @param b the button being painted
       * @param g the Graphics context in which to paint
       * @param x the x position of the painted border
       * @param y the y position of the painted border
       * @param width the width of the painted border
       * @param height the height of the painted border
       */
      protected void paintRollover(AbstractButton b, Graphics g, int x, int y,
                                   int width, int height)
      {
      }

      /**
       * Paints the border for a pressed button state.
       *
       * @param b the button being painted
       * @param g the Graphics context in which to paint
       * @param x the x position of the painted border
       * @param y the y position of the painted border
       * @param width the width of the painted border
       * @param height the height of the painted border
       */
      protected void paintPressed(AbstractButton b, Graphics g, int x, int y,
                                  int width, int height)
      {
      }

      /**
       * Returns the insets of the border.
       *
       * @param c the component for which this border insets value applies
       * @return the insets of the border
       */
      public Insets getBorderInsets(Component c)
      {
            return getBorderInsets(c, new Insets(0, 0, 0, 0));
      }

      /**
       * Reinitializes the insets parameter with this border's current insets.
       *
       * @param c the component for which this border insets value applies
       * @param insets the object to be reinitialized
       * @return the insets of the border
       */
      public Insets getBorderInsets(Component c, Insets insets)
      {
            return insets;
      }
}