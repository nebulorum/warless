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

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.assertEquals;

public class MD5DigestTest {

  @Test
  public void computeMD5String() throws NoSuchAlgorithmException {
    assertEquals("5ac749fbeec93607fc28d666be85e73a", stringMD5("some string"));
  }

  @Test
  public void makeSureWePadMD5() throws NoSuchAlgorithmException {
    assertEquals("0cc175b9c0f1b6a831c399e269772661", stringMD5("a"));
    assertEquals("00411460f7c92d2124a67ea0f4cb5f85", stringMD5("363"));
    assertEquals("0000000018e6137ac2caab16074784a6", stringMD5("jk8ssl"));
  }

  @Test
  public void digestFromString() throws NoSuchAlgorithmException {
    assertEquals("5ac749fbeec93607fc28d666be85e73a", MD5Digest.digestFromString("some string"));
  }

  private String stringMD5(String s) throws NoSuchAlgorithmException {
    return MD5Digest.digestFromStream(new ByteArrayInputStream(s.getBytes()));
  }
}