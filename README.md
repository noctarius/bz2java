bz2java
=======

JNR based libbz2 native library integration into Java

Usage
-----

```java
public class Bz2Packer {

  public static void main(String[] args) throws Exception {
    if (args.length != 2) {
      System.err.println("Usage Bz2Packer extract|compress inputfile");
      return;
    }

    boolean extract = "extract".equals(args[0].toLowerCase());
    Path source = Paths.get(args[1]);

    String inputFilename = source.getFilename().toString();
    Path target = extract ?
        Bzip2Utils.getUncompressedFilename(inputFilename) :
        Bzip2Utils.getCompressedFilename(inputFilename);

    if (extract) {
      Bzip2Decompressor.decompress(source, target);
    } else {
      Bzip2Compressor.compress(source, target);
    }
  }
}
```