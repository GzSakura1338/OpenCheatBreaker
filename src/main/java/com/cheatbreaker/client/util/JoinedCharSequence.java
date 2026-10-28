package com.cheatbreaker.client.util;

public class JoinedCharSequence implements CharSequence {
    public CharSequence first;
    public CharSequence second;

    @Override
    public int length() {
        int n = this.first.length() + this.second.length() - 1;

        while (n > 0 && Character.isWhitespace(this.charAt(n))) {
            n--;
        }

        return n + 1;
    }

    @Override
    public char charAt(int n) {
        return n < this.first.length() ? this.first.charAt(n) : this.second.charAt(n - this.first.length());
    }

    public JoinedCharSequence(CharSequence charSequence, CharSequence charSequence2) {
        this.first = charSequence;
        this.second = charSequence2;
    }

    @Override
    public CharSequence subSequence(final int n, final int n2) {
        return new CharSequence() {
            @Override
            public int length() {
                return n2 - n;
            }

            @Override
            public CharSequence subSequence(int n3, int n4) {
                StringBuilder sb = new StringBuilder(n4 - n3);

                for (int i = n3; i < n4; i++) {
                    sb.append(this.charAt(i));
                }

                return sb;
            }

            @Override
            public char charAt(int n3) {
                return JoinedCharSequence.this.charAt(n + n3);
            }

            @Override
            public String toString() {
                StringBuilder sb = new StringBuilder();
                int n3 = this.length();

                for (int i = 0; i < n3; i++) {
                    sb.append(this.charAt(i));
                }

                return sb.toString();
            }
        };
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int n = this.length();

        for (int i = 0; i < n; i++) {
            sb.append(this.charAt(i));
        }

        return sb.toString();
    }
}
