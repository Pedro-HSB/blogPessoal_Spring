package com.generation.blogpessoal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.generation.blogpessoal.model.Postagem;
import com.generation.blogpessoal.model.Tema;

public interface TemaRepository extends JpaRepository<Tema,Long>{

	List<Tema> findAllByDescricaoContainingIgnoreCase(@Param("descricao")String descricao);
}

