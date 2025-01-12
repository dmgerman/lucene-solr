begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|LeafReaderContext
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
name|queries
operator|.
name|function
operator|.
name|FunctionValues
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
name|queries
operator|.
name|function
operator|.
name|ValueSource
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
name|queries
operator|.
name|function
operator|.
name|docvalues
operator|.
name|FloatDocValues
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
name|FunctionValues
name|getValues
parameter_list|(
name|Map
name|context
parameter_list|,
name|LeafReaderContext
name|readerContext
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|FunctionValues
name|str1DV
init|=
name|str1
operator|.
name|getValues
argument_list|(
name|context
argument_list|,
name|readerContext
argument_list|)
decl_stmt|;
specifier|final
name|FunctionValues
name|str2DV
init|=
name|str2
operator|.
name|getValues
argument_list|(
name|context
argument_list|,
name|readerContext
argument_list|)
decl_stmt|;
return|return
operator|new
name|FloatDocValues
argument_list|(
name|this
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|float
name|floatVal
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|s1
init|=
name|str1DV
operator|.
name|strVal
argument_list|(
name|doc
argument_list|)
decl_stmt|;
name|String
name|s2
init|=
name|str2DV
operator|.
name|strVal
argument_list|(
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|s1
operator|||
literal|null
operator|==
name|s2
condition|)
block|{
comment|// the only thing a missing value scores 1.0 with is another missing value
return|return
operator|(
name|s1
operator|==
name|s2
operator|)
condition|?
literal|1.0F
else|:
literal|0.0F
return|;
block|}
return|return
name|dist
operator|.
name|getDistance
argument_list|(
name|s1
argument_list|,
name|s2
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|exists
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|str1DV
operator|.
name|exists
argument_list|(
name|doc
argument_list|)
operator|&&
name|str2DV
operator|.
name|exists
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
throws|throws
name|IOException
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
annotation|@
name|Override
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

