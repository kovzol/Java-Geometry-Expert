package webapp;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;

public class JGEXWebApp implements EntryPoint {
    private int count = 0;

    @Override
    public void onModuleLoad() {
        // Main Layout Panel
        DockLayoutPanel rootPanel = new DockLayoutPanel(Style.Unit.PX);
        rootPanel.setSize("100%", "100%");

        // Menu Bar
        MenuBar menuBar = new MenuBar();
        MenuBar fileMenu = new MenuBar(true);  // "true" makes it a drop-down menu
        fileMenu.addStyleName("dropdown-menu"); // Add CSS class for styling
        fileMenu.addItem("Neew", (Command) () -> Window.alert("New File Clicked"));
        fileMenu.addItem("Open", (Command) () -> Window.alert("Open File Clicked"));
        fileMenu.addItem("Save", (Command) () -> Window.alert("Save File Clicked"));
        fileMenu.addItem("Exit", (Command) () -> Window.alert("Exit Clicked"));

        // Add the "File" menu with the sub-menu
        menuBar.addItem("File", fileMenu);

        menuBar.addItem("Examples", (Command) () -> Window.alert("Examples Clicked"));
        menuBar.addItem("Construct", (Command) () -> Window.alert("Construct Clicked"));
        menuBar.addItem("Constraint", (Command) () -> Window.alert("Constraint Clicked"));
        menuBar.addItem("Action", (Command) () -> Window.alert("Action Clicked"));
        menuBar.addItem("Prove", (Command) () -> Window.alert("Prove Clicked"));
        menuBar.addItem("Lemmas", (Command) () -> Window.alert("Lemmas Clicked"));
        menuBar.addItem("Options", (Command) () -> Window.alert("Options Clicked"));
        menuBar.addItem("Help", (Command) () -> Window.alert("Help Clicked"));

        // Wrap menu in a container
        SimplePanel menuContainer = new SimplePanel();
        menuContainer.setWidget(menuBar);
        menuContainer.setStyleName("menu-bar-border"); // Optional: Add custom styling

        rootPanel.addNorth(menuContainer, 30);

        // Central Content Panel
        VerticalPanel contentPanel = new VerticalPanel();
        contentPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        contentPanel.setSpacing(10);

        Label label = new Label("Count: 0");
        Button button = new Button("Click me!");
        Button resetButton = new Button("Reset count");

        resetButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                count = 0;
                label.setText("Count: " + count);
            }
        });

        button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                count++;
                label.setText("Count: " + count);
            }
        });

        contentPanel.add(button);
        contentPanel.add(label);
        contentPanel.add(resetButton);

        rootPanel.add(contentPanel);

        RootLayoutPanel.get().clear();
        RootLayoutPanel.get().add(rootPanel);
    }
}