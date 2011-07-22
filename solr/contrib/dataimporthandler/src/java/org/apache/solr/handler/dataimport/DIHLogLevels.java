begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.handler.dataimport
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
package|;
end_package

begin_enum
DECL|enum|DIHLogLevels
specifier|public
enum|enum
name|DIHLogLevels
block|{
DECL|enum constant|START_ENTITY
DECL|enum constant|END_ENTITY
DECL|enum constant|TRANSFORMED_ROW
DECL|enum constant|ENTITY_META
DECL|enum constant|PRE_TRANSFORMER_ROW
DECL|enum constant|START_DOC
DECL|enum constant|END_DOC
DECL|enum constant|ENTITY_OUT
DECL|enum constant|ROW_END
DECL|enum constant|TRANSFORMER_EXCEPTION
DECL|enum constant|ENTITY_EXCEPTION
DECL|enum constant|DISABLE_LOGGING
DECL|enum constant|ENABLE_LOGGING
DECL|enum constant|NONE
name|START_ENTITY
block|,
name|END_ENTITY
block|,
name|TRANSFORMED_ROW
block|,
name|ENTITY_META
block|,
name|PRE_TRANSFORMER_ROW
block|,
name|START_DOC
block|,
name|END_DOC
block|,
name|ENTITY_OUT
block|,
name|ROW_END
block|,
name|TRANSFORMER_EXCEPTION
block|,
name|ENTITY_EXCEPTION
block|,
name|DISABLE_LOGGING
block|,
name|ENABLE_LOGGING
block|,
name|NONE
block|}
end_enum

end_unit

