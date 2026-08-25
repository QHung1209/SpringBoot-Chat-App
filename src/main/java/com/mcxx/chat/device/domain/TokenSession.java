package com.mcxx.chat.device.domain;

import java.util.UUID;

public record TokenSession(
    UUID userId,
    Integer tokenVersion
) {}
