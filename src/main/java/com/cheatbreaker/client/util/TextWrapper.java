package com.cheatbreaker.client.util;

import com.google.common.base.Preconditions;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class TextWrapper {
    public Number maxWidth = 80;
    public Function<? super CharSequence, ? extends Number> lengthFunction = TextUtils.defaultLengthFunction;
    public Set<Character> softBreakChars = TextUtils.defaultSoftBreakChars;
    public String lineSeparator = "\n";
    public boolean hyphenate = true;
    public boolean breakLongWords = true;
    public Reader reader;
    public boolean closeReader;

    public TextWrapper setBreakLongWords(boolean flag) {
        this.breakLongWords = flag;
        return this;
    }

    public TextWrapper setSoftBreakCharSet(Set<Character> set) {
        this.softBreakChars = set;
        return this;
    }

    public TextWrapper setLineSeparator(String str) {
        this.lineSeparator = str;
        return this;
    }

    public TextWrapper setLengthFunction(Function<? super CharSequence, ? extends Number> function) {
        this.lengthFunction = function;
        return this;
    }

    public TextWrapper(Reader reader, boolean flag) {
        this.reader = reader;
        this.closeReader = flag;
    }

    public TextWrapper setMaxWidth(Number number) {
        Preconditions.checkArgument(number.doubleValue() > 0.0);
        this.maxWidth = number;
        return this;
    }

    public TextWrapper setSoftBreakChars(String str) {
        return this.setSoftBreakCharSet(TextUtils.toCharSet(str));
    }

    public void writeToFile(File file, Charset charset) {
        try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(file), charset)) {
            this.writeTo(outputStreamWriter);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeToUtf8File(File file) {
        this.writeToFile(file, StandardCharsets.UTF_8);
    }

    public void writeTo(Writer writer) {
        try {
            TextUtils.wrapToWriter(
                this.reader, writer, this.lineSeparator, this.maxWidth, this.lengthFunction, this.softBreakChars, this.hyphenate, this.breakLongWords
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (this.closeReader) {
                TextUtils.closeReader(this.reader);
            }
        }
    }

    public String wrapToString() {
        try (StringWriter stringWriter = new StringWriter()) {
            this.writeTo(stringWriter);
            return stringWriter.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeToPath(String str, Charset charset) {
        this.writeToFile(new File(str), charset);
    }

    public TextWrapper removeSoftBreakChars(String str) {
        Set toCharSet = TextUtils.toCharSet(str);
        this.softBreakChars.removeAll(toCharSet);
        return this;
    }

    public List<String> toLines() {
        final ArrayList list = new ArrayList();
        final StringBuilder sb = new StringBuilder();
        final boolean[] flags = new boolean[1];
        this.wrapTo(new LineSink() {
            @Override
            public void writeChars(char[] chars, int n, int n2) {
                flags[0] = true;
                sb.append(chars, n, n2);
            }

            @Override
            public void endLine() {
                list.add(sb.toString());
                sb.setLength(0);
                flags[0] = false;
            }
        });
        if (flags[0]) {
            list.add(sb.toString());
        }

        return list;
    }

    public TextWrapper addSoftBreakChars(String str) {
        Set toCharSet = TextUtils.toCharSet(str);
        this.softBreakChars.addAll(toCharSet);
        return this;
    }

    public void writeToUtf8Path(String str) {
        this.writeToUtf8File(new File(str));
    }

    public TextWrapper setHyphenate(boolean flag) {
        this.hyphenate = flag;
        return this;
    }

    public void wrapTo(LineSink lineSink) {
        try {
            TextUtils.wrapText(this.reader, lineSink, this.maxWidth, this.lengthFunction, this.softBreakChars, this.hyphenate, this.breakLongWords);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (this.closeReader) {
                TextUtils.closeReader(this.reader);
            }
        }
    }
}
