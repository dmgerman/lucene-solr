begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
name|Collection
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
name|AtomicReaderContext
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
name|Term
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
name|TermContext
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
name|Weight
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
name|similarities
operator|.
name|Similarity
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
DECL|method|doc
specifier|public
name|int
name|doc
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
DECL|method|end
specifier|public
name|int
name|end
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
DECL|method|next
specifier|public
name|boolean
name|next
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
DECL|method|skipTo
specifier|public
name|boolean
name|skipTo
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
DECL|method|start
specifier|public
name|int
name|start
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
DECL|method|getPayload
specifier|public
name|Collection
argument_list|<
name|byte
index|[]
argument_list|>
name|getPayload
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
DECL|method|isPayloadAvailable
specifier|public
name|boolean
name|isPayloadAvailable
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
DECL|method|getSpans
specifier|public
name|Spans
name|getSpans
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|,
name|Map
argument_list|<
name|Term
argument_list|,
name|TermContext
argument_list|>
name|termContexts
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
block|}
DECL|class|JustCompilePayloadSpans
specifier|static
specifier|final
class|class
name|JustCompilePayloadSpans
extends|extends
name|Spans
block|{
annotation|@
name|Override
DECL|method|getPayload
specifier|public
name|Collection
argument_list|<
name|byte
index|[]
argument_list|>
name|getPayload
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
DECL|method|isPayloadAvailable
specifier|public
name|boolean
name|isPayloadAvailable
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
DECL|method|doc
specifier|public
name|int
name|doc
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
DECL|method|end
specifier|public
name|int
name|end
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
DECL|method|next
specifier|public
name|boolean
name|next
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
DECL|method|skipTo
specifier|public
name|boolean
name|skipTo
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
DECL|method|start
specifier|public
name|int
name|start
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
DECL|class|JustCompileSpanScorer
specifier|static
specifier|final
class|class
name|JustCompileSpanScorer
extends|extends
name|SpanScorer
block|{
DECL|method|JustCompileSpanScorer
specifier|protected
name|JustCompileSpanScorer
parameter_list|(
name|Spans
name|spans
parameter_list|,
name|Weight
name|weight
parameter_list|,
name|Similarity
operator|.
name|SloppySimScorer
name|docScorer
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|spans
argument_list|,
name|weight
argument_list|,
name|docScorer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setFreqCurrentDoc
specifier|protected
name|boolean
name|setFreqCurrentDoc
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
block|}
block|}
end_class

end_unit

