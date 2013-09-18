begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.expressions
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|expressions
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
name|search
operator|.
name|FieldComparator
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
name|SortField
import|;
end_import

begin_comment
comment|/** A {@link SortField} which sorts documents by the evaluated value of an expression for each document */
end_comment

begin_class
DECL|class|ExpressionSortField
class|class
name|ExpressionSortField
extends|extends
name|SortField
block|{
DECL|field|source
specifier|private
specifier|final
name|ValueSource
name|source
decl_stmt|;
DECL|method|ExpressionSortField
name|ExpressionSortField
parameter_list|(
name|String
name|name
parameter_list|,
name|ValueSource
name|source
parameter_list|,
name|boolean
name|reverse
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|Type
operator|.
name|CUSTOM
argument_list|,
name|reverse
argument_list|)
expr_stmt|;
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getComparator
specifier|public
name|FieldComparator
argument_list|<
name|?
argument_list|>
name|getComparator
parameter_list|(
specifier|final
name|int
name|numHits
parameter_list|,
specifier|final
name|int
name|sortPos
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|ExpressionComparator
argument_list|(
name|source
argument_list|,
name|numHits
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|needsScores
specifier|public
name|boolean
name|needsScores
parameter_list|()
block|{
return|return
literal|true
return|;
comment|// TODO: maybe we can optimize by "figuring this out" somehow...
block|}
block|}
end_class

end_unit

