/*
    This file is part of SchedVis.

    SchedVis is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    SchedVis is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with SchedVis.  If not, see <http://www.gnu.org/licenses/>.

 */
/**
 * 
 */
package cz.muni.fi.spc.SchedVis.model;

import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public abstract class BaseEntity implements Cloneable {

    protected static Criteria getCriteria(final Class<?> clazz,
	    final boolean cacheable) {
	final Criteria crit = Database.getSession().createCriteria(clazz);
	crit.setCacheMode(CacheMode.NORMAL);
	crit.setCacheable(cacheable);
	return crit;
    }

}
