package com.galaxianexus.browser;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebContentRenderer {
    private String currentUrl;
    private String content;
    private List<String> renderedLines;
    private boolean isLoading;
    private String errorMessage;
    private int scrollOffset = 0;
    
    public WebContentRenderer() {
        this.renderedLines = new ArrayList<>();
        this.content = "";
        this.isLoading = false;
    }
    
    public void loadUrl(String url) {
        if (url == null || url.isEmpty() || url.equals("about:blank")) {
            this.content = "Welcome to GalaxiaNexus Browser!\n\n" +
                          "Enter a URL in the address bar above and press Enter or click 'Go'.\n\n" +
                          "Features:\n" +
                          "- Automatic UUID injection into all requests\n" +
                          "- Player avatar display\n" +
                          "- External browser support\n\n" +
                          "Use the 'External' button to open URLs in your system browser.";
            this.renderedLines = wrapText(this.content, 100);
            return;
        }
        
        this.currentUrl = url;
        this.isLoading = true;
        this.errorMessage = null;
        
        new Thread(() -> {
            try {
                URL urlObj = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                connection.setRequestProperty("User-Agent", "GalaxiaNexus-Browser/1.0");
                
                int responseCode = connection.getResponseCode();
                
                if (responseCode == 200) {
                    StringBuilder responseBuilder = new StringBuilder();
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            responseBuilder.append(line).append("\n");
                        }
                    }
                    
                    String rawContent = responseBuilder.toString();
                    this.content = parseHtmlContent(rawContent);
                    this.renderedLines = wrapText(this.content, 100);
                    
                } else {
                    this.errorMessage = "HTTP Error: " + responseCode + " - " + connection.getResponseMessage();
                    this.content = this.errorMessage;
                    this.renderedLines = wrapText(this.content, 100);
                }
                
            } catch (Exception e) {
                this.errorMessage = "Failed to load URL: " + e.getMessage();
                this.content = this.errorMessage;
                this.renderedLines = wrapText(this.content, 100);
                GalaxiaNexusBrowserMod.LOGGER.error("Failed to load URL: {}", url, e);
            } finally {
                this.isLoading = false;
            }
        }, "Web-Content-Loader").start();
    }
    
    private String parseHtmlContent(String html) {
        // Simple HTML parser - strips tags and extracts text
        StringBuilder text = new StringBuilder();
        
        // Remove script and style tags with their content
        html = html.replaceAll("(?is)<script[^>]*>.*?</script>", "");
        html = html.replaceAll("(?is)<style[^>]*>.*?</style>", "");
        
        // Extract title
        Pattern titlePattern = Pattern.compile("(?is)<title[^>]*>(.*?)</title>");
        Matcher titleMatcher = titlePattern.matcher(html);
        if (titleMatcher.find()) {
            text.append("=== ").append(titleMatcher.group(1).trim()).append(" ===\n\n");
        }
        
        // Remove HTML tags but preserve newlines for certain tags
        html = html.replaceAll("(?i)<br\\s*/?>", "\n");
        html = html.replaceAll("(?i)</p>", "\n\n");
        html = html.replaceAll("(?i)</div>", "\n");
        html = html.replaceAll("(?i)</h[1-6]>", "\n\n");
        html = html.replaceAll("(?i)<li[^>]*>", "\n• ");
        html = html.replaceAll("(?i)</li>", "");
        
        // Remove remaining HTML tags
        html = html.replaceAll("<[^>]+>", "");
        
        // Decode HTML entities
        html = html.replace("&nbsp;", " ");
        html = html.replace("&amp;", "&");
        html = html.replace("&lt;", "<");
        html = html.replace("&gt;", ">");
        html = html.replace("&quot;", "\"");
        html = html.replace("&#39;", "'");
        
        // Clean up whitespace
        html = html.replaceAll("(?m)^\\s+", ""); // Remove leading whitespace
        html = html.replaceAll("(?m)\\s+$", ""); // Remove trailing whitespace
        html = html.replaceAll("\n{3,}", "\n\n"); // Replace multiple newlines with double newline
        
        text.append(html.trim());
        
        return text.toString();
    }
    
    private List<String> wrapText(String text, int maxCharsPerLine) {
        List<String> lines = new ArrayList<>();
        String[] paragraphs = text.split("\n");
        
        for (String paragraph : paragraphs) {
            if (paragraph.isEmpty()) {
                lines.add("");
                continue;
            }
            
            String[] words = paragraph.split(" ");
            StringBuilder currentLine = new StringBuilder();
            
            for (String word : words) {
                if (currentLine.length() + word.length() + 1 > maxCharsPerLine) {
                    if (currentLine.length() > 0) {
                        lines.add(currentLine.toString());
                        currentLine = new StringBuilder();
                    }
                }
                
                if (currentLine.length() > 0) {
                    currentLine.append(" ");
                }
                currentLine.append(word);
            }
            
            if (currentLine.length() > 0) {
                lines.add(currentLine.toString());
            }
        }
        
        return lines;
    }
    
    public void render(DrawContext context, int x, int y, int width, int height) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        
        if (isLoading) {
            String loadingText = "Loading...";
            context.drawText(textRenderer, loadingText, x + 10, y + 10, 0xFFFFFF, true);
            return;
        }
        
        if (errorMessage != null) {
            context.drawText(textRenderer, "§c" + errorMessage, x + 10, y + 10, 0xFF5555, true);
            return;
        }
        
        // Render content with scrolling
        int lineHeight = textRenderer.fontHeight + 2;
        int maxVisibleLines = (height - 20) / lineHeight;
        
        int startLine = Math.max(0, scrollOffset);
        int endLine = Math.min(renderedLines.size(), startLine + maxVisibleLines);
        
        int currentY = y + 10;
        for (int i = startLine; i < endLine; i++) {
            String line = renderedLines.get(i);
            context.drawText(textRenderer, line, x + 10, currentY, 0xCCCCCC, false);
            currentY += lineHeight;
        }
        
        // Scrollbar indicator
        if (renderedLines.size() > maxVisibleLines) {
            int scrollbarHeight = Math.max(20, (maxVisibleLines * height) / renderedLines.size());
            int scrollbarY = y + ((scrollOffset * (height - scrollbarHeight)) / (renderedLines.size() - maxVisibleLines));
            context.fill(x + width - 5, scrollbarY, x + width - 2, scrollbarY + scrollbarHeight, 0x88FFFFFF);
        }
    }
    
    public void scroll(int amount) {
        int maxScroll = Math.max(0, renderedLines.size() - 20);
        scrollOffset = Math.max(0, Math.min(maxScroll, scrollOffset + amount));
    }
}
