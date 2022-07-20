package com.revature.orm.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.revature.orm.enums.RelationshipType;

@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Relationship {
	/**
	 * the type of multiplicity relationship
	 */
	RelationshipType type();
	/**
	 * if using a join/junction table, the name of that table
	 */
	String joinTable() default "";
	/**
	 * the foreign key column in the owner table (current class)
	 */
	String ownerJoinColumn() default "";
	/**
	 * the foreign key column in the owned table (this field's type)
	 */
	String ownedJoinColumn() default "";
}