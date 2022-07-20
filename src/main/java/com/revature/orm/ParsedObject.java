package com.revature.orm;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.revature.orm.annotations.PrimaryKey;
import com.revature.orm.annotations.Relationship;
import com.revature.orm.annotations.Table;
import com.revature.orm.annotations.Transient;
import com.revature.orm.enums.RelationshipType;

public class ParsedObject {
	private Class originalType;
	private String tableName;
	private String primaryKeyField;
	private String primaryKeyColumn;
	/**
	 *  key: field name, value: column name
	 */
	private Map<String, String> columns;
	/**
	 *  key: field name, value: column name
	 */
	private Map<String, String> nonRelationshipColumns;
	private Map<ParsedObject, RelationshipInfo> relationships;
	
	public <T> ParsedObject(Class<T> type) {
		this.originalType=type;
		this.mapBaseTable(type);
		this.mapRelationships(type);
	}

	private String getSnakeCase(String camelCase) {
		// split the words on the capital letters
		String[] words = camelCase.toString().split("[A-Z]");

		// start building the new snake case string
		StringBuilder finalFieldName = new StringBuilder(words[0]);
		int nextCapitalIndex = 0;
		for (int i = 1; i < words.length; i++) {
			// the index of the next capital letter: add the previous index
			// to the length of the next word in the words array, plus 1 if
			// we're not working with the first word
			nextCapitalIndex += words[i - 1].length() + ((i == 1) ? 0 : 1);
			finalFieldName.append("_");
			finalFieldName.append(camelCase.toString().toLowerCase().charAt(nextCapitalIndex));
			finalFieldName.append(words[i]);
		}
		return finalFieldName.toString();
	}
	
	private RelationshipInfo getRelationship(Field field) {
		Annotation relationship = field.getAnnotation(Relationship.class);
		try {
			Method method = relationship.getClass().getMethod("type");
			RelationshipType type = (RelationshipType) method.invoke(relationship);
			
			method = relationship.getClass().getMethod("joinTable");
			String joinTable = (String) method.invoke(relationship);
			
			method = relationship.getClass().getMethod("ownerJoinColumn");
			String ownerJoinCol = (String) method.invoke(relationship);
			
			method = relationship.getClass().getMethod("ownedJoinColumn");
			String ownedJoinCol = (String) method.invoke(relationship);
			
			RelationshipInfo info = new RelationshipInfo(type, joinTable, ownerJoinCol, ownedJoinCol);
			return info;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private <T> void mapBaseTable(Class<T> type) {
		if (type.isAnnotationPresent(Table.class)) {
			try {
				this.tableName = (String) type
						.getAnnotation(Table.class).getClass()
						.getMethod("name").invoke(type.getAnnotation(Table.class));
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			StringBuilder className = new StringBuilder(type.getName());
			className.delete(0, className.lastIndexOf(".")+1);
			className.replace(0, 1, String.valueOf(className.charAt(0)).toLowerCase());
			this.tableName = getSnakeCase(className.toString());
		}
		
		Field[] fields = type.getDeclaredFields();
		this.columns = new HashMap<>();
		this.nonRelationshipColumns = new HashMap<>();
		
		for (Field field : fields) {
			if (field.getAnnotation(Transient.class) == null) {
				StringBuilder fieldString = new StringBuilder(field.toString());

				// the field String starts by including modifiers, types, and the full class
				// path
				// so this delete just skips straight to the field name by starting at the last
				// "."
				fieldString.delete(0, fieldString.lastIndexOf(".") + 1);

				String finalFieldName = null;
				// if field name is camel case w/ multiple words
				if (fieldString.toString().matches("[a-z]+[A-Z]+\\w*")) {
					if (field.getAnnotation(Relationship.class) == null) {
						finalFieldName = getSnakeCase(fieldString.toString());
					} else {
						String joinTable = field.getAnnotation(Relationship.class).joinTable();
						// if there is no join table
						if ("".equals(joinTable) || joinTable == null) {
							String joinColumn = field.getAnnotation(Relationship.class).ownerJoinColumn();
							// if the join column is not empty
							if (!"".equals(joinColumn)) {
								finalFieldName = joinColumn;
							}
						}
					}
				} else if (!"this$0".equals(fieldString.toString())) {
					// if it's not a multi-word/camelcase field and it's not the this$0 field,
					// add it to the strings directly
					if (field.getAnnotation(Relationship.class) == null) {
						finalFieldName = fieldString.toString();
					} else {
						String joinTable = field.getAnnotation(Relationship.class).joinTable();
						// if there is no join table
						if ("".equals(joinTable) || joinTable == null) {
							String joinColumn = field.getAnnotation(Relationship.class).ownerJoinColumn();
							// if the join column is not empty
							if (!"".equals(joinColumn)) {
								finalFieldName = joinColumn;
							}
						}
					}
				}
				if (finalFieldName != null) {
					columns.put(fieldString.toString(), finalFieldName.toString());
					if (field.getAnnotation(Relationship.class) == null) {
						nonRelationshipColumns.put(fieldString.toString(), finalFieldName.toString());
					}
				}

				// save the primary key field
				if (field.getAnnotation(PrimaryKey.class) != null) {
					this.primaryKeyField = fieldString.toString();
					this.primaryKeyColumn = finalFieldName;
				}
			}
		}
	}
	
	private <T> void mapRelationships(Class<T> type) {
		relationships = new HashMap<>();
		Field[] fields = type.getDeclaredFields();
		for (Field field : fields) {
			if (field.getAnnotation(Relationship.class) != null) {
				ParsedObject fieldType = new ParsedObject(field.getType());
				
				RelationshipInfo relationshipInfo = getRelationship(field);
				relationships.put(fieldType, relationshipInfo);
			}
		}
	}

	public Class getOriginalType() {
		return originalType;
	}
	
	public String getTableName() {
		return tableName;
	}
	
	public String getPrimaryKeyField() {
		return primaryKeyField;
	}

	public String getPrimaryKeyColumn() {
		return primaryKeyColumn;
	}
	
	public Map<String, String> getColumns() {
		return columns;
	}

	public Map<String, String> getNonRelationshipColumns() {
		return nonRelationshipColumns;
	}
	
	public Map<ParsedObject, RelationshipInfo> getRelationships() {
		return relationships;
	}

	public class RelationshipInfo {
		private RelationshipType type;
		private String joinTable;
		private String ownerJoinColumn;
		private String ownedJoinColumn;
		
		RelationshipInfo (RelationshipType type, String joinTable, String ownerJoinCol, String ownedJoinCol) {
			this.type=type;
			this.joinTable=joinTable;
			this.ownerJoinColumn=ownerJoinCol;
			this.ownedJoinColumn=ownedJoinCol;
		}

		public RelationshipType getType() {
			return type;
		}

		public void setType(RelationshipType type) {
			this.type = type;
		}

		public String getJoinTable() {
			return joinTable;
		}

		public void setJoinTable(String joinTable) {
			this.joinTable = joinTable;
		}

		public String getOwnerJoinColumn() {
			return ownerJoinColumn;
		}

		public void setOwnerJoinColumn(String ownerJoinColumn) {
			this.ownerJoinColumn = ownerJoinColumn;
		}

		public String getOwnedJoinColumn() {
			return ownedJoinColumn;
		}

		public void setOwnedJoinColumn(String ownedJoinColumn) {
			this.ownedJoinColumn = ownedJoinColumn;
		}
	}
}