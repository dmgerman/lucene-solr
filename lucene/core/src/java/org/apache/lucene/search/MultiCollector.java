begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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

begin_comment
comment|/**  * A {@link Collector} which allows running a search with several  * {@link Collector}s. It offers a static {@link #wrap} method which accepts a  * list of collectors and wraps them with {@link MultiCollector}, while  * filtering out the<code>null</code> null ones.  */
end_comment

begin_class
DECL|class|MultiCollector
specifier|public
class|class
name|MultiCollector
implements|implements
name|Collector
block|{
comment|/** See {@link #wrap(Iterable)}. */
DECL|method|wrap
specifier|public
specifier|static
name|Collector
name|wrap
parameter_list|(
name|Collector
modifier|...
name|collectors
parameter_list|)
block|{
return|return
name|wrap
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|collectors
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Wraps a list of {@link Collector}s with a {@link MultiCollector}. This    * method works as follows:    *<ul>    *<li>Filters out the<code>null</code> collectors, so they are not used    * during search time.    *<li>If the input contains 1 real collector (i.e. non-<code>null</code> ),    * it is returned.    *<li>Otherwise the method returns a {@link MultiCollector} which wraps the    * non-<code>null</code> ones.    *</ul>    *     * @throws IllegalArgumentException    *           if either 0 collectors were input, or all collectors are    *<code>null</code>.    */
DECL|method|wrap
specifier|public
specifier|static
name|Collector
name|wrap
parameter_list|(
name|Iterable
argument_list|<
name|?
extends|extends
name|Collector
argument_list|>
name|collectors
parameter_list|)
block|{
comment|// For the user's convenience, we allow null collectors to be passed.
comment|// However, to improve performance, these null collectors are found
comment|// and dropped from the array we save for actual collection time.
name|int
name|n
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Collector
name|c
range|:
name|collectors
control|)
block|{
if|if
condition|(
name|c
operator|!=
literal|null
condition|)
block|{
name|n
operator|++
expr_stmt|;
block|}
block|}
if|if
condition|(
name|n
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"At least 1 collector must not be null"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|n
operator|==
literal|1
condition|)
block|{
comment|// only 1 Collector - return it.
name|Collector
name|col
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Collector
name|c
range|:
name|collectors
control|)
block|{
if|if
condition|(
name|c
operator|!=
literal|null
condition|)
block|{
name|col
operator|=
name|c
expr_stmt|;
break|break;
block|}
block|}
return|return
name|col
return|;
block|}
else|else
block|{
name|Collector
index|[]
name|colls
init|=
operator|new
name|Collector
index|[
name|n
index|]
decl_stmt|;
name|n
operator|=
literal|0
expr_stmt|;
for|for
control|(
name|Collector
name|c
range|:
name|collectors
control|)
block|{
if|if
condition|(
name|c
operator|!=
literal|null
condition|)
block|{
name|colls
index|[
name|n
operator|++
index|]
operator|=
name|c
expr_stmt|;
block|}
block|}
return|return
operator|new
name|MultiCollector
argument_list|(
name|colls
argument_list|)
return|;
block|}
block|}
DECL|field|cacheScores
specifier|private
specifier|final
name|boolean
name|cacheScores
decl_stmt|;
DECL|field|collectors
specifier|private
specifier|final
name|Collector
index|[]
name|collectors
decl_stmt|;
DECL|method|MultiCollector
specifier|private
name|MultiCollector
parameter_list|(
name|Collector
modifier|...
name|collectors
parameter_list|)
block|{
name|this
operator|.
name|collectors
operator|=
name|collectors
expr_stmt|;
name|int
name|numNeedsScores
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Collector
name|collector
range|:
name|collectors
control|)
block|{
if|if
condition|(
name|collector
operator|.
name|needsScores
argument_list|()
condition|)
block|{
name|numNeedsScores
operator|+=
literal|1
expr_stmt|;
block|}
block|}
name|this
operator|.
name|cacheScores
operator|=
name|numNeedsScores
operator|>=
literal|2
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|needsScores
specifier|public
name|boolean
name|needsScores
parameter_list|()
block|{
for|for
control|(
name|Collector
name|collector
range|:
name|collectors
control|)
block|{
if|if
condition|(
name|collector
operator|.
name|needsScores
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|getLeafCollector
specifier|public
name|LeafCollector
name|getLeafCollector
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|List
argument_list|<
name|LeafCollector
argument_list|>
name|leafCollectors
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Collector
name|collector
range|:
name|collectors
control|)
block|{
specifier|final
name|LeafCollector
name|leafCollector
decl_stmt|;
try|try
block|{
name|leafCollector
operator|=
name|collector
operator|.
name|getLeafCollector
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CollectionTerminatedException
name|e
parameter_list|)
block|{
comment|// this leaf collector does not need this segment
continue|continue;
block|}
name|leafCollectors
operator|.
name|add
argument_list|(
name|leafCollector
argument_list|)
expr_stmt|;
block|}
switch|switch
condition|(
name|leafCollectors
operator|.
name|size
argument_list|()
condition|)
block|{
case|case
literal|0
case|:
throw|throw
operator|new
name|CollectionTerminatedException
argument_list|()
throw|;
case|case
literal|1
case|:
return|return
name|leafCollectors
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
default|default:
return|return
operator|new
name|MultiLeafCollector
argument_list|(
name|leafCollectors
argument_list|,
name|cacheScores
argument_list|)
return|;
block|}
block|}
DECL|class|MultiLeafCollector
specifier|private
specifier|static
class|class
name|MultiLeafCollector
implements|implements
name|LeafCollector
block|{
DECL|field|cacheScores
specifier|private
specifier|final
name|boolean
name|cacheScores
decl_stmt|;
DECL|field|collectors
specifier|private
specifier|final
name|LeafCollector
index|[]
name|collectors
decl_stmt|;
DECL|field|numCollectors
specifier|private
name|int
name|numCollectors
decl_stmt|;
DECL|method|MultiLeafCollector
specifier|private
name|MultiLeafCollector
parameter_list|(
name|List
argument_list|<
name|LeafCollector
argument_list|>
name|collectors
parameter_list|,
name|boolean
name|cacheScores
parameter_list|)
block|{
name|this
operator|.
name|collectors
operator|=
name|collectors
operator|.
name|toArray
argument_list|(
operator|new
name|LeafCollector
index|[
name|collectors
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
name|this
operator|.
name|cacheScores
operator|=
name|cacheScores
expr_stmt|;
name|this
operator|.
name|numCollectors
operator|=
name|this
operator|.
name|collectors
operator|.
name|length
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setScorer
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|cacheScores
condition|)
block|{
name|scorer
operator|=
operator|new
name|ScoreCachingWrappingScorer
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|LeafCollector
name|c
range|:
name|collectors
control|)
block|{
name|c
operator|.
name|setScorer
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|removeCollector
specifier|private
name|void
name|removeCollector
parameter_list|(
name|int
name|i
parameter_list|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|collectors
argument_list|,
name|i
operator|+
literal|1
argument_list|,
name|collectors
argument_list|,
name|i
argument_list|,
name|numCollectors
operator|-
name|i
operator|-
literal|1
argument_list|)
expr_stmt|;
operator|--
name|numCollectors
expr_stmt|;
name|collectors
index|[
name|numCollectors
index|]
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|LeafCollector
index|[]
name|collectors
init|=
name|this
operator|.
name|collectors
decl_stmt|;
name|int
name|numCollectors
init|=
name|this
operator|.
name|numCollectors
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numCollectors
condition|;
control|)
block|{
specifier|final
name|LeafCollector
name|collector
init|=
name|collectors
index|[
name|i
index|]
decl_stmt|;
try|try
block|{
name|collector
operator|.
name|collect
argument_list|(
name|doc
argument_list|)
expr_stmt|;
operator|++
name|i
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CollectionTerminatedException
name|e
parameter_list|)
block|{
name|removeCollector
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|numCollectors
operator|=
name|this
operator|.
name|numCollectors
expr_stmt|;
if|if
condition|(
name|numCollectors
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|CollectionTerminatedException
argument_list|()
throw|;
block|}
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

