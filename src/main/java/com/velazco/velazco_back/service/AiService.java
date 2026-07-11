package com.velazco.velazco_back.service;

import com.velazco.velazco_back.dto.AiRequestDto;
import com.velazco.velazco_back.dto.AiResponseDto;

public interface AiService {
    AiResponseDto generateResponse(AiRequestDto request);
}
