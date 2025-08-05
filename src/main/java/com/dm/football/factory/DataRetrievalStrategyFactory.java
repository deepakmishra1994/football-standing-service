package com.dm.football.factory;

import com.dm.football.service.DataRetrievalStrategy;
import com.dm.football.service.impl.OfflineDataRetrievalStrategy;
import com.dm.football.service.impl.OnlineDataRetrievalStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataRetrievalStrategyFactory {

    private final OnlineDataRetrievalStrategy onlineStrategy;
    private final OfflineDataRetrievalStrategy offlineStrategy;

    public DataRetrievalStrategy getStrategy(boolean isOfflineMode) {
        return isOfflineMode ? offlineStrategy : onlineStrategy;
    }
}