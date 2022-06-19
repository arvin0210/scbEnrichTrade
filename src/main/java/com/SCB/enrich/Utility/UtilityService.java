package com.SCB.enrich.Utility;

import org.springframework.stereotype.Service;

@Service
public interface UtilityService {
    public boolean isInteger(String str);
    public boolean isDouble(String str);
}
