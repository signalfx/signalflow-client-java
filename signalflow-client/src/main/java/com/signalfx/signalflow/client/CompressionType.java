/*
 * Copyright (C) 2026 SignalFx, Inc. All rights reserved.
 */
package com.signalfx.signalflow.client;

public enum CompressionType {
    GZIP("gzip"),
    ZSTD("zstd"),
    LZ4("lz4");

    private final String value;

    CompressionType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
