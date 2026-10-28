package optifine;

import java.io.IOException;
import java.io.InputStream;

public interface IResourceProvider {
    InputStream getResourceStream(String str) throws IOException;
}
