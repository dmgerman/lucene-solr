begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.search.function.distance
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|function
operator|.
name|distance
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexReader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|spell
operator|.
name|StringDistance
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|function
operator|.
name|DocValues
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|function
operator|.
name|ValueSource
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  *  *  **/
end_comment

begin_class
DECL|class|StringDistanceFunction
specifier|public
class|class
name|StringDistanceFunction
extends|extends
name|ValueSource
block|{
DECL|field|str1
DECL|field|str2
specifier|protected
name|ValueSource
name|str1
decl_stmt|,
name|str2
decl_stmt|;
DECL|field|dist
specifier|protected
name|StringDistance
name|dist
decl_stmt|;
comment|/**    * @param str1    * @param str2    * @param measure    */
DECL|method|StringDistanceFunction
specifier|public
name|StringDistanceFunction
parameter_list|(
name|ValueSource
name|str1
parameter_list|,
name|ValueSource
name|str2
parameter_list|,
name|StringDistance
name|measure
parameter_list|)
block|{
name|this
operator|.
name|str1
operator|=
name|str1
expr_stmt|;
name|this
operator|.
name|str2
operator|=
name|str2
expr_stmt|;
name|dist
operator|=
name|measure
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getValues
specifier|public
name|DocValues
name|getValues
parameter_list|(
name|Map
name|context
parameter_list|,
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|DocValues
name|str1DV
init|=
name|str1
operator|.
name|getValues
argument_list|(
name|context
argument_list|,
name|reader
argument_list|)
decl_stmt|;
specifier|final
name|DocValues
name|str2DV
init|=
name|str2
operator|.
name|getValues
argument_list|(
name|context
argument_list|,
name|reader
argument_list|)
decl_stmt|;
return|return
operator|new
name|DocValues
argument_list|()
block|{
specifier|public
name|float
name|floatVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
operator|(
name|float
operator|)
name|dist
operator|.
name|getDistance
argument_list|(
name|str1DV
operator|.
name|strVal
argument_list|(
name|doc
argument_list|)
argument_list|,
name|str2DV
operator|.
name|strVal
argument_list|(
name|doc
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|int
name|intVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
operator|(
name|int
operator|)
name|doubleVal
argument_list|(
name|doc
argument_list|)
return|;
block|}
specifier|public
name|long
name|longVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
operator|(
name|long
operator|)
name|doubleVal
argument_list|(
name|doc
argument_list|)
return|;
block|}
specifier|public
name|double
name|doubleVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
operator|(
name|double
operator|)
name|floatVal
argument_list|(
name|doc
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"strdist"
argument_list|)
operator|.
name|append
argument_list|(
literal|'('
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|str1DV
operator|.
name|toString
argument_list|(
name|doc
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|','
argument_list|)
operator|.
name|append
argument_list|(
name|str2DV
operator|.
name|toString
argument_list|(
name|doc
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|", dist="
argument_list|)
operator|.
name|append
argument_list|(
name|dist
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
return|;
block|}
DECL|method|description
specifier|public
name|String
name|description
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"strdist"
argument_list|)
operator|.
name|append
argument_list|(
literal|'('
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|str1
argument_list|)
operator|.
name|append
argument_list|(
literal|','
argument_list|)
operator|.
name|append
argument_list|(
name|str2
argument_list|)
operator|.
name|append
argument_list|(
literal|", dist="
argument_list|)
operator|.
name|append
argument_list|(
name|dist
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|StringDistanceFunction
operator|)
condition|)
return|return
literal|false
return|;
name|StringDistanceFunction
name|that
init|=
operator|(
name|StringDistanceFunction
operator|)
name|o
decl_stmt|;
if|if
condition|(
operator|!
name|dist
operator|.
name|equals
argument_list|(
name|that
operator|.
name|dist
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|str1
operator|.
name|equals
argument_list|(
name|that
operator|.
name|str1
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|str2
operator|.
name|equals
argument_list|(
name|that
operator|.
name|str2
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|result
init|=
name|str1
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|str2
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|dist
operator|.
name|hashCode
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

