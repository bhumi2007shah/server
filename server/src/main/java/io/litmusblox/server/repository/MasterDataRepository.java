package io.litmusblox.server.repository;

import io.litmusblox.server.model.MasterData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author : Shital Raval
 * Date : 4/7/19
 * Time : 1:34 PM
 * Class Name : MasterDataRepository
 * Project Name : server
 */
@Repository
public interface MasterDataRepository extends JpaRepository<MasterData, Long> {
}
