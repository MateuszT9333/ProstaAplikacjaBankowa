package pl.mateusz.springdemo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.mateusz.springdemo.model.UserHistory;
import pl.mateusz.springdemo.model.UserLogPass;

@Repository
public interface UserHistoryRepository extends JpaRepository<UserHistory, Long> {

}
