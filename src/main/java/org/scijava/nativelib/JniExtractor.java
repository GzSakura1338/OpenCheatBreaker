package org.scijava.nativelib;

import java.io.File;

public interface JniExtractor {
   void extractRegistered() throws java.io.IOException;

   File extractJni(String str, String str2) throws java.io.IOException;
}
