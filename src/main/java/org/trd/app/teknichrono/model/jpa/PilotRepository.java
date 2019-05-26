package org.trd.app.teknichrono.model.jpa;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PilotRepository implements PanacheRepository<Pilot> {

    public Pilot findByName(String firstname, String lastname) {
        return find("firstName = ?1 AND lastName = ?2", firstname, lastname).firstResult();
    }
}
