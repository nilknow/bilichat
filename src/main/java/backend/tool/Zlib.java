package backend.tool;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import java.util.zip.InflaterInputStream;

public class Zlib {
    public static String inflate(byte[] bytes) {
//78da448e414bc3401085e341f0e86f78e777989d6477b3b949239eea453cad8b686c4ba0466836a7d2ff2e1b94cec06306be8f99aabab9abeeab52b725ce18bebfd0a17f78debebe6f5f9e408cd3fe075d8c4243b534ce7bafa60c2ab5a86d82adfddf26a1ad29840bfe73d83b03cada4022f26ece60d4c6b7c1bbc6108765cc1fa7399fc6e9f08f1a1129a78a1213a352185ad7062bc4db2252ef6c4140498cc00a16715a8e479e916774d7d78821a3836efac7deea06972bba86508da4cb6f000000ffff63f23d1d
//78da64904f6bdc3010c54da1b71efa19e6ac83ecfd57eb56da1e7a496059c82104a19566bd03b664a471202cfeee41b63759b23e18deb3677eef4d517cfb51fc2cf2f33dbf2e603b070afe3f1cfeed7fff39e8a7c7fd5f10e00c1b5017b0c173a4e3c0147cd64d340e41c95180eb920d11416da58093f14977e84c9bff32de9e43d431848e1c2829a0194c74bac5576c274d3678bd7ca3a45b6ace8cb39cb6681bda10bf6a7d0cd1e1bd8dfe6e542736916fdc4ff6acbde910148080a58614907ab4942b649b4d6c90e790a30072e8999830817a2e5fa6d8a98f6816746a34bff508aa14702d5eafaa4db9d97d20ca6d255752d695acea0997c7b5c3649724b341fe14ae19a8d5f9561383a9c3c4a6eb6f37091832aa5aef7ed5bbedba14302cd59a81d8c4c4917c038b7dbd2bc038be070000ffff89caa342
//        String s = "78da448e414bc3401085e341f0e86f78e777989d6477b3b949239eea453cad8b686c4ba0466836a7d2ff2e1b94cec06306be8f99aabab9abeeab52b725ce18bebfd0a17f78debebe6f5f9e408cd3fe075d8c4243b534ce7bafa60c2ab5a86d82adfddf26a1ad29840bfe73d83b03cada4022f26ece60d4c6b7c1bbc6108765cc1fa7399fc6e9f08f1a1129a78a1213a352185ad7062bc4db2252ef6c4140498cc00a16715a8e479e916774d7d78821a3836efac7deea06972bba86508da4cb6f000000ffff63f23d1d";

        InputStream is = new InflaterInputStream(new ByteArrayInputStream(bytes));
        return new BufferedReader(new InputStreamReader(is))
                .lines().collect(Collectors.joining("\n"));
    }

    public static String inflate(String hexStr) {
        int len = hexStr.length();
        byte[] bytes = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            bytes[i / 2] = (byte) ((Character.digit(hexStr.charAt(i), 16) << 4)
                    + Character.digit(hexStr.charAt(i + 1), 16));
        }
        return inflate(bytes);
    }

    public static String hexStrToStr(String hexStr, Charset charset){
        int len = hexStr.length();
        byte[] bytes = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            bytes[i / 2] = (byte) ((Character.digit(hexStr.charAt(i), 16) << 4)
                    + Character.digit(hexStr.charAt(i + 1), 16));
        }
        return new String(bytes, charset);
    }

    public static void main(String[] args) {
        inflate("78da448e414bc3401085e341f0e86f78e777989d6477b3b949239eea453cad8b686c4ba0466836a7d2ff2e1b94cec06306be8f99aabab9abeeab52b725ce18bebfd0a17f78debebe6f5f9e408cd3fe075d8c4243b534ce7bafa60c2ab5a86d82adfddf26a1ad29840bfe73d83b03cada4022f26ece60d4c6b7c1bbc6108765cc1fa7399fc6e9f08f1a1129a78a1213a352185ad7062bc4db2252ef6c4140498cc00a16715a8e479e916774d7d78821a3836efac7deea06972bba86508da4cb6f000000ffff63f23d1d");
    }
}
