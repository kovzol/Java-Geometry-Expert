package webapp.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import core.ui.GExpertUI;
import webapp.client.GExpertCore;
import webapp.client.tools.ToolManager;
import webapp.client.tools.Tool;

import java.util.ArrayList;
import java.util.List;

public class JGEXWebApp implements EntryPoint {
    private int count = 0;

    // UI abstraction and core components
    private GExpertUI ui;
    private GExpertCore core;
    private ToolManager toolManager;

    /**
     * Updates the styles of tool buttons to show which one is active.
     * @param activeButton The active button
     * @param otherButtons Other buttons to deactivate
     */
    private void updateToolButtonStyles(Button activeButton, Button... otherButtons) {
        // Set active button style
        activeButton.getElement().getStyle().setBackgroundColor("#CCCCCC");
        activeButton.getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);

        // Reset other buttons
        for (Button button : otherButtons) {
            button.getElement().getStyle().setBackgroundColor("");
            button.getElement().getStyle().setFontWeight(Style.FontWeight.NORMAL);
        }
    }

    /**
     * Creates a button with an icon for a tool.
     * @param toolId The tool ID
     * @param label The button label
     * @return The created button
     */
    private Button createToolButton(String toolId, String label) {
        Button button = new Button();
        Tool tool = toolManager.getTool(toolId);

        if (tool != null && tool.getIconPath() != null && !tool.getIconPath().isEmpty()) {
            // Build an <img> + label safely:
            String iconUrl = GWT.getModuleBaseURL() + tool.getIconPath();
            String safeLabel = SafeHtmlUtils.htmlEscape(label);
            String imgTag =
                    "<img src=\"" + iconUrl + "\""
                            + " width=\"16\" height=\"16\""
                            + " style=\"vertical-align:middle; margin-right:4px;\"/>";
            SafeHtml html = SafeHtmlUtils.fromTrustedString(imgTag + safeLabel);
            button.setHTML(html);
        } else {
            button.setText(label);
        }
        // Tooltip (if available)
        if (tool != null && tool.getDescription() != null) {
            button.setTitle(tool.getDescription());
        }

        return button;
    }

    @Override
    public void onModuleLoad() {
        // Initialize UI and core components
        ui = new GwtGExpertUI(this);
        core = new GExpertCore();
        core.setUI(ui);
        ((GwtGExpertUI)ui).setCore(core); // Set core on UI
        core.init(); // Initialize GExpertCore

        // Initialize tool manager
        toolManager = new ToolManager(core);

        // Main Layout Panel
        DockLayoutPanel rootPanel = new DockLayoutPanel(Style.Unit.PX);
        rootPanel.setSize("100%", "100%");

        // Menu Bar
        MenuBar menuBar = new MenuBar();
        MenuBar fileMenu = new MenuBar(true);  // drop-down menu
        fileMenu.getElement().getStyle().setBorderWidth(1, Style.Unit.PX);
        fileMenu.getElement().getStyle().setBorderStyle(Style.BorderStyle.SOLID);
        fileMenu.getElement().getStyle().setBorderColor("#CCCCCC");

        fileMenu.addItem("New", (Command) () -> Window.alert("New File Clicked"));
        fileMenu.addItem("Open", (Command) () -> {
            // In a GWT version using GExpertCore, we pass a dummy filename.
            core.loadFile("default.gex");
            Window.alert("Loaded file: default.gex");
        });
        fileMenu.addItem("Save", (Command) () -> {
            core.saveFile("default.gex");
            Window.alert("Saved file: default.gex");
        });
        fileMenu.addItem("Exit", (Command) () -> Window.alert("Exit Clicked"));
        fileMenu.addItem("Save As", (Command) () -> Window.alert("Save As Clicked"));

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
        menuContainer.getElement().getStyle().setBorderWidth(1, Style.Unit.PX);
        menuContainer.getElement().getStyle().setBorderStyle(Style.BorderStyle.SOLID);
        menuContainer.getElement().getStyle().setBorderColor("#CCCCCC");
        menuContainer.getElement().getStyle().setBackgroundColor("#F5F5F5");

        rootPanel.addNorth(menuContainer, 30);

        // Create a DockLayoutPanel for the content area
        DockLayoutPanel contentPanel = new DockLayoutPanel(Style.Unit.PX);
        contentPanel.setSize("100%", "100%");

        // Create toolbar
        HorizontalPanel toolbar = new HorizontalPanel();
        toolbar.setSpacing(5);
        toolbar.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        toolbar.getElement().getStyle().setPadding(5, Style.Unit.PX);
        toolbar.getElement().getStyle().setBackgroundColor("#F0F0F0");
        // Use CSS properties directly instead of GWT Style methods
        toolbar.getElement().getStyle().setProperty("borderBottomWidth", "1px");
        toolbar.getElement().getStyle().setProperty("borderBottomStyle", "solid");
        toolbar.getElement().getStyle().setProperty("borderBottomColor", "#CCCCCC");

        // Add tool buttons
        Button selectButton = createToolButton("select", "Select");
        Button pointButton = createToolButton("point", "Point");
        Button lineButton = createToolButton("line", "Line");
        Button parallelButton = createToolButton("parallel", "Parallel");
        Button perpButton = createToolButton("perp", "Perpendicular");
        Button footButton = createToolButton("foot", "Foot");
        Button circleButton = createToolButton("circle", "Circle");
        Button circle3pButton = createToolButton("circle3p", "Circle 3P");
        Button compassButton = createToolButton("circler", "Compass");
        Button angleButton = createToolButton("angle", "Angle");
        Button polygonButton = createToolButton("polygon", "Polygon");
        Button textButton = createToolButton("text", "Text");
        Button midpointButton = createToolButton("midpoint", "Midpoint");
        Button intersectButton = createToolButton("intersect", "Intersect");
        Button mirrorButton = createToolButton("mirror", "Mirror");
        Button isoscelesButton = createToolButton("iso", "Isosceles");
        Button squareButton = createToolButton("square", "Square");
        Button triangleButton = createToolButton("triangle", "Triangle");

        // Set button styles
        Style selectStyle = selectButton.getElement().getStyle();
        Style pointStyle = pointButton.getElement().getStyle();
        Style lineStyle = lineButton.getElement().getStyle();
        Style parallelStyle = parallelButton.getElement().getStyle();
        Style perpStyle = perpButton.getElement().getStyle();
        Style footStyle = footButton.getElement().getStyle();
        Style circleStyle = circleButton.getElement().getStyle();
        Style circle3pStyle = circle3pButton.getElement().getStyle();
        Style compassStyle = compassButton.getElement().getStyle();
        Style angleStyle = angleButton.getElement().getStyle();
        Style polygonStyle = polygonButton.getElement().getStyle();
        Style textStyle = textButton.getElement().getStyle();
        Style midpointStyle = midpointButton.getElement().getStyle();
        Style intersectStyle = intersectButton.getElement().getStyle();
        Style mirrorStyle = mirrorButton.getElement().getStyle();
        Style isoscelesStyle = isoscelesButton.getElement().getStyle();
        Style squareStyle = squareButton.getElement().getStyle();
        Style triangleStyle = triangleButton.getElement().getStyle();

        selectStyle.setMarginRight(5, Style.Unit.PX);
        pointStyle.setMarginRight(5, Style.Unit.PX);
        lineStyle.setMarginRight(5, Style.Unit.PX);
        parallelStyle.setMarginRight(5, Style.Unit.PX);
        perpStyle.setMarginRight(5, Style.Unit.PX);
        footStyle.setMarginRight(5, Style.Unit.PX);
        circleStyle.setMarginRight(5, Style.Unit.PX);
        circle3pStyle.setMarginRight(5, Style.Unit.PX);
        compassStyle.setMarginRight(5, Style.Unit.PX);
        angleStyle.setMarginRight(5, Style.Unit.PX);
        polygonStyle.setMarginRight(5, Style.Unit.PX);
        textStyle.setMarginRight(5, Style.Unit.PX);
        midpointStyle.setMarginRight(5, Style.Unit.PX);
        intersectStyle.setMarginRight(5, Style.Unit.PX);
        mirrorStyle.setMarginRight(5, Style.Unit.PX);
        isoscelesStyle.setMarginRight(5, Style.Unit.PX);
        squareStyle.setMarginRight(5, Style.Unit.PX);
        triangleStyle.setMarginRight(5, Style.Unit.PX);

        // Add click handlers for tool buttons
        selectButton.addClickHandler(event -> {
            toolManager.activateTool("select");
            updateToolButtonStyles(selectButton, pointButton, lineButton, parallelButton, perpButton, footButton, 
                                  circleButton, circle3pButton, compassButton, angleButton, polygonButton, textButton, midpointButton,
                                  intersectButton, mirrorButton, isoscelesButton, squareButton, triangleButton);
        });

        pointButton.addClickHandler(event -> {
            toolManager.activateTool("point");
            updateToolButtonStyles(pointButton, selectButton, lineButton, parallelButton, perpButton, footButton, 
                                  circleButton, circle3pButton, compassButton, angleButton, polygonButton, textButton, midpointButton,
                                  intersectButton, mirrorButton, isoscelesButton, squareButton, triangleButton);
        });

        lineButton.addClickHandler(event -> {
            toolManager.activateTool("line");
            updateToolButtonStyles(lineButton, selectButton, pointButton, parallelButton, perpButton, footButton, 
                                  circleButton, circle3pButton, compassButton, angleButton, polygonButton, textButton, midpointButton,
                                  intersectButton, mirrorButton, isoscelesButton, squareButton, triangleButton);
        });

        parallelButton.addClickHandler(event -> {
            toolManager.activateTool("parallel");
            updateToolButtonStyles(parallelButton, selectButton, pointButton, lineButton, perpButton, footButton, 
                                  circleButton, circle3pButton, compassButton, angleButton, polygonButton, textButton, midpointButton,
                                  intersectButton, mirrorButton, isoscelesButton, squareButton, triangleButton);
        });

        perpButton.addClickHandler(event -> {
            toolManager.activateTool("perp");
            updateToolButtonStyles(perpButton, selectButton, pointButton, lineButton, parallelButton, footButton, 
                                  circleButton, circle3pButton, compassButton, angleButton, polygonButton, textButton, midpointButton,
                                  intersectButton, mirrorButton, isoscelesButton, squareButton, triangleButton);
        });

        footButton.addClickHandler(event -> {
            toolManager.activateTool("foot");
            updateToolButtonStyles(footButton, selectButton, pointButton, lineButton, parallelButton, perpButton, 
                                  circleButton, circle3pButton, compassButton, angleButton, polygonButton, textButton, midpointButton,
                                  intersectButton, mirrorButton, isoscelesButton, squareButton, triangleButton);
        });

        circleButton.addClickHandler(event -> {
            toolManager.activateTool("circle");
            updateToolButtonStyles(circleButton, selectButton, pointButton, lineButton, parallelButton, perpButton, footButton, 
                                  circle3pButton, compassButton, angleButton, polygonButton, textButton, midpointButton,
                                  intersectButton, mirrorButton, isoscelesButton, squareButton, triangleButton);
        });

        circle3pButton.addClickHandler(event -> {
            toolManager.activateTool("circle3p");
            updateToolButtonStyles(circle3pButton, selectButton, pointButton, lineButton, parallelButton, perpButton, footButton, 
                                  circleButton, compassButton, angleButton, polygonButton, textButton, midpointButton,
                                  intersectButton, mirrorButton, isoscelesButton, squareButton, triangleButton);
        });

        compassButton.addClickHandler(event -> {
            toolManager.activateTool("circler");
            updateToolButtonStyles(compassButton, selectButton, pointButton, lineButton, parallelButton, perpButton, footButton, 
                                  circleButton, circle3pButton, angleButton, polygonButton, textButton, midpointButton,
                                  intersectButton, mirrorButton, isoscelesButton, squareButton, triangleButton);
        });

        angleButton.addClickHandler(event -> {
            toolManager.activateTool("angle");
            updateToolButtonStyles(angleButton, selectButton, pointButton, lineButton, parallelButton, perpButton, footButton, 
                                  circleButton, circle3pButton, compassButton, polygonButton, textButton, midpointButton,
                                  intersectButton, mirrorButton, isoscelesButton, squareButton, triangleButton);
        });

        polygonButton.addClickHandler(event -> {
            toolManager.activateTool("polygon");
            updateToolButtonStyles(polygonButton, selectButton, pointButton, lineButton, parallelButton, perpButton, footButton, 
                                  circleButton, circle3pButton, compassButton, angleButton, textButton, midpointButton,
                                  intersectButton, mirrorButton, isoscelesButton, squareButton, triangleButton);
        });

        textButton.addClickHandler(event -> {
            toolManager.activateTool("text");
            updateToolButtonStyles(textButton, selectButton, pointButton, lineButton, parallelButton, perpButton, footButton, 
                                  circleButton, circle3pButton, compassButton, angleButton, polygonButton, midpointButton,
                                  intersectButton, mirrorButton, isoscelesButton, squareButton, triangleButton);
        });

        midpointButton.addClickHandler(event -> {
            toolManager.activateTool("midpoint");
            updateToolButtonStyles(midpointButton, selectButton, pointButton, lineButton, parallelButton, perpButton, footButton, 
                                  circleButton, circle3pButton, compassButton, angleButton, polygonButton, textButton,
                                  intersectButton, mirrorButton, isoscelesButton, squareButton, triangleButton);
        });

        intersectButton.addClickHandler(event -> {
            toolManager.activateTool("intersect");
            updateToolButtonStyles(intersectButton, selectButton, pointButton, lineButton, parallelButton, perpButton, footButton, 
                                  circleButton, circle3pButton, compassButton, angleButton, polygonButton, textButton,
                                  midpointButton, mirrorButton, isoscelesButton, squareButton, triangleButton);
        });

        mirrorButton.addClickHandler(event -> {
            toolManager.activateTool("mirror");
            updateToolButtonStyles(mirrorButton, selectButton, pointButton, lineButton, parallelButton, perpButton, footButton, 
                                  circleButton, circle3pButton, compassButton, angleButton, polygonButton, textButton,
                                  midpointButton, intersectButton, isoscelesButton, squareButton, triangleButton);
        });

        isoscelesButton.addClickHandler(event -> {
            toolManager.activateTool("iso");
            updateToolButtonStyles(isoscelesButton, selectButton, pointButton, lineButton, parallelButton, perpButton, footButton, 
                                  circleButton, circle3pButton, compassButton, angleButton, polygonButton, textButton,
                                  midpointButton, intersectButton, mirrorButton, squareButton, triangleButton);
        });

        squareButton.addClickHandler(event -> {
            toolManager.activateTool("square");
            updateToolButtonStyles(squareButton, selectButton, pointButton, lineButton, parallelButton, perpButton, footButton, 
                                  circleButton, circle3pButton, compassButton, angleButton, polygonButton, textButton,
                                  midpointButton, intersectButton, mirrorButton, isoscelesButton, triangleButton);
        });

        triangleButton.addClickHandler(event -> {
            toolManager.activateTool("triangle");
            updateToolButtonStyles(triangleButton, selectButton, pointButton, lineButton, parallelButton, perpButton, footButton, 
                                  circleButton, circle3pButton, compassButton, angleButton, polygonButton, textButton,
                                  midpointButton, intersectButton, mirrorButton, isoscelesButton, squareButton);
        });

        // Add buttons to toolbar
        toolbar.add(selectButton);
        toolbar.add(pointButton);
        toolbar.add(lineButton);
        toolbar.add(parallelButton);
        toolbar.add(perpButton);
        toolbar.add(footButton);
        toolbar.add(circleButton);
        toolbar.add(circle3pButton);
        toolbar.add(compassButton);
        toolbar.add(angleButton);
        toolbar.add(polygonButton);
        toolbar.add(textButton);
        toolbar.add(midpointButton);
        toolbar.add(intersectButton);
        toolbar.add(mirrorButton);
        toolbar.add(isoscelesButton);
        toolbar.add(squareButton);
        toolbar.add(triangleButton);

        // Add toolbar to content panel
        contentPanel.addNorth(toolbar, 40);

        // Add drawing canvas
        SimplePanel canvasContainer = new SimplePanel();
        canvasContainer.setSize("100%", "100%");
        // Cast DrawingCanvas to Widget since GwtDrawingCanvas extends FocusPanel (which is a Widget)
        canvasContainer.setWidget((Widget) ui.getDrawingCanvas());
        contentPanel.add(canvasContainer);

        // Add content panel to root panel
        rootPanel.add(contentPanel);

        // Set initial tool
        toolManager.activateTool("select");
        updateToolButtonStyles(selectButton, pointButton, lineButton, parallelButton, perpButton, footButton, 
                              circleButton, circle3pButton, compassButton, angleButton, polygonButton, textButton, midpointButton,
                              intersectButton, mirrorButton, isoscelesButton, squareButton, triangleButton);

        RootLayoutPanel.get().clear();
        RootLayoutPanel.get().add(rootPanel);
    }
}
