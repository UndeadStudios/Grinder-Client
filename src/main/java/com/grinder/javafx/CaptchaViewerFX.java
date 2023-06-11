package com.grinder.javafx;

import com.grinder.Configuration;
import com.grinder.ui.ClientUI;
import com.runescape.Client;
import com.runescape.cache.graphics.sprite.Sprite;
import com.grinder.model.CaptchaViewer;
import com.grinder.client.ClientCompanion;
import com.grinder.client.util.Log;
import com.sun.javafx.application.PlatformImpl;
import javafx.concurrent.Worker;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 21/12/2019
 */
public class CaptchaViewerFX {
    public static BackgroundImage backgroundImage;

    public static void bind(CountDownLatch loaded) {

        final FutureTask<WebView> createEngineTask = new FutureTask<>(() -> {

            final WebView webView = new WebView();

            final WebEngine webEngine = webView.getEngine();

            webEngine.setUserStyleSheetLocation(CaptchaViewer.class.getResource("captcha.css").toString());

            final Worker<Void> loadWorker = webEngine.getLoadWorker();
            final boolean[] firstLoad = {true};

            loadWorker.stateProperty().addListener((obs, oldValue, newValue) -> {

                if (newValue == Worker.State.SUCCEEDED) {

                    if (firstLoad[0]) {
                        firstLoad[0] = false;
                        onLoaded(com.grinder.javafx.FXUtil.fxPanel, webView);
                        loaded.countDown();
                    } else {
                        SwingUtilities.invokeLater(() -> {
                            Client.instance.captchaRequired.set(false);
                            Client.instance.setLoginMessages(
                                    "Thanks for entering the captcha!",
                                    "You can try to login again :)"
                            );
                            ClientUI.frame.remove(com.grinder.javafx.FXUtil.fxPanel);
                            ClientUI.frame.repaint();
                        });
                    }
                }
            });
            loadWorker.exceptionProperty().addListener((observable, oldValue, newValue) ->
                    Log.error("CaptchaViewer load failure", newValue));
            configureEngine(webEngine);
            webEngine.load("http://"+ Configuration.connected_world.getAddress() +":8080/");
            return webView;
        });

        PlatformImpl.runAndWait(createEngineTask);

        try {
            loaded.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ClientUI.instance.captchaRequired.set(true);
        SwingUtilities.invokeLater(() -> {
            ClientUI.frame.add(com.grinder.javafx.FXUtil.fxPanel, BorderLayout.CENTER, 0);
            ClientUI.frame.revalidate();
            ClientUI.frame.repaint();
        });
    }

    private static void onLoaded(JFXPanel jfxPanel,  WebView webView) {
        try {
            Field f = webView.getEngine().getClass().getDeclaredField("page");
            f.setAccessible(true);
            com.sun.webkit.WebPage page = (com.sun.webkit.WebPage) f.get(webView.getEngine());
            page.setBackgroundColor((new java.awt.Color(0, 0, 0, 0)).getRGB());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        jfxPanel.setScene(createScene(webView));
    }

    private static void configureEngine(WebEngine webEngine){
        URI uri = URI.create("http://"+ Configuration.connected_world.getAddress() +":8080");
        Map<String, List<String>> headers = new LinkedHashMap<>();
        headers.put("Set-Cookie", Collections.singletonList("name=bla"));
        try {
            java.net.CookieHandler.getDefault().put(uri, headers);
        } catch (IOException e) {
            e.printStackTrace();
        }
        webEngine.setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_5) AppleWebKit/601.6.17 (KHTML, like Gecko) Version/9.1.1 Safari/601.6.17");
        webEngine.setJavaScriptEnabled(true);
    }

    private static Scene createScene(WebView browser) {

        final StackPane root = new StackPane();
        root.getChildren().add(browser);

        if(backgroundImage == null){
            root.setStyle("-fx-background-color: transparent;");
        } else {
            root.setBackground(new Background(backgroundImage));
        }

        final Scene scene = new Scene(root, 765, 503);
        scene.setFill(Color.TRANSPARENT);

        return scene;
    }

    public static void setBackgroundImage(Sprite backgroundSprite){
        backgroundImage = new BackgroundImage(FXUtil.toFxImage(backgroundSprite),
                BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);
    }
}
