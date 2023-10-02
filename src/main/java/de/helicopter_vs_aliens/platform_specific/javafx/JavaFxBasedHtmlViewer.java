package de.helicopter_vs_aliens.platform_specific.javafx;

import de.helicopter_vs_aliens.gui.window.AbstractHtmlViewer;
import de.helicopter_vs_aliens.util.geometry.Dimension;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;

import java.util.Objects;


public class JavaFxBasedHtmlViewer extends AbstractHtmlViewer<WebView>
{
    private final WebView
        webView;

    private final Dimension
        displayShift;

    private final double
        scalingFactor;


    public static JavaFxBasedHtmlViewer makeInstance(Dimension displayShift, double scalingFactor)
    {
        var htmlViewer = new JavaFxBasedHtmlViewer(displayShift, scalingFactor);
        htmlViewer.setWide();
        htmlViewer.hide();
        return htmlViewer;
    }

    private JavaFxBasedHtmlViewer(Dimension displayShift, double scalingFactor)
    {
        this.displayShift = displayShift;
        this.scalingFactor = scalingFactor;
        webView = getConfiguredWebView();
    }

    private WebView getConfiguredWebView()
    {
        WebView localWebView = new WebView();
        localWebView.setLayoutY(displayShift.getHeight() + scalingFactor*(VERTICAL_BOUNDS.y()));
        localWebView.setPrefHeight(scalingFactor * VERTICAL_BOUNDS.height());
        localWebView.setMouseTransparent(true);
        localWebView.setPageFill(Color.BLACK);

        localWebView.getEngine()
                    .setUserStyleSheetLocation(Objects.requireNonNull(getClass().getResource("/style.css"))
                                                      .toExternalForm());

        return localWebView;
    }

    @Override
    protected WebView getComponent()
    {
        return webView;
    }

    @Override
    public void setText(String htmlContent)
    {
        webView.getEngine()
               .loadContent(htmlContent);
    }

    @Override
    public void show()
    {
        webView.setVisible(true);
    }

    @Override
    public void hide()
    {
        webView.setVisible(false);
    }

    @Override
    protected void setHorizontalBounds(HorizontalBounds horizontalBounds)
    {
        webView.setLayoutX(scalingFactor * (displayShift.getWidth() + horizontalBounds.x()));
        webView.setPrefWidth(scalingFactor * horizontalBounds.width());
        AnchorPane.setLeftAnchor(webView, webView.getLayoutX());
    }
}