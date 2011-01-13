begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search.function
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
name|index
operator|.
name|IndexReader
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
name|search
operator|.
name|*
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
name|ReaderUtil
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
name|common
operator|.
name|SolrException
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
comment|/**  *<code>QueryValueSource</code> returns the relevance score of the query  */
end_comment

begin_class
DECL|class|QueryValueSource
specifier|public
class|class
name|QueryValueSource
extends|extends
name|ValueSource
block|{
DECL|field|q
specifier|final
name|Query
name|q
decl_stmt|;
DECL|field|defVal
specifier|final
name|float
name|defVal
decl_stmt|;
DECL|method|QueryValueSource
specifier|public
name|QueryValueSource
parameter_list|(
name|Query
name|q
parameter_list|,
name|float
name|defVal
parameter_list|)
block|{
name|this
operator|.
name|q
operator|=
name|q
expr_stmt|;
name|this
operator|.
name|defVal
operator|=
name|defVal
expr_stmt|;
block|}
DECL|method|getQuery
specifier|public
name|Query
name|getQuery
parameter_list|()
block|{
return|return
name|q
return|;
block|}
DECL|method|getDefaultValue
specifier|public
name|float
name|getDefaultValue
parameter_list|()
block|{
return|return
name|defVal
return|;
block|}
DECL|method|description
specifier|public
name|String
name|description
parameter_list|()
block|{
return|return
literal|"query("
operator|+
name|q
operator|+
literal|",def="
operator|+
name|defVal
operator|+
literal|")"
return|;
block|}
annotation|@
name|Override
DECL|method|getValues
specifier|public
name|DocValues
name|getValues
parameter_list|(
name|Map
name|fcontext
parameter_list|,
name|AtomicReaderContext
name|readerContext
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|QueryDocValues
argument_list|(
name|readerContext
argument_list|,
name|q
argument_list|,
name|defVal
argument_list|,
name|fcontext
argument_list|)
return|;
block|}
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|q
operator|.
name|hashCode
argument_list|()
operator|*
literal|29
return|;
block|}
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
name|QueryValueSource
operator|.
name|class
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|QueryValueSource
name|other
init|=
operator|(
name|QueryValueSource
operator|)
name|o
decl_stmt|;
return|return
name|this
operator|.
name|q
operator|.
name|equals
argument_list|(
name|other
operator|.
name|q
argument_list|)
operator|&&
name|this
operator|.
name|defVal
operator|==
name|other
operator|.
name|defVal
return|;
block|}
annotation|@
name|Override
DECL|method|createWeight
specifier|public
name|void
name|createWeight
parameter_list|(
name|Map
name|context
parameter_list|,
name|IndexSearcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
name|Weight
name|w
init|=
name|q
operator|.
name|weight
argument_list|(
name|searcher
argument_list|)
decl_stmt|;
name|context
operator|.
name|put
argument_list|(
name|this
argument_list|,
name|w
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_class
DECL|class|QueryDocValues
class|class
name|QueryDocValues
extends|extends
name|DocValues
block|{
DECL|field|q
specifier|final
name|Query
name|q
decl_stmt|;
comment|//  final IndexReader reader;
DECL|field|readerContext
specifier|final
name|AtomicReaderContext
name|readerContext
decl_stmt|;
DECL|field|weight
specifier|final
name|Weight
name|weight
decl_stmt|;
DECL|field|defVal
specifier|final
name|float
name|defVal
decl_stmt|;
DECL|field|fcontext
specifier|final
name|Map
name|fcontext
decl_stmt|;
DECL|field|scorer
name|Scorer
name|scorer
decl_stmt|;
DECL|field|scorerDoc
name|int
name|scorerDoc
decl_stmt|;
comment|// the document the scorer is on
comment|// the last document requested... start off with high value
comment|// to trigger a scorer reset on first access.
DECL|field|lastDocRequested
name|int
name|lastDocRequested
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
DECL|method|QueryDocValues
specifier|public
name|QueryDocValues
parameter_list|(
name|AtomicReaderContext
name|readerContext
parameter_list|,
name|Query
name|q
parameter_list|,
name|float
name|defVal
parameter_list|,
name|Map
name|fcontext
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexReader
name|reader
init|=
name|readerContext
operator|.
name|reader
decl_stmt|;
name|this
operator|.
name|readerContext
operator|=
name|readerContext
expr_stmt|;
name|this
operator|.
name|q
operator|=
name|q
expr_stmt|;
name|this
operator|.
name|defVal
operator|=
name|defVal
expr_stmt|;
name|this
operator|.
name|fcontext
operator|=
name|fcontext
expr_stmt|;
name|Weight
name|w
init|=
name|fcontext
operator|==
literal|null
condition|?
literal|null
else|:
operator|(
name|Weight
operator|)
name|fcontext
operator|.
name|get
argument_list|(
name|q
argument_list|)
decl_stmt|;
comment|// TODO: sort by function doesn't weight (SOLR-1297 is open because of this bug)... so weightSearcher will currently be null
if|if
condition|(
name|w
operator|==
literal|null
condition|)
block|{
name|IndexSearcher
name|weightSearcher
decl_stmt|;
if|if
condition|(
name|fcontext
operator|==
literal|null
condition|)
block|{
name|weightSearcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|ReaderUtil
operator|.
name|getTopLevelContext
argument_list|(
name|readerContext
argument_list|)
argument_list|,
name|readerContext
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|weightSearcher
operator|=
operator|(
name|IndexSearcher
operator|)
name|fcontext
operator|.
name|get
argument_list|(
literal|"searcher"
argument_list|)
expr_stmt|;
if|if
condition|(
name|weightSearcher
operator|==
literal|null
condition|)
block|{
name|weightSearcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|ReaderUtil
operator|.
name|getTopLevelContext
argument_list|(
name|readerContext
argument_list|)
argument_list|,
name|readerContext
argument_list|)
expr_stmt|;
block|}
block|}
name|w
operator|=
name|q
operator|.
name|weight
argument_list|(
name|weightSearcher
argument_list|)
expr_stmt|;
block|}
name|weight
operator|=
name|w
expr_stmt|;
block|}
DECL|method|floatVal
specifier|public
name|float
name|floatVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|doc
operator|<
name|lastDocRequested
condition|)
block|{
name|scorer
operator|=
name|weight
operator|.
name|scorer
argument_list|(
name|readerContext
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|scorer
operator|==
literal|null
condition|)
return|return
name|defVal
return|;
name|scorerDoc
operator|=
operator|-
literal|1
expr_stmt|;
block|}
name|lastDocRequested
operator|=
name|doc
expr_stmt|;
if|if
condition|(
name|scorerDoc
operator|<
name|doc
condition|)
block|{
name|scorerDoc
operator|=
name|scorer
operator|.
name|advance
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|scorerDoc
operator|>
name|doc
condition|)
block|{
comment|// query doesn't match this document... either because we hit the
comment|// end, or because the next doc is after this doc.
return|return
name|defVal
return|;
block|}
comment|// a match!
return|return
name|scorer
operator|.
name|score
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"caught exception in QueryDocVals("
operator|+
name|q
operator|+
literal|") doc="
operator|+
name|doc
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|intVal
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
name|floatVal
argument_list|(
name|doc
argument_list|)
return|;
block|}
DECL|method|longVal
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
name|floatVal
argument_list|(
name|doc
argument_list|)
return|;
block|}
DECL|method|doubleVal
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
DECL|method|strVal
specifier|public
name|String
name|strVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|Float
operator|.
name|toString
argument_list|(
name|floatVal
argument_list|(
name|doc
argument_list|)
argument_list|)
return|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
literal|"query("
operator|+
name|q
operator|+
literal|",def="
operator|+
name|defVal
operator|+
literal|")="
operator|+
name|floatVal
argument_list|(
name|doc
argument_list|)
return|;
block|}
block|}
end_class

end_unit

