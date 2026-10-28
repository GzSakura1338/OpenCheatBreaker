package optifine.xdelta;

import com.cheatbreaker.client.util.SneakyThrow;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Checksum {
    public static final int BASE = 65521;
    public static final int CONSTANT_16 = 16;
    public static boolean debug = false;
    public int[] hashtable;
    public long[] checksums;
    public int prime;
    public static char[] single_hash = new char[]{
        '병',
        '뭥',
        '䋂',
        '\udffe',
        '陦',
        '䌛',
        '蔄',
        '\ueb46',
        '捹',
        '푠',
        '켔',
        '叏',
        '\udb51',
        '\udb08',
        'ወ',
        '\uf602',
        '\ue766',
        '⎔',
        '┍',
        '\udcbb',
        'ꙸ',
        'ʯ',
        'ꗆ',
        '约',
        '뙅',
        '쭍',
        '쑋',
        '\ue5dc',
        '鿦',
        '孜',
        '㗵',
        '瀚',
        '∏',
        '永',
        'ᩖ',
        '䲣',
        'ￆ',
        '녒',
        '赡',
        '穘',
        '逥',
        '謽',
        '뼏',
        '閣',
        '\ue5f4',
        '섧',
        '㯭',
        '㈋',
        '럳',
        '恔',
        '㌼',
        '펃',
        '腔',
        '剂',
        '不',
        'ઔ',
        '瀨',
        '蚉',
        '㨢',
        'ঀ',
        'ᡇ',
        '냱',
        '魜',
        '䅶',
        '롘',
        '핂',
        'Ὤ',
        '⒗',
        '橚',
        '龩',
        '豚',
        '睃',
        'ꢩ',
        '騂',
        '䤘',
        '䎌',
        '쎈',
        '鸫',
        '䲭',
        'ƶ',
        '\uab19',
        '\uf777',
        '㙟',
        'Ẳ',
        'ञ',
        '篸',
        '窎',
        '刧',
        '\ueab1',
        '⁴',
        '䔣',
        '\ue781',
        'ƣ',
        'ᘽ',
        '㬮',
        '⡽',
        '广',
        'ꁣ',
        '넴',
        '辮',
        '庎',
        '랷',
        '䕈',
        '\u1f5a',
        '節',
        '稤',
        '透',
        '䋜',
        '챩',
        'ʠ',
        'ଢ',
        '\udb31',
        '燾',
        '౽',
        'ᜲ',
        'ᅙ',
        '쬉',
        '\ue1d2',
        'ፑ',
        '勩',
        '\uf536',
        '婏',
        '쌖',
        '毹',
        '覔',
        '띴',
        '弾',
        '\uf6d6',
        '㩡',
        '\uf82c',
        '찢',
        '鴆',
        '⦜',
        '\u09e5',
        'Ử',
        '兏',
        '赓',
        'Ꙑ',
        '屮',
        '앷',
        '祘',
        '熬',
        '褖',
        '魏',
        'Ⰹ',
        '刑',
        '\uf6d8',
        '쪪',
        '\uf7ef',
        '⡿',
        '窔',
        'ꭉ',
        '館',
        '爢',
        '\ue457',
        '휚',
        'Ã',
        '᩶',
        '\ue98c',
        '쀷',
        '興',
        '尭',
        '\udfda',
        '\ue5f5',
        '\u0b45',
        'ᗎ',
        '詾',
        'ﲭ',
        'ꨭ',
        '䭜',
        '퐮',
        '뉑',
        '遾',
        '驇',
        '즦',
        '\ud93f',
        '࡞',
        '㗎',
        'ꅓ',
        '繻',
        '鼋',
        '▪',
        '嶟',
        '쁍',
        '討',
        '⡵',
        '䨜',
        '⥟',
        '᎓',
        '\uf760',
        '酸',
        'ཛ',
        '墳',
        '莴',
        '₂',
        '爝',
        '摢',
        'ͨ',
        '柢',
        '蘤',
        '᥍',
        '⋶',
        '磻',
        '枑',
        '눸',
        '댲',
        '牶',
        '\uf272',
        '䟬',
        '䔄',
        'ꥡ',
        '鿈',
        '㿜',
        '됓',
        'z',
        'ࠆ',
        '瑘',
        '闆',
        '첪',
        'ᣖ',
        '\ue2ae',
        'ᬆ',
        '\uf3f6',
        '偐',
        '죨',
        '\uf4ac',
        '쁌',
        '\uf41c',
        '餯',
        '깄',
        '弛',
        'ᄓ',
        '\u1738',
        '\ud9a8',
        '᧪',
        'ⴳ',
        '隘',
        '\u2fe9',
        '㈿',
        '췢',
        '浱',
        '\ue37d',
        '뚗',
        'ⱏ',
        '䍳',
        '鄂',
        'ݝ',
        '踥',
        'ᙲ',
        '\uec28',
        '櫋',
        '蛌',
        'ᡮ',
        '鐔',
        '홴',
        '톥'
    };

    public static long queryChecksum(byte[] bytes, int n) {
        int n2 = 0;
        int n3 = 0;

        for (int i = 0; i < n; i++) {
            n3 += single_hash[bytes[i] + 128];
            n2 += n3;
        }

        return (n2 & '\uffff') << 16 | n3 & 65535;
    }

    public static long incrementChecksum(long l, byte b, byte b2) {
        char c = single_hash[b + 128];
        char c2 = single_hash[b2 + 128];
        int n = (int)(l & 65535L) - c + c2 & 65535;
        int n2 = (int)(l >> 16) - c * 16 + n & 65535;
        return n2 << 16 | n & 65535;
    }

    public static int generateHash(long l) {
        long l2 = l >> 16 & 65535L;
        long l3 = l & 65535L;
        long l4 = (l2 >> 2) + (l3 << 3) + (l2 << 16);
        int n = (int)(l4 ^ l2 ^ l3);
        return n > 0 ? n : -n;
    }

    public void generateChecksums(File file, int n) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);

        try {
            this.generateChecksums(fileInputStream, n);
        } catch (IOException e) {
            throw SneakyThrow.sneakyThrow(e);
        } finally {
            fileInputStream.close();
        }
    }

    public void generateChecksums(InputStream inputStream, int n) throws IOException {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        int n2 = n / 16;
        if (debug) {
            System.out.println("generating checksum array of size " + n2);
        }

        this.checksums = new long[n2];
        this.hashtable = new int[n2];
        this.prime = findClosestPrime(n2);
        if (debug) {
            System.out.println("using prime " + this.prime);
        }

        for (int i = 0; i < n2; i++) {
            byte[] bytes = new byte[16];
            bufferedInputStream.read(bytes, 0, 16);
            this.checksums[i] = queryChecksum(bytes, 16);
        }

        for (int i = 0; i < n2; i++) {
            this.hashtable[i] = -1;
        }

        for (int i = 0; i < n2; i++) {
            int n3 = generateHash(this.checksums[i]) % this.prime;
            if (debug) {
                System.out.println("checking with hash: " + n3);
            }

            if (this.hashtable[n3] != -1) {
                if (debug) {
                    System.out.println("hash table collision for index " + i);
                }
            } else {
                this.hashtable[n3] = i;
            }
        }
    }

    public int findChecksumIndex(long l) {
        return this.hashtable[generateHash(l) % this.prime];
    }

    public static int findClosestPrime(int n) {
        int belowOrEqual = (int)SimplePrime.belowOrEqual(n - 1);
        return belowOrEqual < 2 ? 1 : belowOrEqual;
    }

    public String printIntArray(int[] ints) {
        String str = "";
        str = str + "[";

        for (int i = 0; i < ints.length; i++) {
            str = str + ints[i];
            if (i != ints.length - 1) {
                str = str + ",";
            } else {
                str = str + "]";
            }
        }

        return str;
    }

    public String printLongArray(long[] longs) {
        String str = "";
        str = str + "[";

        for (int i = 0; i < longs.length; i++) {
            str = str + longs[i];
            if (i != longs.length - 1) {
                str = str + ",";
            } else {
                str = str + "]";
            }
        }

        return str;
    }
}
