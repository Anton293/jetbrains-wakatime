/* ==========================================================
File:        ApiKey.java
Description: Prompts user for Clockify API key if it does not exist.
Maintainer:  Clockify Integration
License:     BSD, see LICENSE for more details.
Website:     https://clockify.me/
===========================================================*/

package com.clockify.intellij.plugin;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;

public class ApiKey extends DialogWrapper {
    private final JPanel panel;
    private final JLabel label;
    private final JTextField input;
    private final LinkPane link;

    public ApiKey(@Nullable Project project) {
        super(project, true);
        setTitle("Clockify API Key");
        setOKButtonText("Save");
        panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1));
        label = new JLabel("Enter your Clockify API key:", JLabel.CENTER);
        panel.add(label);
        input = new JTextField(36);
        panel.add(input);
        link = new LinkPane("https://clockify.me/user/settings");
        panel.add(link);

        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return panel;
    }

    @Override
    protected ValidationInfo doValidate() {
        String apiKey = input.getText().trim();
        if (apiKey.isEmpty()) {
            return new ValidationInfo("API key cannot be empty.");
        }
        if (!isValidApiKey(apiKey)) {
            return new ValidationInfo("Invalid Clockify API key.");
        }
        return null;
    }

    private boolean isValidApiKey(String apiKey) {
        // A simple check for API key format. Clockify API keys are typically 36 characters long.
        return apiKey.length() == 36;
    }

    @Override
    public void doOKAction() {
        ConfigFile.setApiKey(input.getText().trim());
        super.doOKAction();
    }

    @Override
    public void doCancelAction() {
        ClockifyIntegration.cancelApiKey = true;
        super.doCancelAction();
    }

    public String promptForApiKey() {
        input.setText(ConfigFile.getApiKey());
        this.show();
        return input.getText().trim();
    }
}

class LinkPane extends JTextPane {
    private final String url;

    public LinkPane(String url) {
        this.url = url;
        this.setEditable(false);
        this.addHyperlinkListener(new UrlHyperlinkListener());
        this.setContentType("text/html");
        this.setBackground(new Color(0, 0, 0, 0));
        this.setText("Visit Clockify Settings to get your API key");
    }

    private class UrlHyperlinkListener implements javax.swing.event.HyperlinkListener {
        @Override
        public void hyperlinkUpdate(final javax.swing.event.HyperlinkEvent event) {
            if (event.getEventType() == javax.swing.event.HyperlinkEvent.EventType.ACTIVATED) {
                try {
                    Desktop.getDesktop().browse(event.getURL().toURI());
                } catch (final IOException | URISyntaxException e) {
                    throw new RuntimeException("Can't open URL", e);
                }
            }
        }
    }

    @Override
    public void setText(final String text) {
        super.setText("<html><body style=\"text-align:center;\"><a href=\"" + url + "\">" + text + "</a></body></html>");
    }
}
