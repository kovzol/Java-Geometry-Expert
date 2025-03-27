package wprover;

import javax.swing.*;
import java.awt.*;

/**
 * DialogProperty is a class that represents a dialog for displaying properties.
 * It extends the JBaseDialog class and provides a constructor to initialize the dialog with a specified owner and panel.
 */
public class DialogProperty extends JBaseDialog
{
    /**
     * Constructs a DialogProperty with specified owner and panel.
     * @param owner the owner of the dialog
     * @param panel the panel to be displayed in the dialog
     */
      public DialogProperty(GExpert owner,JPanel panel)
      {
          super(owner.getFrame(),false);
          this.setTitle(GExpert.getLanguage("Properties"));
          this.setSize(370,310);
          getContentPane().add(panel);
          this.setBackground(Color.white);
      }
}
