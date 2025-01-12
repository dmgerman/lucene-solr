begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.search.spans
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|spans
package|;
end_package

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
name|search
operator|.
name|IndexSearcher
import|;
end_import

begin_comment
comment|/**  * Holds all implementations of classes in the o.a.l.s.spans package as a  * back-compatibility test. It does not run any tests per-se, however if  * someone adds a method to an interface or abstract method to an abstract  * class, one of the implementations here will fail to compile and so we know  * back-compat policy was violated.  */
end_comment

begin_class
DECL|class|JustCompileSearchSpans
specifier|final
class|class
name|JustCompileSearchSpans
block|{
DECL|field|UNSUPPORTED_MSG
specifier|private
specifier|static
specifier|final
name|String
name|UNSUPPORTED_MSG
init|=
literal|"unsupported: used for back-compat testing only !"
decl_stmt|;
DECL|class|JustCompileSpans
specifier|static
specifier|final
class|class
name|JustCompileSpans
extends|extends
name|Spans
block|{
annotation|@
name|Override
DECL|method|docID
specifier|public
name|int
name|docID
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|advance
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|startPosition
specifier|public
name|int
name|startPosition
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|endPosition
specifier|public
name|int
name|endPosition
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|width
specifier|public
name|int
name|width
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
name|SpanCollector
name|collector
parameter_list|)
throws|throws
name|IOException
block|{      }
annotation|@
name|Override
DECL|method|nextStartPosition
specifier|public
name|int
name|nextStartPosition
parameter_list|()
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|cost
specifier|public
name|long
name|cost
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|positionsCost
specifier|public
name|float
name|positionsCost
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
block|}
DECL|class|JustCompileSpanQuery
specifier|static
specifier|final
class|class
name|JustCompileSpanQuery
extends|extends
name|SpanQuery
block|{
annotation|@
name|Override
DECL|method|getField
specifier|public
name|String
name|getField
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|createWeight
specifier|public
name|SpanWeight
name|createWeight
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|boolean
name|needsScores
parameter_list|,
name|float
name|boost
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
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
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
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
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

