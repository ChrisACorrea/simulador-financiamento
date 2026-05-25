package br.gov.caixaverso.repositories.abstractions;

import br.gov.caixaverso.entities.abstractions.EntityBase;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;

public interface IRepositoryBase<TEntity extends EntityBase, TId> extends PanacheRepositoryBase<TEntity, TId> {

}
