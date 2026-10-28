package optifine.json;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class ItemList {
    public String text = ",";
    public List items = new ArrayList();

    public ItemList() {
    }

    public ItemList(String str) {
        this.split(str, this.text, this.items);
    }

    public ItemList(String str, String str2) {
        this.text = str;
        this.split(str, str2, this.items);
    }

    public ItemList(String str, String str2, boolean flag) {
        this.split(str, str2, this.items, flag);
    }

    public List getItems() {
        return this.items;
    }

    public String[] getArray() {
        return (String[])this.items.toArray();
    }

    public void split(String str, String str2, List list, boolean flag) {
        if (str != null && str2 != null) {
            if (flag) {
                StringTokenizer stringTokenizer = new StringTokenizer(str, str2);

                while (stringTokenizer.hasMoreTokens()) {
                    list.add(stringTokenizer.nextToken().trim());
                }
            } else {
                this.split(str, str2, list);
            }
        }
    }

    public void split(String str, String str2, List list) {
        if (str != null && str2 != null) {
            int n = 0;
            boolean flag = false;

            int n2;
            do {
                n2 = n;
                n = str.indexOf(str2, n);
                if (n == -1) {
                    break;
                }

                list.add(str.substring(n2, n).trim());
                n += str2.length();
            } while (n != -1);

            list.add(str.substring(n2).trim());
        }
    }

    public void setSP(String str) {
        this.text = str;
    }

    public void add(int n, String str) {
        if (str != null) {
            this.items.add(n, str.trim());
        }
    }

    public void add(String str) {
        if (str != null) {
            this.items.add(str.trim());
        }
    }

    public void addAll(ItemList itemList) {
        this.items.addAll(itemList.items);
    }

    public void addAll(String str) {
        this.split(str, this.text, this.items);
    }

    public void addAll(String str, String str2) {
        this.split(str, str2, this.items);
    }

    public void addAll(String str, String str2, boolean flag) {
        this.split(str, str2, this.items, flag);
    }

    public String get(int n) {
        return (String)this.items.get(n);
    }

    public int size() {
        return this.items.size();
    }

    @Override
    public String toString() {
        return this.toString(this.text);
    }

    public String toString(String str) {
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < this.items.size(); i++) {
            if (i == 0) {
                sb.append(this.items.get(i));
            } else {
                sb.append(str);
                sb.append(this.items.get(i));
            }
        }

        return sb.toString();
    }

    public void clear() {
        this.items.clear();
    }

    public void reset() {
        this.text = ",";
        this.items.clear();
    }
}
