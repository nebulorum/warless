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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Digest {
  private static final String ZERO_FILLER = "00000000000000000000000000000000";

  public static String digestFromStream(InputStream is) {
    MessageDigest digest = getMD5MessageDigest();
    return normalizeMD5Digest(computeDigest(is, digest));
  }

  private static MessageDigest getMD5MessageDigest() {
    try {
      return MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("Failed to get MD5 digest instance", e);
    }
  }

  static private String normalizeMD5Digest(byte[] md5sum) {
    BigInteger bigInt = new BigInteger(1, md5sum);
    String md5text = bigInt.toString(16);
    return MD5Digest.ZERO_FILLER.substring(md5text.length()) + md5text;
  }

  static private byte[] computeDigest(InputStream is, MessageDigest digest) {
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

  public static String digestFromString(String message) {
    return digestFromStream(new ByteArrayInputStream(message.getBytes()));
  }
}