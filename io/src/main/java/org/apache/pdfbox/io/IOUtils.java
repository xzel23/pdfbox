/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* $Id$ */

package org.apache.pdfbox.io;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.io.RandomAccessStreamCache.StreamCacheCreateFunction;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This class contains various I/O-related methods.
 */
public final class IOUtils
{

    /**
     * Log instance.
     */
    private static final Log LOG = LogFactory.getLog(IOUtils.class);

    private IOUtils() {
        //Utility class. Don't instantiate.
    }

    /**
     * Reads the input stream and returns its contents as a byte array.
     * @param in the input stream to read from.
     * @return the byte array
     * @throws IOException if an I/O error occurs
     */
    public static byte[] toByteArray(InputStream in) throws IOException {
        return in.readAllBytes();
    }

    /**
     * Copies all the contents from the given input stream to the given output stream.
     * @param input the input stream
     * @param output the output stream
     * @return the number of bytes that have been copied
     * @throws IOException if an I/O error occurs
     */
    public static long copy(InputStream input, OutputStream output) throws IOException {
        return input.transferTo(output);
    }

    /**
     * Populates the given buffer with data read from the input stream. If the data doesn't
     * fit the buffer, only the data that fits in the buffer is read. If the data is less than
     * fits in the buffer, the buffer is not completely filled.
     * @param in the input stream to read from
     * @param buffer the buffer to fill
     * @return the number of bytes written to the buffer
     * @throws IOException if an I/O error occurs
     */
    public static long populateBuffer(InputStream in, byte[] buffer) throws IOException {
        return in.readNBytes(buffer, 0, buffer.length);
    }

    /**
     * Null safe close of the given {@link Closeable} suppressing any exception.
     *
     * @param closeable to be closed
     */
    public static void closeQuietly(Closeable closeable) {
        try (closeable) {
            // nop
        } catch (IOException ioe) {
            LOG.debug("An exception occurred while trying to close - ignoring", ioe);
            // ignore
        }
    }

    /**
     * Try to close an IO resource and log and return if there was an exception.
     *  
     * <p>An exception is only returned if the IOException passed in is null.
     * 
     * @param closeable to be closed
     * @param logger the logger to be used so that logging appears under that log instance
     * @param resourceName the name to appear in the log output
     * @param initialException if set, this exception will be returned even where there is another
     * exception while closing the IO resource
     * @return the IOException is there was any but only if initialException is null
     */
    public static IOException closeAndLogException(Closeable closeable, Log logger, String resourceName, IOException initialException) {
        try {
            closeable.close();
        } catch (IOException ioe) {
            logger.warn("Error closing " + resourceName, ioe);
            if (initialException == null) {
                return ioe;
            }
        }
        return initialException;
    }

    /**
     * Provides a function to create an instance of a memory only StreamCache using unrestricted main memory.
     * ScratchFile is used as current default implementation.
     *
     * @return a function to create an instance of a memory only StreamCache using unrestricted main memory
     */
    public static StreamCacheCreateFunction createMemoryOnlyStreamCache() {
        return MemoryUsageSetting.setupMainMemoryOnly().streamCache;
    }

    /**
     * Provides a function to create an instance of a temp file only StreamCache using unrestricted size. ScratchFile is
     * used as current default implementation.
     *
     * @return a function to create an instance of a temp file only StreamCache using unrestricted size
     */
    public static StreamCacheCreateFunction createTempFileOnlyStreamCache()
    {
        return MemoryUsageSetting.setupTempFileOnly().streamCache;
    }
}
