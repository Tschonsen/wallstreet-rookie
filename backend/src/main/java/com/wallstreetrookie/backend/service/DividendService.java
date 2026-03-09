package com.wallstreetrookie.backend.service;

import java.util.List;

public interface DividendService {

    int payDividends(String gameSessionId, List<String> playerIds);
}
