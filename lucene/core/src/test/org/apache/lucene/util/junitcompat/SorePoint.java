begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util.junitcompat
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|junitcompat
package|;
end_package

begin_comment
comment|/**  * A pointcut-like definition where we should trigger  * an assumption or error.  */
end_comment

begin_enum
DECL|enum|SorePoint
specifier|public
enum|enum
name|SorePoint
block|{
comment|// STATIC_INITIALIZER, // I assume this will result in JUnit failure to load a suite.
DECL|enum constant|BEFORE_CLASS
name|BEFORE_CLASS
block|,
DECL|enum constant|INITIALIZER
name|INITIALIZER
block|,
DECL|enum constant|RULE
name|RULE
block|,
DECL|enum constant|BEFORE
name|BEFORE
block|,
DECL|enum constant|TEST
name|TEST
block|,
DECL|enum constant|AFTER
name|AFTER
block|,
DECL|enum constant|AFTER_CLASS
name|AFTER_CLASS
block|}
end_enum

end_unit

