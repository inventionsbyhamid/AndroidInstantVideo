package com.chillingvan.instantvideo.sample.test.publisher.data;


/**
 * Created by inventionsbyhamid on 24/12/19.
 */

public class TextData extends Data {
    private String text, textAlign;
    private float textSize;
    private int textColor;
    private int x, y;
    private boolean bold, italic;
    private int charLimit = 50;

    public TextData(int position, String text, String textAlign, float textSize, int textColor, int x, int y, boolean bold, boolean italic, int charLimit) {
        super(position);
        this.text = text;
        this.textAlign = textAlign;
        this.textSize = textSize;
        this.textColor = textColor;
        this.x = x;
        this.y = y;
        this.bold = bold;
        this.italic = italic;
        this.charLimit = charLimit;
        preProcess();
    }

    public TextData(int position, String text, String textAlign, float textSize, int textColor, int x, int y, boolean bold, boolean italic) {
        super(position);
        this.text = text;
        this.textAlign = textAlign;
        this.textSize = textSize;
        this.textColor = textColor;
        this.x = x;
        this.y = y;
        this.bold = bold;
        this.italic = italic;
        preProcess();
    }

    private void preProcess() {
        if (this.text.length() > charLimit) {
            this.text = text.substring(0, charLimit) + "...";
        }
    }

    public String getText() {
        return text;
    }

    public String getTextAlign() {
        return textAlign;
    }

    public float getTextSize() {
        return textSize;
    }

    public int getTextColor() {
        return textColor;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isBold() {
        return bold;
    }

    public boolean isItalic() {
        return italic;
    }

    @Override
    public String getType() {
        return "TYPE_TEXT";
    }

    @Override
    public String toString() {
        return "TextData{" +
                "text='" + text + '\'' +
                ", textAlign='" + textAlign + '\'' +
                ", textSize=" + textSize +
                ", textColor=" + textColor +
                ", x=" + x +
                ", y=" + y +
                ", bold=" + bold +
                ", italic=" + italic +
                '}';
    }
}
