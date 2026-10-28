package optifine.json;

import com.cheatbreaker.client.util.SneakyThrow;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class JSONParser {
    public static final int S_INIT = 0;
    public static final int S_IN_FINISHED_VALUE = 1;
    public static final int S_IN_OBJECT = 2;
    public static final int S_IN_ARRAY = 3;
    public static final int S_PASSED_PAIR_KEY = 4;
    public static final int S_IN_PAIR_VALUE = 5;
    public static final int S_END = 6;
    public static final int S_IN_ERROR = -1;
    public LinkedList handlerStatusStack;
    public Yylex lexer = new Yylex((Reader)null);
    public Yytoken token = null;
    public int status = 0;

    public int peekStatus(LinkedList linkedList) {
        if (linkedList.size() == 0) {
            return -1;
        }

        Integer first = (Integer)linkedList.getFirst();
        return first;
    }

    public void reset() {
        this.token = null;
        this.status = 0;
        this.handlerStatusStack = null;
    }

    public void reset(Reader reader) {
        this.lexer.yyreset(reader);
        this.reset();
    }

    public int getPosition() {
        return this.lexer.getPosition();
    }

    public Object parse(String str) throws ParseException {
        return this.parse(str, (ContainerFactory)null);
    }

    public Object parse(String str, ContainerFactory containerFactory) throws ParseException {
        StringReader stringReader = new StringReader(str);

        try {
            return this.parse(stringReader, containerFactory);
        } catch (IOException e) {
            throw new ParseException(-1, 2, e);
        }
    }

    public Object parse(Reader reader) throws IOException, ParseException {
        return this.parse(reader, (ContainerFactory)null);
    }

    public Object parse(Reader reader, ContainerFactory containerFactory) throws IOException, ParseException {
        this.reset(reader);
        LinkedList linkedList = new LinkedList();
        LinkedList linkedList2 = new LinkedList();

        try {
            do {
                this.nextToken();
                label61:
                switch (this.status) {
                    case -1:
                        throw new ParseException(this.getPosition(), 1, this.token);
                    case 0:
                        switch (this.token.type) {
                            case 0:
                                this.status = 1;
                                linkedList.addFirst(new Integer(this.status));
                                linkedList2.addFirst(this.token.value);
                                break label61;
                            case 1:
                                this.status = 2;
                                linkedList.addFirst(new Integer(this.status));
                                linkedList2.addFirst(this.createObjectContainer(containerFactory));
                                break label61;
                            case 2:
                            default:
                                this.status = -1;
                                break label61;
                            case 3:
                                this.status = 3;
                                linkedList.addFirst(new Integer(this.status));
                                linkedList2.addFirst(this.createArrayContainer(containerFactory));
                                break label61;
                        }
                    case 1:
                        if (this.token.type == -1) {
                            return linkedList2.removeFirst();
                        }

                        throw new ParseException(this.getPosition(), 1, this.token);
                    case 2:
                        switch (this.token.type) {
                            case 0:
                                if (this.token.value instanceof String) {
                                    String value = (String)this.token.value;
                                    linkedList2.addFirst(value);
                                    this.status = 4;
                                    linkedList.addFirst(new Integer(this.status));
                                } else {
                                    this.status = -1;
                                }
                                break label61;
                            case 1:
                            case 3:
                            case 4:
                            default:
                                this.status = -1;
                                break label61;
                            case 2:
                                if (linkedList2.size() > 1) {
                                    linkedList.removeFirst();
                                    linkedList2.removeFirst();
                                    this.status = this.peekStatus(linkedList);
                                } else {
                                    this.status = 1;
                                }
                            case 5:
                                break label61;
                        }
                    case 3:
                        switch (this.token.type) {
                            case 0:
                                List first = (List)linkedList2.getFirst();
                                first.add(this.token.value);
                                break label61;
                            case 1:
                                List first2 = (List)linkedList2.getFirst();
                                Map createObjectContainer = this.createObjectContainer(containerFactory);
                                first2.add(createObjectContainer);
                                this.status = 2;
                                linkedList.addFirst(new Integer(this.status));
                                linkedList2.addFirst(createObjectContainer);
                                break label61;
                            case 2:
                            default:
                                this.status = -1;
                                break label61;
                            case 3:
                                List first3 = (List)linkedList2.getFirst();
                                List createArrayContainer = this.createArrayContainer(containerFactory);
                                first3.add(createArrayContainer);
                                this.status = 3;
                                linkedList.addFirst(new Integer(this.status));
                                linkedList2.addFirst(createArrayContainer);
                                break label61;
                            case 4:
                                if (linkedList2.size() > 1) {
                                    linkedList.removeFirst();
                                    linkedList2.removeFirst();
                                    this.status = this.peekStatus(linkedList);
                                } else {
                                    this.status = 1;
                                }
                            case 5:
                                break label61;
                        }
                    case 4:
                        switch (this.token.type) {
                            case 0:
                                linkedList.removeFirst();
                                String removeFirst = (String)linkedList2.removeFirst();
                                Map first = (Map)linkedList2.getFirst();
                                first.put(removeFirst, this.token.value);
                                this.status = this.peekStatus(linkedList);
                                break;
                            case 1:
                                linkedList.removeFirst();
                                String removeFirst2 = (String)linkedList2.removeFirst();
                                Map first2 = (Map)linkedList2.getFirst();
                                Map createObjectContainer = this.createObjectContainer(containerFactory);
                                first2.put(removeFirst2, createObjectContainer);
                                this.status = 2;
                                linkedList.addFirst(new Integer(this.status));
                                linkedList2.addFirst(createObjectContainer);
                                break;
                            case 2:
                            case 4:
                            case 5:
                            default:
                                this.status = -1;
                                break;
                            case 3:
                                linkedList.removeFirst();
                                String removeFirst3 = (String)linkedList2.removeFirst();
                                Map first3 = (Map)linkedList2.getFirst();
                                List createArrayContainer = this.createArrayContainer(containerFactory);
                                first3.put(removeFirst3, createArrayContainer);
                                this.status = 3;
                                linkedList.addFirst(new Integer(this.status));
                                linkedList2.addFirst(createArrayContainer);
                            case 6:
                        }
                }

                if (this.status == -1) {
                    throw new ParseException(this.getPosition(), 1, this.token);
                }
            } while (this.token.type != -1);
        } catch (IOException e) {
            throw SneakyThrow.sneakyThrow(e);
        }

        throw new ParseException(this.getPosition(), 1, this.token);
    }

    public void nextToken() throws IOException, ParseException {
        this.token = this.lexer.yylex();
        if (this.token == null) {
            this.token = new Yytoken(-1, null);
        }
    }

    public Map createObjectContainer(ContainerFactory containerFactory) {
        if (containerFactory == null) {
            return new JSONObject();
        }

        Map createObjectContainer = containerFactory.createObjectContainer();
        return createObjectContainer == null ? new JSONObject() : createObjectContainer;
    }

    public List createArrayContainer(ContainerFactory containerFactory) {
        if (containerFactory == null) {
            return new JSONArray();
        }

        List creatArrayContainer = containerFactory.creatArrayContainer();
        return creatArrayContainer == null ? new JSONArray() : creatArrayContainer;
    }

    public void parse(String str, ContentHandler contentHandler) throws ParseException {
        this.parse(str, contentHandler, false);
    }

    public void parse(String str, ContentHandler contentHandler, boolean flag) throws ParseException {
        StringReader stringReader = new StringReader(str);

        try {
            this.parse(stringReader, contentHandler, flag);
        } catch (IOException e) {
            throw new ParseException(-1, 2, e);
        }
    }

    public void parse(Reader reader, ContentHandler contentHandler) throws IOException, ParseException {
        this.parse(reader, contentHandler, false);
    }

    public void parse(Reader reader, ContentHandler contentHandler, boolean flag) throws IOException, ParseException {
        if (!flag) {
            this.reset(reader);
            this.handlerStatusStack = new LinkedList();
        } else if (this.handlerStatusStack == null) {
            flag = false;
            this.reset(reader);
            this.handlerStatusStack = new LinkedList();
        }

        LinkedList handlerStatusStack = this.handlerStatusStack;

        try {
            do {
                label168:
                switch (this.status) {
                    case -1:
                        throw new ParseException(this.getPosition(), 1, this.token);
                    case 0:
                        contentHandler.startJSON();
                        this.nextToken();
                        switch (this.token.type) {
                            case 0:
                                this.status = 1;
                                handlerStatusStack.addFirst(new Integer(this.status));
                                if (!contentHandler.primitive(this.token.value)) {
                                    return;
                                }
                                break label168;
                            case 1:
                                this.status = 2;
                                handlerStatusStack.addFirst(new Integer(this.status));
                                if (!contentHandler.check3()) {
                                    return;
                                }
                                break label168;
                            case 2:
                            default:
                                this.status = -1;
                                break label168;
                            case 3:
                                this.status = 3;
                                handlerStatusStack.addFirst(new Integer(this.status));
                                if (!contentHandler.check2()) {
                                    return;
                                }
                                break label168;
                        }
                    case 1:
                        this.nextToken();
                        if (this.token.type == -1) {
                            contentHandler.endJSON();
                            this.status = 6;
                            return;
                        }

                        this.status = -1;
                        throw new ParseException(this.getPosition(), 1, this.token);
                    case 2:
                        this.nextToken();
                        switch (this.token.type) {
                            case 0:
                                if (this.token.value instanceof String) {
                                    String value = (String)this.token.value;
                                    this.status = 4;
                                    handlerStatusStack.addFirst(new Integer(this.status));
                                    if (!contentHandler.startObjectEntry(value)) {
                                        return;
                                    }
                                } else {
                                    this.status = -1;
                                }
                                break label168;
                            case 1:
                            case 3:
                            case 4:
                            default:
                                this.status = -1;
                                break label168;
                            case 2:
                                if (handlerStatusStack.size() > 1) {
                                    handlerStatusStack.removeFirst();
                                    this.status = this.peekStatus(handlerStatusStack);
                                } else {
                                    this.status = 1;
                                }

                                if (!contentHandler.endObject()) {
                                    return;
                                }
                            case 5:
                                break label168;
                        }
                    case 3:
                        this.nextToken();
                        switch (this.token.type) {
                            case 0:
                                if (!contentHandler.primitive(this.token.value)) {
                                    return;
                                }
                                break label168;
                            case 1:
                                this.status = 2;
                                handlerStatusStack.addFirst(new Integer(this.status));
                                if (!contentHandler.check3()) {
                                    return;
                                }
                                break label168;
                            case 2:
                            default:
                                this.status = -1;
                                break label168;
                            case 3:
                                this.status = 3;
                                handlerStatusStack.addFirst(new Integer(this.status));
                                if (!contentHandler.check2()) {
                                    return;
                                }
                                break label168;
                            case 4:
                                if (handlerStatusStack.size() > 1) {
                                    handlerStatusStack.removeFirst();
                                    this.status = this.peekStatus(handlerStatusStack);
                                } else {
                                    this.status = 1;
                                }

                                if (!contentHandler.endObjectEntry()) {
                                    return;
                                }
                            case 5:
                                break label168;
                        }
                    case 4:
                        this.nextToken();
                        switch (this.token.type) {
                            case 0:
                                handlerStatusStack.removeFirst();
                                this.status = this.peekStatus(handlerStatusStack);
                                if (!contentHandler.primitive(this.token.value)) {
                                    return;
                                }

                                if (!contentHandler.check()) {
                                    return;
                                }
                                break label168;
                            case 1:
                                handlerStatusStack.removeFirst();
                                handlerStatusStack.addFirst(new Integer(5));
                                this.status = 2;
                                handlerStatusStack.addFirst(new Integer(this.status));
                                if (!contentHandler.check3()) {
                                    return;
                                }
                                break label168;
                            case 2:
                            case 4:
                            case 5:
                            default:
                                this.status = -1;
                                break label168;
                            case 3:
                                handlerStatusStack.removeFirst();
                                handlerStatusStack.addFirst(new Integer(5));
                                this.status = 3;
                                handlerStatusStack.addFirst(new Integer(this.status));
                                if (!contentHandler.check2()) {
                                    return;
                                }
                            case 6:
                                break label168;
                        }
                    case 5:
                        handlerStatusStack.removeFirst();
                        this.status = this.peekStatus(handlerStatusStack);
                        if (!contentHandler.check()) {
                            return;
                        }
                        break;
                    case 6:
                        return;
                }

                if (this.status == -1) {
                    throw new ParseException(this.getPosition(), 1, this.token);
                }
            } while (this.token.type != -1);
        } catch (IOException e) {
            this.status = -1;
            throw e;
        } catch (ParseException e) {
            this.status = -1;
            throw e;
        } catch (RuntimeException e) {
            this.status = -1;
            throw e;
        } catch (Error e) {
            this.status = -1;
            throw e;
        }

        this.status = -1;
        throw new ParseException(this.getPosition(), 1, this.token);
    }

    public static Date parseDate(String str) {
        if (str == null) {
            return null;
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");
        if (str.endsWith("Z")) {
            str = str.substring(0, str.length() - 1) + "GMT-00:00";
        } else {
            byte b = 6;
            String substring = str.substring(0, str.length() - b);
            String substring2 = str.substring(str.length() - b, str.length());
            str = substring + "GMT" + substring2;
        }

        try {
            return simpleDateFormat.parse(str);
        } catch (java.text.ParseException e) {
            System.out.println("Error parsing date: " + str);
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
            return null;
        }
    }
}
