begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|TestRule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|Description
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|model
operator|.
name|Statement
import|;
end_import

begin_comment
comment|/**  * Restore system properties from before the nested {@link Statement}.  */
end_comment

begin_class
DECL|class|SystemPropertiesRestoreRule
specifier|public
class|class
name|SystemPropertiesRestoreRule
implements|implements
name|TestRule
block|{
annotation|@
name|Override
DECL|method|apply
specifier|public
name|Statement
name|apply
parameter_list|(
specifier|final
name|Statement
name|s
parameter_list|,
name|Description
name|d
parameter_list|)
block|{
return|return
operator|new
name|Statement
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|evaluate
parameter_list|()
throws|throws
name|Throwable
block|{
name|TreeMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|before
init|=
name|cloneAsMap
argument_list|(
name|System
operator|.
name|getProperties
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|s
operator|.
name|evaluate
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|TreeMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|after
init|=
name|cloneAsMap
argument_list|(
name|System
operator|.
name|getProperties
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|after
operator|.
name|equals
argument_list|(
name|before
argument_list|)
condition|)
block|{
comment|// Restore original properties.
name|restore
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
return|;
block|}
DECL|method|cloneAsMap
specifier|static
name|TreeMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|cloneAsMap
parameter_list|(
name|Properties
name|properties
parameter_list|)
block|{
name|TreeMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|result
init|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Enumeration
argument_list|<
name|?
argument_list|>
name|e
init|=
name|properties
operator|.
name|propertyNames
argument_list|()
init|;
name|e
operator|.
name|hasMoreElements
argument_list|()
condition|;
control|)
block|{
specifier|final
name|Object
name|key
init|=
name|e
operator|.
name|nextElement
argument_list|()
decl_stmt|;
comment|// Skip non-string properties or values, they're abuse of Properties object.
if|if
condition|(
name|key
operator|instanceof
name|String
condition|)
block|{
name|String
name|value
init|=
name|properties
operator|.
name|getProperty
argument_list|(
operator|(
name|String
operator|)
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
name|Object
name|ovalue
init|=
name|properties
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|ovalue
operator|!=
literal|null
condition|)
block|{
comment|// ovalue has to be a non-string object. Skip the property because
comment|// System.clearProperty won't be able to cast back the existing value.
continue|continue;
block|}
block|}
name|result
operator|.
name|put
argument_list|(
operator|(
name|String
operator|)
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
DECL|method|restore
specifier|static
name|void
name|restore
parameter_list|(
name|TreeMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|before
parameter_list|,
name|TreeMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|after
parameter_list|)
block|{
name|after
operator|.
name|keySet
argument_list|()
operator|.
name|removeAll
argument_list|(
name|before
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|key
range|:
name|after
operator|.
name|keySet
argument_list|()
control|)
block|{
name|System
operator|.
name|clearProperty
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|e
range|:
name|before
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|e
operator|.
name|getValue
argument_list|()
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|clearProperty
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
comment|// Can this happen?
block|}
else|else
block|{
name|System
operator|.
name|setProperty
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

