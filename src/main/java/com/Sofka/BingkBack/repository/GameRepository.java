package com.Sofka.BingkBack.repository;

import com.Sofka.BingkBack.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface GameRepository extends JpaRepository<Game, Long> {



}
