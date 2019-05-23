package org.trd.app.teknichrono.model.jpa;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BeaconRepository implements PanacheRepository<Beacon> {

    public Beacon findByNumber(long number) {
        return find("number", number).firstResult();
    }
}
