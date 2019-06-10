package org.trd.app.teknichrono.model.jpa;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class LapTimeRepository extends PanacheRepositoryWrapper<LapTime> {

  @ApplicationScoped
  public static class Panache implements PanacheRepository<LapTime> {
  }

  private final Panache panacheRepository;

  protected LapTimeRepository() {
    // Only needed because of Weld proxy being a subtype of current type: https://stackoverflow.com/a/48418256/2989857
    this(null);
  }

  @Inject
  public LapTimeRepository(Panache panacheRepository) {
    super(panacheRepository);
    this.panacheRepository = panacheRepository;
  }
}
