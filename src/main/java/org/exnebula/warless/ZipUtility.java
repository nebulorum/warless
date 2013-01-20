/*
 * Copyright (C) 2013-2013 - Thomas Santana <tms@exnebula.org>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package org.exnebula.warless;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class ZipUtility {
  static private final String ZERO_FILLER = "00000000000000000000000000000000";

  static private class FilterAll implements UnzipFilter {
    public boolean filter(String name) {
      return true;
    }
  }

  static public class PrefixUnzipFilter implements UnzipFilter {

    private final String prefix;

    public PrefixUnzipFilter(String prefix) {
      this.prefix = prefix;
    }

    public boolean filter(String name) {
      return name.startsWith(prefix);
    }
  }

  public String streamMD5(InputStream is) {
    MessageDigest digest = getMD5MessageDigest();
    return normalizeMD5Digest(computeDigest(is, digest));
  }

  private String normalizeMD5Digest(byte[] md5sum) {
    BigInteger bigInt = new BigInteger(1, md5sum);
    String md5text = bigInt.toString(16);
    return ZERO_FILLER.substring(md5text.length()) + md5text;
  }

  private byte[] computeDigest(InputStream is, MessageDigest digest) {
    try {
      byte[] buffer = new byte[8192];
      int read = is.read(buffer);
      while (read > 0) {
        digest.update(buffer, 0, read);
        read = is.read(buffer);
      }
      is.close();
      return digest.digest();
    } catch (IOException e) {
      throw new RuntimeException("Unable to process file for MD5", e);
    }
  }

  private MessageDigest getMD5MessageDigest() {
    try {
      return MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("Failed to get MD5 digest instance", e);
    }
  }


  public void unzipAll(InputStream is, File targetDirectory) throws IOException {
    unzipFiltered(is, targetDirectory, new FilterAll());
  }

  public void unzipFiltered(InputStream is, File targetDirectory, UnzipFilter filter) throws IOException {
      ZipInputStream zipInputStream = new ZipInputStream(is);

      ZipEntry zipentry = zipInputStream.getNextEntry();
      while (zipentry != null) {
        handleEntry(targetDirectory, filter, zipInputStream, zipentry);
        zipentry = zipInputStream.getNextEntry();
      }
      zipInputStream.close();
  }

  private void handleEntry(File targetDirectory, UnzipFilter filter, ZipInputStream zipInputStream, ZipEntry zipentry) throws IOException {
    String name = zipentry.getName();
    if (filter.filter(name)) {
      File dest = new File(targetDirectory, name);
      if (zipentry.isDirectory()) {
        makeParentDirectory(dest);
      } else {
        makeParentDirectory(dest.getParentFile());
        extractFile(zipInputStream, dest);
      }
    }
    zipInputStream.closeEntry();
  }

  private void makeParentDirectory(File parentFile) {
    if (!parentFile.exists()) parentFile.mkdirs();
  }

  private void extractFile(ZipInputStream zipinputstream, File dest) throws IOException {
    FileOutputStream outputStream = new FileOutputStream(dest);
    byte[] buf = new byte[4096];
    int n = zipinputstream.read(buf);
    while ((n) > -1) {
      outputStream.write(buf, 0, n);
      n = zipinputstream.read(buf);
    }
    outputStream.close();
  }
}