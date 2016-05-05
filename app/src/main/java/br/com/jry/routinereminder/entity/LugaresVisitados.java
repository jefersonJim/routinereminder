package br.com.jry.routinereminder.entity;

import java.io.Serializable;

/**
 * Created by betoe on 04/05/2016.
 */
public class LugaresVisitados implements Serializable {

    private Integer id;
    private String titulo;
    private String descricao;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
