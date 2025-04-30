/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.fontbox.cmap;

import java.nio.charset.StandardCharsets;

/**
 * Many CMaps are using the same values for the mapped strings. This class provides all common one- and two-byte
 * mappings to avoid duplicate strings.
 */
public final class CMapStrings
{
    private static final String[] oneByteMappings = new String[256];
    private static final String[] twoByteMappings = new String[256 * 256];

    private static final byte[][] oneByteValues = new byte[256][];
    private static final byte[][] twoByteValues = new byte[256 * 256][];

    static
    {
        // create all mappings when loading the class to avoid concurrency issues
        fillMappings();
    }

    private CMapStrings()
    {
    }

    private static void fillMappings()
    {
        for (int i = 0; i < 256; i++)
        {
            for (int j = 0; j < 256; j++)
            {
                byte[] bytes = { (byte) i, (byte) j };
                twoByteMappings[(i << 8) | j] = new String(bytes, StandardCharsets.UTF_16BE);
                twoByteValues[(i << 8) | j] = bytes;
            }
        }
        for (int i = 0; i < 256; i++)
        {
            byte[] bytes = { (byte) i };
            oneByteMappings[i] = new String(bytes, StandardCharsets.ISO_8859_1);
            oneByteValues[i] = bytes;
        }
    }

    /**
     * Get the mapped string value for the given combination of bytes. The mapping is limited to one and two-byte
     * mappings. Any longer byte sequence produces null as return value.
     * 
     * @param bytes the given combination of bytes
     * @return the string representation for the given combination of bytes
     */
    public static String getMapping(byte[] bytes)
    {
        switch (bytes.length) {
            case 1:
                return oneByteMappings[bytes[0] & 0xff];
            case 2:
                return twoByteMappings[((bytes[0] & 0xff) << 8) | (bytes[1] & 0xff)];
            default:
                return null;
        }
    }

    /**
     * Get an Integer instance of the given combination of bytes. Each value is a singleton to avoid multiple instances
     * for same value. The values are limited to one and two-byte sequences. Any longer byte sequence produces null as
     * return value.
     * 
     * @param bytes the given combination of bytes
     * @return the Integer representation for the given combination of bytes
     */
    public static int getIndexValue(byte[] bytes)
    {
        switch (bytes.length) {
            case 1:
                return bytes[0] & 0xff;
            case 2:
                return ((bytes[0] & 0xff) << 8) | (bytes[1] & 0xff);
            default:
                throw new IllegalArgumentException( "Invalid number of bytes " + bytes.length);
        }
    }

    /**
     * Get a singleton instance of the given combination of bytes to avoid multiple instances for same value. The values
     * are limited to one and two-byte sequences. Any longer byte sequence produces null as return value.
     * 
     * @param bytes the given combination of bytes
     * @return a singleton instance for the given combination of bytes
     */
    public static byte[] getByteValue(byte[] bytes)
    {
        switch (bytes.length) {
            case 1:
                return oneByteValues[bytes[0] & 0xff];
            case 2:
                return twoByteValues[((bytes[0] & 0xff) << 8) | (bytes[1] & 0xff)];
            default:
                return null;
        }
    }

}
