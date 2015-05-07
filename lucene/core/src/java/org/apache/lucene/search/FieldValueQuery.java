begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|Objects
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
name|index
operator|.
name|LeafReader
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
name|util
operator|.
name|Bits
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
name|util
operator|.
name|ToStringUtils
import|;
end_import

begin_comment
comment|/**  * A {@link Query} that matches documents that have a value for a given field  * as reported by {@link LeafReader#getDocsWithField(String)}.  */
end_comment

begin_class
DECL|class|FieldValueQuery
specifier|public
specifier|final
class|class
name|FieldValueQuery
extends|extends
name|Query
block|{
DECL|field|field
specifier|private
specifier|final
name|String
name|field
decl_stmt|;
comment|/** Create a query that will match that have a value for the given    *  {@code field}. */
DECL|method|FieldValueQuery
specifier|public
name|FieldValueQuery
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|instanceof
name|FieldValueQuery
operator|==
literal|false
condition|)
block|{
return|return
literal|false
return|;
block|}
specifier|final
name|FieldValueQuery
name|that
init|=
operator|(
name|FieldValueQuery
operator|)
name|obj
decl_stmt|;
return|return
name|super
operator|.
name|equals
argument_list|(
name|obj
argument_list|)
operator|&&
name|field
operator|.
name|equals
argument_list|(
name|that
operator|.
name|field
argument_list|)
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
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|getClass
argument_list|()
argument_list|,
name|field
argument_list|,
name|getBoost
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
literal|"FieldValueQuery [field="
operator|+
name|this
operator|.
name|field
operator|+
literal|"]"
operator|+
name|ToStringUtils
operator|.
name|boost
argument_list|(
name|getBoost
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|createWeight
specifier|public
name|Weight
name|createWeight
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|boolean
name|needsScores
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|RandomAccessWeight
argument_list|(
name|this
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|Bits
name|getMatchingDocs
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|context
operator|.
name|reader
argument_list|()
operator|.
name|getDocsWithField
argument_list|(
name|field
argument_list|)
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

