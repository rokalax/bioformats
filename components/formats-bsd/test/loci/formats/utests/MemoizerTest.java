/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2016 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package loci.formats.utests;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

import java.io.File;
import java.util.UUID;

import loci.formats.Memoizer;
import loci.formats.in.FakeReader;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


public class MemoizerTest {

  private static final String TEST_FILE =
    "test&pixelType=int8&sizeX=20&sizeY=20&sizeC=1&sizeZ=1&sizeT=1.fake";

  private File idDir;
  private String id;
  private FakeReader reader;

  private static void checkMemo(Memoizer memoizer, String id)
      throws Exception {
    memoizer.setId(id);
    assertFalse(memoizer.isLoadedFromMemo());
    assertTrue(memoizer.isSavedToMemo());
    memoizer.close();
    memoizer.setId(id);
    assertTrue(memoizer.isLoadedFromMemo());
    assertFalse(memoizer.isSavedToMemo());
    memoizer.close();
  }

  private static void checkNoMemo(Memoizer memoizer, String id)
      throws Exception {
    memoizer.setId(id);
    assertFalse(memoizer.isLoadedFromMemo());
    assertFalse(memoizer.isSavedToMemo());
    memoizer.close();
  }

  private void checkMemoFile(File memoFile) {
    checkMemoFile(memoFile, idDir);
  }

  private void checkMemoFile(File memoFile, File memoDir) {
    File expMemoFile = new File(memoDir, "." + TEST_FILE + ".bfmemo");
    assertEquals(memoFile.getAbsolutePath(), expMemoFile.getAbsolutePath());
  }

  @BeforeMethod
  public void setUp() throws Exception {
    String uuid = UUID.randomUUID().toString();
    idDir = new File(System.getProperty("java.io.tmpdir"), uuid);
    idDir.mkdirs();
    File tempFile = new File(idDir, TEST_FILE);
    tempFile.createNewFile();
    id = tempFile.getAbsolutePath();
    reader = new FakeReader(); // No setId !
  }

  @AfterMethod
  public void tearDown() throws Exception {
    reader.close();
  }

  public void testDefaultConstructor() throws Exception {
    Memoizer memoizer = new Memoizer();
    checkMemoFile(memoizer.getMemoFile(id));
  }

  @Test
  public void testConstructorTimeElapsed() throws Exception {
    Memoizer memoizer = new Memoizer(0);
    checkMemoFile(memoizer.getMemoFile(id));
    checkMemo(memoizer, id);
  }

  @Test
  public void testConstructorReader() throws Exception {
    Memoizer memoizer = new Memoizer(reader);
    checkMemoFile(memoizer.getMemoFile(id));
  }

  @Test
  public void testConstructorReaderTimeElapsed() throws Exception {
    Memoizer memoizer = new Memoizer(reader, 0);
    checkMemoFile(memoizer.getMemoFile(id));
    checkMemo(memoizer, id);
  }

  @Test
  public void testConstructorTimeElapsedDirectory() throws Exception {
    String uuid = UUID.randomUUID().toString();
    File directory = new File(System.getProperty("java.io.tmpdir"), uuid);
    Memoizer memoizer = new Memoizer(0, directory);

    // Check non-existing memo directory returns null
    assertEquals(memoizer.getMemoFile(id), null);

    // Create memoizer directory and memoizer reader
    directory.mkdirs();

    String memoDir = idDir.getAbsolutePath();
    memoDir = memoDir.substring(memoDir.indexOf(File.separator) + 1);
    checkMemoFile(memoizer.getMemoFile(id), new File(directory, memoDir));
    checkMemo(memoizer, id);
  }

  @Test
  public void testConstructorTimeElapsedNull() throws Exception {
    Memoizer memoizer = new Memoizer(0, null);

    // Check null memo directory returns null
    assertEquals(memoizer.getMemoFile(id), null);
    checkNoMemo(memoizer, id);
  }

  @Test
  public void testConstructorReaderTimeElapsedDirectory() throws Exception {
    String uuid = UUID.randomUUID().toString();
    File directory = new File(System.getProperty("java.io.tmpdir"), uuid);
    Memoizer memoizer = new Memoizer(reader, 0, directory);

    // Check non-existing memo directory returns null
    assertEquals(memoizer.getMemoFile(id), null);

    // Create memoizer directory and memoizer reader
    directory.mkdirs();

    String memoDir = idDir.getAbsolutePath();
    memoDir = memoDir.substring(memoDir.indexOf(File.separator) + 1);
    checkMemoFile(memoizer.getMemoFile(id), new File(directory, memoDir));
    checkMemo(memoizer, id);
  }

  @Test
  public void testConstructorReaderTimeElapsedNull() throws Exception {
    Memoizer memoizer = new Memoizer(reader, 0, null);

    // Check null memo directory returns null
    assertEquals(memoizer.getMemoFile(id), null);
    checkNoMemo(memoizer, id);
  }

  @Test
  public void testGetMemoFilePermissionsDirectory() throws Exception {
    String uuid = UUID.randomUUID().toString();
    File directory = new File(System.getProperty("java.io.tmpdir"), uuid);
    Memoizer memoizer = new Memoizer(reader, 0, directory);

    // Check non-existing memo directory returns null
    assertEquals(memoizer.getMemoFile(id), null);

    // Create memoizer directory and memoizer reader
    directory.mkdirs();
    memoizer = new Memoizer(reader, 0, directory);

    // Check existing non-writeable memo directory returns null
    if (File.separator.equals("/")) {
      // File.setWritable() does not work properly on Windows
      directory.setWritable(false);
      assertEquals(memoizer.getMemoFile(id), null);
    }

    // Check existing writeable memo diretory returns a memo file
    directory.setWritable(true);
    String memoDir = idDir.getAbsolutePath();
    memoDir = memoDir.substring(memoDir.indexOf(File.separator) + 1);
    checkMemoFile(memoizer.getMemoFile(id), new File(directory, memoDir));
  }

  @Test
  public void testGetMemoFilePermissionsInPlaceDirectory() throws Exception {
    String rootPath = id.substring(0, id.indexOf(File.separator) + 1);
    Memoizer memoizer = new Memoizer(reader, 0, new File(rootPath));

    // Check non-writeable file directory returns null for in-place caching
    if (File.separator.equals("/")) {
      // File.setWritable() does not work properly on Windows
      idDir.setWritable(false);
      assertEquals(memoizer.getMemoFile(id), null);
    }

    // Check writeable file directory returns memo file beside file
    idDir.setWritable(true);
    checkMemoFile(memoizer.getMemoFile(id));
  }

  @Test
  public void testGetMemoFilePermissionsInPlace() throws Exception {
    Memoizer memoizer = new Memoizer(reader);

    // Check non-writeable file directory returns null for in-place caching
    if (File.separator.equals("/")) {
      // File.setWritable() does not work properly on Windows
      idDir.setWritable(false);
      assertEquals(memoizer.getMemoFile(id), null);
    }
    // Check writeable file directory returns memo file beside file
    idDir.setWritable(true);
    checkMemoFile(memoizer.getMemoFile(id));
  }

  @Test
  public void testRelocate() throws Exception {
    // Create an in-place memo file
    Memoizer memoizer = new Memoizer(reader, 0);
    memoizer.setId(id);
    memoizer.close();
    assertFalse(memoizer.isLoadedFromMemo());
    assertTrue(memoizer.isSavedToMemo());

    // Rename the directory (including the file and the memo file)
    String uuid = UUID.randomUUID().toString();
    File newidDir = new File(System.getProperty("java.io.tmpdir"), uuid);
    idDir.renameTo(newidDir);
    File newtempFile = new File(newidDir, TEST_FILE);
    String newid = newtempFile.getAbsolutePath();

    // Try to reopen the file with the Memoizer
    memoizer.setId(newid);
    memoizer.close();
    assertTrue(memoizer.isLoadedFromMemo());
    assertFalse(memoizer.isSavedToMemo());
  }

}
