# bz2java

JNR based libbz2 native library integration into Java

## Usage

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

## Maven Dependency

### Latest Release:
```xml
<dependencies>
  <dependency>
    <groupId>com.noctarius.bz2java</groupId>
    <artifactId>bz2java</artifactId>
    <version>1.0.1</version>
  </dependency>
</dependencies>
```

### Latest Snapshot:
```xml
<dependencies>
  <dependency>
    <groupId>com.noctarius.bz2java</groupId>
    <artifactId>bz2java</artifactId>
    <version>1.0.2-SNAPSHOT</version>
  </dependency>
</dependencies>
<repositories>
  <repository>
    <id>sonatype-nexus-public</id>
    <name>SonaType public snapshots and releases repository</name>
    <url>https://oss.sonatype.org/content/groups/public</url>
    <releases>
      <enabled>true</enabled>
    </releases>
    <snapshots>
      <enabled>true</enabled>
    </snapshots>
  </repository>
</repositories>
```

For non Maven users you can download the latest releases here:
[Download](http://repo1.maven.org/maven2/com/noctarius/bz2java/bz2java/)

Snapshots can be found here:
[Download](https://oss.sonatype.org/content/repositories/snapshots/com/noctarius/bz2java/bz2java/)
