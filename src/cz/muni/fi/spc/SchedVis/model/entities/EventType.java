package cz.muni.fi.spc.SchedVis.model.entities;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import cz.muni.fi.spc.SchedVis.model.BaseEntity;

@Entity
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class EventType extends BaseEntity {

    private String name;
    private Integer id;

    @Id
    public Integer getId() {
	return this.id;
    }

    public String getName() {
	return this.name;
    }

    public void setId(final Integer id) {
	this.id = id;
    }

    public void setName(final String name) {
	this.name = name;
    }

}
