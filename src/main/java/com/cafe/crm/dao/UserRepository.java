package com.cafe.crm.dao;

import com.cafe.crm.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Created by User on 18.04.2017.
 */
public interface UserRepository extends JpaRepository<User,Long> {



    @Query("SELECT u FROM User u WHERE u.login =:name")
    User getUserByName(@Param("name")String name);



}