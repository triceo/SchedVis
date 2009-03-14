/**
 * Hibernate dialect for SQLite, as found at:
 * http://elbart0.free.fr/SQLiteDialect.java.txt
 */
package org.hibernate.dialect;

import java.sql.Types;

import org.hibernate.Hibernate;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.dialect.function.VarArgsSQLFunction;

public class SQLiteDialect extends Dialect {
    public SQLiteDialect() {
	this.registerColumnType(Types.BIT, "integer");
	this.registerColumnType(Types.TINYINT, "tinyint");
	this.registerColumnType(Types.SMALLINT, "smallint");
	this.registerColumnType(Types.INTEGER, "integer");
	this.registerColumnType(Types.BIGINT, "bigint");
	this.registerColumnType(Types.FLOAT, "float");
	this.registerColumnType(Types.REAL, "real");
	this.registerColumnType(Types.DOUBLE, "double");
	this.registerColumnType(Types.NUMERIC, "numeric");
	this.registerColumnType(Types.DECIMAL, "decimal");
	this.registerColumnType(Types.CHAR, "char");
	this.registerColumnType(Types.VARCHAR, "varchar");
	this.registerColumnType(Types.LONGVARCHAR, "longvarchar");
	this.registerColumnType(Types.DATE, "date");
	this.registerColumnType(Types.TIME, "time");
	this.registerColumnType(Types.TIMESTAMP, "timestamp");
	this.registerColumnType(Types.BINARY, "blob");
	this.registerColumnType(Types.VARBINARY, "blob");
	this.registerColumnType(Types.LONGVARBINARY, "blob");
	// registerColumnType(Types.NULL, "null");
	this.registerColumnType(Types.BLOB, "blob");
	this.registerColumnType(Types.CLOB, "clob");
	this.registerColumnType(Types.BOOLEAN, "integer");

	this.registerFunction("concat", new VarArgsSQLFunction(
		Hibernate.STRING, "", "||", ""));
	this.registerFunction("mod", new SQLFunctionTemplate(Hibernate.INTEGER,
	"?1 % ?2"));
	this.registerFunction("substr", new StandardSQLFunction("substr",
		Hibernate.STRING));
	this.registerFunction("substring", new StandardSQLFunction("substr",
		Hibernate.STRING));
    }

    @Override
    public boolean dropConstraints() {
	return false;
    }

    /*
     * public boolean supportsInsertSelectIdentity() { return true; // As
     * specify in NHibernate dialect }
     */

    @Override
    public boolean dropTemporaryTableAfterUse() {
	return false;
    }

    /*
     * public String appendIdentitySelectToInsert(String insertString) { return
     * new StringBuffer(insertString.length()+30). // As specify in NHibernate
     * dialect append(insertString).
     * append("; ").append(getIdentitySelectString()). toString(); }
     */

    @Override
    public String getAddColumnString() {
	return "add column";
    }

    @Override
    public String getAddForeignKeyConstraintString(final String constraintName,
	    final String[] foreignKey, final String referencedTable,
	    final String[] primaryKey, final boolean referencesPrimaryKey) {
	throw new UnsupportedOperationException(
	"No add foreign key syntax supported by SQLiteDialect");
    }

    @Override
    public String getAddPrimaryKeyConstraintString(final String constraintName) {
	throw new UnsupportedOperationException(
	"No add primary key syntax supported by SQLiteDialect");
    }

    @Override
    public String getCreateTemporaryTableString() {
	return "create temporary table if not exists";
    }

    @Override
    public String getCurrentTimestampSelectString() {
	return "select current_timestamp";
    }

    @Override
    public String getDropForeignKeyString() {
	throw new UnsupportedOperationException(
	"No drop foreign key syntax supported by SQLiteDialect");
    }

    @Override
    public String getForUpdateString() {
	return "";
    }

    @Override
    public String getIdentityColumnString() {
	// return "integer primary key autoincrement";
	return "integer";
    }

    @Override
    public String getIdentitySelectString() {
	return "select last_insert_rowid()";
    }

    @Override
    protected String getLimitString(final String query, final boolean hasOffset) {
	return new StringBuffer(query.length() + 20).append(query).append(
		hasOffset ? " limit ? offset ?" : " limit ?").toString();
    }

    @Override
    public boolean hasAlterTable() {
	return false; // As specify in NHibernate dialect
    }

    @Override
    public boolean hasDataTypeInIdentityColumn() {
	return false; // As specify in NHibernate dialect
    }

    @Override
    public boolean isCurrentTimestampSelectStringCallable() {
	return false;
    }

    @Override
    public boolean supportsCascadeDelete() {
	return false;
    }

    @Override
    public boolean supportsCurrentTimestampSelection() {
	return true;
    }

    @Override
    public boolean supportsIdentityColumns() {
	return true;
    }

    @Override
    public boolean supportsIfExistsBeforeTableName() {
	return true;
    }

    @Override
    public boolean supportsLimit() {
	return true;
    }

    @Override
    public boolean supportsOuterJoinForUpdate() {
	return false;
    }

    @Override
    public boolean supportsTemporaryTables() {
	return true;
    }

    @Override
    public boolean supportsUnionAll() {
	return true;
    }
}
