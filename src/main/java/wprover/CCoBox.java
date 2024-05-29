package wprover;

import javax.swing.*;
import java.util.Vector;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2005-3-4
 * Time: 14:25:06
 * To change this template use File | Settings | File Templates.
 * This class represents a custom JComboBox for color selection.
 */
public class CCoBox extends JComboBox {
    private static Vector instanceList = new Vector();
    int defaultindex = 0;

    /**
     * Creates an instance of CCoBox with color options.
     *
     * @return the created CCoBox instance
     */
    public static CCoBox CreateAInstance() {
        Integer[] intArray = new Integer[DrawData.getColorCounter() + 1];
        for (int i = 0; i <= DrawData.getColorCounter(); i++) {
            intArray[i] = i;
        }
        CCoBox cb = new CCoBox(intArray);

        cb.setMaximumRowCount(30);
        cb.setPreferredSize(new Dimension(40, 20));
        ColorComboRender render = new ColorComboRender(0, 100, 20);
        render.setPreferredSize(new Dimension(40, 20));
        cb.setRenderer(render);
        instanceList.add(cb);
        return cb;
    }

    /**
     * Constructs a CCoBox with the specified items.
     *
     * @param items the items to display in the combo box
     */
    private CCoBox(final Object items[]) {
        super(items);
    }

    /**
     * Sets the selected index of the combo box.
     *
     * @param index the index to select
     */
    public void setSelectedIndex(int index) {
        ((ColorComboRender) super.getRenderer()).index = index;
        super.setSelectedIndex(index);
    }

    /**
     * Sets the default selected index of the combo box.
     *
     * @param index the default index
     */
    public void setDefaultIndex(int index) {
        defaultindex = index;
    }

    /**
     * Regenerates all instances of CCoBox.
     */
    public static void reGenerateAll() {
        for (int i = 0; i < instanceList.size(); i++) {
            CCoBox cb = (CCoBox) instanceList.get(i);
            int co = DrawData.getColorCounter();
            int n = cb.getItemCount();

            if (co >= n)
                for (int j = n; j <= co; j++) {
                    cb.addItem(j);
                }
        }
    }

    /**
     * Resets all instances of CCoBox.
     */
    public static void resetAll() {
        DrawData.reset();

        for (int i = 0; i < instanceList.size(); i++) {
            CCoBox cb = (CCoBox) instanceList.get(i);
            cb.setSelectedIndex(cb.defaultindex);
            int num = DrawData.getColorCounter();
            for (int j = num + 1; j < cb.getItemCount(); j++)
                cb.removeItemAt(j);
        }
    }
}
