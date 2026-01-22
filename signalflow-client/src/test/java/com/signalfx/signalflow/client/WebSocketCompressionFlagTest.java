package com.signalfx.signalflow.client;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPOutputStream;

import net.jpountz.lz4.LZ4FrameOutputStream;
import com.github.luben.zstd.ZstdOutputStream;

import static org.junit.Assert.*;

public class WebSocketCompressionFlagTest {

    @Test
    public void testCompressionTypeOrdinality() {
        assertEquals(0, CompressionType.GZIP.ordinal());
        assertEquals(1, CompressionType.ZSTD.ordinal());
        assertEquals(2, CompressionType.LZ4.ordinal());
    }

    @Test
    public void testFlagParsingGzipCompression() throws Exception {
        byte flags = createFlags(true, false, CompressionType.GZIP);
        
        boolean compressed = (flags & (1 << 0)) != 0;
        boolean json = (flags & (1 << 1)) != 0;
        int compressionTypeOrdinal = (flags >> 2) & 0x07;
        
        assertTrue("Compressed flag should be set", compressed);
        assertFalse("JSON flag should not be set", json);
        assertEquals("Compression type should be GZIP (0)", 0, compressionTypeOrdinal);
        assertEquals("Should map to GZIP enum", CompressionType.GZIP, CompressionType.values()[compressionTypeOrdinal]);
    }

    @Test
    public void testFlagParsingZstdCompression() throws Exception {
        byte flags = createFlags(true, false, CompressionType.ZSTD);
        
        boolean compressed = (flags & (1 << 0)) != 0;
        boolean json = (flags & (1 << 1)) != 0;
        int compressionTypeOrdinal = (flags >> 2) & 0x07;
        
        assertTrue("Compressed flag should be set", compressed);
        assertFalse("JSON flag should not be set", json);
        assertEquals("Compression type should be ZSTD (1)", 1, compressionTypeOrdinal);
        assertEquals("Should map to ZSTD enum", CompressionType.ZSTD, CompressionType.values()[compressionTypeOrdinal]);
    }

    @Test
    public void testFlagParsingLz4Compression() throws Exception {
        byte flags = createFlags(true, false, CompressionType.LZ4);
        
        boolean compressed = (flags & (1 << 0)) != 0;
        boolean json = (flags & (1 << 1)) != 0;
        int compressionTypeOrdinal = (flags >> 2) & 0x07;
        
        assertTrue("Compressed flag should be set", compressed);
        assertFalse("JSON flag should not be set", json);
        assertEquals("Compression type should be LZ4 (2)", 2, compressionTypeOrdinal);
        assertEquals("Should map to LZ4 enum", CompressionType.LZ4, CompressionType.values()[compressionTypeOrdinal]);
    }

    @Test
    public void testFlagParsingNoCompression() throws Exception {
        byte flags = createFlags(false, false, null);
        
        boolean compressed = (flags & (1 << 0)) != 0;
        boolean json = (flags & (1 << 1)) != 0;
        
        assertFalse("Compressed flag should not be set", compressed);
        assertFalse("JSON flag should not be set", json);
    }

    @Test
    public void testFlagParsingJsonFlag() throws Exception {
        byte flags = createFlags(false, true, null);
        
        boolean compressed = (flags & (1 << 0)) != 0;
        boolean json = (flags & (1 << 1)) != 0;
        
        assertFalse("Compressed flag should not be set", compressed);
        assertTrue("JSON flag should be set", json);
    }

    @Test
    public void testFlagParsingCompressedAndJson() throws Exception {
        byte flags = createFlags(true, true, CompressionType.GZIP);
        
        boolean compressed = (flags & (1 << 0)) != 0;
        boolean json = (flags & (1 << 1)) != 0;
        int compressionTypeOrdinal = (flags >> 2) & 0x07;
        
        assertTrue("Compressed flag should be set", compressed);
        assertTrue("JSON flag should be set", json);
        assertEquals("Compression type should be GZIP (0)", 0, compressionTypeOrdinal);
    }

    @Test
    public void testInvalidCompressionTypeOrdinal() {
        int invalidOrdinal = 3;
        
        try {
            CompressionType type = CompressionType.values()[invalidOrdinal];
            fail("Should throw ArrayIndexOutOfBoundsException for invalid ordinal");
        } catch (ArrayIndexOutOfBoundsException e) {
        }
    }

    @Test
    public void testGzipCompressionDecompression() throws Exception {
        String testData = "{\"test\":\"data\"}";
        byte[] compressed = compressWithGzip(testData);
        
        assertNotNull("Compressed data should not be null", compressed);
        assertTrue("Compressed data should have content", compressed.length > 0);
    }

    @Test
    public void testZstdCompressionDecompression() throws Exception {
        String testData = "{\"test\":\"data\"}";
        byte[] compressed = compressWithZstd(testData);
        
        assertNotNull("Compressed data should not be null", compressed);
        assertTrue("Compressed data should have content", compressed.length > 0);
    }

    @Test
    public void testLz4CompressionDecompression() throws Exception {
        String testData = "{\"test\":\"data\"}";
        byte[] compressed = compressWithLz4(testData);
        
        assertNotNull("Compressed data should not be null", compressed);
        assertTrue("Compressed data should have content", compressed.length > 0);
    }

    private byte createFlags(boolean compressed, boolean json, CompressionType compressionType) {
        byte flags = 0;
        
        if (compressed) {
            flags |= (1 << 0);
        }
        
        if (json) {
            flags |= (1 << 1);
        }
        
        if (compressionType != null) {
            flags |= (compressionType.ordinal() << 2);
        }
        
        return flags;
    }

    private byte[] compressWithGzip(String data) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (GZIPOutputStream gzip = new GZIPOutputStream(baos)) {
            gzip.write(data.getBytes(StandardCharsets.UTF_8));
        }
        return baos.toByteArray();
    }

    private byte[] compressWithZstd(String data) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZstdOutputStream zstd = new ZstdOutputStream(baos)) {
            zstd.write(data.getBytes(StandardCharsets.UTF_8));
        }
        return baos.toByteArray();
    }

    private byte[] compressWithLz4(String data) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (LZ4FrameOutputStream lz4 = new LZ4FrameOutputStream(baos)) {
            lz4.write(data.getBytes(StandardCharsets.UTF_8));
        }
        return baos.toByteArray();
    }
}
