package tech.jhipster.lite.common.domain;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tech.jhipster.lite.TestUtils.assertFileNotExist;
import static tech.jhipster.lite.common.domain.FileUtils.getPath;
import static tech.jhipster.lite.common.domain.FileUtils.isPosix;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import tech.jhipster.lite.UnitTest;
import tech.jhipster.lite.error.domain.GeneratorException;
import tech.jhipster.lite.error.domain.MissingMandatoryValueException;

@UnitTest
class FileUtilsTest {

  @Nested
  class Exists {

    @Test
    void shouldExists() {
      String tmp = FileUtils.tmpDir();

      assertTrue(FileUtils.exists(tmp));
    }

    @Test
    void shouldNotExistsForNull() {
      assertThatThrownBy(() -> FileUtils.exists(null))
        .isExactlyInstanceOf(MissingMandatoryValueException.class)
        .hasMessageContaining("path");
    }

    @Test
    void shouldNotExistsForBlank() {
      assertThatThrownBy(() -> FileUtils.exists(null))
        .isExactlyInstanceOf(MissingMandatoryValueException.class)
        .hasMessageContaining("path");
    }
  }

  @Nested
  class CreateFolder {

    @Test
    void shouldNotCreateFolderForNull() {
      assertThatThrownBy(() -> FileUtils.createFolder(null))
        .isExactlyInstanceOf(MissingMandatoryValueException.class)
        .hasMessageContaining("path");
    }

    @Test
    void shouldNotCreateFolderForBlank() {
      assertThatThrownBy(() -> FileUtils.createFolder(null))
        .isExactlyInstanceOf(MissingMandatoryValueException.class)
        .hasMessageContaining("path");
    }

    @Test
    void shouldCreateFolderOnlyOnceTime() {
      String path = FileUtils.tmpDirForTest();

      assertFileNotExist(path);

      assertThatCode(() -> FileUtils.createFolder(path)).doesNotThrowAnyException();
      assertTrue(FileUtils.exists(path));

      assertThatCode(() -> FileUtils.createFolder(path)).doesNotThrowAnyException();
      assertTrue(FileUtils.exists(path));
    }

    @Test
    void shouldNotCreateFolderWhenItsAFile() throws Exception {
      String path = FileUtils.tmpDirForTest();
      String destinationFile = getPath(path, "chips");

      assertFalse(FileUtils.exists(path));
      assertThatCode(() -> FileUtils.createFolder(path)).doesNotThrowAnyException();
      assertTrue(FileUtils.exists(path));
      Files.createFile(FileUtils.getPathOf(destinationFile));

      assertThatThrownBy(() -> FileUtils.createFolder(destinationFile)).isInstanceOf(IOException.class);
    }
  }

  @Nested
  class GetPath {

    @Test
    void shouldGetPath() {
      String result = getPath("chips", "beer");

      assertThat(result).isEqualTo("chips/beer");
    }

    @Test
    void shouldGetPathForLinux() {
      String result = getPath("/home/chips/beer");

      assertThat(result).isEqualTo(File.separator + "home" + File.separator + "chips" + File.separator + "beer");
    }

    @Test
    void shouldGetPathForWindows() {
      String result = getPath("C:\\chips\\beer");

      assertThat(result).isEqualTo("C:/chips/beer");
    }

    @Test
    void shouldGetPathOf() {
      Path result = FileUtils.getPathOf("chips", "beer");

      assertThat(result).isEqualTo(Path.of("chips" + File.separator + "beer"));
    }

    @Test
    void shouldGetInputStream() {
      InputStream in = FileUtils.getInputStream("generator/mustache/README.txt");

      assertThat(in).isNotNull();
    }

    @Test
    void shouldNotGetInputStream() {
      assertThatThrownBy(() -> FileUtils.getInputStream("generator/mustache/chips.txt")).isExactlyInstanceOf(GeneratorException.class);
    }
  }

  @Nested
  class Read {

    @Test
    void shouldRead() throws Exception {
      String filename = getPath("src/test/resources/generator/utils/readme-short.md");

      String result = FileUtils.read(filename);

      String lineSeparator = System.lineSeparator();
      String expectedResult = new StringBuilder()
        .append("this is a short readme")
        .append(lineSeparator)
        .append("used for unit tests")
        .append(lineSeparator)
        .append("powered by JHipster \uD83E\uDD13")
        .append(lineSeparator)
        .toString();
      assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void shouldNotReadWhenFileNotExist() {
      String filename = getPath("src/test/resources/generator/utils/unknown.md");

      assertThatThrownBy(() -> FileUtils.read(filename)).isExactlyInstanceOf(NoSuchFileException.class);
    }
  }

  @Nested
  class GetLine {

    @Test
    void shouldGetLine() throws Exception {
      String filename = getPath("src/test/resources/generator/utils/readme-short.md");

      assertThat(FileUtils.getLine(filename, "used for unit tests")).isEqualTo(2);
      assertThat(FileUtils.getLine(filename, "JHipster")).isEqualTo(3);
    }

    @Test
    void shouldNotGetLineAsCaseSensitive() throws Exception {
      String filename = getPath("src/test/resources/generator/utils/readme-short.md");

      assertThat(FileUtils.getLine(filename, "jhipster")).isEqualTo(-1);
    }

    @Test
    void shouldNotGetLineForAnotherText() throws Exception {
      String filename = getPath("src/test/resources/generator/utils/readme-short.md");

      assertThat(FileUtils.getLine(filename, "beer")).isEqualTo(-1);
    }

    @Test
    void shouldNotGetLineWhenFileNotExist() {
      String filename = getPath("src/test/resources/generator/utils/unknown.md");

      assertThatThrownBy(() -> FileUtils.getLine(filename, "beer")).isInstanceOf(IOException.class);
    }
  }

  @Nested
  class ContainsInLine {

    @Test
    void shouldContainsInLine() {
      String filename = getPath("src", "test", "resources", "generator", "utils", "example-readme.md");

      assertTrue(FileUtils.containsInLine(filename, "Before you can build this project"));
    }

    @Test
    void shouldNotContainsInLine() {
      String filename = getPath("src", "test", "resources", "generator", "utils", "example-readme.md");

      assertFalse(FileUtils.containsInLine(filename, "apero with beers"));
    }

    @Test
    void shouldNotContainsInLineWhenFilenameNotExist() {
      String filename = getPath("src", "test", "resources", "generator", "utils", "unknown.md");

      assertFalse(FileUtils.containsInLine(filename, "apero with beers"));
    }
  }

  @Nested
  class ContainsLines {

    @Test
    void shouldContainsLinesSingle() {
      String filename = getPath("src/test/resources/generator/buildtool/maven/pom.test.xml");
      List<String> lines = List.of("<dependency>");

      assertTrue(FileUtils.containsLines(filename, lines));
    }

    @Test
    void shouldContainsLines() {
      String filename = getPath("src/test/resources/generator/buildtool/maven/pom.test.xml");
      List<String> lines = List.of(
        "<dependency>",
        "<groupId>org.junit.jupiter</groupId>",
        "<artifactId>junit-jupiter-engine</artifactId>",
        "<version>${junit-jupiter.version}</version>",
        "<scope>test</scope>",
        "</dependency>"
      );

      assertTrue(FileUtils.containsLines(filename, lines));
    }

    @Test
    void shouldNotContainsLines() {
      String filename = getPath("src/test/resources/generator/buildtool/maven/pom.test.xml");
      List<String> lines = List.of(
        "<dependency>",
        "<groupId>org.junit.jupiter</groupId>",
        "<artifactId>junit-jupiter-engine</artifactId>",
        "<version>${junit-jupiter.version}</version>",
        "<scope>WRONG_SCOPE</scope>",
        "</dependency>"
      );

      assertFalse(FileUtils.containsLines(filename, lines));
    }

    @Test
    void shouldNotContainsLinesWhenFileNotExist() {
      String filename = getPath("chips.txt");
      List<String> lines = List.of("chips");

      assertFalse(FileUtils.containsLines(filename, lines));
    }

    @Test
    void shouldNotContainsLinesWithNullFilename() {
      assertThatThrownBy(() -> FileUtils.containsLines(null, List.of()))
        .isExactlyInstanceOf(MissingMandatoryValueException.class)
        .hasMessageContaining("filename");
    }

    @Test
    void shouldNotContainsLinesWithBlankFilename() {
      assertThatThrownBy(() -> FileUtils.containsLines(" ", List.of()))
        .isExactlyInstanceOf(MissingMandatoryValueException.class)
        .hasMessageContaining("filename");
    }

    @Test
    void shouldNotContainsLinesWithEmptyLines() {
      String filename = getPath("src/test/resources/generator/buildtool/maven/pom.test.xml");

      assertThatThrownBy(() -> FileUtils.containsLines(filename, List.of()))
        .isExactlyInstanceOf(MissingMandatoryValueException.class)
        .hasMessageContaining("lines");
    }
  }

  @Nested
  class Replace {

    @Test
    void shouldReplaceInFile() throws Exception {
      String filename = getPath("src/test/resources/generator/utils/readme-short.md");

      String result = FileUtils.replaceInFile(filename, "powered by JHipster \uD83E\uDD13", "Hello JHipster Lite");

      String lineSeparator = System.lineSeparator();
      String expectedResult = new StringBuilder()
        .append("this is a short readme")
        .append(lineSeparator)
        .append("used for unit tests")
        .append(lineSeparator)
        .append("Hello JHipster Lite")
        .append(lineSeparator)
        .toString();
      assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void shouldNotReplaceInFileWhenFileNotExist() {
      String filename = getPath("src/test/resources/generator/utils/unknown.md");

      assertThatThrownBy(() -> FileUtils.replaceInFile(filename, "powered by JHipster", "Hello JHipster Lite"))
        .isInstanceOf(IOException.class);
    }
  }

  @Nested
  class FileSystem {

    @Test
    @EnabledOnOs(OS.WINDOWS)
    void shouldReturnPosixFalseForWindows(){
       assertFalse(isPosix());
    }

    @Test
    @DisabledOnOs(OS.WINDOWS)
    void shouldReturnPosixTrueForNonWindows(){
      assertTrue(isPosix());
    }

  }
}
