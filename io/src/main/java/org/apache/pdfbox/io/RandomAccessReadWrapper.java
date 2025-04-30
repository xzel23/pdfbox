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
package org.apache.pdfbox.io;

import java.io.IOException;
import java.util.Objects;

/**
 * An implementation of the RandomAccessRead interface to store data in memory. The data will be stored in chunks
 * organized in an ArrayList.
 */
public class RandomAccessReadWrapper implements RandomAccessRead
{
    // the data to be read
    private byte[] data;
    // current pointer to the whole buffer
    private long pointer = 0;

    public RandomAccessReadWrapper(byte[] data) {
        this.data = Objects.requireNonNull(data, "data must not be null");
    }

    private void checkClosed() throws IOException {
        if (isClosed()) {
            throw new IOException("RandomAccessWrapper is closed");
        }
    }

    @Override
    public int read() throws IOException {
        checkClosed();
        if (pointer >= data.length)
        {
            return -1;
        }
        return data[(int) pointer++] & 0xff;
    }

    @Override
    public int read(byte[] b, int offset, int length) throws IOException {
        checkClosed();

        if (isEOF()) {
            return -1;
        }

        int n = Math.min(length, data.length - (int) pointer);
        System.arraycopy(data, (int) pointer, b, offset, n);
        pointer += n;
        return n;
    }

    @Override
    public long getPosition() throws IOException {
        checkClosed();
        return pointer;
    }

    @Override
    public void seek(long position) throws IOException {
        checkClosed();
        if (position < 0)
        {
            throw new IOException("Invalid position " + position);
        }
        if (position < data.length)
        {
            pointer = position;
        }
        else
        {
            // it is allowed to jump beyond the end of the file
            // jump to the end of the buffer
            pointer = data.length;
        }
    }

    @Override
    public long length() throws IOException {
        return data.length;
    }

    @Override
    public boolean isClosed() {
        return data == null;
    }

    @Override
    public boolean isEOF() throws IOException {
        checkClosed();
        return pointer >= data.length;
    }

    @Override
    public RandomAccessReadView createView(long startPosition, long streamLength) throws IOException {
        return new RandomAccessReadView(this, startPosition, streamLength);
    }

    @Override
    public void close() throws IOException {
        checkClosed();
        data = null;
    }
}
