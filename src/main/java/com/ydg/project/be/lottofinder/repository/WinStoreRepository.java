package com.ydg.project.be.lottofinder.repository;

import com.ydg.project.be.lottofinder.entity.WinStoreEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface WinStoreRepository extends ReactiveMongoRepository<WinStoreEntity, String> {
}
