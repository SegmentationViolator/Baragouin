package segv;

import java.util.NoSuchElementException;
import java.util.PrimitiveIterator;

public class Tokenizer {
    PrimitiveIterator.OfInt source;
    Integer currentCodePoint = null;
    Integer lastCodePoint;

    public Tokenizer(String text) {
        this.source = text.codePoints().iterator();
        nextCodePoint();
    }

    private void nextCodePoint() {
        this.lastCodePoint = this.currentCodePoint;

        try {
            this.currentCodePoint = this.source.next();
        } catch (NoSuchElementException e) {
            this.currentCodePoint = null;
        }
    }

    public String nextToken() {
        if (this.currentCodePoint == null)
            return null;

        if (Character.isWhitespace(this.currentCodePoint) || Character.isISOControl(this.currentCodePoint)) {
            while (Character.isWhitespace(this.currentCodePoint) || Character.isISOControl(this.currentCodePoint)) {
                nextCodePoint();
                if (this.currentCodePoint == null)
                    return null;
            }
        }

        if (Character.isLetter(this.currentCodePoint)) {
            var tokenBuilder = new StringBuilder();

            tokenBuilder.appendCodePoint(this.currentCodePoint);
            nextCodePoint();

            while (this.currentCodePoint != null && Character.isLetter(this.currentCodePoint)) {
                tokenBuilder.appendCodePoint(this.currentCodePoint);
                nextCodePoint();
            }

            return tokenBuilder.toString();
        }
        
        if (Character.isDigit(this.currentCodePoint)) {
            var tokenBuilder = new StringBuilder();

            tokenBuilder.appendCodePoint(this.currentCodePoint);
            nextCodePoint();

            while (this.currentCodePoint != null && Character.isDigit(this.currentCodePoint)) {
                tokenBuilder.appendCodePoint(this.currentCodePoint);
                nextCodePoint();
            }

            return tokenBuilder.toString();
        }

        nextCodePoint();
        return Character.toString(this.lastCodePoint);
    }
}
