package org.trd.app.teknichrono.model.jpa;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ChronometerRepository implements PanacheRepository<Chronometer> {

    public Chronometer findByName(String name) {
        return find("name", name).firstResult();
    }
}
